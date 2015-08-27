
package cn.common.ui.widgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.common.R;

/**
 * 描述:自定义圆形的ImageView,可设置边缘宽度和颜色。边缘宽度和颜色属性在attrs.xml有定义
 * 
 * @author liux
 * @since 2013-10-11 下午05:26:48
 */

public class RoundImageView extends ImageView {
    private int mBorderThickness = 1;

    private Context mContext;

    private int mBorderColor = 0xFFFFFFFF;

    public RoundImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.roundImageView);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.roundImageView_game_border_thickness,
                0);
        mBorderColor = a.getColor(R.styleable.roundImageView_game_border_color, mBorderColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable instanceof NinePatchDrawable) {
            return;
        }
        if (drawable instanceof BitmapDrawable) {
            Bitmap bCopy = ((BitmapDrawable) drawable).getBitmap();
            if (bCopy == null || bCopy.isRecycled()) {
                return;
            }
            int radius = (getWidth() < getHeight() ? getWidth() : getHeight()) / 2
                    - mBorderThickness;
            try {
                bCopy = getCroppedBitmap(bCopy, radius);
                final Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);
                paint.setColor(mBorderColor);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius + mBorderThickness,
                        paint);
                canvas.drawBitmap(bCopy, getWidth() / 2 - radius, getHeight() / 2 - radius, null);
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置裁剪的Bitmap
     * 
     * @param bmp 需要裁剪的Bitmap
     * @param radius 裁剪成圆形的半径
     * @return
     */
    private Bitmap getCroppedBitmap(Bitmap bmp, int radius) throws OutOfMemoryError {
        int diameter = radius * 2;
        if (bmp.getWidth() != diameter || bmp.getHeight() != diameter) {
            bmp = Bitmap.createScaledBitmap(bmp, diameter, diameter, false);
        }
        Bitmap output = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawCircle(bmp.getWidth() / 2, bmp.getHeight() / 2, bmp.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bmp, rect, rect, paint);
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }
        return output;
    }
}
