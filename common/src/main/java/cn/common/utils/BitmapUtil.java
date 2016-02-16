package cn.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;

/**
 * Created by Administrator on 2015/8/16.
 */
public final class BitmapUtil {
    public static Bitmap decodeResource(int drawableId) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(DisplayUtil.getResources(), drawableId);
        } catch (OutOfMemoryError error) {
        }
        return bitmap;
    }

    public static Bitmap decodeResource(int drawableId, int width, int height) {
        return ThumbnailUtils.extractThumbnail(decodeResource(drawableId), width, height);
    }

    public static Bitmap scale(Bitmap b, int x, int y) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = (float)x /(float) w;
        float sy =(float) y /(float) h;
        float scale=Math.min(sy,sx);
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, false);
        return resizeBmp;
    }
}
