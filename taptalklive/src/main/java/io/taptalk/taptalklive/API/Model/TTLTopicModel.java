package io.taptalk.taptalklive.API.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TTLTopicModel implements Parcelable {
    @JsonProperty("id") private Integer id;
    @JsonProperty("name") private String name;
    @JsonProperty("createdTime") private Long createdTime;
    @JsonProperty("updatedTime") private Long updatedTime;
    @JsonProperty("deletedTime") private Long deletedTime;

    public TTLTopicModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeString(this.name);
        dest.writeValue(this.createdTime);
        dest.writeValue(this.updatedTime);
        dest.writeValue(this.deletedTime);
    }

    protected TTLTopicModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.createdTime = (Long) in.readValue(Long.class.getClassLoader());
        this.updatedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.deletedTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<TTLTopicModel> CREATOR = new Parcelable.Creator<TTLTopicModel>() {
        @Override
        public TTLTopicModel createFromParcel(Parcel source) {
            return new TTLTopicModel(source);
        }

        @Override
        public TTLTopicModel[] newArray(int size) {
            return new TTLTopicModel[size];
        }
    };
}
