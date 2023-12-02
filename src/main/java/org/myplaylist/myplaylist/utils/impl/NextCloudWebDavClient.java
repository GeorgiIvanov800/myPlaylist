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
import java.net.URI;
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

    public NextCloudWebDavClient(NextCloudProperties nextCloudProperties, XmlParser xmlParser) {
        this.nextCloudProperties = nextCloudProperties;
        this.xmlParser = xmlParser;
    }

    public String uploadFile(File file, String remotePath, String email) throws Exception {
        // Split the remotePath to get the directory and the filename separately
        int lastSlashIndex = remotePath.lastIndexOf('/');
        String directoryPath = remotePath.substring(0, lastSlashIndex + 1);
        String fileName = remotePath.substring(lastSlashIndex + 1);

        String userDirectoryPath = "/Songs/" + URLEncoder.encode(email, StandardCharsets.UTF_8).replace("+", "%20") + "/";

        // Ensure the user-specific directory exists
        createSubfolderIfNotExists(userDirectoryPath);
        String filePath = userDirectoryPath + fileName;

        // URL-encode only the fileName part
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        // Construct the full file path for the upload
        String fullPath = nextCloudProperties.getWebdavUrl() + userDirectoryPath + encodedFileName;

        HttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut(fullPath);

        // Basic authentication
        String auth = nextCloudProperties.getUsername() + ":" + nextCloudProperties.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        put.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        // Set the file as the entity
        put.setEntity(new FileEntity(file));

        // Execute the request
        HttpResponse response = client.execute(put);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == 201) {
            String shareLink = createShareLink(filePath);
            return shareLink;
        } else {
            throw new RuntimeException("Failed to upload file. HTTP error code: " + statusCode);
        }
    }

    public void deleteFile(String remotePath) throws Exception {

        String webdavUrl = nextCloudProperties.getWebdavUrl() + "/remote.php/dav/files/" + nextCloudProperties.getUsername() + "/" + remotePath;

        // URL-encode the path (excluding domain and protocol)
        URI uri = new URI(
                new URL(webdavUrl).getProtocol(),
                null,
                new URL(webdavUrl).getHost(),
                new URL(webdavUrl).getPort(),
                new URL(webdavUrl).getPath(),
                null,
                null
        );

        HttpClient client = HttpClients.createDefault();
        HttpDelete delete = new HttpDelete(uri);

        // Basic authentication
        String auth = nextCloudProperties.getUsername() + ":" + nextCloudProperties.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        delete.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        // Execute the request
        HttpResponse response = client.execute(delete);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 204) { // 204 No Content is the expected response for a successful deletion
            throw new RuntimeException("Failed to delete file. HTTP error code: " + statusCode);
        }
    }

    private String createShareLink(String remotePath) throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(nextCloudProperties.getShareLink());

        // Basic authentication
        String auth = nextCloudProperties.getUsername() + ":" + nextCloudProperties.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        post.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        post.setHeader("OCS-APIRequest", "true");

        // Set up the request body for creating a share
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("path", remotePath));
        params.add(new BasicNameValuePair("shareType", "3")); // 3 for a public link
        params.add(new BasicNameValuePair("permissions", "1")); // 1 for read-only
        post.setEntity(new UrlEncodedFormEntity(params));

        // Execute the request
        HttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();

        // Handle the response to extract and store the shareable link
        if (statusCode == 200) {
            return extractShareLink(response);
        } else {
            throw new RuntimeException("Failed to create share link. HTTP error code: " + statusCode);
        }
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
//        String fullUrl = "http://192.168.0.204/remote.php/dav/files/admin" + directoryPath;

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
            throw e; // Other IO exceptions should be handled or thrown
        }
    }

}
