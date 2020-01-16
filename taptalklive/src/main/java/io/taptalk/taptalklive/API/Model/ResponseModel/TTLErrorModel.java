package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLErrorModel {
    @JsonProperty("code") private String code;
    @JsonProperty("message") private String message;
    @JsonProperty("field") private String field;

    public TTLErrorModel() {}

    public TTLErrorModel(String code, String message, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
