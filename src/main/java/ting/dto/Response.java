package ting.dto;

public class Response<T> {
    private T data;

    private ResponseError error;

    public Response(T data) {
        this.data = data;
    }

    public Response(ResponseError error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseError getError() {
        return error;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }
}
