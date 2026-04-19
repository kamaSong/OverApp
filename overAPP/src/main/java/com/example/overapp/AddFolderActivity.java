package com.example.overapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.overapp.Utils.TimeController;
import com.example.overapp.database.WordFolder;

public class AddFolderActivity extends AppCompatActivity {


    private EditText editName, editRemark;

    private RelativeLayout layoutAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_folder);

//        初始化
        editName = findViewById(R.id.text_foldwordname);
        editRemark = findViewById(R.id.text_foldwordname_remark);
        layoutAdd = findViewById(R.id.layout_foldwordname_add);

        layoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editName.getText().toString().trim())) {
                    WordFolder wordFolder = new WordFolder();
                    wordFolder.setCreateTime(TimeController.getCurrentTimeStamp() + "");
                    wordFolder.setName(editName.getText().toString().trim());
                    if (!TextUtils.isEmpty(editRemark.getText().toString().trim()))
                        wordFolder.setRemark(editRemark.getText().toString().trim());
                    wordFolder.save();
                    onBackPressed();
                    Toast.makeText(AddFolderActivity.this, "新建成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddFolderActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}