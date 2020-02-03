package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.taptalklive.API.Model.TTLUserModel;

public class TTLCreateUserResponse {
    @JsonProperty("user") private TTLUserModel user;
    @JsonProperty("ticket") private String ticket;

    public TTLUserModel getUser() {
        return user;
    }

    public void setUser(TTLUserModel user) {
        this.user = user;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
