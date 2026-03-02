package com.images.service;

import com.images.dto.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    ImageResponse uploadImage(MultipartFile file, String description);

    List<ImageResponse> getAllImages();

    ImageResponse getImageById(String id);

    ImageResponse updateImage(String id, MultipartFile file, String description);

    void deleteImage(String id);

    byte[] downloadImage(String id);
}
