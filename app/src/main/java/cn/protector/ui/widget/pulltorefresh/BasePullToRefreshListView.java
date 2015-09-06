package cn.protector.ui.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 描述：上拉刷新，下拉加载的listView的基类
 *
 * @author Created by jake chen on 2015/9/5.
 */
public abstract class BasePullToRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private float mLastY;
    protected static final int SCROLL_STATUS_UP = 0;
    protected static final int SCROLL_STATUS_DOWN = 1;
    protected static final int SCROLL_STATUS_OTHER = 2;
    protected static final int LOCATION_TOP = 2;
    protected static final int LOCATION_BOTTOM = 3;
    protected static final int LOCATION_CENTER = 4;
    private int mScrollStatus = SCROLL_STATUS_OTHER;

    private int mLocation = LOCATION_CENTER;

    public BasePullToRefreshListView(Context context) {
        super(context);
    }

    public BasePullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float y = ev.getY();
                if (mLastY > y) {
                    //上滑
                    mScrollStatus = SCROLL_STATUS_UP;
                } else if (mLastY < y) {
                    //下滑
                    mScrollStatus = SCROLL_STATUS_DOWN;
                } else {
                    mScrollStatus = SCROLL_STATUS_OTHER;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        handleOnTouchEvent(ev,mScrollStatus, mLocation);
        return super.onTouchEvent(ev);
    }

    protected abstract void handleOnTouchEvent(MotionEvent ev,int scrollStatus, int location);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            //已经停止
            case OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getFirstVisiblePosition() == 0) {
                    mLocation = LOCATION_TOP;
                } else if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    mLocation = LOCATION_BOTTOM;
                } else {
                    mLocation = LOCATION_CENTER;
                }
                break;
            //开始滚动
            case OnScrollListener.SCROLL_STATE_FLING:
                break;
            //正在滚动
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
