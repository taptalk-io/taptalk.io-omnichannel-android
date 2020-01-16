package io.taptalk.taptalklive.API.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TTLUserModel implements Parcelable {
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("source")
    private String source;
    @JsonProperty("customerUserID")
    private String customerUserID;
    @JsonProperty("whatsappUserID")
    private String whatsappUserID;
    @JsonProperty("telegramUserID")
    private String telegramUserID;
    @JsonProperty("lineUserID")
    private String lineUserID;
    @JsonProperty("twitterUserID")
    private String twitterUserID;
    @JsonProperty("facebookPSID")
    private String facebookPSID;
    @JsonProperty("email")
    private String email;
    @JsonProperty("isEmailVerified")
    private Boolean isEmailVerified;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("isPhoneVerified")
    private Boolean isPhoneVerified;
    @JsonProperty("photoURL")
    private String photoURL;
    @JsonProperty("mergedToUserID")
    private String mergedToUserID;
    @JsonProperty("mergedTime")
    private Long mergedTime;
    @JsonProperty("createdTime")
    private Long createdTime;
    @JsonProperty("updatedTime")
    private Long updatedTime;
    @JsonProperty("deletedTime")
    private Long deletedTime;

    public TTLUserModel() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCustomerUserID() {
        return customerUserID;
    }

    public void setCustomerUserID(String customerUserID) {
        this.customerUserID = customerUserID;
    }

    public String getWhatsappUserID() {
        return whatsappUserID;
    }

    public void setWhatsappUserID(String whatsappUserID) {
        this.whatsappUserID = whatsappUserID;
    }

    public String getTelegramUserID() {
        return telegramUserID;
    }

    public void setTelegramUserID(String telegramUserID) {
        this.telegramUserID = telegramUserID;
    }

    public String getLineUserID() {
        return lineUserID;
    }

    public void setLineUserID(String lineUserID) {
        this.lineUserID = lineUserID;
    }

    public String getTwitterUserID() {
        return twitterUserID;
    }

    public void setTwitterUserID(String twitterUserID) {
        this.twitterUserID = twitterUserID;
    }

    public String getFacebookPSID() {
        return facebookPSID;
    }

    public void setFacebookPSID(String facebookPSID) {
        this.facebookPSID = facebookPSID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getMergedToUserID() {
        return mergedToUserID;
    }

    public void setMergedToUserID(String mergedToUserID) {
        this.mergedToUserID = mergedToUserID;
    }

    public Long getMergedTime() {
        return mergedTime;
    }

    public void setMergedTime(Long mergedTime) {
        this.mergedTime = mergedTime;
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
        dest.writeString(this.userID);
        dest.writeString(this.fullName);
        dest.writeString(this.source);
        dest.writeString(this.customerUserID);
        dest.writeString(this.whatsappUserID);
        dest.writeString(this.telegramUserID);
        dest.writeString(this.lineUserID);
        dest.writeString(this.twitterUserID);
        dest.writeString(this.facebookPSID);
        dest.writeString(this.email);
        dest.writeValue(this.isEmailVerified);
        dest.writeString(this.phone);
        dest.writeValue(this.isPhoneVerified);
        dest.writeString(this.photoURL);
        dest.writeString(this.mergedToUserID);
        dest.writeValue(this.mergedTime);
        dest.writeValue(this.createdTime);
        dest.writeValue(this.updatedTime);
        dest.writeValue(this.deletedTime);
    }

    protected TTLUserModel(Parcel in) {
        this.userID = in.readString();
        this.fullName = in.readString();
        this.source = in.readString();
        this.customerUserID = in.readString();
        this.whatsappUserID = in.readString();
        this.telegramUserID = in.readString();
        this.lineUserID = in.readString();
        this.twitterUserID = in.readString();
        this.facebookPSID = in.readString();
        this.email = in.readString();
        this.isEmailVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.phone = in.readString();
        this.isPhoneVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.photoURL = in.readString();
        this.mergedToUserID = in.readString();
        this.mergedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.createdTime = (Long) in.readValue(Long.class.getClassLoader());
        this.updatedTime = (Long) in.readValue(Long.class.getClassLoader());
        this.deletedTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<TTLUserModel> CREATOR = new Parcelable.Creator<TTLUserModel>() {
        @Override
        public TTLUserModel createFromParcel(Parcel source) {
            return new TTLUserModel(source);
        }

        @Override
        public TTLUserModel[] newArray(int size) {
            return new TTLUserModel[size];
        }
    };
}
