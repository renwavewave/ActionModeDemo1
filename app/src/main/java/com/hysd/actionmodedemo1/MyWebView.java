package com.hysd.actionmodedemo1;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author wavewave
 * @CreateDate: 2023/6/27 3:56 PM
 * @Description:
 * @Version: 1.0
 */
public class MyWebView extends WebView {
    public int markTop = 0;
    public int markLeft = 0;
    public boolean isFirst = true;
    private OnTextSelectedListener textSelectedListener;

    public MyWebView(@NonNull Context context) {
        super(context);
    }

    public MyWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        ActionMode actionMode = super.startActionMode(buildCustomCallback(callback));
        actionMode.getMenu().clear();
        return actionMode;
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        ActionMode actionMode = super.startActionMode(buildCustomCallback(callback), type);
        actionMode.getMenu().clear();
        return actionMode;
    }

    /**
     * 自定义callback，用于菜单过滤
     *
     * @param callback
     * @return
     */
    private ActionMode.Callback buildCustomCallback(final ActionMode.Callback callback) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return new ActionMode.Callback2() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    if (mode.getMenu() != null)
                        mode.getMenu().clear();
                    return callback.onCreateActionMode(mode, menu);
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    callback.onPrepareActionMode(mode, menu);
                    if (isFirst && textSelectedListener != null) {
                        textSelectedListener.OnTextSelected(markTop, markLeft);
                        markTop = 0;
                        markLeft = 0;
                    }
                    if (mode.getMenu() != null)
                        mode.getMenu().clear();
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if (item == null || TextUtils.isEmpty(item.getTitle())) {
                        return callback.onActionItemClicked(mode, item);
                    }
                    String title = item.getTitle().toString();
                    return callback.onActionItemClicked(mode, item);
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    if (mode != null)
                        mode.invalidate();
                    callback.onDestroyActionMode(mode);
                }

                @Override
                public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                    if (outRect.top != 0) {
                        isFirst = true;
                        markTop = outRect.top + 28;
                        markLeft = outRect.centerX();
                    } else {
                        isFirst = false;
                    }
                    if (callback instanceof ActionMode.Callback2) {
                        ActionMode.Callback2 tempCallback2 = (ActionMode.Callback2) callback;
                        tempCallback2.onGetContentRect(mode, view, outRect);
                    } else {
                        super.onGetContentRect(mode, view, outRect);
                    }
                }
            };
        }
        return callback;
    }

    public void setTextSelectedListener(OnTextSelectedListener textSelectedListener) {
        this.textSelectedListener = textSelectedListener;
    }

    public interface OnTextSelectedListener {
        public void OnTextSelected(int top, int left);
    }
}
