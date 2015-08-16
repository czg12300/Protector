package cn.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
}
