package com.amazon.ata.creatingexceptions.exception;

public class FlavorOutOfStockException extends RuntimeException{
    private static final long serialVersionUID = -6842075089065894013L;

    public FlavorOutOfStockException() {super();}
    public FlavorOutOfStockException(String message) {
        super(message);
    }
    public FlavorOutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
