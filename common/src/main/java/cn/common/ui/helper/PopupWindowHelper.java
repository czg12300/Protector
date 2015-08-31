
package cn.common.ui.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.PopupWindow;

/**
 * 创建popupwindow
 * 
 * @author jake
 */
public class PopupWindowHelper {
    Context mContext;

    private PopupWindow pw;

    public PopupWindowHelper(Context context) {
        this(context, 0);
    }

    public PopupWindowHelper(Context context, int style) {
        this(context, null, style);
    }

    public PopupWindowHelper(Context context, View popupView, int stype) {
        this.mContext = context;
        pw = new PopupWindow(context, null, stype);
        setView(popupView);
    }

    /**
     * 设置view
     * 
     * @param popupView
     */
    public void setView(View popupView) {
        if (popupView == null) {
            return;
        }
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        popupView.measure(width, height);
        height = popupView.getMeasuredHeight();
        width = popupView.getMeasuredWidth();
        pw.setHeight(height);
        pw.setWidth(width);
        pw.setContentView(popupView);
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
    }

    public void setStyle(int style) {
        pw.setAnimationStyle(style);
    }

    /**
     * 设置view
     * 
     * @param popupView
     */
    public void setView(View popupView, int width, int height) {
        setView(popupView, width, height, new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 设置view
     * 
     * @param popupView
     */
    public void setView(View popupView, int width, int height, Drawable drawable) {
        if (popupView == null) {
            return;
        }
        popupView.measure(width, height);
        pw.setHeight(height);
        pw.setWidth(width);
        pw.setContentView(popupView);
        pw.setBackgroundDrawable(drawable);
        setOutsideTouchable(true);
    }

    /**
     * 设置是否点击外部消失
     * 
     * @param bool
     */
    public void setOutsideTouchable(boolean bool) {
        pw.setOutsideTouchable(bool);
    }

    public void showAsDropDown(View view) {
        pw.showAsDropDown(view);
        pw.setFocusable(true);
    }

    public void showAsDropDown(View view, int xoff, int yoff) {
        pw.showAsDropDown(view, xoff, yoff);
        pw.setFocusable(true);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        pw.showAtLocation(parent, gravity, x, y);
        pw.setFocusable(true);
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        pw.dismiss();
        pw.setFocusable(false);
    }

    public void setAnimationStyle(int animationStyle) {
        if (pw != null) {
            pw.setAnimationStyle(animationStyle);
        }
    }

    /**
     * 是否显示
     * 
     * @return
     */
    public boolean isShowing() {
        return pw.isShowing();
    }

    public void setBackground(Drawable drawable) {
        pw.setBackgroundDrawable(drawable);
    }
}
