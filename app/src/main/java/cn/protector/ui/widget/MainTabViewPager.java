
package cn.protector.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MainTabViewPager extends ViewPager {
    private boolean canScroll = false;

    private boolean canScrollLeft = true;

    private boolean canScrollRight = true;

    public boolean isCanScrollLeft() {
        return canScrollLeft;
    }

    public void setCanScrollLeft(boolean canScrollLeft) {
        this.canScrollLeft = canScrollLeft;
    }

    public boolean isCanScrollRight() {
        return canScrollRight;
    }

    public void setCanScrollRight(boolean canScrollRight) {
        this.canScrollRight = canScrollRight;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public MainTabViewPager(Context context) {
        super(context);
    }

    public MainTabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!canScroll) {
            return false;
        } else {
            if (canInvideScroll(event)) {
                return super.onInterceptTouchEvent(event);
            }

        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canScroll) {
            return false;
        } else {
            if (canInvideScroll(ev)) {
                return super.onTouchEvent(ev);
            }
        }
        return false;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (!canScroll) {
            return true;
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }

    }

    private float xDistance, yDistance, xLast, yLast;

    private boolean canInvideScroll(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                yLast = curY;
                if (xDistance > yDistance) {
                    if (curX - xLast > 0) {
                        xLast = curX;
                        if (canScrollLeft) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        xLast = curX;
                        if (canScrollRight) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        }
        return true;
    }
}
