package com.hysd.actionmodedemo1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by mengfei on 15-8-11.
 */
public class MarkPopupMenu {
    private PopupWindow mWindow;
    private Context mContext;

    private int displayWidth;
    private int padding;
    private int itemHeight;
    private int moveLimit;

    public MarkPopupMenu(Context $context, PopupWindow.OnDismissListener dismissListener) {
        mContext = $context;
        Resources resources = mContext.getResources();
        displayWidth = resources.getDisplayMetrics().widthPixels;
        padding = resources.getDimensionPixelSize(R.dimen.dip_10);
        itemHeight = resources.getDimensionPixelSize(R.dimen.mark_menu_item_height);
        moveLimit = resources.getDimensionPixelSize(R.dimen.move_limit);

        mWindow = new PopupWindow();
        mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setOnDismissListener(dismissListener);

        mWindow.setFocusable(false);
        mWindow.setOutsideTouchable(true);
        initContentView($context);
    }

    private LinearLayout mRootView;
    private ImageView mIvRightSlip;
    private HorizontalScrollView scrollView;

    public PopupWindow getMyWindow() {
        return mWindow;
    }

    private void initContentView(Context $context) {
        View view = View.inflate($context, R.layout.my_popwindow_root, null);
        scrollView = view.findViewById(R.id.hsv_content);
        scrollView.setHorizontalScrollBarEnabled(false);
        mRootView = view.findViewById(R.id.ll_content);
        mIvRightSlip = view.findViewById(R.id.iv_arrow_right_slip);
        mIvRightSlip.setOnClickListener(v -> scrollView.fullScroll(ScrollView.FOCUS_RIGHT));
        mWindow.setContentView(view);
    }

    private int yPos;

    public int getY() {
        return yPos;
    }

    private int[] textArray;
    private View.OnClickListener clickListener;

    public void showMenu(View anchor, int[] $textArray, int yPos, View.OnClickListener $click, boolean toolbarShow, String color, int Xpostion) {
        textArray = $textArray;
        if (null == textArray) {
            dismiss();
            return;
        }
        this.yPos = yPos;
        clickListener = $click;
        int length = textArray.length;
        for (int i = 0; i < length; i++) {
            TextView convertView = (TextView) mRootView.getChildAt(i);
//            TextView itemView = getItemView(convertView, i);
            TextView itemView = getItemView(convertView, i, color);
            if (convertView == null) {
                mRootView.addView(itemView);
            }
        }
        for (int i = 0, count = mRootView.getChildCount() - length; i < count; i++) {
            View childView = mRootView.getChildAt(length + i);
            if (childView != null) setViewVisibility(childView, View.GONE);
        }
//        LogUtil.e("MarkPopupMenu", String.format("yPos = %s", yPos));
        scrollView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean inOnBottom = true;
        int xPos = (displayWidth - scrollView.getMeasuredWidth()) / 2;
        if (!toolbarShow && yPos < moveLimit) {
            inOnBottom = false;
            yPos = yPos + mContext.getResources().getDimensionPixelSize(R.dimen.mark_menu_move_distance);
        }
        ImageView imageView = mWindow.getContentView().findViewById(R.id.iv_down_arrow);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup viewGroup = (ViewGroup) imageView.getParent();
        int maxWidth = viewGroup.getWidth();
        int myWidth = imageView.getWidth();
        if (Xpostion == 0) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        } else {
            Xpostion = maxWidth == 0 ? Xpostion : Math.min(Xpostion, maxWidth - myWidth);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.leftMargin = Xpostion;
        }
        imageView.setLayoutParams(layoutParams);
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    public void showMenu(View anchor, int[] $textArray, int yPos, boolean isOnBottom, View.OnClickListener $click, boolean toolbarShow, String color, int Xpostion, int screenWidth) {
        textArray = $textArray;
        if (null == textArray) {
            dismiss();
            return;
        }
        this.yPos = yPos;
        clickListener = $click;
        int length = textArray.length;
        for (int i = 0; i < length; i++) {
            TextView convertView = (TextView) mRootView.getChildAt(i);
            TextView itemView = getItemView(convertView, i, color);
            if (convertView == null) {
                mRootView.addView(itemView);
            }
        }
        for (int i = 0, count = mRootView.getChildCount() - length; i < count; i++) {
            View childView = mRootView.getChildAt(length + i);
            if (childView != null) setViewVisibility(childView, View.GONE);
        }
        scrollView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int xPos;
        int menuWidth = scrollView.getMeasuredWidth();

        if (menuWidth >= screenWidth) {
            menuWidth = screenWidth - 80;
        }
        if (Xpostion <= (screenWidth / 2)) {  //左屏
            if (Xpostion < (menuWidth / 2)) {
                xPos = 40;
            } else {
                xPos = ((screenWidth - menuWidth) / 2) - (screenWidth / 2 - Xpostion);
            }
        } else {  //右屏
            if ((screenWidth - Xpostion) < (menuWidth / 2)) {
                xPos = screenWidth - menuWidth - 40;
            } else {
                xPos = ((screenWidth - menuWidth) / 2) + (Xpostion - screenWidth / 2);
            }
        }

        ImageView imageView;
        if (isOnBottom) {
            imageView = mWindow.getContentView().findViewById(R.id.iv_up_arrow);
            mWindow.getContentView().findViewById(R.id.iv_down_arrow).setVisibility(View.GONE);
            mWindow.getContentView().findViewById(R.id.iv_up_arrow).setVisibility(View.VISIBLE);
        } else {
            imageView = mWindow.getContentView().findViewById(R.id.iv_down_arrow);
            mWindow.getContentView().findViewById(R.id.iv_up_arrow).setVisibility(View.GONE);
            mWindow.getContentView().findViewById(R.id.iv_down_arrow).setVisibility(View.VISIBLE);
        }
        imageView.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(48, 24);

        int[] location = new int[2];
        LinearLayout scrollViewL = mWindow.getContentView().findViewById(R.id.ll_content);
        int left = scrollViewL.getLeft();
        int right = scrollViewL.getRight();
        int maxWidth = menuWidth;
        int myWidth = imageView.getWidth();
        mIvRightSlip.setVisibility(View.GONE);
        if (maxWidth >= screenWidth - 100) {//显示右滑动箭头
            mIvRightSlip.setVisibility(View.VISIBLE);
        }
        if (Xpostion == 0) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        } else {
            int xp = Xpostion - xPos - (imageView.getMeasuredWidth() / 2) + 10;
            if (Xpostion <= xPos + 30) {
                xp = 60;
            } else if (Xpostion >= xPos + menuWidth - 30) {
                xp = maxWidth - 60;
            }
            if (xp > maxWidth - 60 && maxWidth > 0) {
                xp = maxWidth - 60;
            }
            layoutParams.leftMargin = xp;
        }
        imageView.setLayoutParams(layoutParams);

        if (!isOnBottom) {
            mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
        } else {
            if (toolbarShow) {
                yPos = yPos + mContext.getResources().getDimensionPixelSize(R.dimen.title_height);
            }
            mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos + 44);
        }
        anchor.post(() -> scrollView.fullScroll(ScrollView.FOCUS_LEFT));
    }

    public void showMenu(View anchor, int[] $textArray, int yPos, View.OnClickListener $click, boolean toolbarShow, int xPosition) {
        showMenu(anchor, $textArray, yPos, $click, toolbarShow, null, xPosition);
    }

    public void showMenu(View anchor, int[] $textArray, int yPos, boolean isOnBottom, View.OnClickListener $click, boolean toolbarShow, int xPosition, int screenWidth) {
        showMenu(anchor, $textArray, yPos, isOnBottom, $click, toolbarShow, null, xPosition, screenWidth);
    }


    private TextView getItemView(TextView convertView, int position, String color) {
        if (convertView == null) {
            convertView = new TextView(mContext);
//            convertView.setId(R.id.mark_menu_item_id);
            convertView.setGravity(Gravity.CENTER);
            convertView.setOnClickListener(clickListener);
            convertView.setPadding(padding, 0, padding, 0);
            LayoutParams layoutParams = new LayoutParams(-2, itemHeight);
            convertView.setLayoutParams(layoutParams);
        } else {
            setViewVisibility(convertView, View.VISIBLE);
        }
        int item = getItem(position);
        if (position == 0 && color != null
                && color.length() > 0 && textArray[position] == R.string.mark_color) {//重叠color图片
            convertView.setTag(color);
            convertView.setId(R.id.mark_menu_color_item_id);
            convertView.setText(null);
            convertView.setBackgroundResource(selectColor(color));
        } else {
            convertView.setTag(item);
            convertView.setId(R.id.mark_menu_item_id);
            convertView.setText(item);
            convertView.setTextColor(Color.parseColor("#FFFFFF"));
            convertView.setBackgroundResource(0);
        }
        setParams(convertView);
        return convertView;
    }

    private void setParams(TextView convertView) {
        LayoutParams lp = (LayoutParams) convertView.getLayoutParams();
        if (convertView.getId() == R.id.mark_menu_color_item_id) {
            lp.leftMargin = padding;
            lp.rightMargin = padding;
            convertView.setPadding(0, 0, 0, 0);
        } else {
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            convertView.setPadding(padding, 0, padding, 0);
        }
        convertView.setLayoutParams(lp);
    }

    /**
     * 书的版本号小于11时，将颜色选择隐藏，因为老书太多，一下更新不完，用户可能删了重新下还是下载的老书。
     * 所以颜色选择只针对新书，book.appSupportVersion >= 11 的书
     */
    public void setViewVisibleById(int viewId) {
        if (mRootView == null || viewId <= 0) return;
        TextView textView = (TextView) mRootView.findViewById(viewId);
        if (textView != null && textView.getVisibility() == View.VISIBLE) {
            textView.setVisibility(View.GONE);
        }
    }

    private int getItem(int position) {
        if (textArray == null || position < 0 || position >= textArray.length)
            return 0;
        return textArray[position];
    }

    private void setViewVisibility(View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }


    public void dismiss() {
        if (mWindow != null && mWindow.isShowing()) mWindow.dismiss();
    }

    /**
     * 选取叠图显示
     *
     * @param color
     * @return
     */
    private int selectColor(String color) {
        return R.mipmap.ic_launcher;
    }

}