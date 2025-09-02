package io.taptalk.taptalklive.API.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLCaseModel implements Parcelable {
    @JsonProperty("id") private Long id;
    @JsonProperty("stringID") private String stringID;
    @JsonProperty("userID") private String userID;
    @JsonProperty("userFullName") private String userFullName;
    @JsonProperty("userAlias") private String userAlias;
    @JsonProperty("userEmail") private String userEmail;
    @JsonProperty("userPhone") private String userPhone;
    @JsonProperty("topicID") private Integer topicID;
    @JsonProperty("topicName") private String topicName;
    @JsonProperty("agentAccountID") private Integer agentAccountID;
    @JsonProperty("agentFullName") private String agentFullName;
    @JsonProperty("tapTalkXCRoomID") private String tapTalkXCRoomID;
    @JsonProperty("medium") private String medium;
    @JsonProperty("mediumChannelID") private Integer mediumChannelID;
    @JsonProperty("firstMessage") private String firstMessage;
    @JsonProperty("firstResponseTime") private Long firstResponseTime;
    @JsonProperty("firstResponseAgentAccountID") private Long firstResponseAgentAccountID;
    @JsonProperty("firstResponseAgentFullName") private String firstResponseAgentFullName;
    @JsonProperty("agentRemark") private String agentRemark;
    @JsonProperty("isCreatedByAgent") private Boolean isCreatedByAgent;
    @JsonProperty("creatorAgentAccountID") private Long creatorAgentAccountID;
    @JsonProperty("firstUserReplyTime") private Long firstUserReplyTime;
    @JsonProperty("isJunk") private Boolean isJunk;
    //@JsonProperty("labelIDs") private ArrayList<Long> labelIDs;
    @JsonProperty("isClosed") private Boolean isClosed;
    @JsonProperty("closedTime") private Long closedTime;
    @JsonProperty("createdTime") private Long createdTime;
    @JsonProperty("updatedTime") private Long updatedTime;
    @JsonProperty("deletedTime") private Long deletedTime;
    @JsonProperty("tapTalkRoom") private TTLTapTalkRoom tapTalkRoom;

    public TTLCaseModel() {
    }

    public TTLCaseModel(HashMap<String, Object> caseDataMap) {
        try {
            this.id = (Long) caseDataMap.get("id");
            this.stringID = (String) caseDataMap.get("stringID");
            this.userID = (String) caseDataMap.get("userID");
            this.userFullName = (String) caseDataMap.get("userFullName");
            this.userAlias = (String) caseDataMap.get("userAlias");
            this.userEmail = (String) caseDataMap.get("userEmail");
            this.userPhone = (String) caseDataMap.get("userPhone");
            this.topicID = (Integer) caseDataMap.get("topicID");
            this.topicName = (String) caseDataMap.get("topicName");
            this.agentAccountID = (Integer) caseDataMap.get("agentAccountID");
            this.agentFullName = (String) caseDataMap.get("agentFullName");
            this.tapTalkXCRoomID = (String) caseDataMap.get("tapTalkXCRoomID");
            this.medium = (String) caseDataMap.get("medium");
            this.mediumChannelID = (Integer) caseDataMap.get("mediumChannelID");
            this.firstMessage = (String) caseDataMap.get("firstMessage");
            this.creatorAgentAccountID = (Long) caseDataMap.get("creatorAgentAccountID");
            this.firstResponseTime = (Long) caseDataMap.get("firstResponseTime");
            this.firstUserReplyTime = (Long) caseDataMap.get("firstUserReplyTime");
            this.firstResponseAgentAccountID = (Long) caseDataMap.get("firstResponseAgentAccountID");
            this.firstResponseAgentFullName = (String) caseDataMap.get("firstResponseAgentFullName");
            this.agentRemark = (String) caseDataMap.get("agentRemark");
            this.isCreatedByAgent = (Boolean) caseDataMap.get("isCreatedByAgent");
            //this.labelIDs = (ArrayList<Long>) caseDataMap.get("labelIDs");
            this.isJunk = (Boolean) caseDataMap.get("isJunk");
            this.isClosed = (Boolean) caseDataMap.get("isClosed");
            this.closedTime = (Long) caseDataMap.get("closedTime");
            this.createdTime = (Long) caseDataMap.get("createdTime");
            this.updatedTime = (Long) caseDataMap.get("updatedTime");
            this.deletedTime = (Long) caseDataMap.get("deletedTime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
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

    public Integer getMediumChannelID() {
        return mediumChannelID;
    }

    public void setMediumChannelID(Integer mediumChannelID) {
        this.mediumChannelID = mediumChannelID;
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

    public Long getFirstResponseAgentAccountID() {
        return firstResponseAgentAccountID;
    }

    public void setFirstResponseAgentAccountID(Long firstResponseAgentAccountID) {
        this.firstResponseAgentAccountID = firstResponseAgentAccountID;
    }

    public String getFirstResponseAgentFullName() {
        return firstResponseAgentFullName;
    }

    public void setFirstResponseAgentFullName(String firstResponseAgentFullName) {
        this.firstResponseAgentFullName = firstResponseAgentFullName;
    }

    public String getAgentRemark() {
        return agentRemark;
    }

    public void setAgentRemark(String agentRemark) {
        this.agentRemark = agentRemark;
    }

    public Boolean getCreatedByAgent() {
        return isCreatedByAgent;
    }

    public void setCreatedByAgent(Boolean createdByAgent) {
        isCreatedByAgent = createdByAgent;
    }

    public Long getCreatorAgentAccountID() {
        return creatorAgentAccountID;
    }

    public void setCreatorAgentAccountID(Long creatorAgentAccountID) {
        this.creatorAgentAccountID = creatorAgentAccountID;
    }

    public Long getFirstUserReplyTime() {
        return firstUserReplyTime;
    }

    public void setFirstUserReplyTime(Long firstUserReplyTime) {
        this.firstUserReplyTime = firstUserReplyTime;
    }

    public Boolean getJunk() {
        return isJunk;
    }

    public void setJunk(Boolean junk) {
        isJunk = junk;
    }

//    public ArrayList<Long> getLabelIDs() {
//        return labelIDs;
//    }
//
//    public void setLabelIDs(ArrayList<Long> labelIDs) {
//        this.labelIDs = labelIDs;
//    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean closed) {
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

    public TTLTapTalkRoom getTapTalkRoom() {
        return tapTalkRoom;
    }

    public void setTapTalkRoom(TTLTapTalkRoom tapTalkRoom) {
        this.tapTalkRoom = tapTalkRoom;
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
        dest.writeString(this.userAlias);
        dest.writeString(this.userEmail);
        dest.writeString(this.userPhone);
        dest.writeValue(this.topicID);
        dest.writeString(this.topicName);
        dest.writeValue(this.agentAccountID);
        dest.writeString(this.agentFullName);
        dest.writeString(this.tapTalkXCRoomID);
        dest.writeString(this.medium);
        dest.writeValue(this.mediumChannelID);
        dest.writeString(this.firstMessage);
        dest.writeValue(this.firstResponseTime);
        dest.writeValue(this.firstResponseAgentAccountID);
        dest.writeString(this.firstResponseAgentFullName);
        dest.writeString(this.agentRemark);
        dest.writeValue(this.isCreatedByAgent);
        dest.writeValue(this.creatorAgentAccountID);
        dest.writeValue(this.firstUserReplyTime);
        dest.writeValue(this.isJunk);
        //dest.writeList(this.labelIDs);
        dest.writeValue(this.isClosed);
        dest.writeValue(this.closedTime);
        dest.writeValue(this.createdTime);
        dest.writeValue(this.updatedTime);
        dest.writeValue(this.deletedTime);
        dest.writeParcelable(this.tapTalkRoom, flags);
    }

    public void readFromParcel(Parcel source) {
        this.id = (Long) source.readValue(Long.class.getClassLoader());
        this.stringID = source.readString();
        this.userID = source.readString();
        this.userFullName = source.readString();
        this.userAlias = source.readString();
        this.userEmail = source.readString();
        this.userPhone = source.readString();
        this.topicID = (Integer) source.readValue(Integer.class.getClassLoader());
        this.topicName = source.readString();
        this.agentAccountID = (Integer) source.readValue(Integer.class.getClassLoader());
        this.agentFullName = source.readString();
        this.tapTalkXCRoomID = source.readString();
        this.medium = source.readString();
        this.mediumChannelID = (Integer) source.readValue(Integer.class.getClassLoader());
        this.firstMessage = source.readString();
        this.firstResponseTime = (Long) source.readValue(Long.class.getClassLoader());
        this.firstResponseAgentAccountID = (Long) source.readValue(Long.class.getClassLoader());
        this.firstResponseAgentFullName = source.readString();
        this.agentRemark = source.readString();
        this.isCreatedByAgent = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.creatorAgentAccountID = (Long) source.readValue(Long.class.getClassLoader());
        this.firstUserReplyTime = (Long) source.readValue(Long.class.getClassLoader());
        this.isJunk = (Boolean) source.readValue(Boolean.class.getClassLoader());
        //this.labelIDs = new ArrayList<Long>();
        //source.readList(this.labelIDs, Long.class.getClassLoader());
        this.isClosed = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.closedTime = (Long) source.readValue(Long.class.getClassLoader());
        this.createdTime = (Long) source.readValue(Long.class.getClassLoader());
        this.updatedTime = (Long) source.readValue(Long.class.getClassLoader());
        this.deletedTime = (Long) source.readValue(Long.class.getClassLoader());
        this.tapTalkRoom = source.readParcelable(TTLTapTalkRoom.class.getClassLoader());
    }

    protected TTLCaseModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.stringID = in.readString();
        this.userID = in.readString();
        this.userFullName = in.readString();
        this.userAlias = in.readString();
        this.userEmail = in.readString();
        this.userPhone = in.readString();
        this.topicID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.topicName = in.readString();
        this.agentAccountID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.agentFullName = in.readString();
        this.tapTalkXCRoomID = in.readString();
        this.medium = in.readString();
        this.mediumChannelID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.firstMessage = in.readString();
        this.firstResponseTime = (Long) in.readValue(Long.class.getClassLoader());
        this.firstResponseAgentAccountID = (Long) in.readValue(Long.class.getClassLoader());
        this.firstResponseAgentFullName = in.readString();
        this.agentRemark = in.readString();
        this.isCreatedByAgent = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.creatorAgentAccountID = (Long) in.readValue(Long.class.getClassLoader());
        this.firstUserReplyTime = (Long) in.readValue(Long.class.getClassLoader());
        this.isJunk = (Boolean) in.readValue(Boolean.class.getClassLoader());
        //this.labelIDs = new ArrayList<Long>();
        //in.readList(this.labelIDs, Long.class.getClassLoader());
        this.isClosed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.closedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.createdTime = (Long) in.readValue(Long.class.getClassLoader());
        this.updatedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.deletedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.tapTalkRoom = in.readParcelable(TTLTapTalkRoom.class.getClassLoader());
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
