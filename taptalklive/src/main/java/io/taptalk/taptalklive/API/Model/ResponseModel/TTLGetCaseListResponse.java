package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLCaseModel;

public class TTLGetCaseListResponse {
    @JsonProperty("cases") private List<TTLCaseModel> cases;

    public List<TTLCaseModel> getCases() {
        return cases;
    }

    public void setCases(List<TTLCaseModel> cases) {
        this.cases = cases;
    }
}
