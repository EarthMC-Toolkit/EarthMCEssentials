package net.emc.emce.object.exception;

public class APIException extends Exception {
    private static final long serialVersionUID = -3129573277546383741L;

    public APIException() {
        super("An unknown API exception has occurred.");
    }

    public APIException(String message) {
        super(message);
    }
}
