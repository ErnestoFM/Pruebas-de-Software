package com.images.repository;

import com.images.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

    Optional<Image> findByFileName(String fileName);

    boolean existsByFileName(String fileName);
}
