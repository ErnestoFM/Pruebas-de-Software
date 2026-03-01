package com.ticketmaster.demo.exception;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(int stock) {
        super("No hay suficientes boletos disponibles. Quedan: " + stock);
    }
}