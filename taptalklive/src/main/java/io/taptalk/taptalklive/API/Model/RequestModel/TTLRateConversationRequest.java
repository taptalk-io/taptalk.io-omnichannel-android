package io.taptalk.taptalklive.API.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLRateConversationRequest {
    @JsonProperty("id") private Integer caseID;
    @JsonProperty("rating") private Integer rating;
    @JsonProperty("note") private String note;

    public TTLRateConversationRequest(Integer caseID, Integer rating, String note) {
        this.caseID = caseID;
        this.rating = rating;
        this.note = note;
    }

    public Integer getCaseID() {
        return caseID;
    }

    public void setCaseID(Integer caseID) {
        this.caseID = caseID;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
