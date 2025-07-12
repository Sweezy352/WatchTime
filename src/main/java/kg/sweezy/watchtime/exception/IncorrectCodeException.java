package kg.sweezy.watchtime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectCodeException extends BaseException {
    public IncorrectCodeException(String message) {
        super(message);
    }
}
