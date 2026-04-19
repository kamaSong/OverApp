package com.example.overapp.database;


import org.litepal.crud.LitePalSupport;

public class Sentence extends LitePalSupport {


    // 英文句子
    private String enSentence;

    // 中文句子
    private String chSentence;

    // 归属单词
    private int wordId;

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getEnSentence() {
        return enSentence;
    }

    public void setEnSentence(String enSentence) {
        this.enSentence = enSentence;
    }

    public String getChSentence() {
        return chSentence;
    }

    public void setChSentence(String chSentence) {
        this.chSentence = chSentence;
    }
}
