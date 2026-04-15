package com.example.s3sample;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @GetMapping("/objects")
    public List<S3ObjectResponse> listObjects(
            @RequestParam(required = false) String prefix) {

        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucketName);

        if (prefix != null && !prefix.isBlank()) {
            requestBuilder.prefix(prefix);
        }

        ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());

        return response.contents().stream()
                .map(obj -> new S3ObjectResponse(obj.key(), obj.size(), obj.lastModified().toString()))
                .toList();
    }

    @PostMapping("/objects")
    @ResponseStatus(HttpStatus.CREATED)
    public S3UploadResponse uploadObject(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String prefix) throws IOException {

        String key = (prefix != null && !prefix.isBlank())
                ? prefix.replaceAll("/+$", "") + "/" + file.getOriginalFilename()
                : file.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return new S3UploadResponse(key, file.getSize());
    }

    public record S3ObjectResponse(String key, long size, String lastModified) {}

    public record S3UploadResponse(String key, long size) {}
}
