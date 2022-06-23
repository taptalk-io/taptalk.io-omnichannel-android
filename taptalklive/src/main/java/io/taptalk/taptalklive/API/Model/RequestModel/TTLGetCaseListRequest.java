package io.taptalk.taptalklive.API.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLGetCaseListRequest {
    @JsonProperty("withTapTalkRoom") private Boolean withTapTalkRoom;

    public TTLGetCaseListRequest(Boolean withTapTalkRoom) {
        this.withTapTalkRoom = withTapTalkRoom;
    }

    public Boolean getWithTapTalkRoom() {
        return withTapTalkRoom;
    }

    public void setWithTapTalkRoom(Boolean withTapTalkRoom) {
        this.withTapTalkRoom = withTapTalkRoom;
    }
}
