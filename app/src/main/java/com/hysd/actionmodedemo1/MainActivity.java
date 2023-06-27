package com.hysd.actionmodedemo1;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int cursorY;
    private int cursorX;
    private MyWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        webView = findViewById(R.id.web_view);
        webView.loadUrl("file:///android_asset/text.html");
        webView.setTextSelectedListener((top, left) -> {
            cursorY = top;
            cursorX = left;
            showMarkMenu(cursorY);
        });
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
    }

    private MarkPopupMenu markMenu;
    private int menuY;
    int MORE_MARK_INCLUDE_ARRAY[] = new int[]{R.string.do_note, R.string.combine_highlight, R.string.encyclopedia, R.string.dictionary, R.string.copy, R.string.speech};

    private void showMarkMenu(int y) {//高亮或者笔记result 自己决定
        boolean isOnBottom = false;
        if (y < 128) {
            y = 128;
            isOnBottom = true;
        } else {

        }
        y = y + 44;
        if (null == markMenu) {
            markMenu = new MarkPopupMenu(this, null);
        }
        markMenu.dismiss();
        markMenu.showMenu(webView, MORE_MARK_INCLUDE_ARRAY, y, isOnBottom, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, true, cursorX, 0);
        menuY = markMenu.getY();
    }
}