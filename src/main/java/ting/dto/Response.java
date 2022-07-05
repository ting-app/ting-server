package ting.dto;

public class Response<T> {
    private T data;
    private Error error;

    public Response(T data) {
        this.data = data;
    }

    public Response(Error error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
