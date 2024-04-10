package shikiri.board.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class CustomDataIntegrityViolationException extends DataIntegrityViolationException {

    public CustomDataIntegrityViolationException(String msg) {
        super(msg);
    }

    public CustomDataIntegrityViolationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}