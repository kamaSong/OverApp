package com.example.overapp.JSON;
// JsonAccess类用于封装JSON中的access_token字段
public class JsonAccess {

        // 声明一个私有字段access_token，用于存储访问令牌
        private String access_token;

        // 提供一个公共的getter方法，用于获取access_token字段的值
        public String getAccess_token() {
            return access_token;
        }

        // 提供一个公共的setter方法，用于设置access_token字段的值
        public void setAccess_token(String access_token) {
            this.access_token = access_token; // 使用this关键字来区分成员变量和参数
        }
    }

