package com.example.myinstaclone;

public class UserModel {
    private String id;
    private String first;
    private String last;
    private String avatarName;
    /*
        public UserModel(String first, String last, String avatarName){
            this._first = first;
            this._last = last;
            this._avatarName = avatarName;
        }
     */
    public UserModel(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }


}
