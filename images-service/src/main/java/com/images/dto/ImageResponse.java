package com.images.dto;

import java.time.LocalDateTime;

public record ImageResponse(
        String id,
        String fileName,
        String originalFileName,
        String contentType,
        long size,
        String description,
        LocalDateTime uploadDate,
        LocalDateTime updatedDate,
        String downloadUrl
) {
}
