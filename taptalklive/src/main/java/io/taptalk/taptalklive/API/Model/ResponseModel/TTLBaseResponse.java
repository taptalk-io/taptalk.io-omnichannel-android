package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.taptalk.taptalklive.API.deserializer.TTLErrorEmptyAsNullDeserializer;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLBaseResponse<T> {
    @JsonDeserialize(using = TTLErrorEmptyAsNullDeserializer.class)
    @JsonProperty("error")
    private TTLErrorModel error;

    @JsonProperty("data") private T data;

    @JsonProperty("status") private int status;

    public TTLErrorModel getError() {
        return error;
    }

    public void setError(TTLErrorModel error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
