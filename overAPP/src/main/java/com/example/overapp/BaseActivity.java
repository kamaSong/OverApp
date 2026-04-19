package com.example.overapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.overapp.ConfigData.ConfigData;
import com.example.overapp.ConfigData.ConstantData;
import com.example.overapp.InterFace.PermissionListener;
import com.example.overapp.JSON.JsonBing;
import com.example.overapp.JSON.JsonDailySentence;
import com.example.overapp.Utils.ActivityCollector;
import com.example.overapp.Utils.HttpHelper;
import com.example.overapp.Utils.TimeController;
import com.example.overapp.database.DailyData;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
//接口，嗲用处理权限操作
    private PermissionListener Listener;

    private static final int PERMISSION_REQUESTCODE = 190;

    // 当活动被创建时，都会创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 调用父类的onCreate方法
        super.onCreate(savedInstanceState);

//  Log.d(TAG, getClass().getSimpleName());用于获取当前处于那种活动
//        让其他activity继承base就会打印活动的类名

        Log.d(TAG, getClass().getSimpleName());

        // 检查当前应用是否处于夜间模式（深色主题）。
        if (ConfigData.getIsNight()) {
            // 如果应用处于夜间模式，使用ImmersionBar库来设置沉浸式状态栏，
            // 并指定状态栏字体颜色为浅色，这样字体在深色背景上更易于阅读。
            // 初始化ImmersionBar，使其应用设置。
            ImmersionBar.with(this)
                    .statusBarDarkFont(false)
                    .init();
        } else {
            // 如果应用不处于夜间模式，则设置状态栏字体颜色为深色，确保在浅色背景上可读。
            ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .init();
        }

        // 将当前活动添加到ActivityCollector中，ActivityCollector可能是一个自定义的类，
        // 用于管理活动的生命周期，确保活动在不再需要时能够正确关闭，防止内存泄漏。
        ActivityCollector.addActivity(this);


/*1.软键盘弹出时，获取焦点的输入框会被顶起，整个界面往上移动，这种设置
activity.getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_ADJUST_PAN);
2.软键盘弹出时，获取焦点的输入框被顶起，整个界面系统自动重新布局（挤压），这种设置
activity.getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_ADJUST_RESIZE);
3.软键盘弹出时，整个界面不会变动，输入框也不会顶起，这种设置
activity.getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_ADJUST_NOTHING);*/

        /* 此时使用第一种，设置窗口的软键盘输入模式，以防止输入法弹出时将布局内容顶上去。
         SOFT_INPUT_ADJUST_PAN表示当软键盘显示时，当前窗口的内容会被平移，而不是调整大小。*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
//    在活动即将被销毁时会被调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        调用位于Collector中的remove方法移除活动
        ActivityCollector.removeActivity(this);
    }

    //   - permissions: 需要请求的权限数组
//   - listener: 回调监听器，用于在权限请求结果返回时接收通知
    public void requestRunPermission(String[] permissions, PermissionListener listener) {
        // 将传入的监听器赋值给成员变量Listener
        Listener = listener;

        // 创建一个列表，用于存放那些尚未被授予的权限
        List<String> permissionLists = new ArrayList<>();

        // 遍历传入的权限数组
        for (String permission : permissions) {
            // 检查当前应用是否已经获得了该权限
            // 如果没有获得，则将该权限添加到permissionLists列表中
//         使用 checkSelfPermission(String)检查权限
//  权限：PackageManager.PERMISSION_DENIED：拒绝。
//       PackageManager.PERMISSION_GRANTED：授权
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLists.add(permission);
            }
        }

        // 如果permissionLists列表不为空，说明有权限尚未被授予
        if (!permissionLists.isEmpty()) {
           /* 使用ActivityCompat的requestPermissions方法来向用户请求权限

                    - permissionLists.toArray(new String[permissionLists.size()])
                     将列表转换为数组，作为需要请求的权限列表
               - PERMISSION_REQUESTCODE: 请求权限的请求码，用于在回调方法中识别此次权限请求*/
            ActivityCompat.requestPermissions(this,
                    permissionLists.toArray(new String[permissionLists.size()]), PERMISSION_REQUESTCODE);
        } else {
            // 如果permissionLists列表为空，说明所有权限都已经被授予
            // 调用监听器的onGranted方法，通知监听器所有权限已经被授予
            // 表示全都授权了
            Listener.onGranted();
        }
    }
////处理权限结果回调
//void onRequestPermissionsResult(int,String[],int[])
// 覆盖父类的onRequestPermissionsResult方法，用于处理权限请求的结果
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    // 调用父类的onRequestPermissionsResult方法，确保其他默认行为得到执行
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    // 根据请求码判断是哪个权限请求的回调
    switch (requestCode) {
        case PERMISSION_REQUESTCODE:

            if (grantResults.length > 0) {
                // 创建一个列表，用于存放用户拒绝的权限
                List<String> deniedPermissions = new ArrayList<>();

                // 遍历所有请求的权限及其对应的决策结果
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i]; // 当前权限的决策结果
                    String permission = permissions[i]; // 当前权限的名称

                    // 如果权限被拒绝，则添加到deniedPermissions列表中
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permission);
                    }
                }

                // 检查deniedPermissions列表是否为空
                if (deniedPermissions.isEmpty()) {
                    // 如果为空，说明所有权限都被用户授予了
                    // 调用监听器的onGranted方法，通知所有权限已授权
                    Listener.onGranted();
                } else {
                    // 如果列表不为空，说明有部分或全部权限被拒绝
                    // 调用监听器的onDenied方法，通知哪些权限被拒绝
                    Listener.onDenied(deniedPermissions);
                }
            }
            break;


        default:
            break;
    }
}
//判断活动是否运行
    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    // 开启数据监测
    // 准备每日数据的静态方法
    public static void prepareDailyData() {
        // 调用时间控中的方法获取当前日期的时间戳
        long currentDate = TimeController.getCurrentDateStamp();

        // 使用LitePal ORM框架查询数据库，在DailyData中寻找dayTime字段与当前日期时间戳相等的DailyData记录
        List<DailyData> dailyDataList = LitePal.where("dayTime = ?", currentDate + "").find(DailyData.class);

        // 检查查询结果列表是否为空
        if (dailyDataList.isEmpty()) {
            // 如果列表为空，说明数据库中没有当前日期的数据，因此调用方法分析JSON数据并保存
            analyseAndSaveJson();
        } else {
            // 如果列表不为空，说明数据库中有当前日期的数据，进一步检查这些数据是否完整
            // 获取列表中的第一条数据（假设列表按照日期排序，并且当前日期的数据是第一条）
//            List中的第一条数据
            DailyData firstDailyData = dailyDataList.get(0);

            // 检查第一条数据的各个字段是否为空
            if (firstDailyData.getPicVertical() == null ||  // 检查垂直图片是否为空
                    firstDailyData.getPicHorizontal() == null ||  // 检查水平图片是否为空
                    firstDailyData.getDailyEn() == null ||  // 检查英文日报是否为空
                    firstDailyData.getDailyChinese() == null) {  // 检查中文日报是否为空
                // 如果任何一个字段为空，说明数据不完整，因此调用方法分析JSON数据并更新数据库
              analyseAndSaveJson();
            }
        }
    }


    // 定义一个静态方法，用于分析JSON数据并将其保存到数据库
    public static void analyseAndSaveJson() {
        // 声明用于存储图片数据的字节数组
        byte[] imgVertical;
        byte[] imgHorizontal;

        // 声明用于存储的字符串
        String dailyCn;
        String dailyEn;
         /* 1用于存储图片URL修改后的结果
         2.用于存储从API请求返回的JSON数据
         3.用于存储临时构建的URl
        声明用于临时存储的字符串*/
        String finResult = "";
        String jsonData = "";
        String temUrl = "";

        // 删除DailyData表中的所有数据，以确保在保存新数据前表是空的
        LitePal.deleteAll(DailyData.class);

        // 创建一个新的DailyData对象，用于设置和保存数据
        DailyData dailyData = new DailyData();

        try {
            // 从ConstantData.IMG_API指定的URL请求数据，并将返回的JSON字符串存储在json变量中
            jsonData = HttpHelper.requestResult(ConstantData.imgApi);

            // 打印返回的JSON数据，用于调试
            Log.d(TAG, "数据" + jsonData);

            // 创建一个Gson对象，用于将JSON字符串转换为Java对象
            Gson gson = new Gson();

            // 使用Gson将JSON数据转换为JsonBing对象
            JsonBing jsonBing = gson.fromJson(jsonData, JsonBing.class);

//          调试
            Log.d(TAG, "prepareDailyData: " + jsonBing.toString());

            // 构建图片的完整URL，由常量和JsonBing中的URL拼接而成
            temUrl = ConstantData.frontImg_Api + jsonBing.getImages().get(0).getUrl();

            // 打印构建的图片URL，用于调试
            Log.d(TAG, "URL" + temUrl);

            // 从构建的图片URL请求图片数据，并存储在imgHorizontal字节数组中
            imgHorizontal = HttpHelper.requestBytes(temUrl);

            // 检查URL是否包含"1920x1080"，如果是则将其替换为"1080x1920"，否则保持不变
            if (temUrl.indexOf("1920x1080") != -1) {
                finResult = temUrl.replace("1920x1080", "1080x1920");
            } else {
                finResult = temUrl;
            }

            // 从修改后的URL请求垂直方向的图片数据，并存储在imgVertical字节数组中
            imgVertical = HttpHelper.requestBytes(finResult);

            // 从ConstantData.DAILY_SENTENCE_API指定的URL请求日报内容的JSON数据
            jsonData = HttpHelper.requestResult(ConstantData.everyDaySentence);

            // 创建另一个Gson对象，用于将日报内容的JSON数据转换为Java对象
            Gson gson2 = new Gson();

            // 使用Gson将JSON数据转换为JsonDailySentence对象
            JsonDailySentence dailySentence = gson2.fromJson(jsonData, JsonDailySentence.class);

            // 从JsonDailySentence对象中提取中文和英文的内容
            dailyCn = dailySentence.getNote();
            dailyEn = dailySentence.getContent();

            // 设置DailyData对象的属性
            dailyData.setPicHorizontal(imgHorizontal); // 设置水平图片
            dailyData.setPicVertical(imgVertical); // 设置垂直图片
            dailyData.setDailyEn(dailyEn);  // 设置每日一句的英文
            dailyData.setDailyChinese(dailyCn);  // 设置每日一句的中文
            dailyData.setDayTime(TimeController.getCurrentDateStamp() + ""); // 设置当前日期时间戳

            // 将设置好的DailyData对象保存到数据库中
            dailyData.save();

        } catch (Exception e) {
            // 如果在以上过程中出现任何异常，捕获该异常并打印异常信息，用于调试
            Log.d(TAG, "prepareDailyData: " + e.toString());
        }
    }

    public void windowFade() {
        getWindow().setEnterTransition(new Fade().setDuration(500));
        getWindow().setExitTransition(new Fade().setDuration(500));
        getWindow().setReenterTransition(new Fade().setDuration(500));
        getWindow().setReturnTransition(new Fade().setDuration(500));
    }

    public void windowSlide(int position) {
        getWindow().setEnterTransition(new Slide(position).setDuration(300));
        getWindow().setExitTransition(new Slide(position).setDuration(300));
        getWindow().setReenterTransition(new Slide(position).setDuration(300));
        getWindow().setReturnTransition(new Slide(position).setDuration(300));
    }

    public void windowExplode() {
        getWindow().setEnterTransition(new Explode().setDuration(300));
        getWindow().setExitTransition(new Explode().setDuration(300));
        getWindow().setReenterTransition(new Explode().setDuration(300));
        getWindow().setReturnTransition(new Explode().setDuration(300));
    }

    // 不支持夜间模式
    public void noNight(){
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    /*
     * fromXType：动画开始前的X坐标类型。取值范围为ABSOLUTE（绝对位置）、RELATIVE_TO_SELF（以自身宽或高为参考）、RELATIVE_TO_PARENT（以父控件宽或高为参考）。
     * fromXValue：动画开始前的X坐标值。当对应的Type为ABSOLUTE时，表示绝对位置；否则表示相对位置，1.0表示100%。
     * toXType：动画结束后的X坐标类型。
     * toXValue：动画结束后的X坐标值。
     * fromYType：动画开始前的Y坐标类型。
     * fromYValue：动画开始前的Y坐标值。
     * toYType：动画结束后的Y坐标类型。
     * toYValue：动画结束后的Y坐标值
     * *//*
    // 下部分操作布局从底部进入动画
    animation = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 1.0f,Animation.RELATIVE_TO_PARENT, 0.0f
    );
            animation.setDuration(2000);
    //relativeLayout.startAnimation(animation);
    //imgShow.setVisibility(View.VISIBLE);
    // 上部分从顶部进入动画
    animation = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -0.5f, Animation.RELATIVE_TO_PARENT, 0.0f
    );
            animation.setDuration(2000);
    // imgShow.startAnimation(animation);*/

}

