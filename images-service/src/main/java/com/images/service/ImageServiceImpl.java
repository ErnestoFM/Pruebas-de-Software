package com.images.service;

import com.images.dto.ImageResponse;
import com.images.exception.ImageException;
import com.images.model.Image;
import com.images.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${images.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public ImageResponse uploadImage(MultipartFile file, String description) {
        if (file == null || file.isEmpty()) {
            throw new ImageException("El archivo no puede estar vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageException("El archivo debe ser una imagen");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.write(filePath, file.getBytes());

            Image image = new Image();
            image.setFileName(uniqueFileName);
            image.setOriginalFileName(file.getOriginalFilename());
            image.setContentType(contentType);
            image.setSize(file.getSize());
            image.setFilePath(filePath.toString());
            image.setDescription(description);
            image.setUploadDate(LocalDateTime.now());
            image.setUpdatedDate(LocalDateTime.now());

            Image saved = imageRepository.save(image);
            log.info("Imagen subida exitosamente con id: {}", saved.getId());
            return toResponse(saved);
        } catch (IOException e) {
            throw new ImageException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    @Override
    @Cacheable(value = "images")
    public List<ImageResponse> getAllImages() {
        return imageRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "image", key = "#id")
    public ImageResponse getImageById(String id) {
        Image image = findImageOrThrow(id);
        return toResponse(image);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"image", "images"}, key = "#id", allEntries = true)
    public ImageResponse updateImage(String id, MultipartFile file, String description) {
        Image image = findImageOrThrow(id);

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ImageException("El archivo debe ser una imagen");
            }
            try {
                Path oldPath = Paths.get(image.getFilePath());
                if (Files.exists(oldPath)) {
                    Files.delete(oldPath);
                }

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path newFilePath = uploadPath.resolve(uniqueFileName);
                Files.write(newFilePath, file.getBytes());

                image.setFileName(uniqueFileName);
                image.setOriginalFileName(file.getOriginalFilename());
                image.setContentType(contentType);
                image.setSize(file.getSize());
                image.setFilePath(newFilePath.toString());
            } catch (IOException e) {
                throw new ImageException("Error al actualizar el archivo: " + e.getMessage(), e);
            }
        }

        if (description != null && !description.isBlank()) {
            image.setDescription(description);
        }
        image.setUpdatedDate(LocalDateTime.now());

        Image updated = imageRepository.save(image);
        log.info("Imagen actualizada exitosamente con id: {}", id);
        return toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"image", "images"}, allEntries = true)
    public void deleteImage(String id) {
        Image image = findImageOrThrow(id);
        try {
            Path filePath = Paths.get(image.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo físico para la imagen {}: {}", id, e.getMessage());
        }
        imageRepository.delete(image);
        log.info("Imagen eliminada exitosamente con id: {}", id);
    }

    @Override
    @Cacheable(value = "imageData", key = "#id")
    public byte[] downloadImage(String id) {
        Image image = findImageOrThrow(id);
        try {
            Path filePath = Paths.get(image.getFilePath());
            if (!Files.exists(filePath)) {
                throw new ImageException("El archivo físico no existe para la imagen con id: " + id);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new ImageException("Error al leer el archivo: " + e.getMessage(), e);
        }
    }

    private Image findImageOrThrow(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ImageException("Imagen no encontrada con id: " + id));
    }

    private ImageResponse toResponse(Image image) {
        return new ImageResponse(
                image.getId(),
                image.getFileName(),
                image.getOriginalFileName(),
                image.getContentType(),
                image.getSize(),
                image.getDescription(),
                image.getUploadDate(),
                image.getUpdatedDate(),
                "/api/images/" + image.getId() + "/download"
        );
    }
}
