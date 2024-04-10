package shikiri.board.exceptions;

import org.springframework.dao.DataAccessException;

public class CustomDataAccessException extends DataAccessException {

    public CustomDataAccessException(String msg) {
        super(msg);
    }

    public CustomDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
