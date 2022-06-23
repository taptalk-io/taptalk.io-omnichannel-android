package io.taptalk.taptalklive.API.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLTapTalkRoom implements Parcelable {
    @JsonProperty("lastMessage") private HashMap<String, Object> lastMessage;
    @JsonProperty("unreadCount") private Integer unreadCount;

    public TTLTapTalkRoom() {
    }

    public TTLTapTalkRoom(HashMap<String, Object> lastMessage, Integer unreadCount) {
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public HashMap<String, Object> getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(HashMap<String, Object> lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.lastMessage);
        dest.writeValue(this.unreadCount);
    }

    public void readFromParcel(Parcel source) {
        this.lastMessage = (HashMap<String, Object>) source.readSerializable();
        this.unreadCount = (Integer) source.readValue(Integer.class.getClassLoader());
    }

    protected TTLTapTalkRoom(Parcel in) {
        this.lastMessage = (HashMap<String, Object>) in.readSerializable();
        this.unreadCount = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<TTLTapTalkRoom> CREATOR = new Creator<TTLTapTalkRoom>() {
        @Override
        public TTLTapTalkRoom createFromParcel(Parcel source) {
            return new TTLTapTalkRoom(source);
        }

        @Override
        public TTLTapTalkRoom[] newArray(int size) {
            return new TTLTapTalkRoom[size];
        }
    };
}
