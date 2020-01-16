package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLTopic;
import io.taptalk.taptalklive.API.Model.TTLUserModel;

public class TTLGetTopicListResponse {
    @JsonProperty("topics") private List<TTLTopic> topics;

    public List<TTLTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<TTLTopic> topics) {
        this.topics = topics;
    }
}
