
package cn.protector.ui.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 描述：上拉刷新，下拉加载的listView的基类
 *
 * @author Created by jake chen on 2015/9/5.
 */
public abstract class BasePullToRefreshListView extends ListView {
    private float mLastY = -1;

    protected static final int SCROLL_STATUS_UP = 0;

    protected static final int SCROLL_STATUS_DOWN = 1;

    protected static final int SCROLL_STATUS_OTHER = 2;

    protected static final int LOCATION_TOP = 2;

    protected static final int LOCATION_BOTTOM = 3;

    protected static final int LOCATION_CENTER = 4;

    private int mScrollStatus = SCROLL_STATUS_OTHER;

    private int mLocation = LOCATION_CENTER;

    // support iOS like pull
    private final static float OFFSET_RADIO = 1.8f;

    /**
     * 判断是否回弹，在不执行刷新和加载的情况下
     */
    protected boolean mCanOverScroll = false;

    protected boolean canPullRefresh = false;

    protected boolean canPullLoad = false;

    public boolean isCanPullLoad() {
        return canPullLoad;
    }

    public void setCanPullLoad(boolean canPullLoad) {
        this.canPullLoad = canPullLoad;
    }

    public boolean isCanPullRefresh() {
        return canPullRefresh;
    }

    public void setCanPullRefresh(boolean canPullRefresh) {
        this.canPullRefresh = canPullRefresh;
    }

    public boolean isCanOverScroll() {
        return mCanOverScroll;
    }

    public void setCanOverScroll(boolean mCanOverScroll) {
        this.mCanOverScroll = mCanOverScroll;
    }

    public BasePullToRefreshListView(Context context) {
        super(context);
    }

    public BasePullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshListener getPullToRefreshListener() {
        return mPullToRefreshListener;
    }

    public void setPullToRefreshListener(PullToRefreshListener mPullToRefreshListener) {
        this.mPullToRefreshListener = mPullToRefreshListener;
    }

    protected PullToRefreshListener mPullToRefreshListener;

    protected boolean mPullRefreshing = false;

    private boolean mPullLoading = false;

    protected abstract boolean needUpdateRefreshView();

    protected abstract boolean needUpdateRefresh();

    protected abstract boolean needUpdateLoadView();

    protected abstract boolean needUpdateLoad();

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (needUpdateRefreshView() || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateRefreshView(deltaY / OFFSET_RADIO);

                } else if (getLastVisiblePosition() == getCount() - 1
                        && (needUpdateLoadView() || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateLoadView(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                // reset
                mLastY = -1;
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (canPullRefresh && needUpdateRefresh()) {
                        mPullRefreshing = true;
                        if (mPullToRefreshListener != null) {
                            mPullToRefreshListener.onRefresh();
                        }
                        resetRefreshViewHeight();
                    }

                } else if (canPullLoad && getLastVisiblePosition() == getCount() - 1
                        && needUpdateLoad()) {
                    mPullLoading = true;
                    if (mPullToRefreshListener != null) {
                        mPullToRefreshListener.onLoadMore();
                    }
                    resetLoadViewHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public abstract void resetLoadViewHeight();

    public abstract void resetRefreshViewHeight();

    protected abstract void updateLoadView(float instanceY);

    protected abstract void updateRefreshView(float instanceY);

}
