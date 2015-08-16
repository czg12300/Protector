
package cn.common.ui.widgt;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RadioButton;

import cn.common.R;
import cn.common.utils.BitmapUtil;
import cn.common.utils.DisplayUtil;

/**
 * 描述:带消息提醒的RadioButton
 *
 * @author chenys
 * @since 2013-11-6 下午5:05:12
 */
public class CustomRadioButton extends RadioButton {

    private Paint paint;

    private Bitmap bitmap;

    private boolean firstCreate = true;

    private boolean showRedPoint;

    private int mDownloadingCount = 0;

    private int mNeedUpdateCount = 0;

    public CustomRadioButton(Context context) {
        this(context, null);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
        bitmap = BitmapUtil.decodeResource(R.drawable.img_shadow_bottom);
    }

    private void setupPaint() {
        paint = new Paint();
        // 设置抗锯齿效果
        paint.setAntiAlias(true);
        // 设置画刷的颜色
        paint.setColor(Color.RED);
    }

    Rect t = new Rect();

    int height;

    int width;

    DisplayMetrics dm = getResources().getDisplayMetrics();

    float value = dm.scaledDensity;

    int textSize = 8;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setTextSize(textSize * value);
        if (firstCreate) {
            canvas.getClipBounds(t);
            firstCreate = false;
            height = getHeight();
            width = getWidth();
        }
        int totalCount = mDownloadingCount + mNeedUpdateCount;
        if (totalCount > 0) {
            Drawable[] drawables = getCompoundDrawables();
            Rect rect = new Rect();
            for (int i = 0; i < drawables.length; i++) {
                if (drawables[i] != null) {
                    rect = drawables[i].getBounds();
                    break;
                }
            }
            int offset = (getWidth() - rect.right) / 2 - 5;
            float paddingTop = 5;
            paint.setColor(Color.WHITE);
            canvas.drawBitmap(bitmap, t.right - offset, t.top + paddingTop, paint);
            String messageConut = String.valueOf(totalCount);
            int heightOffset = Math.abs((bitmap.getHeight() - getFontHeight(paint.getTextSize())));
            int wigthOffset = (bitmap.getWidth() - getStringWidth(paint, messageConut)) / 2;
            canvas.drawText(messageConut, t.right - offset + wigthOffset,
                    t.top + paddingTop + 1 + bitmap.getHeight() - heightOffset - 1, paint);
        }

        if (showRedPoint) {
            Rect rect = new Rect();
            Drawable[] drawables = getCompoundDrawables();
            for (int i = 0; i < drawables.length; i++) {
                if (drawables[i] != null) {
                    rect = drawables[i].getBounds();
                    break;
                }
            }
            int offset = (getWidth() - rect.right) / 2;
            // canvas.drawBitmap(bitmap, t.right - offset,
            // rect.top + GameUtil.dip2px(getContext(), 5), paint);

            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(t.right - offset, rect.top + DisplayUtil.dip(10),
                    DisplayUtil.dip(5), paint);

        }

    }

    public void setDownloadingCount(int count) {
        mDownloadingCount = count;
        invalidate();
    }

    public void setNeedUpdateCount(int count) {
        mNeedUpdateCount = count;
        invalidate();
    }

    private int getStringWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    public void setShowRedPoint(boolean showRedPoint) {
        this.showRedPoint = showRedPoint;
        invalidate();
    }

}
