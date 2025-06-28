package kg.sweezy.watchtime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileIsNotExistException extends RuntimeException {
    public FileIsNotExistException(String message) {
        super(message);
    }
}
