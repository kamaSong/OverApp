package com.example.overapp.InterFace;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.overapp.Item.ItemWordMeanChoice;

public interface OnItemClickListener {

    void onItemClick(RecyclerView parent, View view, int position, ItemWordMeanChoice itemWordMeanChoice);
}
