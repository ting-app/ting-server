package ting.dto;

/**
 * The error entity returned to client.
 */
public class ResponseError {
    private Error error;

    public ResponseError(String message) {
        this.error = new Error(message);
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    /**
     * The detailed error message.
     */
    public static class Error {
        private String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
