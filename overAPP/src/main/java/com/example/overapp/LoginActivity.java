package com.example.overapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.overapp.ConfigData.ConfigData;
import com.example.overapp.Utils.ActivityCollector;
import com.example.overapp.database.User;
import com.example.overapp.database.UserConfig;
import com.lihang.ShadowLayout;

import org.litepal.LitePal;

import java.util.List;

public class LoginActivity extends BaseActivity {

    private ShadowLayout cardLogin; // 定义一个ShadowLayout类型的私有变量cardLogin，可能用于放置登录按钮

    private LinearLayout linearLayout; // 定义一个LinearLayout类型的私有变量linearLayout，可能用于布局或容器

    // 定义一个常量TAG，用于日志输出，方便调试和跟踪
    private static final String TAG = "LoginActivity";

    // 定义两个常量，用于表示登录的结果
    private final int SUCCESS = 1; // 登录成功
    private final int FAILED = 2;  // 登录失败

    // 定义一个ProgressDialog对象，用于显示登录过程中的进度
    private ProgressDialog progressDialog;

    // 定义一个String类型的私有变量content，可能用于存储一些内容或数据
    private String content;

    // 定义一个Handler对象，用于处理从其他线程发送过来的消息
    @SuppressLint("HandlerLeak") // 忽略Handler内存泄漏的警告
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 根据msg.what的值，处理不同的消息
            switch (msg.what) {
                case FAILED: // 如果msg.what的值为FAILED，表示登录失败
                    Toast.makeText(LoginActivity.this, "登录失败，请检查服务器与网络状态", Toast.LENGTH_SHORT).show();
                    // 显示一个短暂的Toast消息，告知用户登录失败
                    break;
                case SUCCESS: // 如果msg.what的值为SUCCESS，表示登录成功
                    ActivityCollector.startOtherActivity(LoginActivity.this, ChooseWordDBActivity.class);
                    // 启动另一个Activity，可能是选择数据库或单词的界面
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 调用父类的 onCreate 方法
        super.onCreate(savedInstanceState);
        // 设置 Activity 的布局为 activity_login.xml
        setContentView(R.layout.activity_login);

        // 查找布局中 ID 为 card_sina_login 的 ShadowLayout 控件，并赋值给 cardLogin
        cardLogin = findViewById(R.id.card_sina_login);
        // 查找布局中 ID 为 linear_login 的 LinearLayout 控件，并赋值给 linearLayout
        linearLayout = findViewById(R.id.linear_login);

        // 为 cardLogin 设置点击事件监听器
        cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 定义一个整数 id，值为 00001
                int id = 00001;
                // 定义一个字符串 name，值为 "User"
                String name = "User";

                // 使用 LitePal ORM 查询用户表中 userId 等于指定 id 的用户
                List<User> users = LitePal.where("userId = ?", id+"").find(User.class);
                // 如果查询结果为空，即用户表中不存在该用户
                if (users.isEmpty()) {
                    // 创建一个新的 User 对象
                    User user = new User();
                    // 设置 User 对象的 userName 属性
                    user.setUserName(name);
                    // 设置 User 对象的 userId 属性
                    user.setUserId(id);
                    // 测试用，设置 User 对象的 userMoney 属性为 0
                    user.setUserMoney(0);
                    // 测试用，设置 User 对象的 userWordNumber 属性为 0
                    user.setUserWordNumber(0);
                    // 将 User 对象保存到数据库中
                    user.save();
                }

                // 使用 LitePal ORM 查询用户配置表中 userId 等于指定 id 的配置
                List<UserConfig> userConfigs = LitePal.where("userId = ?", id+"").find(UserConfig.class);
                // 如果查询结果为空，即用户配置表中不存在该用户的配置
                if (userConfigs.isEmpty()) {
                    // 创建一个新的 UserConfig 对象
                    UserConfig userConfig = new UserConfig();
                    // 设置 UserConfig 对象的 userId 属性
                    userConfig.setUserId(id);
                    // 设置 UserConfig 对象的 currentBookId 属性为 -1
                    userConfig.setCurrentBookId(-1);
                    // 将 UserConfig 对象保存到数据库中
                    userConfig.save();
                }

                // 设置 ConfigData 的静态属性 isLogged 为 true，表示用户已登录
                ConfigData.setIsLogged(true);
                // 设置 ConfigData 的静态属性 numLogged 为用户 id，表示已登录的微博 ID
                ConfigData.setNumLogged(id);

                // 创建一个新的 Message 对象
                Message message = new Message();
                // 设置 Message 对象的 what 属性为 SUCCESS，表示登录成功
                message.what = SUCCESS;
                // 将 Message 对象发送给 Handler 进行处理
                handler.sendMessage(message);
            }
        });
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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