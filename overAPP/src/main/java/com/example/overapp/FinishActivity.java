package com.example.overapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.overapp.ConfigData.ConfigData;
import com.example.overapp.Utils.ActivityCollector;
import com.example.overapp.Utils.TimeController;
import com.example.overapp.Utils.WordController;
import com.example.overapp.database.MyDate;
import com.example.overapp.database.User;
import com.example.overapp.database.UserConfig;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;

public class FinishActivity extends BaseActivity {

    private TextView textWord, textDay;


    private Button btnBack;

    private int wordNum;

    private List<UserConfig> userConfigs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
//初始化
        textWord = findViewById(R.id.text__word_num);
        textDay = findViewById(R.id.text__days);
        btnBack = findViewById(R.id.btn__back);

        windowExplode();

        userConfigs = LitePal.where("userId = ?", ConfigData.getNumLogged() + "").find(UserConfig.class);

        wordNum = userConfigs.get(0).getWordNeedReciteNum() + WordController.wordReviewNum;
        textWord.setText(wordNum + "");
        List<MyDate> myDateList = LitePal.findAll(MyDate.class);
        textDay.setText((myDateList.size() + 1) + "");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }



    @Override
    public void onBackPressed() {
        saveData();
        ActivityCollector.startOtherActivity(FinishActivity.this, MainActivity.class);
    }

    private void saveData() {
        Calendar calendar = Calendar.getInstance();
        List<MyDate> myDates = LitePal.where("year = ? and month = ? and date = ? and userId = ?",
                calendar.get(Calendar.YEAR) + "",
                (calendar.get(Calendar.MONTH) + 1) + "",
                calendar.get(Calendar.DATE) + "",
                ConfigData.getNumLogged() + "").find(MyDate.class);
        if (myDates.isEmpty()) {
            dataControl();
        } else {
            int result = LitePal.deleteAll("year = ? and month = ? and date = ? and userId = ?",
                    calendar.get(Calendar.YEAR) + "",
                    (calendar.get(Calendar.MONTH) + 1) + "",
                    calendar.get(Calendar.DATE) + "",
                    ConfigData.getNumLogged() + "");
            if (result != 0) {
                dataControl();
            }
        }
    }

    private void dataControl() {
        String[] s = TimeController.getStringDate(TimeController.todayDate).split("-");
        MyDate myDate = new MyDate();
        myDate.setWordLearnNumber(userConfigs.get(0).getWordNeedReciteNum());
        myDate.setWordReviewNumber(WordController.wordReviewNum);
        myDate.setYear(Integer.valueOf(s[0]));
        myDate.setMonth(Integer.valueOf(s[1]));
        myDate.setDate(Integer.valueOf(s[2]));
        myDate.setUserId(ConfigData.getNumLogged());
        myDate.save();
        // 增加10金币
        List<User> users = LitePal.where("userId = ?", ConfigData.getNumLogged() + "").find(User.class);
        User user = new User();
        user.setUserMoney(users.get(0).getUserMoney() + 10);
        user.setUserWordNumber(users.get(0).getUserWordNumber() + userConfigs.get(0).getWordNeedReciteNum());
        user.updateAll("userId = ?", ConfigData.getNumLogged() + "");
    }
}