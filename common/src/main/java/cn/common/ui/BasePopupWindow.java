package cn.common.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * 描述：PopupWindow的父类Created by Administrator on 2015/11/13.
 */
public class BasePopupWindow extends PopupWindow {
    private Context mContext;

    public BasePopupWindow(Context context) {
        super(context);
        mContext = context;
    }

    public void onCreate() {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(true); // 如果把焦点设置为false，则其他部份是可以点击的，也就是说传递事件时，不会先走PopupWindow
    }

    public void setContentView(int contentView) {
        setContentView(View.inflate(mContext, contentView, null));
    }

    public View findViewById(int id) {
        return getContentView() != null ? getContentView().findViewById(id) : null;
    }
}
