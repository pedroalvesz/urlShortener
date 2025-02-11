package com.pedroalvesz.createurllambda.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedroalvesz.createurllambda.UrlData;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

public class S3Manager {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client = S3Client.builder().build();

    public String putObject(UrlData urlData) {
        try {
            final String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("url-shortener-pedroalvesz")
                    .key(shortUrlCode + ".json")
                    .build();

            final String urlDataJson = objectMapper.writeValueAsString(urlData);
            s3Client.putObject(putObjectRequest, RequestBody.fromString(urlDataJson));

            return shortUrlCode;
        } catch (Exception exception) {
            throw new RuntimeException("ERROR | Error saving data to S3: " + exception.getMessage(), exception);
        }
    }

    public InputStream getObject(String shortUrlCode) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket("url-shortener-pedroalvesz")
                    .key(shortUrlCode + ".json")
                    .build();

            return s3Client.getObject(getObjectRequest);
        } catch (Exception exception) {
            throw new RuntimeException("ERROR | Error fetching URL from S3: " + exception.getMessage(), exception);
        }
    }
}
