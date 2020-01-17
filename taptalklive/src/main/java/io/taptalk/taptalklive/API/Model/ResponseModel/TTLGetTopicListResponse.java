package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLTopicModel;

public class TTLGetTopicListResponse {
    @JsonProperty("topics") private List<TTLTopicModel> topics;

    public List<TTLTopicModel> getTopics() {
        return topics;
    }

    public void setTopics(List<TTLTopicModel> topics) {
        this.topics = topics;
    }
}
