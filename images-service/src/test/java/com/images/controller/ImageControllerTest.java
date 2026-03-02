package com.images.controller;

import com.images.dto.ImageResponse;
import com.images.exception.ImageException;
import com.images.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ImageController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    private ImageResponse buildResponse(String id) {
        return new ImageResponse(id, "file.jpg", "original.jpg", "image/jpeg",
                100L, "desc", LocalDateTime.now(), LocalDateTime.now(),
                "/api/images/" + id + "/download");
    }

    @Test
    void uploadImage_debeRetornar201ConDatosCorrectos() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes());

        when(imageService.uploadImage(any(), any())).thenReturn(buildResponse("1"));

        mockMvc.perform(multipart("/api/images")
                        .file(file)
                        .param("description", "desc"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.fileName").value("file.jpg"));
    }

    @Test
    void getAllImages_debeRetornar200ConLista() throws Exception {
        when(imageService.getAllImages()).thenReturn(List.of(buildResponse("1"), buildResponse("2")));

        mockMvc.perform(get("/api/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getImageById_debeRetornar200SiExiste() throws Exception {
        when(imageService.getImageById("1")).thenReturn(buildResponse("1"));

        mockMvc.perform(get("/api/images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getImageById_debeRetornar404SiNoExiste() throws Exception {
        when(imageService.getImageById("999")).thenThrow(new ImageException("Imagen no encontrada con id: 999"));

        mockMvc.perform(get("/api/images/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateImage_debeRetornar200ConImagenActualizada() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "updated.jpg", "image/jpeg", "updated".getBytes());

        when(imageService.updateImage(eq("1"), any(), any())).thenReturn(buildResponse("1"));

        mockMvc.perform(multipart("/api/images/1")
                        .file(file)
                        .param("description", "nueva desc")
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deleteImage_debeRetornar204() throws Exception {
        doNothing().when(imageService).deleteImage("1");

        mockMvc.perform(delete("/api/images/1"))
                .andExpect(status().isNoContent());

        verify(imageService).deleteImage("1");
    }

    @Test
    void deleteImage_debeRetornar404SiNoExiste() throws Exception {
        doThrow(new ImageException("Imagen no encontrada con id: 999"))
                .when(imageService).deleteImage("999");

        mockMvc.perform(delete("/api/images/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadImage_debeRetornar200ConBytesDeImagen() throws Exception {
        byte[] imageData = "image bytes".getBytes();
        when(imageService.getImageById("1")).thenReturn(buildResponse("1"));
        when(imageService.downloadImage("1")).thenReturn(imageData);

        mockMvc.perform(get("/api/images/1/download"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageData))
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE));
    }
}
