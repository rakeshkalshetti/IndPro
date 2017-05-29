package com.indpro.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rakesh on 27/05/17.
 */

public class ImageDetails implements Parcelable {


    public static final Parcelable.Creator<ImageDetails> CREATOR = new Parcelable.Creator<ImageDetails>() {

        @Override
        public ImageDetails createFromParcel(Parcel source) {
            return new ImageDetails(source);
        }

        @Override
        public ImageDetails[] newArray(int size) {
            return new ImageDetails[size];
        }
    };


    private int id;
    private int like;
    private int disLike;
    private int click;
    private String updatedAt;
    private String createdAt;
    private String imageUrl;

    public ImageDetails() {

    }
    private ImageDetails(Parcel in) {
        this.imageUrl = in.readString();
        this.id = in.readInt();
        this.like = in.readInt();
        this.disLike = in.readInt();
        this.click = in.readInt();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDisLike() {
        return disLike;
    }

    public void setDisLike(int disLike) {
        this.disLike = disLike;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeInt(id);
        dest.writeInt(like);
        dest.writeInt(disLike);
        dest.writeInt(click);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
}
