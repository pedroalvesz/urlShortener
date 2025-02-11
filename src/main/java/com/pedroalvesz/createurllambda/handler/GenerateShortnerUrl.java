package com.pedroalvesz.createurllambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedroalvesz.createurllambda.UrlData;
import com.pedroalvesz.createurllambda.manager.S3Manager;

import java.util.HashMap;
import java.util.Map;

public class GenerateShortnerUrl implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        String body = input.get("body").toString();

        Map<String, String> bodyMap;
        try {
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch (Exception exception) {
            throw new RuntimeException("ERROR | Error parsing JSON body: " + exception.getMessage(), exception);
        }

        String originalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get("expirationTime");
        Long expirationTimeInSeconds = Long.parseLong(expirationTime);
        // UrlData virar DTO
        UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

        S3Manager s3Manager = new S3Manager();
        String shortUrlCode = s3Manager.putObject(urlData);

        Map<String, String> response = new HashMap<>();
        response.put("urlCode", shortUrlCode);

        return response;
    }
}
