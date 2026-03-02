package com.images.service;

import com.images.dto.ImageResponse;
import com.images.exception.ImageException;
import com.images.model.Image;
import com.images.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageServiceImpl imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "uploadDir", tempDir.toString());
    }

    @Test
    void uploadImage_debeGuardarYRetornarRespuesta() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "contenido de prueba".getBytes());

        Image savedImage = new Image("1", "uuid_test.jpg", "test.jpg", "image/jpeg",
                20L, tempDir + "/uuid_test.jpg", "desc", LocalDateTime.now(), LocalDateTime.now());

        when(imageRepository.save(any(Image.class))).thenReturn(savedImage);

        ImageResponse response = imageService.uploadImage(file, "desc");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("1");
        assertThat(response.contentType()).isEqualTo("image/jpeg");
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    void uploadImage_debeLanzarExcepcionSiArchivoEsNulo() {
        assertThatThrownBy(() -> imageService.uploadImage(null, "desc"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("vacío");
    }

    @Test
    void uploadImage_debeLanzarExcepcionSiNoEsImagen() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "pdf content".getBytes());

        assertThatThrownBy(() -> imageService.uploadImage(file, "desc"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("imagen");
    }

    @Test
    void uploadImage_debeLanzarExcepcionSiArchivoEstaVacio() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> imageService.uploadImage(file, "desc"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("vacío");
    }

    @Test
    void getAllImages_debeRetornarListaDeImagenes() {
        Image image1 = new Image("1", "file1.jpg", "original1.jpg", "image/jpeg",
                100L, "/path/1", "desc1", LocalDateTime.now(), LocalDateTime.now());
        Image image2 = new Image("2", "file2.png", "original2.png", "image/png",
                200L, "/path/2", "desc2", LocalDateTime.now(), LocalDateTime.now());

        when(imageRepository.findAll()).thenReturn(List.of(image1, image2));

        List<ImageResponse> result = imageService.getAllImages();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("1");
        assertThat(result.get(1).id()).isEqualTo("2");
    }

    @Test
    void getImageById_debeRetornarImagenExistente() {
        Image image = new Image("1", "file.jpg", "orig.jpg", "image/jpeg",
                100L, "/path", "desc", LocalDateTime.now(), LocalDateTime.now());

        when(imageRepository.findById("1")).thenReturn(Optional.of(image));

        ImageResponse response = imageService.getImageById("1");

        assertThat(response.id()).isEqualTo("1");
        assertThat(response.fileName()).isEqualTo("file.jpg");
    }

    @Test
    void getImageById_debeLanzarExcepcionSiNoExiste() {
        when(imageRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.getImageById("999"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateImage_debeActualizarDescripcion() {
        Image image = new Image("1", "file.jpg", "orig.jpg", "image/jpeg",
                100L, tempDir + "/file.jpg", "desc", LocalDateTime.now(), LocalDateTime.now());

        when(imageRepository.findById("1")).thenReturn(Optional.of(image));
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        ImageResponse response = imageService.updateImage("1", null, "nueva descripcion");

        assertThat(response).isNotNull();
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void updateImage_debeActualizarArchivoYDescripcion() throws IOException {
        Path existingFile = tempDir.resolve("file.jpg");
        Files.write(existingFile, "old content".getBytes());

        Image image = new Image("1", "file.jpg", "orig.jpg", "image/jpeg",
                100L, existingFile.toString(), "desc", LocalDateTime.now(), LocalDateTime.now());

        MockMultipartFile newFile = new MockMultipartFile(
                "file", "new.jpg", "image/jpeg", "new content".getBytes());

        when(imageRepository.findById("1")).thenReturn(Optional.of(image));
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        ImageResponse response = imageService.updateImage("1", newFile, "nueva descripcion");

        assertThat(response).isNotNull();
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void updateImage_debeLanzarExcepcionSiImagenNoExiste() {
        when(imageRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.updateImage("999", null, "desc"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateImage_debeLanzarExcepcionSiArchivoNoEsImagen() {
        Image image = new Image("1", "file.jpg", "orig.jpg", "image/jpeg",
                100L, tempDir + "/file.jpg", "desc", LocalDateTime.now(), LocalDateTime.now());
        MockMultipartFile badFile = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "pdf content".getBytes());

        when(imageRepository.findById("1")).thenReturn(Optional.of(image));

        assertThatThrownBy(() -> imageService.updateImage("1", badFile, "desc"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("imagen");
    }

    @Test
    void deleteImage_debeEliminarImagenExistente() throws IOException {
        Path fileToDelete = tempDir.resolve("file.jpg");
        Files.write(fileToDelete, "content".getBytes());

        Image image = new Image("1", "file.jpg", "orig.jpg", "image/jpeg",
                100L, fileToDelete.toString(), "desc", LocalDateTime.now(), LocalDateTime.now());

        when(imageRepository.findById("1")).thenReturn(Optional.of(image));

        imageService.deleteImage("1");

        verify(imageRepository).delete(image);
        assertThat(Files.exists(fileToDelete)).isFalse();
    }

    @Test
    void deleteImage_debeLanzarExcepcionSiNoExiste() {
        when(imageRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.deleteImage("999"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("999");
    }

    @Test
    void downloadImage_debeRetornarBytesDelArchivo() throws IOException {
        byte[] content = "image bytes".getBytes();
        Path imageFile = tempDir.resolve("file.jpg");
        Files.write(imageFile, content);

        Image image = new Image("1", "file.jpg", "orig.jpg", "image/jpeg",
                (long) content.length, imageFile.toString(), "desc", LocalDateTime.now(), LocalDateTime.now());

        when(imageRepository.findById("1")).thenReturn(Optional.of(image));

        byte[] result = imageService.downloadImage("1");

        assertThat(result).isEqualTo(content);
    }

    @Test
    void downloadImage_debeLanzarExcepcionSiArchivoFisicoNoExiste() {
        Image image = new Image("1", "file.jpg", "orig.jpg", "image/jpeg",
                100L, tempDir + "/nonexistent.jpg", "desc", LocalDateTime.now(), LocalDateTime.now());

        when(imageRepository.findById("1")).thenReturn(Optional.of(image));

        assertThatThrownBy(() -> imageService.downloadImage("1"))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("no existe");
    }
}
