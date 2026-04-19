package com.example.overapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.overapp.ConfigData.ConfigData;
import com.example.overapp.InterFace.PermissionListener;
import com.example.overapp.Utils.ActivityCollector;
import com.example.overapp.Utils.BaiduHelper;
import com.example.overapp.Utils.MyPopWindow;
import com.example.overapp.Utils.TimeController;
import com.example.overapp.database.DailyData;
import com.example.overapp.database.UserConfig;

import org.litepal.LitePal;

import java.util.List;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{

    // 壁纸图片显示的ImageView控件
    private ImageView imgBackground;

    // 每日一句的卡片视图，使用CardView来展示
    private CardView cardWelCome;

    // 每日一句的文字显示，使用TextView来展示
    private TextView textWelCome;

    // 弹出窗口中的同意按钮的卡片视图
    private CardView cardAgree;

    // 弹出窗口中的不同意按钮的文本视图
    private TextView textNotAgree;

    // 自定义的弹出窗口对象
    private MyPopWindow welWindow;

    // 缩放动画对象
    private ScaleAnimation animation;

    // 日志输出的标签
    private static final String TAG = "WelcomeActivity";

    // 定义了一个常量，用于后续的消息处理
    private final int FINISH = 1;

    // 存储根路径的字符串变量
    private String rootPath;

    // 创建一个Handler对象，用于处理来自其他线程的消息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 根据msg.what的值来处理不同的消息
            switch (msg.what) {
                case FINISH:
                    // 查询数据库中与当前日期匹配的每日一句数据
                    List<DailyData> dailyDataList = LitePal.where("dayTime = ?", TimeController.getCurrentDateStamp() + "").find(DailyData.class);
                    // 如果查询结果不为空
                    if (!dailyDataList.isEmpty()) {
                        // 获取第一条数据
                        DailyData dailyData = dailyDataList.get(0);
                        // 设置每日一句的文本内容
                        textWelCome.setText(dailyData.getDailyEn());
                        // 使用Glide库加载每日一句的背景图片并显示
                        Glide.with(WelcomeActivity.this).load(dailyData.getPicVertical()).into(imgBackground);
                    }
                    break;
            }
        }
    };
    // 当Activity被创建时调用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // 设置Activity的布局文件

        rootPath = Environment.getDataDirectory().getPath(); // 获取设备的数据目录路径
        Log.d(TAG, "路径" + rootPath); // 在日志中打印路径信息

        // 检查Activity是否被带到前台，如果是则移除它并从栈中结束
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            ActivityCollector.removeActivity(this);
            finish();
            return;
        }

        // 初始化自定义的弹出窗口
        welWindow = new MyPopWindow(this);

        // 初始化UI组件
        cardWelCome = findViewById(R.id.card_wel1); // 每日一句的卡片视图
        textWelCome = findViewById(R.id.text_wel); // 每日一句的文本显示
        imgBackground = findViewById(R.id.img_wel_bg); // 背景图片显示的ImageView

        // 设置弹出窗口中的组件并添加点击监听器
        cardAgree = welWindow.findViewById(R.id.card_agree);
        cardAgree.setOnClickListener(this);
        textNotAgree = welWindow.findViewById(R.id.text_noagree);
        textNotAgree.setOnClickListener(this);

        // 配置缩放动画
        animationConfig();

        // 设置卡片的透明度
        cardWelCome.getBackground().setAlpha(200);

        // 在新线程中准备每日数据，并发送消息到Handler处理
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareDailyData(); // 准备每日数据
                Message message = new Message();
                message.what = FINISH; // 设置消息类型为FINISH
                handler.sendMessage(message); // 发送消息给Handler处理
                BaiduHelper.getAssessToken(); // 获取百度相关的token
            }
        }).start();

        // 如果是第一次运行应用
        if (ConfigData.getIsFirst()) {
            // 显示弹出窗口，并设置一些属性
            welWindow.setClipChildren(false)
                    .setBlurBackgroundEnable(true)
                    .setOutSideDismiss(false)
                    .showPopupWindow();
        } else {
            // 如果不是第一次运行，则在延迟后启动背景图片的缩放动画
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgBackground.startAnimation(animation);
                }
            }, 500);

            // 根据配置设置学习提醒
            if (ConfigData.getIsAlarm()) {
                // 解析配置中的提醒时间，并设置闹钟
                int hour = Integer.parseInt(ConfigData.getAlarmTime().split("-")[0]);
                int minute = Integer.parseInt(ConfigData.getAlarmTime().split("-")[1]);
                AlarmActivity.startAlarm(hour, minute, false, false);
            }
        }
    }


//设置点击事件
    @Override
    public void onClick(View v) {
        // 通过switch语句判断被点击的视图是哪个
        switch (v.getId()) {
            // 如果被点击的是ID为R.id.card_agree的视图（可能是一个卡片）
            case R.id.card_agree:
                // 调用requestPermission()方法来请求权限
                requestPermission();
                break; // 退出switch语句

            // 如果被点击的是ID为R.id.text_not_agree的视图（可能是一段文本）
            case R.id.text_noagree:
                // 显示一个短暂的Toast消息，告诉用户程序即将退出
                Toast.makeText(this, "抱歉，程序即将退出", Toast.LENGTH_SHORT).show();

                // 结束所有的Activity，这通常意味着应用将完全退出
                ActivityCollector.finishAll();
                break; // 退出switch语句
        }
    }

    // 权限管理的方法
    private void requestPermission() {
        // 调用requestRunPermission方法来请求权限
        // ConfigData.permissions是一个包含所需权限的字符串数组
        // PermissionListener是一个接口，用于处理权限请求的结果
        requestRunPermission(ConfigData.permissions, new PermissionListener() {
            @Override
            // 当所有权限都被授予时调用的方法
            public void onGranted() {
                // 关闭或隐藏自定义的弹出窗口
                welWindow.dismiss();

                // 设置应用是否为第一次运行的标志为否
                // 这意味着应用已经运行过至少一次，并且已经处理过首次运行的逻辑
                ConfigData.setIsFirst(false);

                // 使用Handler延迟一段时间（MyPopWindow.animatTime）后开始播放背景图片的动画
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgBackground.startAnimation(animation);
                    }
                }, MyPopWindow.animatTime);
            }

            // 权限请求被拒绝时的处理逻辑

            @Override
            public void onDenied(List<String> deniedPermission) {
                if (!deniedPermission.isEmpty()) {
                    Toast.makeText(WelcomeActivity.this, "无法获得权限，程序即将退出", Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                }
            }
        });
    }




    // 缩放动画配置的方法
    private void animationConfig() {
        // 创建一个新的缩放动画，从原始大小（1.0倍）放大到1.5倍
        // 这个动画会同时影响X轴和Y轴上的尺寸
        // 动画的起始和结束点都是相对于视图自身的中心点（0.5f表示中心点）
        animation = new ScaleAnimation(1, 1.5f, 1, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, 1, 0.5f);

        // 设置动画的持续时间为4000毫秒，即4秒
        animation.setDuration(4000);

        // 设置动画结束后，视图保持动画结束时的状态，而不是返回到原始状态
        animation.setFillAfter(true);

        // 设置动画的循环次数为0，意味着动画不会重复播放
        animation.setRepeatCount(0);

        // 为动画设置监听器，以处理动画开始、重复和结束时的事件
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            // 动画开始时调用此方法，但当前没有实现任何逻辑
            public void onAnimationStart(Animation animation) {
                // 动画开始时的处理逻辑可以在这里实现
            }

            @Override
            // 动画结束时调用此方法
            public void onAnimationEnd(Animation animation) {
                // 检查用户是否已登录
                if (ConfigData.getIsLogged()) {
                    // 如果已登录，使用Litepal从数据库中查询用户配置信息
                    List<UserConfig> userConfigs = LitePal.where("userId = ?", ConfigData.getNumLogged() + "").find(UserConfig.class);

                    // 获取用户当前选择的词书ID
                    int currentBookId = userConfigs.get(0).getCurrentBookId();

                    // 获取用户需要复习的单词数量
                    int wordNeedReciteNum = userConfigs.get(0).getWordNeedReciteNum();

                    // 根据用户配置决定启动哪个Activity
                    if (currentBookId == -1) {
                        // 如果用户没有选择词书，则跳转到选择词书页面
                        Intent intent = new Intent(WelcomeActivity.this, ChooseWordDBActivity.class);
                        startActivity(intent);
                    } else if (currentBookId != -1 && wordNeedReciteNum == 0) {
                        // 如果用户选择了词书且没有需要复习的单词，则跳转到修改计划页面
                        Intent intent = new Intent(WelcomeActivity.this, ChangePlanActivity.class);
                        startActivity(intent);
                    } else {
                        // 其他情况，跳转到主页面
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            // 动画重复播放时调用此方法
            public void onAnimationRepeat(Animation animation) {
                // 当动画重复时在这里写入代码处理逻辑
            }
        });
    }


}
