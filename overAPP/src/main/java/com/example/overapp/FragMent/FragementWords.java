package com.example.overapp.FragMent;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.overapp.WordDetailActivity;
import com.example.overapp.BaseActivity;
import com.example.overapp.ConfigData.ConfigData;
import com.example.overapp.ConfigData.ConstantData;
import com.example.overapp.LoadWordActivity;
import com.example.overapp.MainActivity;
import com.example.overapp.R;
import com.example.overapp.SearchActivity;
import com.example.overapp.Utils.NumberController;
import com.example.overapp.WordFolderActivity;
import com.example.overapp.database.Interpretation;
import com.example.overapp.database.MyDate;
import com.example.overapp.database.UserConfig;
import com.example.overapp.database.Word;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;


public class FragementWords extends Fragment implements View.OnClickListener {


    private CardView cardStart, cardSearch;
    private ImageView imgRefresh;
    private TextView textStart;
    private RelativeLayout layoutFiles;

    private TextView textWord, textMean, textWordNum, textBook;

    private TextView textDate, textMonth;

    private static final String TAG = "FragmentWord";

    private int currentBookId;

    private boolean isOnClick = true;

    private int currentRandomId;

    public static int prepareData = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragement_words, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//       初始化并绑定点
        imgRefresh = getActivity().findViewById(R.id.img_refresh);
        imgRefresh.setOnClickListener(this);
        cardStart = getActivity().findViewById(R.id.card_start);
        cardStart.setOnClickListener(this);
        textMean = getActivity().findViewById(R.id.text_main_show_word_mean);
        textMean.setOnClickListener(this);
        textWord = getActivity().findViewById(R.id.text_main_show_word);
        textWordNum = getActivity().findViewById(R.id.text_main_show_word_num);
        textBook = getActivity().findViewById(R.id.TOPbookname);
        textStart = getActivity().findViewById(R.id.text_main_start);
        textStart.setOnClickListener(this);
        layoutFiles = getActivity().findViewById(R.id.layout_main_words);
        layoutFiles.setOnClickListener(this);
        cardSearch = getActivity().findViewById(R.id.card_main_search);
        cardSearch.setOnClickListener(this);


        Log.d(TAG, "onActivityCreated: ");

        if (MainActivity.needRefresh) {
            prepareData = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BaseActivity.prepareDailyData();
                }
            }).start();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_refresh:
                // 旋转动画
                RotateAnimation animation = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(700);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setRandomWord();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imgRefresh.startAnimation(animation);
                break;
            case R.id.text_main_start:
                if (isOnClick) {
                    Intent mIntent = new Intent(getActivity(), LoadWordActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    isOnClick = false;
                }
                break;
            case R.id.text_main_show_word_mean:
                WordDetailActivity.wordId = currentRandomId;
                Intent intent = new Intent(getActivity(), WordDetailActivity.class);
                intent.putExtra(WordDetailActivity.TYPE_NAME, WordDetailActivity.TYPE_GENERAL);
                startActivity(intent);
                break;
            case R.id.card_main_search:
                Intent intent2 = new Intent(getActivity(), SearchActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;
            case R.id.layout_main_words:
                Intent intent3 = new Intent(getActivity(), WordFolderActivity.class);
                startActivity(intent3, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void setRandomWord() {
        ++prepareData;
        Log.d(TAG, "setRandomWord: " + ConstantData.wordTotalNumber(currentBookId));
        int randomId = NumberController.getRandomNumber(1, ConstantData.wordTotalNumber(currentBookId));
        Log.d(TAG, "当前ID" + randomId);
        currentRandomId = randomId;
        Log.d(TAG, "要传入的ID" + currentRandomId);
        Log.d(TAG, randomId + "");
        Word word = LitePal.where("wordId = ?", randomId + "").select("wordId", "word").find(Word.class).get(0);
        Log.d(TAG, word.getWord());
        List<Interpretation> interpretations = LitePal.where("wordId = ?", word.getWordId() + "").find(Interpretation.class);
        textWord.setText(word.getWord());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < interpretations.size(); ++i) {
            stringBuilder.append(interpretations.get(i).getWordType() + ". " + interpretations.get(i).getCHSMeaning());
            if (i != interpretations.size() - 1)
                stringBuilder.append("\n");
        }
        textMean.setText(stringBuilder.toString());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Calendar calendar = Calendar.getInstance();
        List<Word> words = LitePal.where("deepMasterTimes <> ?", 3 + "").select("wordId").find(Word.class);
        List<MyDate> myDates = LitePal.where("year = ? and month = ? and date = ? and userId = ?",
                calendar.get(Calendar.YEAR) + "",
                (calendar.get(Calendar.MONTH) + 1) + "",
                calendar.get(Calendar.DATE) + "",
                ConfigData.getNumLogged() + "").find(MyDate.class);
        if (!words.isEmpty()) {
            if (myDates.isEmpty()) {
                // 未完成计划
                cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorLightBlue));
                textStart.setTextColor(getActivity().getColor(R.color.colorFontInBlue));
                textStart.setText("开始背单词");
                isOnClick = true;
            } else {
                // 完成计划
                if ((myDates.get(0).getWordLearnNumber() + myDates.get(0).getWordReviewNumber()) > 0) {
                    cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorBgWhite));
                    textStart.setTextColor(getActivity().getColor(R.color.colorFontInWhite));
                    textStart.setText("已完成今日任务");
                    cardStart.setClickable(false);
                    isOnClick = false;
                } else {
                    // 未完成计划
                    cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorLightBlue));
                    textStart.setTextColor(getActivity().getColor(R.color.colorFontInBlue));
                    textStart.setText("开始背单词");
                    isOnClick = true;
                }
            }
        } else {
            cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorBgWhite));
            textStart.setTextColor(getActivity().getColor(R.color.colorFontInWhite));
            textStart.setText("恭喜！已背完此书");
            cardStart.setClickable(false);
            isOnClick = false;
        }
        // 设置界面数据
        List<UserConfig> userConfigs = LitePal.where("userId = ?", ConfigData.getNumLogged() + "").find(UserConfig.class);
        currentBookId = userConfigs.get(0).getCurrentBookId();
        textWordNum.setText("每日须学" + userConfigs.get(0).getWordNeedReciteNum() + "个单词");
        textBook.setText(ConstantData.bookName(currentBookId));
        if (prepareData == 0)
            // 设置随机数据
            setRandomWord();
    }
}
