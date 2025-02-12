package com.pedroalvesz.createurllambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.pedroalvesz.createurllambda.UrlDataDTO;
import com.pedroalvesz.createurllambda.manager.S3ManagerService;
import com.pedroalvesz.createurllambda.utils.ObjectMapperUtils;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class RedirectHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final S3ManagerService s3ManagerService;

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String pathParameters = (String) input.get("rawPath");
        String shortUrlCode = pathParameters.replace("/", "");

        if (shortUrlCode == null || shortUrlCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required.");
        }

        InputStream s3InputStream = s3ManagerService.getObject(shortUrlCode);
        UrlDataDTO urlDataDTO = ObjectMapperUtils.fromInputStream(s3InputStream, UrlDataDTO.class);

        Map<String, Object> response = new HashMap<>();

        Long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        if (currentTimeInSeconds >= urlDataDTO.getExpirationTime()) {
            response.put("statusCode", 410);
            response.put("body", "This URl has expired.");
            return response;
        }

        response.put("statusCode", 302);

        Map<String, String> headers = new HashMap<>();
        headers.put("Location", urlDataDTO.getOriginalUrl());

        response.put("headers", headers);

        return response;
    }
}
