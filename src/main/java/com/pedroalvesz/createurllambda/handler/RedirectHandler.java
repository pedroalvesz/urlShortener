package com.pedroalvesz.createurllambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedroalvesz.createurllambda.UrlData;
import com.pedroalvesz.createurllambda.manager.S3Manager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RedirectHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String pathParameters = (String) input.get("rawPath");
        String shortUrlCode = pathParameters.replace("/", "");

        if (shortUrlCode == null || shortUrlCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required.");
        }

        S3Manager s3Manager = new S3Manager();
        InputStream s3InputStream = s3Manager.getObject(shortUrlCode);

        UrlData urlData;
        try {
            urlData = objectMapper.readValue(s3InputStream, UrlData.class);
        } catch (Exception exception) {
            throw new RuntimeException("ERROR | Error deserializing URL data from S3: " + exception.getMessage(), exception);
        }

        Map<String, Object> response = new HashMap<>();

        Long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        if (currentTimeInSeconds >= urlData.getExpirationTime()) {
            response.put("statusCode", 410);
            response.put("body", "This URl has expired.");
            return response;
        }

        response.put("statusCode", 302);

        Map<String, String> headers = new HashMap<>();
        headers.put("Location", urlData.getOriginalUrl());

        response.put("headers", headers);

        return response;
    }
}
