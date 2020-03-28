package com.example.instagramclone.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String email;
    private String phone_number;
    private String user_id;
    private String username;

    public User(String email, String phone_number, String user_id, String username) {
        this.email = email;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.username = username;
    }

    protected User(Parcel in) {
        email = in.readString();
        phone_number = in.readString();
        user_id = in.readString();
        username = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(phone_number);
        dest.writeString(user_id);
        dest.writeString(username);
    }
}
