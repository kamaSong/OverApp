package com.example.overapp.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    // 唯一
    @Column(unique = true, defaultValue = "000000")
    private int userId;



    private String userName;

    // 词汇量
    @Column(defaultValue = "0")
    private int userWordNumber;

    // 金币数
    @Column(defaultValue = "0")
    private int userMoney;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserWordNumber() {
        return userWordNumber;
    }

    public void setUserWordNumber(int userWordNumber) {
        this.userWordNumber = userWordNumber;
    }

    public int getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(int userMoney) {
        this.userMoney = userMoney;
    }

}
