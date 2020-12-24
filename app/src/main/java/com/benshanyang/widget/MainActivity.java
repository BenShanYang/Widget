package com.benshanyang.widget;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.benshanyang.widgetlibrary.widget.SearchBarView;
import com.benshanyang.widgetlibrary.widget.TitleBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetStateBar();
        setContentView(R.layout.activity_main);

        TitleBarView titleBarView = findViewById(R.id.title_view);
        titleBarView.setOnActionButtonClickListener(new TitleBarView.OnActionButtonClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击了菜单按钮", Toast.LENGTH_SHORT).show();
            }
        });
        titleBarView.setOnFinishListener(new TitleBarView.OnFinishListener() {
            @Override
            public void onFinish(View view) {
                Toast.makeText(MainActivity.this, "点击了返回", Toast.LENGTH_SHORT).show();
            }
        });


        ((SearchBarView) findViewById(R.id.search_bar_view_1)).setOnTextChangedListener(new SearchBarView.OnTextChangedListener() {
            @Override
            public void onChanged(Editable s, final EditText editText, ImageButton imageButton) {
                imageButton.setImageResource(TextUtils.isEmpty(s) ? R.drawable.ic_camera : R.drawable.ic_clear);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(((SearchBarView) findViewById(R.id.search_bar_view_1)).getText())) {
                    Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                } else {
                    ((SearchBarView) findViewById(R.id.search_bar_view_1)).setText("");
                }
            }
        });

        ((SearchBarView) findViewById(R.id.search_bar_view_2)).setOnActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
            }
        });

        ((SearchBarView) findViewById(R.id.search_bar_view_3)).setOnTextChangedListener(new SearchBarView.OnTextChangedListener() {
            @Override
            public void onChanged(Editable s, EditText editText, ImageButton imageButton) {
                imageButton.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });
        ((SearchBarView) findViewById(R.id.search_bar_view_3)).setOnActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(((SearchBarView) findViewById(R.id.search_bar_view_3)).getText())) {
                    ((SearchBarView) findViewById(R.id.search_bar_view_3)).setActionIconVisibility(View.GONE);
                    ((SearchBarView) findViewById(R.id.search_bar_view_3)).setText("");
                    ((SearchBarView) findViewById(R.id.search_bar_view_3)).getEditText().setFocusable(true);
                    ((SearchBarView) findViewById(R.id.search_bar_view_3)).getEditText().setFocusableInTouchMode(true);
                    ((SearchBarView) findViewById(R.id.search_bar_view_3)).getEditText().requestFocus();
                    ((SearchBarView) findViewById(R.id.search_bar_view_3)).showKeyboard();
                }
            }
        });
    }

    //设置沉浸式状态栏
    private void resetStateBar() {
        //设置窗体为没有标题的模式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //android系统大于6.0系统则设置标题栏字体问深色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //android系统大于5.0系统则设置状态栏灰色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(0x33000000);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.BLACK);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //android系统大于4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

}