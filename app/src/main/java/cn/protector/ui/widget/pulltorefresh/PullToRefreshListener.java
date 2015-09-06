package cn.protector.ui.widget.pulltorefresh;

/**
 * 描述：上拉刷新、下拉加载
 *
 * @author Created by jakechen on 2015/9/5.
 */
public interface PullToRefreshListener {
    void onRefresh();

    void onLoadMore();
}
