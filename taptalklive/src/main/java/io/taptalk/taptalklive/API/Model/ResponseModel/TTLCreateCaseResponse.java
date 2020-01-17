package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.taptalklive.API.Model.TTLCaseModel;

public class TTLCreateCaseResponse {
    @JsonProperty("case") private TTLCaseModel caseResponse;

    public TTLCaseModel getCaseResponse() {
        return caseResponse;
    }

    public void setCaseResponse(TTLCaseModel caseResponse) {
        this.caseResponse = caseResponse;
    }
}
