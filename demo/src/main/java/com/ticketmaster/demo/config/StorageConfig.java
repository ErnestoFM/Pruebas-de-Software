package com.ticketmaster.demo.config;

import com.ticketmaster.demo.dto.UploadResult;
import com.ticketmaster.demo.interfaces.ImageStorageInterface;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Component
public class StorageConfig implements ImageStorageInterface {

        @Value("${aws.access.key}")
        private String accessKey;

        @Value("${aws.secret.key}")
        private String secretKey;

        @Value("${aws.region}")
        private String region;

        @Value("${aws.bucket}")
        private String bucket;

        @Getter
        private S3Client s3Client;

        @PostConstruct
        public void init() {
            this.s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        }


    @Override
    public String saveImage(String path, InputStream inputStream) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();

        try {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, inputStream.available())
            );
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo el InputStream", e);
        }        return "https://" + bucket + ".s3.amazonaws.com/" + path;
    }

    @Override
    public UploadResult upload(MultipartFile file, String folder) {
        return null;
    }

    @Override
    public void delete(String publicId) {

    }

    @Override
    public String getUrl(String publicId) {
        return null;
    }

    @Override
    public boolean isCloudProvider() {
        return false;
    }
}
