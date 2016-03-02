package cn.protector.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.protector.utils.ToastUtil;

/**
 * 描述:web页面
 *
 * @author jakechen
 * @since 2016/3/2 11:16
 */
public class WebActivity extends CommonTitleActivity {
  public static final String KEY_TITLE = "key_title";
  public static final String KEY_URL = "key_url";
  private WebView webView;

  public static void start(Context context, Bundle bundle) {
    if (context != null && context instanceof Activity) {
      Intent it = new Intent(context, WebActivity.class);
      if (bundle != null) {
        it.putExtras(bundle);
      }
      context.startActivity(it);
    }
  }

  @Override
  protected void initView() {
    String title = "热门活动";
    String url = "";
    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(KEY_TITLE)) {
      title = bundle.getString(KEY_TITLE);
      url = bundle.getString(KEY_URL);
    }
    if (TextUtils.isEmpty(url)) {
      ToastUtil.show("初始化失败,请点击重试");
      finish();
    } else {
      if (!url.startsWith("http")) {
        url = "http://" + url;
      }

    }
    setTitle(title);
    webView = createWebView(this);
    setContentView(webView);
    webView.loadUrl(url);
  }

  private WebView createWebView(Context context) {
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

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (webView.canGoBack()) {
        webView.goBack();
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }
}
