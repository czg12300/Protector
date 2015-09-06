
package cn.protector.ui.widget.pulltorefresh;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Scroller;

import cn.common.ui.widgt.ChangeThemeUtils;
import cn.protector.R;

/**
 * 描述：用于健康页面的listview
 *
 * @author Created by jakechen on 2015/9/5.
 */
public class HealthListView extends ListView implements AbsListView.OnScrollListener {
    private static final int SCROLL_BACK_HEADER = 0;

    private static final int SCROLL_BACK_FOOTER = 1;

    // support iOS like pull
    private final static float OFFSET_RADIO = 1.8f;

    private Scroller mScroller;

    private static final int DIRECTION_UP = 0;

    private static final int DIRECTION_DOWN = 1;

    private int scrollDirection = DIRECTION_UP;

    private int mScrollBack;

    /**
     * title上次设置的透明值
     */
    private int lastAlpha = 0;

    protected boolean canPullRefresh = false;

    protected boolean canPullLoad = false;

    protected boolean mPullRefreshing = false;

    private boolean mPullLoading = false;

    private boolean isChangeTitle = false;

    private float mLastY = -1;

    private PullToRefreshListener mPullToRefreshListener;

    private View mVTitle;

    private View mHeaderView;

    private View mFooterView;

    public HealthListView(Context context) {
        this(context, null);
    }

    public HealthListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context, new DecelerateInterpolator());
        setOnScrollListener(this);
    }

    @Override
    public void addHeaderView(View v) {
        mHeaderView = v.findViewById(R.id.ll_header);
        super.addHeaderView(v);
    }

    @Override
    public void addFooterView(View v) {
        super.addFooterView(v);
        mFooterView = v.findViewById(R.id.ll_footer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        isChangeTitle = true;
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                handleMove(ev);
                break;
            default:
                // reset
                handleReset();
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 重置状态
     */
    private void handleReset() {
        mLastY = -1;
        if (getFirstVisiblePosition() == 0) {
            // invoke refresh
            int paddingTop = (int) getResources().getDimension(R.dimen.title_height)
                    + ChangeThemeUtils.getStatusBarHeight(getContext());
            if (canPullRefresh && mHeaderView.getPaddingTop() > paddingTop) {
                mPullRefreshing = true;
                if (mPullToRefreshListener != null) {
                    mPullToRefreshListener.onRefresh();
                }
                mScrollBack = SCROLL_BACK_HEADER;
                mScroller.startScroll(0, mHeaderView.getPaddingTop(), 0,
                        paddingTop - mHeaderView.getPaddingTop(), 300);
            }

        } else if (getLastVisiblePosition() == getCount() - 1) {
            if (canPullLoad && mFooterView.getPaddingBottom() > 0) {
                mPullLoading = true;
                if (mPullToRefreshListener != null) {
                    mPullToRefreshListener.onLoadMore();
                }
                mScrollBack = SCROLL_BACK_FOOTER;
                int h = mFooterView.getPaddingBottom();
                mScroller.startScroll(0, mFooterView.getPaddingBottom(), 0,
                        -mFooterView.getPaddingBottom(), 300);
            }
        }
    }

    /**
     * 处理滑动事件
     * 
     * @param ev
     */
    private void handleMove(MotionEvent ev) {
        final float deltaY = ev.getRawY() - mLastY;
        mLastY = ev.getRawY();
        if (deltaY > 0) {
            scrollDirection = DIRECTION_DOWN;
        } else if (deltaY < 0) {
            scrollDirection = DIRECTION_UP;
        }
        if (getFirstVisiblePosition() == 0 && deltaY > 0 && canPullRefresh) {
            // the first item is showing, header has shown or pull down.
            int paddingTop = mHeaderView.getPaddingTop() + (int) (deltaY / OFFSET_RADIO);
            mHeaderView.setPadding(0, paddingTop, 0, 0);
        } else if (getLastVisiblePosition() == getCount() - 1 && deltaY < 0 && canPullLoad) {
            // last item, already pulled up or want to pull up.
            int paddingBottom = mFooterView.getPaddingBottom() + (int) (-deltaY / OFFSET_RADIO);
            mFooterView.setPadding(0, 0, 0, paddingBottom);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLL_BACK_HEADER) {
                mHeaderView.setPadding(0, mScroller.getCurrY(), 0, 0);
            } else if (mScrollBack == SCROLL_BACK_FOOTER) {
                mFooterView.setPadding(0, 0, 0, mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();

    }

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

    public void setPullToRefreshListener(PullToRefreshListener mPullToRefreshListener) {
        this.mPullToRefreshListener = mPullToRefreshListener;
    }

    public void setTitle(View vTitle) {
        mVTitle = vTitle;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (isChangeTitle) {
            Rect viewRect = new Rect();
            mHeaderView.getGlobalVisibleRect(viewRect);
            int height = mHeaderView.getMeasuredHeight();
            int pHeight = viewRect.bottom - viewRect.top;
            int sub = height - pHeight;
            int alpha = sub * 255 / mHeaderView.getMeasuredHeight();
            if (scrollDirection == DIRECTION_UP) {
                if (lastAlpha < alpha) {
                    mVTitle.getBackground().setAlpha(alpha);
                    lastAlpha = alpha;
                } else if (pHeight == height) {
                    mVTitle.getBackground().setAlpha(255);
                }
            } else if (scrollDirection == DIRECTION_DOWN) {
                if (pHeight < height) {
                    mVTitle.getBackground().setAlpha(alpha);
                    lastAlpha = alpha;
                }
            }
        }
    }
}
