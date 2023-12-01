package org.myplaylist.myplaylist.utils;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class NextCloudWebDavClient {

    private static final String NEXTCLOUD_WEBDAV_URL = System.getenv("NEXTCLOUD_WEBDAV_URL");
    private static final String USERNAME_NEXTCLOUD = System.getenv("USERNAME_NEXTCLOUD");
    private static final String PASSWORD = System.getenv("NEXT_CLOUD_PASSWORD");


    public void uploadFile(File file, String remotePath) throws Exception {
        // Split the remotePath to get the directory and the filename separately
        int lastSlashIndex = remotePath.lastIndexOf('/');
        String directoryPath = remotePath.substring(0, lastSlashIndex + 1);
        String fileName = remotePath.substring(lastSlashIndex + 1);

        // URL-encode only the fileName part
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");


        HttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut(NEXTCLOUD_WEBDAV_URL + directoryPath + encodedFileName);

        // Basic authentication
        String auth = USERNAME_NEXTCLOUD + ":" + PASSWORD;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        put.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        // Set the file as the entity
        put.setEntity(new FileEntity(file));

        // Execute the request
        HttpResponse response = client.execute(put);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 201) {
            throw new RuntimeException("Failed to upload file. HTTP error code: " + statusCode);
        }
    }
}
