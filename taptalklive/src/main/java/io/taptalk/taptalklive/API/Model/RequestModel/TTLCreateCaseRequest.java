package io.taptalk.taptalklive.API.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLCreateCaseRequest {
    @JsonProperty("topicID") private Integer topicID;
    @JsonProperty("message") private String message;

    public TTLCreateCaseRequest(Integer topicID, String message) {
        this.topicID = topicID;
        this.message = message;
    }

    public Integer getTopicID() {
        return topicID;
    }

    public void setTopicID(Integer topicID) {
        this.topicID = topicID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
