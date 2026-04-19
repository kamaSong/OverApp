package com.example.overapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.overapp.FragMent.FragementWords;
import com.example.overapp.FragMent.FragmentMe;
import com.example.overapp.FragMent.FragmentReview;
import com.example.overapp.Utils.ActivityCollector;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity{
    private Fragment fragementWords, fragementReview, fragementMe;

    private Fragment[] fragments;

    //用于记录上个选择的Fragment
    public static int lastFragment;

    private BottomNavigationView bottomNavigationView;

    private LinearLayout linearLayout;

    private static final String TAG = "MainActivity";

    public static boolean needRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

        bottomNavigationView = findViewById(R.id.bottom_nav);
        linearLayout = findViewById(R.id.linear_frag_container);

        if (needRefresh) {

            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
            );
            animation.setDuration(2000);
            //bottomNavigationView.startAnimation(animation);
        }
// 创建Fragment实例并存储在数组中
        fragementWords = new FragementWords();
        fragementReview = new FragmentReview();
        fragementMe = new FragmentMe();
        fragments = new Fragment[]{fragementWords, fragementReview, fragementMe};

// 根据lastFragment的值来初始化显示哪个Fragment
        switch (lastFragment) {
            case 0:
                // 如果lastFragment为0，显示FragmentWords
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.linear_frag_container, fragementWords) // 替换容器中的Fragment
                        .show(fragementWords) // 显示FragmentWords
                        .commit(); // 提交事务
                break;
            case 1:
                // 如果lastFragment为1，显示FragmentReview
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.linear_frag_container, fragementReview)
                        .show(fragementReview)
                        .commit();
                break;
            case 2:
                // 如果lastFragment为2，显示FragmentMe
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.linear_frag_container, fragementMe)
                        .show(fragementMe)
                        .commit();
                break;
        }

// 设置BottomNavigationView的导航项选择监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bnavigation_word:
                        if (lastFragment != 0) {
                            switchFragment(lastFragment, 0);
                            lastFragment = 0;
                        }
                        return true;
                    case R.id.bnavigation_review:
                        if (lastFragment != 1) {
                            switchFragment(lastFragment, 1);
                            lastFragment = 1;
                        }
                        return true;
                    case R.id.bnavigation_me:
                        if (lastFragment != 2) {
                            switchFragment(lastFragment, 2);
                            lastFragment = 2;
                        }
                        return true;
                }
                return true;
            }
        });
    }




    // 用于切换Fragment
    private void switchFragment(int lastIndex, int index) {
        // 开始一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 隐藏上一个显示的Fragment
        transaction.hide(fragments[lastIndex]);

        // 检查要显示的Fragment是否已经被添加到容器中
        if (fragments[index].isAdded() == false) {
            // 如果Fragment尚未添加，则将其添加到容器中
            transaction.add(R.id.linear_frag_container, fragments[index]);
        }

        // 显示要切换到的Fragment
        // 使用commitAllowingStateLoss()方法来提交事务，这允许在Activity的保存状态之后提交事务
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示")
                .setMessage("今天不再背单词了吗？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        needRefresh = true;
                        ActivityCollector.finishAll();
                    }
                })
                .setNegativeButton("再看看", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }}


