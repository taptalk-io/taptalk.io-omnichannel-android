package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TTLCommonResponse {
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("message")
    private String message;

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean var1) {
        this.success = var1;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String var1) {
        this.message = var1;
    }
}
