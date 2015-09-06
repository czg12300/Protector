package cn.protector.ui.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 描述：用于健康页面的listview
 *
 * @author Created by jakechen on 2015/9/5.
 */
public class HealthListView extends BasePullToRefreshListView {
    public HealthListView(Context context) {
        super(context);
    }

    public HealthListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void handleOnTouchEvent(MotionEvent ev, int scrollStatus, int location) {

    }
}
