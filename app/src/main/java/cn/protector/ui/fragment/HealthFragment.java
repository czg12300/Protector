
package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.ui.widgt.ChangeThemeUtils;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.ui.activity.usercenter.BabyInfoActivity;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.ui.widget.pulltorefresh.HealthListView;

/**
 * 描述：健康页面
 *
 * @author jakechen on 2015/8/13.
 */
public class HealthFragment extends BaseWorkerFragment implements View.OnClickListener {

    public static HealthFragment newInstance() {
        return new HealthFragment();
    }

    private HealthListView mLvActivity;

    private View mVTitle;

    private MainTitleHelper mTitleHelper;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_health);
        mVTitle = findViewById(R.id.fl_title);
        mLvActivity = (HealthListView) findViewById(R.id.lv_activity);
        mLvActivity.setTitle(mVTitle);
        mTitleHelper = new MainTitleHelper(mVTitle, MainTitleHelper.STYLE_HEALTH);
        initHeader(inflate(R.layout.header_health));
        mVTitle.getBackground().setAlpha(0);
    }

    private void initHeader(View header) {
        View headerTop = header.findViewById(R.id.ll_header);
        int paddingTop = (int) getDimension(R.dimen.title_height)
                + ChangeThemeUtils.getStatusBarHeight(getActivity());
        headerTop.setPadding(0, paddingTop, 0, 0);
        mLvActivity.addHeaderView(header);
        mLvActivity.addFooterView(inflate(R.layout.footer_load));
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_MAIN_DEVICE_CHANGE)) {
            DeviceInfo info = DeviceInfoHelper.getInstance().getPositionDeviceInfo();
            if (info != null && !TextUtils.isEmpty(info.getNikeName())) {
                mTitleHelper.setTitle(info.getNikeName());
            }
        }
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("http://www.baidu.com/");
        mLvActivity.setAdapter(new SettingAdapter(getActivity(), list));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_baby_info) {
            goActivity(BabyInfoActivity.class);
        }
    }

    /**
     * grid view 的适配器
     */
    class SettingAdapter extends BaseListAdapter<String> {
        public SettingAdapter(Context context, List<String> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WebView webView;
            if (convertView == null) {
                webView = createWebView(getContext());
                convertView = webView;
            } else {
                webView = (WebView) convertView;
            }
            webView.loadUrl(mDataList.get(position));
            return convertView;
        }
        private  WebView createWebView(Context context) {
            WebView webView = new WebView(context);
            webView.requestFocus();
            webView.requestFocusFromTouch();
            webView.setBackgroundColor(Color.WHITE);
            webView.setWebViewClient(new WebViewClient());
            webView.setDrawingCacheEnabled(false);
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setDefaultTextEncodingName(Xml.Encoding.UTF_8.toString());
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setAppCacheEnabled(false);
            return webView;
        }

    }
}
