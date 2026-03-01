package com.ticketmaster.demo.interfaces;

import com.ticketmaster.demo.dto.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageStorageInterface {

    String saveImage(String path, InputStream inputStream);

    /**
     * Sube una imagen a la carpeta especificada
     */
    UploadResult upload(MultipartFile file, String folder) throws Exception;

    /**
     * Elimina una imagen por su ID público
     */
    void delete(String publicId);

    /**
     * Obtiene la URL pública de un recurso
     */
    String getUrl(String publicId);

    boolean isCloudProvider();

}
