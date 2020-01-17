package io.taptalk.taptalklive.API.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLCaseModel implements Parcelable {
    @JsonProperty("id") private Integer id;
    @JsonProperty("stringID") private String stringID;
    @JsonProperty("userID") private String userID;
    @JsonProperty("userFullName") private String userFullName;
    @JsonProperty("topicID") private Integer topicID;
    @JsonProperty("topicName") private String topicName;
    @JsonProperty("agentAccountID") private Integer agentAccountID;
    @JsonProperty("agentFullName") private String agentFullName;
    @JsonProperty("tapTalkXCRoomID") private String tapTalkXCRoomID;
    @JsonProperty("medium") private String medium;
    @JsonProperty("firstMessage") private String firstMessage;
    @JsonProperty("firstResponseTime") private Long firstResponseTime;
    @JsonProperty("firstResponseAgentAccountID") private Integer firstResponseAgentAccountID;
    @JsonProperty("firstResponseAgentFullName") private String firstResponseAgentFullName;
    @JsonProperty("isClosed") private Boolean isClosed;
    @JsonProperty("closedTime") private Long closedTime;
    @JsonProperty("createdTime") private Long createdTime;
    @JsonProperty("updatedTime") private Long updatedTime;
    @JsonProperty("deletedTime") private Long deletedTime;

    public TTLCaseModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStringID() {
        return stringID;
    }

    public void setStringID(String stringID) {
        this.stringID = stringID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Integer getTopicID() {
        return topicID;
    }

    public void setTopicID(Integer topicID) {
        this.topicID = topicID;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getAgentAccountID() {
        return agentAccountID;
    }

    public void setAgentAccountID(Integer agentAccountID) {
        this.agentAccountID = agentAccountID;
    }

    public String getAgentFullName() {
        return agentFullName;
    }

    public void setAgentFullName(String agentFullName) {
        this.agentFullName = agentFullName;
    }

    public String getTapTalkXCRoomID() {
        return tapTalkXCRoomID;
    }

    public void setTapTalkXCRoomID(String tapTalkXCRoomID) {
        this.tapTalkXCRoomID = tapTalkXCRoomID;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getFirstMessage() {
        return firstMessage;
    }

    public void setFirstMessage(String firstMessage) {
        this.firstMessage = firstMessage;
    }

    public Long getFirstResponseTime() {
        return firstResponseTime;
    }

    public void setFirstResponseTime(Long firstResponseTime) {
        this.firstResponseTime = firstResponseTime;
    }

    public Integer getFirstResponseAgentAccountID() {
        return firstResponseAgentAccountID;
    }

    public void setFirstResponseAgentAccountID(Integer firstResponseAgentAccountID) {
        this.firstResponseAgentAccountID = firstResponseAgentAccountID;
    }

    public String getFirstResponseAgentFullName() {
        return firstResponseAgentFullName;
    }

    public void setFirstResponseAgentFullName(String firstResponseAgentFullName) {
        this.firstResponseAgentFullName = firstResponseAgentFullName;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public Long getClosedTime() {
        return closedTime;
    }

    public void setClosedTime(Long closedTime) {
        this.closedTime = closedTime;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Long deletedTime) {
        this.deletedTime = deletedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.stringID);
        dest.writeString(this.userID);
        dest.writeString(this.userFullName);
        dest.writeValue(this.topicID);
        dest.writeString(this.topicName);
        dest.writeValue(this.agentAccountID);
        dest.writeString(this.agentFullName);
        dest.writeString(this.tapTalkXCRoomID);
        dest.writeString(this.medium);
        dest.writeString(this.firstMessage);
        dest.writeValue(this.firstResponseTime);
        dest.writeValue(this.firstResponseAgentAccountID);
        dest.writeString(this.firstResponseAgentFullName);
        dest.writeValue(this.isClosed);
        dest.writeValue(this.closedTime);
        dest.writeValue(this.createdTime);
        dest.writeValue(this.updatedTime);
        dest.writeValue(this.deletedTime);
    }

    protected TTLCaseModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.stringID = in.readString();
        this.userID = in.readString();
        this.userFullName = in.readString();
        this.topicID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.topicName = in.readString();
        this.agentAccountID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.agentFullName = in.readString();
        this.tapTalkXCRoomID = in.readString();
        this.medium = in.readString();
        this.firstMessage = in.readString();
        this.firstResponseTime = (Long) in.readValue(Long.class.getClassLoader());
        this.firstResponseAgentAccountID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.firstResponseAgentFullName = in.readString();
        this.isClosed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.closedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.createdTime = (Long) in.readValue(Long.class.getClassLoader());
        this.updatedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.deletedTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<TTLCaseModel> CREATOR = new Creator<TTLCaseModel>() {
        @Override
        public TTLCaseModel createFromParcel(Parcel source) {
            return new TTLCaseModel(source);
        }

        @Override
        public TTLCaseModel[] newArray(int size) {
            return new TTLCaseModel[size];
        }
    };
}
