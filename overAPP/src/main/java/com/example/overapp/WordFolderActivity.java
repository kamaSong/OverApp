package com.example.overapp;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.overapp.Adapter.WordFolderAdapter;
import com.example.overapp.Item.ItemWordFolder;
import com.example.overapp.database.FolderLinkWord;
import com.example.overapp.database.WordFolder;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class WordFolderActivity extends BaseActivity  {

    private RecyclerView recyclerView;

    private List<ItemWordFolder> wordFolderList = new ArrayList<>();

    private ImageView imgAdd;

    private WordFolderAdapter wordFolderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_folder);

        windowExplode();

        recyclerView = findViewById(R.id.recycler_wf);
        imgAdd = findViewById(R.id.img_fold_add);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        wordFolderAdapter = new WordFolderAdapter(wordFolderList);
        recyclerView.setAdapter(wordFolderAdapter);

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordFolderActivity.this, AddFolderActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        List<WordFolder> wordFolders = LitePal.findAll(WordFolder.class);
        if (!wordFolders.isEmpty()) {
            wordFolderList.clear();
            for (WordFolder w : wordFolders) {
                List<FolderLinkWord> folderLinkWords = LitePal.where("folderId = ?", w.getId() + "").find(FolderLinkWord.class);
                wordFolderList.add(new ItemWordFolder(w.getId(), folderLinkWords.size(), w.getName(), w.getRemark()));
            }
            wordFolderAdapter.notifyDataSetChanged();
        }
    }
}