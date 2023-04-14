package io.taptalk.taptalklive.model;

import androidx.annotation.Nullable;

import java.util.LinkedHashMap;

import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.taptalklive.API.Model.TTLCaseModel;
import io.taptalk.taptalklive.TapTalkLive;

public class TTLCaseListModel {
    @Nullable private TTLCaseModel caseModel;
    private TAPMessageModel lastMessage;
    private String lastMessageTimestamp;
    private LinkedHashMap<String, TAPUserModel> typingUsers;
    private int unreadCount = 0;
    private int unreadMentions = 0;
    private int defaultAvatarBackgroundColor; // Save default color in model to prevent lag on bind

    public TTLCaseListModel(TAPMessageModel lastMessage) {
        this.lastMessage = lastMessage;
        this.caseModel = TapTalkLive.getCaseMap().get(lastMessage.getRoom().getXcRoomID());
        setRoomData();
        updateUnreadCountFromCase();
    }

    @Nullable
    public TTLCaseModel getCaseModel() {
        return caseModel;
    }

    public void setCaseModel(@Nullable TTLCaseModel caseModel) {
        this.caseModel = caseModel;
    }

    public void setTypingUsers(LinkedHashMap<String, TAPUserModel> typingUsers) {
        this.typingUsers = typingUsers;
    }

    public TAPMessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TAPMessageModel lastMessage) {
        this.lastMessage = lastMessage;
        setRoomData();
    }

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long timestamp) {
        setLastMessageTimestamp(TAPTimeFormatter.durationString(lastMessage.getCreated()));
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getUnreadMentions() {
        return unreadMentions;
    }

    public void setUnreadMentions(int unreadMentions) {
        this.unreadMentions = unreadMentions;
    }

    public int getDefaultAvatarBackgroundColor() {
        return defaultAvatarBackgroundColor;
    }

    public void setDefaultAvatarBackgroundColor(int defaultAvatarBackgroundColor) {
        this.defaultAvatarBackgroundColor = defaultAvatarBackgroundColor;
    }

    public LinkedHashMap<String, TAPUserModel> getTypingUsers() {
        return null == typingUsers ? typingUsers = new LinkedHashMap<>() : typingUsers;
    }

    public void removeTypingUser(String userID) {
        getTypingUsers().remove(userID);
    }

    private void setRoomData() {
        if (null == lastMessage) {
            return;
        }
        if (null != lastMessage.getCreated()) {
            this.lastMessageTimestamp = TAPTimeFormatter.durationString(lastMessage.getCreated());
        }
        TAPRoomModel room = lastMessage.getRoom();
        if (null != room && (null == room.getImageURL() || room.getImageURL().getThumbnail().isEmpty())) {
            defaultAvatarBackgroundColor = TAPUtils.getRandomColor(TapTalkLive.context, room.getRoomID());
        }
    }

    private void updateUnreadCountFromCase() {
        if (null != caseModel && null != caseModel.getTapTalkRoom()) {
            unreadCount = caseModel.getTapTalkRoom().getUnreadCount();
        }
    }
}
