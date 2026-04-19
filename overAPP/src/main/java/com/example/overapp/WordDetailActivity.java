package com.example.overapp;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.overapp.Adapter.DetailPhraseAdapter;
import com.example.overapp.Adapter.DetailSentenceAdapter;
import com.example.overapp.Item.ItemPhrase;
import com.example.overapp.Item.ItemSentence;
import com.example.overapp.Utils.MediaHelper;
import com.example.overapp.database.FolderLinkWord;
import com.example.overapp.database.Interpretation;
import com.example.overapp.database.Phrase;
import com.example.overapp.database.Sentence;
import com.example.overapp.database.Word;
import com.example.overapp.database.WordFolder;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class WordDetailActivity extends BaseActivity implements View.OnClickListener  {
    private static final String TAG = "WordDetailActivity";

    // 操作栏
    private RelativeLayout layoutContinue, layoutVoice;
    private RelativeLayout layoutStar,  layoutFolder;
    private ImageView imgStar;
    private TextView textContinue;

    // 单词
    private AutofitTextView textWordName;

    // 单词发音
    private LinearLayout layoutPhoneUk, layoutPhoneUs;
    private TextView textPhoneUk, textPhoneUs;

    // 单词释义
    private TextView textInterpretation;

    // 巧记
    private CardView cardRemMind;
    private TextView textRemMind;

    // 例句
    private CardView cardSentence;
    private RecyclerView recyclerSentence;
    private DetailSentenceAdapter detailSentenceAdapter;
    private List<ItemSentence> itemSentenceList = new ArrayList<>();


    // 词组
    private CardView cardPhrase;
    private RecyclerView recyclerPhrase;
    private DetailPhraseAdapter detailPhraseAdapter;
    private List<ItemPhrase> itemPhraseList = new ArrayList<>();

    // 单词
    List<Word> words;

    // 传入的单词ID
    public static int wordId;
    private Word currentWord;

    public static final String TYPE_NAME = "typeName";
    public static final int TYPE_LEARN = 1;
    public static final int TYPE_GENERAL = 2;

    private int currentType;


    private ProgressDialog progressDialog;



    private final int FINISH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_detail);

        layoutContinue = findViewById(R.id.layout_wd_continue);
        layoutContinue.setOnClickListener(this);
        layoutVoice = findViewById(R.id.layout_wd_voice);
        layoutVoice.setOnClickListener(this);
        textWordName = findViewById(R.id.text_wd_name);
        layoutPhoneUk = findViewById(R.id.layout_wd_phone_uk);
        layoutPhoneUk.setOnClickListener(this);
        layoutPhoneUs = findViewById(R.id.layout_wd_phone_usa);
        layoutPhoneUs.setOnClickListener(this);
        layoutStar = findViewById(R.id.layout_wd_star);
        layoutStar.setOnClickListener(this);
        textPhoneUk = findViewById(R.id.text_wd_phone_uk);
        textPhoneUs = findViewById(R.id.text_wd_phone_usa);
        textInterpretation = findViewById(R.id.text_wd_interpretation);
        cardRemMind = findViewById(R.id.card_wd_remMethod);
        textRemMind = findViewById(R.id.text_wd_remMethod);
        recyclerSentence = findViewById(R.id.recycler_wd_sentence);
        recyclerPhrase = findViewById(R.id.recycler_wd_phrase);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerSentence.setLayoutManager(linearLayoutManager);
        recyclerSentence.setHasFixedSize(false);
        recyclerSentence.setNestedScrollingEnabled(false);
        recyclerSentence.setFocusable(false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setSmoothScrollbarEnabled(true);
        linearLayoutManager2.setAutoMeasureEnabled(true);
        recyclerPhrase.setLayoutManager(linearLayoutManager2);
        recyclerPhrase.setHasFixedSize(false);
        recyclerPhrase.setNestedScrollingEnabled(false);
        recyclerPhrase.setFocusable(false);
        detailPhraseAdapter = new DetailPhraseAdapter(itemPhraseList);
        detailSentenceAdapter = new DetailSentenceAdapter(itemSentenceList);
        recyclerPhrase.setAdapter(detailPhraseAdapter);
        recyclerSentence.setAdapter(detailSentenceAdapter);
        cardSentence = findViewById(R.id.card_wd_sentence);
        cardPhrase = findViewById(R.id.card_wd_phrase);
        textContinue = findViewById(R.id.text_wd_continue);
        imgStar = findViewById(R.id.img_wd_star);
        layoutFolder = findViewById(R.id.layout_wd_folder);
        layoutFolder.setOnClickListener(this);

        windowSlide(Gravity.TOP);

        currentType = getIntent().getIntExtra(TYPE_NAME, 0);

        if (currentType == TYPE_GENERAL) {
            textContinue.setText("返回");
        } else {
            textContinue.setText("继续");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_wd_continue:
                if (currentType == TYPE_GENERAL) {
                    onBackPressed();
                } else {
                    LearnWordActivity.needUpdate = true;
                    onBackPressed();
                }
                break;
            case R.id.layout_wd_voice:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MediaHelper.play(words.get(0).getWord());
                    }
                }).start();
                break;
            case R.id.layout_wd_star:
                if (currentWord.getIsCollected() == 1) {
                    Glide.with(this).load(R.drawable.icon_star).into(imgStar);
                    Word word = new Word();
                    word.setToDefault("isCollected");
                    word.updateAll("wordId = ?", wordId + "");
                    currentWord = LitePal.where("wordId = ?", wordId + "").find(Word.class).get(0);
                } else {
                    Glide.with(this).load(R.drawable.icon_star_fill).into(imgStar);
                    Word word = new Word();
                    word.setIsCollected(1);
                    word.updateAll("wordId = ?", wordId + "");
                    currentWord = LitePal.where("wordId = ?", wordId + "").find(Word.class).get(0);
                }
                break;

            case R.id.layout_wd_phone_uk:
                MediaHelper.play(MediaHelper.ENGLISH_VOICE, words.get(0).getWord());
                break;
            case R.id.layout_wd_phone_usa:
                MediaHelper.play(MediaHelper.AMERICA_VOICE, words.get(0).getWord());
                break;
            case R.id.layout_wd_folder:
                final List<WordFolder> wordFolders = LitePal.findAll(WordFolder.class);
                if (wordFolders.isEmpty())
                    Toast.makeText(this, "暂无单词夹", Toast.LENGTH_SHORT).show();
                else {
                    String[] folderNames = new String[wordFolders.size()];
                    for (int i = 0; i < wordFolders.size(); ++i) {
                        folderNames[i] = wordFolders.get(i).getName();
                    }
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(WordDetailActivity.this);
                    builder2.setTitle("存入单词本")
                            .setSingleChoiceItems(folderNames, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                    // 延迟500毫秒取消对话框
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            List<FolderLinkWord> folderLinkWords = LitePal.where("wordId = ? and folderId = ?", currentWord.getWordId() + "", wordFolders.get(which).getId() + "").find(FolderLinkWord.class);
                                            if (folderLinkWords.isEmpty()) {
                                                FolderLinkWord folderLinkWord = new FolderLinkWord();
                                                folderLinkWord.setFolderId(wordFolders.get(which).getId());
                                                folderLinkWord.setWordId(currentWord.getWordId());
                                                folderLinkWord.save();
                                                Toast.makeText(WordDetailActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(WordDetailActivity.this, "该单词已经在此单词夹中了哦", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, 200);
                                }
                            }).show();
                }
                break;
        }
    }

    private void setData() {
        words = LitePal.where("wordId = ?", wordId + "").find(Word.class);
        currentWord = words.get(0);
        // 设置收藏
        if (currentWord.getIsCollected() == 1)
            Glide.with(this).load(R.drawable.icon_star_fill).into(imgStar);
        else
            Glide.with(this).load(R.drawable.icon_star).into(imgStar);
        // 设置名称
        textWordName.setText(currentWord.getWord());
        // 设置英音
        if (currentWord.getUkPhone() != null) {
            layoutPhoneUk.setVisibility(View.VISIBLE);
            textPhoneUk.setText(currentWord.getUkPhone());
        } else {
            layoutPhoneUk.setVisibility(View.GONE);
        }
        // 设置美音
        if (currentWord.getUsPhone() != null) {
            layoutPhoneUs.setVisibility(View.VISIBLE);
            textPhoneUs.setText(currentWord.getUsPhone());
        } else {
            layoutPhoneUs.setVisibility(View.GONE);
        }
        // 设置中文
        List<Interpretation> interpretationList = LitePal.where("wordId = ?", wordId + "").find(Interpretation.class);
        StringBuilder chinese = new StringBuilder();
        StringBuilder english = new StringBuilder();
        ArrayList<String> chsMeans = new ArrayList<>();
        ArrayList<String> enMeans = new ArrayList<>();
        for (int i = 0; i < interpretationList.size(); ++i) {
            chsMeans.add(interpretationList.get(i).getWordType() + ". " + interpretationList.get(i).getCHSMeaning());
            if (interpretationList.get(i).getENMeaning() != null) {
                enMeans.add(interpretationList.get(i).getWordType() + ". " + interpretationList.get(i).getENMeaning());
            }
        }
        for (int i = 0; i < chsMeans.size(); ++i) {
            if (i != chsMeans.size() - 1)
                chinese.append(chsMeans.get(i) + "\n");
            else
                chinese.append(chsMeans.get(i));
        }
        textInterpretation.setText(chinese.toString());
        // 设置巧记
        if (currentWord.getRemMethod() != null) {
            textRemMind.setText(currentWord.getRemMethod());
            cardRemMind.setVisibility(View.VISIBLE);
        } else {
            cardRemMind.setVisibility(View.GONE);
        }
        // 设置例句
        List<Sentence> sentenceList = LitePal.where("wordId = ?", wordId + "").find(Sentence.class);
        if (!sentenceList.isEmpty()) {
            cardSentence.setVisibility(View.VISIBLE);
            setSentenceData(sentenceList);
        } else {
            cardSentence.setVisibility(View.GONE);
        }
        // 设置词组
        List<Phrase> phraseList = LitePal.where("wordId = ?", wordId + "").find(Phrase.class);
        if (!phraseList.isEmpty()) {
            cardPhrase.setVisibility(View.VISIBLE);
            setPhraseData(phraseList);
        } else {
            cardPhrase.setVisibility(View.GONE);
        }



    }

    private void setSentenceData(List<Sentence> sentenceList) {
        itemSentenceList.clear();
        for (Sentence sentence : sentenceList) {
            itemSentenceList.add(new ItemSentence(sentence.getChSentence(), sentence.getEnSentence()));
        }
        detailSentenceAdapter.notifyDataSetChanged();
    }

    private void setPhraseData(List<Phrase> phraseList) {
        itemPhraseList.clear();
        for (Phrase phrase : phraseList) {
            itemPhraseList.add(new ItemPhrase(phrase.getChsPhrase(), phrase.getEnPhrase()));
        }
        detailPhraseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListActivity.isUpdate=true;
        LearnWordActivity.needUpdate = true;
        MediaHelper.releasePlayer();
    }



}