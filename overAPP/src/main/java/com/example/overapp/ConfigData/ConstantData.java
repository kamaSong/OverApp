package com.example.overapp.ConfigData;

import com.example.overapp.R;
//常用固定数据，别的模块使用
public class ConstantData  {

    // 数据存放目录（外置存储卡）
    public static final String DIR_TOTAL = "EnglishLearning";
    // 解压后的数据目录
    public static final String DIR_AFTER_FINISH = "json";

    // 书默认ID
    public static final int KaoYan_WordBook =1;
    public static final int KaoYanAll = 2;

    // 背景图API
    public static final String imgApi = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=7&n=1";
    public static final String frontImg_Api = "https://www.bing.com";

    // 每日一句API
    public static final String everyDaySentence = "https://open.iciba.com/dsapi/";

    // 有道英音发音
    public static final String YOU_DAO_VOICE_UK= "https://dict.youdao.com/dictvoice?type=1&audio=";

    // 有道美音发音
    public static final String YOU_DAO_VOICE_US = "https://dict.youdao.com/dictvoice?type=0&audio=";


    // 通知渠道ID
    public static final String channelId = "default";

    // 通知渠道名称
    public static final String channelName = "默认通知";


    // 提示句子集合
    public static final String[] phrases = {
            "马行软地易失蹄，人贪安逸易失志",
            "每天告诉自己一次：我真的很不错",
            "没有热忱，世间便无进步",
            "有志者，事竟成，破釜沉舟，百二秦关终属楚",
            "有心人，天不负，卧薪尝胆，三千越甲可吞吴",
            "风尘三尺剑，社稷一戎衣",
            "只要站起来的次数比倒下去的次数多，那就是成功",
            "收拾一下心情，开始下一个新的开始",
            "你配不上自己的野心，也辜负了曾经历的苦难",
            "现实很近又很冷，梦想很远却很温暖",
            "前方无绝路，希望在转角",
            "没有人会让我输，除非我不想赢",
            "追踪着鹿的猎人是看不见山的",
            "有志始知蓬莱近，无为总觉咫尺远",
            "业精于勤而荒于嬉，行成于思而毁于随",
            "没有所谓失败，除非你不再尝试"};

    // 根据书ID获取该书的单词总量
    public static int wordTotalNumber(int bookId) {
        int num = 0;
        switch (bookId) {

            case KaoYan_WordBook:
                num = 1341;
                break;
            case KaoYanAll:
                num = 4533;
        }
        return num;
    }

    // 根据书ID获取该书的书名
    public static String bookName(int bookId) {
        String name = "";
        switch (bookId) {

            case KaoYan_WordBook:
                name = "考研必考词汇";
                break;
            case KaoYanAll:
                name = "考研英语大纲";
                break;
        }
        return name;
    }


    // 根据书ID获取该书的图片
    public static String photoBook(int bookId) {
        String picAddress = "";
        switch (bookId) {
            case KaoYan_WordBook:
                picAddress = "https://nos.netease.com/ydschool-online/1496632762670KaoYanluan_1.jpg";
                break;
            case KaoYanAll:
                picAddress = "https://nos.netease.com/ydschool-online/youdao_KaoYan_2.jpg";
                break;
        }
        return picAddress;
    }
// 根据书ID获取该书的类型
    public static String Booktype(int bookId) {
        String name = "";
        switch (bookId) {

            case KaoYan_WordBook:
            case KaoYanAll:
                name = "考研";
                break;
        }
        return name;
    }

    // 根据书ID获取该书的下载地址
    public static String bookDownLoadAddress(int bookId) {
        String picAddress = "";
        switch (bookId) {
            case KaoYan_WordBook:
                picAddress = "http://ydschool-online.nos.netease.com/1521164661106_KaoYanluan_1.zip";
                break;
            case KaoYanAll:
                picAddress = "http://ydschool-online.nos.netease.com/1521164654696_KaoYan_2.zip";
                break;
        }
        return picAddress;
    }

    // 根据书ID获取该书的下载后的文件名
    public static String bookFileName(int bookId) {
        String picAddress = "";
        switch (bookId) {
            case KaoYan_WordBook:
                picAddress = "KaoYanluan_1.zip";
                break;
            case KaoYanAll:
                picAddress = "KaoYan_2.zip";
                break;
        }
        return picAddress;
    }

}

