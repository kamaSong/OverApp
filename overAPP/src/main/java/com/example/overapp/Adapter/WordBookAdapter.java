package com.example.overapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.overapp.ChangePlanActivity;
import com.example.overapp.ConfigData.ConfigData;
import com.example.overapp.Item.ItemWordBook;
import com.example.overapp.R;
import com.example.overapp.Utils.MyApplication;
import com.example.overapp.database.UserConfig;

import org.litepal.LitePal;

import java.util.List;
//使用RecyclerView.Adapter必须重写，其他一样
//方法1：public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//
//方法2：public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
//
//方法3：public int getItemCount()

public class WordBookAdapter extends RecyclerView.Adapter<WordBookAdapter.ViewHolder> {

    private List<ItemWordBook> ItemWordBookList;

    // 定义一个静态内部类ViewHolder，继承自RecyclerView.ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        // 定义ViewHolder持有的视图变量
        // 整个项目的视图
        View view;
        // 书籍的图片视图
        ImageView imgBook;
        // 书籍的名称、来源和总单词数
        TextView BookName, BookSource, bookWordAllNum;

        // ViewHolder的构造函数，接收一个itemView作为参数
        public ViewHolder(View itemView) {
            // 调用父类RecyclerView.ViewHolder的构造函数，传递itemView
            super(itemView);

            // 将传入的itemView赋值给view变量
            view = itemView;

            // 使用findViewById方法从itemView中查找对应的视图元素，
            imgBook = itemView.findViewById(R.id.item_img_book);
            BookName = itemView.findViewById(R.id.itemText_bookName);
            BookSource = itemView.findViewById(R.id.item_text_bookSource);
            bookWordAllNum = itemView.findViewById(R.id.item_text_bookAll_num);
        }
    }

    public WordBookAdapter(List<ItemWordBook> ItemWordBookList) {
        this.ItemWordBookList = ItemWordBookList;
    }
//   使用 onCreateViewHolder(ViewGroup parent, int viewType)创建Holder

    @Override
//    其中ViewGroup parent：可以简单理解为item的根ViewGroup，item的子控件加载在其中
//    int viewType：item的类型，可以根据viewType来创建不同的ViewHolder，来加载不同的类型的item
//    并在其中设置点击事件
    /**
     * 创建新的视图持有者（ViewHolder），并设置点击事件监听器。
     *
     * @param parent ViewGroup，RecyclerView的父视图组，新创建的ViewHolder将与这个ViewGroup关联。
     * @param viewType 视图类型，用于区分不同的视图布局。
     * @return 返回创建的ViewHolder实例。
     */
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 使用LayoutInflater从给定的父视图组的上下文中创建一个新的视图。
        // 视图布局由R.layout.item_book_list定义，该布局文件描述了每个列表项的外观。
        // 第三个参数false表示新创建的视图不会立即附加到父ViewGroup。
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_list, parent, false);

        // 使用新创建的视图初始化ViewHolder对象。ViewHolder将持有视图的引用，并提供对视图元素的快速访问。
        final ViewHolder holder = new ViewHolder(view);

        // 为ViewHolder持有的视图设置点击事件监听器。
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前ViewHolder在Adapter中的位置。
                int position = holder.getAdapterPosition();

                // 从ItemWordBookLists列表中获取对应位置的书籍信息。
                final ItemWordBook itemWordBook = ItemWordBookList.get(position);

                // 使用LitePal ORM查询数据库，获取与当前登录用户ID匹配的UserConfig列表。
                // 假设用户只有一个配置，因此只获取第一个UserConfig实例。
                List<UserConfig> userConfigs = LitePal.where("userId = ?", ConfigData.getNumLogged() + "").find(UserConfig.class);

                // 检查用户配置中的当前书籍ID是否与点击的书籍ID匹配，并且需要复习的单词数量不为0。
                if (userConfigs.get(0).getCurrentBookId() == itemWordBook.getBookId() &&
                        userConfigs.get(0).getWordNeedReciteNum() != 0) {
                    // 如果条件满足，显示一个Toast消息，告知用户已经选择了这本书。
                    Toast.makeText(MyApplication.getContext(), "当前选的就是这本书哦", Toast.LENGTH_SHORT).show();
                } else {
                    // 否则，更新用户配置，设置新的当前书籍ID。
                    // 创建一个新的UserConfig对象，并设置其当前书籍ID为点击的书籍的ID。
                    UserConfig userConfig = new UserConfig();
                    userConfig.setCurrentBookId(itemWordBook.getBookId());

                    // 使用LitePal ORM的updateAll方法更新数据库中与当前用户ID匹配的UserConfig记录。
                    userConfig.updateAll("userId = ?", ConfigData.getNumLogged() + "");

                    // 创建一个新的Intent，用于启动ChangePlanActivity。
                    // ChangePlanActivity是一个新的Activity，用户可以在其中更改学习计划。
                    Intent intent = new Intent(MyApplication.getContext(), ChangePlanActivity.class);

                    // 向Intent中添加一个额外的数据，表示数据已更新。
                    // ConfigData.UPDATE_NAME是一个键，用于在目标Activity中检索这个值。
                    // ConfigData.notUpdate是一个值，表示数据已经更新。
                    intent.putExtra(ConfigData.UPDATE_NAME, ConfigData.notUpdate);

                    // 设置Intent标志，表示这是一个新的任务，并且应该在新的任务历史记录中启动。
                    // 这通常用于启动那些不应该返回到启动它的Activity的Activity。
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // 使用MyApplication的全局上下文启动新的Activity。
                    MyApplication.getContext().startActivity(intent);
                }
            }
        });

        // 返回创建的ViewHolder实例，以便RecyclerView可以使用它来绑定数据和显示视图。
        return holder;
    }
//    onBindViewHolder(RecyclerHolder holder, int position)绑定ViewHolder
//   重写并 将相关数据放入控件
//当增加一个空间是就会调用一次这个方法，经相关数据放置到相关viewhold中
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemWordBook itemWordBook = ItemWordBookList.get(position);
//        图片都通过Glide进行存放
        Glide.with(MyApplication.getContext()).load(itemWordBook.getBookImg()).into(holder.imgBook);
// 使用 setText 方法将 ItemWordBook对象中的书名、书籍来源和总字数设置到 BookName，BookSource 和 bookWordAllNum
        holder.BookName.setText(itemWordBook.getBookName());
        holder.BookSource.setText(itemWordBook.getBookSource());
        holder.bookWordAllNum.setText(itemWordBook.getBookWordAllNum() + "");
    }
//getItemCount()获取Item的数目
//    getItemCount()方法被重写以返回ItemWordBookLists的大小（即列表中的项目数）
//    ItemWordBookLists是一个存储ItemWordBook对象的列表，每个对象代表一个书籍的信息。
    @Override
    public int getItemCount() {
        return ItemWordBookList.size();
    }

}
