package pl.pollub.inwentaryzacja.client.api;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
