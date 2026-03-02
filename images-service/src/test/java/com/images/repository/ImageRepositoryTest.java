package com.images.repository;

import com.images.model.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageRepositoryTest {

    @Mock
    private ImageRepository imageRepository;

    private Image buildImage(String id, String fileName) {
        return new Image(id, fileName, "original.jpg", "image/jpeg",
                100L, "/path/" + fileName, "desc", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void save_debeGuardarImagen() {
        Image image = buildImage(null, "file.jpg");
        Image saved = buildImage("1", "file.jpg");
        when(imageRepository.save(any(Image.class))).thenReturn(saved);

        Image result = imageRepository.save(image);

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getFileName()).isEqualTo("file.jpg");
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void findById_debeRetornarImagenExistente() {
        Image image = buildImage("1", "file.jpg");
        when(imageRepository.findById("1")).thenReturn(Optional.of(image));

        Optional<Image> found = imageRepository.findById("1");

        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("file.jpg");
    }

    @Test
    void findById_debeRetornarVacioSiNoExiste() {
        when(imageRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Image> found = imageRepository.findById("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_debeRetornarTodasLasImagenes() {
        when(imageRepository.findAll()).thenReturn(List.of(
                buildImage("1", "file1.jpg"),
                buildImage("2", "file2.jpg")
        ));

        List<Image> images = imageRepository.findAll();

        assertThat(images).hasSize(2);
    }

    @Test
    void delete_debeEliminarImagen() {
        Image image = buildImage("1", "file.jpg");

        imageRepository.delete(image);

        verify(imageRepository).delete(image);
    }

    @Test
    void findByFileName_debeRetornarImagenPorNombreDeArchivo() {
        Image image = buildImage("1", "unique_file.jpg");
        when(imageRepository.findByFileName("unique_file.jpg")).thenReturn(Optional.of(image));

        Optional<Image> found = imageRepository.findByFileName("unique_file.jpg");

        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("unique_file.jpg");
    }

    @Test
    void existsByFileName_debeRetornarTrueSiExiste() {
        when(imageRepository.existsByFileName("exists.jpg")).thenReturn(true);

        assertThat(imageRepository.existsByFileName("exists.jpg")).isTrue();
    }

    @Test
    void existsByFileName_debeRetornarFalseSiNoExiste() {
        when(imageRepository.existsByFileName("notexists.jpg")).thenReturn(false);

        assertThat(imageRepository.existsByFileName("notexists.jpg")).isFalse();
    }
}
