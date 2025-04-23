package com.epam.edp.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Pavlo_Yemelianov
 */
@RestController
public class HelloEdpController {

    private final S3Client s3Client;
    private final String bucketName;
    private final String objectKey;

    public HelloEdpController(S3Client s3Client,
                              @Value("${s3.bucket}") String bucketName,
                              @Value("${s3.key}") String objectKey) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    @GetMapping(value = "/api/hello")
    public String hello() {
        return "Hello, EDP!";
    }

    @GetMapping(value = "/")
    public String getContentFromS3() {
    GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build();

    try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request)) {
        String content = new BufferedReader(new InputStreamReader(s3Object))
                .lines()
                .collect(Collectors.joining("\n"));

                return Collections.singletonMap("content", content);
    } catch (IOException e) {
        throw new RuntimeException("Failed to read content from S3", e);
    }
}
}
