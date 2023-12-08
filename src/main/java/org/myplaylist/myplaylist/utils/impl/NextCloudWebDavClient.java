package org.myplaylist.myplaylist.utils.impl;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import jakarta.xml.bind.JAXBException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.myplaylist.myplaylist.config.NextCloudProperties;
import org.myplaylist.myplaylist.model.dto.Ocs;
import org.myplaylist.myplaylist.utils.XmlParser;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class NextCloudWebDavClient {
    private final NextCloudProperties nextCloudProperties;
    private final XmlParser xmlParser;
    private static final String BASIC_AUTH = "Basic ";
    private static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    private static final String ISO_8859_1 = StandardCharsets.ISO_8859_1.name();
    private static final int SUCCESS_STATUS_CODE_UPLOAD = 201;
    private static final int SUCCESS_STATUS_CODE_DELETE = 204;
    private static final int SUCCESS_STATUS_CODE_SHARE_LINK = 200;

    public NextCloudWebDavClient(NextCloudProperties nextCloudProperties, XmlParser xmlParser) {
        this.nextCloudProperties = nextCloudProperties;
        this.xmlParser = xmlParser;
    }

    private void validateResponse(HttpResponse response, int expectedStatusCode, String errorMessage) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != expectedStatusCode) {
            throw new RuntimeException(errorMessage + statusCode);
        }
    }

    private HttpClient getHttpClient() {
        return HttpClients.createDefault();
    }

    private String getBasicAuth() throws UnsupportedEncodingException {
        String auth = nextCloudProperties.getUsername() + ":" + nextCloudProperties.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(ISO_8859_1));
        return BASIC_AUTH + new String(encodedAuth);
    }

    public List<String> uploadFile(File file, String remotePath, String email) throws Exception {
        List<String> pathAndShareLink = new ArrayList<>();
        String fileName = remotePath.substring(remotePath.lastIndexOf('/') + 1);
        String userDirectoryPath = "/UsersUploads/" + URLEncoder.encode(email, StandardCharsets.UTF_8).replace("+", "%20") + "/";
        createSubfolderIfNotExists(userDirectoryPath);
        String filePath = userDirectoryPath + fileName;
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        String fullPath = nextCloudProperties.getWebdavUrl() + userDirectoryPath + encodedFileName;
        HttpClient client = getHttpClient();
        HttpPut put = new HttpPut(fullPath);
        put.setHeader(AUTHORIZATION, getBasicAuth());
        put.setEntity(new FileEntity(file));
        HttpResponse response = client.execute(put);
        validateResponse(response, SUCCESS_STATUS_CODE_UPLOAD, "Failed to upload file. HTTP error code: ");
        String shareLink = createShareLink(filePath);
        pathAndShareLink.add(shareLink);
        pathAndShareLink.add(fullPath);
        return pathAndShareLink;
    }

    public void deleteFile(String filePath) throws Exception {
        HttpClient client = getHttpClient();
        HttpDelete delete = new HttpDelete(filePath);
        delete.setHeader(AUTHORIZATION, getBasicAuth());
        HttpResponse response = client.execute(delete);
        validateResponse(response, SUCCESS_STATUS_CODE_DELETE, "Failed to delete file. HTTP error code: ");
    }

    private String createShareLink(String remotePath) throws Exception {
        HttpClient client = getHttpClient();
        HttpPost post = new HttpPost(nextCloudProperties.getShareLink());
        post.setHeader(AUTHORIZATION, getBasicAuth());
        post.setHeader("OCS-APIRequest", "true");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("path", remotePath));
        params.add(new BasicNameValuePair("shareType", "3"));
        params.add(new BasicNameValuePair("permissions", "1"));
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(post);
        validateResponse(response, SUCCESS_STATUS_CODE_SHARE_LINK, "Failed to create share link. HTTP error code: ");
        return extractShareLink(response);
    }

    private String extractShareLink(HttpResponse response) throws IOException, JAXBException {

        // Get the response entity
        HttpEntity entity = response.getEntity();
        String responseXml = EntityUtils.toString(entity);
        Ocs ocs = xmlParser.from(responseXml, Ocs.class);

        String baseUrl = ocs.getData().getUrl();

        return baseUrl + "/download";
    }

    public void createSubfolderIfNotExists( String directoryPath) throws IOException {
        Sardine sardine = SardineFactory.begin(nextCloudProperties.getUsername(), nextCloudProperties.getPassword());
        String fullUrl = nextCloudProperties.getWebdavUrl() + directoryPath;

        if (!doesFolderExist(sardine, fullUrl)) {
            sardine.createDirectory(nextCloudProperties.getWebdavUrl() + directoryPath);
        }
    }

    private boolean doesFolderExist(Sardine sardine, String fullUrl) throws IOException {
        try {
            // Extract the path from the full URL
            URL url = new URL(fullUrl);
            String pathToCheck = url.getPath();

            List<DavResource> resources = sardine.list(url.getProtocol() + "://" + url.getHost() + url.getPath());
            return resources.stream().anyMatch(res -> res.getHref().getPath().equals(pathToCheck));
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                return false; // The Folder does not exist
            }
            throw e;
        }
    }

}
