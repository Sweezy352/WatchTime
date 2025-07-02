package kg.sweezy.watchtime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VideoUploadException extends BaseException {
    public VideoUploadException(String message) {
        super(message);
    }
}
