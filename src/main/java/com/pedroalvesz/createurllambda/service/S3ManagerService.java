package com.pedroalvesz.createurllambda.service;

import com.pedroalvesz.createurllambda.UrlDataDTO;
import com.pedroalvesz.createurllambda.utils.ObjectMapperUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

public class S3ManagerService {

    private final S3Client s3Client = S3Client.builder().build();

    public String putObject(UrlDataDTO urlDataDTO) {
        try {
            final String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("url-shortener-pedroalvesz")
                    .key(shortUrlCode + ".json")
                    .build();

            String urlDataJson = ObjectMapperUtils.toJson(urlDataDTO);
            s3Client.putObject(putObjectRequest, RequestBody.fromString(urlDataJson));

            return shortUrlCode;
        } catch (Exception exception) {
            throw new RuntimeException("ERROR | Error saving data to S3: " + exception.getMessage(), exception);
        }
    }

    public InputStream getObject(String name) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket("url-shortener-pedroalvesz")
                    .key(name + ".json")
                    .build();

            return s3Client.getObject(getObjectRequest);
        } catch (Exception exception) {
            throw new RuntimeException("ERROR | Error fetching data from S3: " + exception.getMessage(), exception);
        }
    }
}
