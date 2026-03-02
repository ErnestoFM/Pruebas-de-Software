package com.images.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageExceptionTest {

    @Test
    void constructor_conMensaje_debeCopiarMensaje() {
        ImageException ex = new ImageException("error de prueba");

        assertThat(ex.getMessage()).isEqualTo("error de prueba");
    }

    @Test
    void constructor_conMensajeYCausa_debeCopiarAmbos() {
        RuntimeException cause = new RuntimeException("causa");
        ImageException ex = new ImageException("error con causa", cause);

        assertThat(ex.getMessage()).isEqualTo("error con causa");
        assertThat(ex.getCause()).isEqualTo(cause);
    }
}
