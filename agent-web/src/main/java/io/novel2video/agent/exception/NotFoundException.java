package io.novel2video.agent.exception;

/**
 * 资源未找到异常
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resource, String id) {
        super(String.format("%s not found with id: %s", resource, id));
    }
}