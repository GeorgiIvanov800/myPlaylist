package org.myplaylist.myplaylist.utils.impl;

import jakarta.xml.bind.JAXBException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

    public String uploadFile(File file, String remotePath) throws Exception {

        // Split the remotePath to get the directory and the filename separately
        int lastSlashIndex = remotePath.lastIndexOf('/');
        String directoryPath = remotePath.substring(0, lastSlashIndex + 1);
        String fileName = remotePath.substring(lastSlashIndex + 1);

        // URL-encode only the fileName part
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");


        HttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut(nextCloudProperties.getWebdavUrl() + directoryPath + encodedFileName);

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
            return createShareLink(remotePath);
        } else {
            throw new RuntimeException("Failed to upload file. HTTP error code: " + statusCode);
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
}
