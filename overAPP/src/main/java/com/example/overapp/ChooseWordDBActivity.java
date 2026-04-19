package com.example.overapp;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.overapp.Adapter.WordBookAdapter;
import com.example.overapp.ConfigData.ConfigData;
import com.example.overapp.ConfigData.ConstantData;
import com.example.overapp.Item.ItemWordBook;
import com.example.overapp.Utils.ActivityCollector;
import com.example.overapp.database.UserConfig;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChooseWordDBActivity extends BaseActivity{


    // 声明一个RecyclerView对象，用于展示列表。
    private RecyclerView recyclerView;

    // 声明并初始化一个ItemWordBook对象的列表，用于存储书籍数据。
    private List<ItemWordBook> itemWordBookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_word_dbactivity); // 设置Activity的布局为activity_choose_word_dbactivity。

        // 通过ID找到布局中的RecyclerView对象。
        recyclerView = findViewById(R.id.recycler_word_book_list);

        // 创建一个LinearLayoutManager对象，用于管理RecyclerView的布局。
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // 将LinearLayoutManager设置给RecyclerView。
        recyclerView.setLayoutManager(linearLayoutManager);

        // 向itemWordBookList列表中添加两个书籍的数据。每个书籍的数据由ItemWordBook对象表示。
        itemWordBookList.add(new ItemWordBook(ConstantData.KaoYan_WordBook, ConstantData.bookName(ConstantData.KaoYan_WordBook), ConstantData.wordTotalNumber(ConstantData.KaoYan_WordBook), "来源：有道考神", ConstantData.photoBook(ConstantData.KaoYan_WordBook)));
        itemWordBookList.add(new ItemWordBook(ConstantData.KaoYanAll, ConstantData.bookName(ConstantData.KaoYanAll), ConstantData.wordTotalNumber(ConstantData.KaoYanAll), "来源：有道考神", ConstantData.photoBook(ConstantData.KaoYanAll)));

        // 创建一个WordBookAdapter对象，并将书籍数据列表传递给它并将数据绑定到RecyclerView的列表项上。
        //设置Adapter给RecyclerView，这样RecyclerView就知道如何展示列表项了。
        WordBookAdapter wordBookAdapter = new WordBookAdapter(itemWordBookList);
        recyclerView.setAdapter(wordBookAdapter);
    }



    @Override
    public void onBackPressed() {
        // 已登录
        if (LitePal.where("userId = ?", ConfigData.getNumLogged() + "").find(UserConfig.class).get(0).getCurrentBookId() != -1)
            super.onBackPressed();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseWordDBActivity.this);
            builder.setTitle("提示")
                    .setMessage("确定要退出吗?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCollector.finishAll();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }
}