package com.example.overapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;

import com.example.overapp.Adapter.MatchAdapter;
import com.example.overapp.Item.ItemMatch;
import com.example.overapp.database.Word;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends BaseActivity  {

    public static List<Word> wordList = new ArrayList<>();

    public static ArrayList<ItemMatch> matchList = new ArrayList<>();

    public static ArrayList<ItemMatch> allMatches = new ArrayList<>();

    private RecyclerView recyclerView;

    private static final String TAG = "MatchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        recyclerView = findViewById(R.id.recycler_mt);

        windowExplode();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        MatchAdapter matchAdapter = new MatchAdapter(matchList);
        recyclerView.setAdapter(matchAdapter);


    }



}