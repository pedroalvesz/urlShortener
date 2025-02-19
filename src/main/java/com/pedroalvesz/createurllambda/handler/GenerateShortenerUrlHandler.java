package com.pedroalvesz.createurllambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.pedroalvesz.createurllambda.UrlDataDTO;
import com.pedroalvesz.createurllambda.service.S3ManagerService;
import com.pedroalvesz.createurllambda.utils.ObjectMapperUtils;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GenerateShortenerUrlHandler implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final S3ManagerService s3ManagerService;

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        String body = input.get("body").toString();
        UrlDataDTO urlDataDTO = ObjectMapperUtils.fromJson(body, UrlDataDTO.class);

        String shortUrlCode = s3ManagerService.putObject(urlDataDTO);

        return Map.of("shortUrlCode", shortUrlCode);
    }
}
