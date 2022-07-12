package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.entity.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Welcome extends AppCompatActivity {

    private User user;


//    protected Toolbar mToobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //初始化user
        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("user");

        // 获取页面上的底部导航栏控件
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // 配置navigation与底部菜单之间的联系
        // 底部菜单的样式里面的item里面的ID与navigation布局里面指定的ID必须相同，否则会出现绑定失败的情况
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,R.id.navigation_edit,R.id.navigation_view,R.id.navigation_user)
                .build();

        // 建立fragment容器的控制器，这个容器就是页面的上的fragment容器
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // 启动
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);






    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
