package ee.taltech.iti03022024project.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}
