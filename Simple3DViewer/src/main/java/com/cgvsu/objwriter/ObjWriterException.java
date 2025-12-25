package com.cgvsu.objwriter;

/**
 * Исключение, возникающее при ошибках записи OBJ файла.
 */
public class ObjWriterException extends RuntimeException {

    public ObjWriterException(String errorMessage) {
        super("Ошибка записи OBJ файла: " + errorMessage);
    }

    public ObjWriterException(String errorMessage, Throwable cause) {
        super("Ошибка записи OBJ файла: " + errorMessage, cause);
    }
}
