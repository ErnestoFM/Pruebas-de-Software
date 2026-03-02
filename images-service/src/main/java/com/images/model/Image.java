package com.images.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    private String id;

    private String fileName;
    private String originalFileName;
    private String contentType;
    private long size;
    private String filePath;
    private String description;
    private LocalDateTime uploadDate;
    private LocalDateTime updatedDate;
}
