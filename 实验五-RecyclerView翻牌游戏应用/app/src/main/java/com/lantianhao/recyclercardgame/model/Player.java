package com.lantianhao.recyclercardgame.model;

import java.io.Serializable;

/**
 * 玩家信息（Model）。姓名、性别、学号、电话、邮件地址、爱好与头像由
 * 个人信息界面修改，最高分由翻牌游戏界面更新。
 */
public class Player implements Serializable {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_GENDER = "extra_gender";
    public static final String EXTRA_STUDENT_ID = "extra_student_id";
    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_HOBBIES = "extra_hobbies";
    public static final String EXTRA_AVATAR_URI = "extra_avatar_uri";

    private String name;
    private String gender;
    private String studentId;
    private String phone;
    private String email;
    private String hobbies;
    private String avatarUri;
    private int highScore;

    public Player() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}
