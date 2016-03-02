package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.common.ui.fragment.BaseWorkerFragment;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.ChatMessage;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.push.DbHelper;
import cn.protector.push.PushMessageReceiver;
import cn.protector.ui.activity.usercenter.BabyInfoActivity;
import cn.protector.ui.adapter.MessageAdapter;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.ui.widget.pulltorefresh.XListView;

/**
 * 描述：消息页面数据适配器
 *
 * @author Created by jakechen on 2015/8/13.
 */
public class MessageFragment extends BaseWorkerFragment implements View.OnClickListener {
  private static final int MSG_UI_START = 0;

  private static final int MSG_UI_LOAD_MESSAGE_SUCCESS = MSG_UI_START + 1;

  private static final int MSG_BACK_START = 100;

  private static final int MSG_BACK_LOAD_MESSAGE = MSG_BACK_START + 1;

  private MainTitleHelper mTitleHelper;

  public static MessageFragment newInstance() {
    return new MessageFragment();
  }

  private XListView mLvMessage;

  private MessageAdapter mMessageAdapter;
  private TextView tvNoData;
  private boolean isFistLoadData = true;

  @Override
  public void initView() {
    setContentView(R.layout.fragment_message);
    tvNoData = (TextView) findViewById(R.id.tv_no_data);
    mLvMessage = (XListView) findViewById(R.id.lv_message);
    mLvMessage.setPullLoadEnable(false);
    mTitleHelper = new MainTitleHelper(findViewById(R.id.fl_title), MainTitleHelper.STYLE_MESSAGE);
    showMessageList(false);
    mLvMessage.setXListViewListener(new XListView.IXListViewListener() {
      @Override
      public void onRefresh() {
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_MESSAGE);
      }

      @Override
      public void onLoadMore() {
      }
    });
  }

  private void showMessageList(boolean isShow) {
    tvNoData.setVisibility(isShow ? View.GONE : View.VISIBLE);
    mLvMessage.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }

  @Override
  protected void initData() {
    mMessageAdapter = new MessageAdapter(getActivity());
    mLvMessage.setAdapter(mMessageAdapter);
    sendEmptyBackgroundMessage(MSG_BACK_LOAD_MESSAGE);
  }

  @Override
  public void setupBroadcastActions(List<String> actions) {
    super.setupBroadcastActions(actions);
    actions.add(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
    actions.add(BroadcastActions.ACTION_PUSH_COMMON_MESSAGE);
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
    } else if (TextUtils.equals(action, BroadcastActions.ACTION_PUSH_COMMON_MESSAGE)) {
      ChatMessage message = (ChatMessage) intent.getSerializableExtra(PushMessageReceiver.KEY_COMMON_MESSAGE);
      if (message != null) {
        mMessageAdapter.addToLast(message);
        mMessageAdapter.notifyDataSetChanged();
      }
      if (getActivity() != null) {
        TextView textView = (TextView) getActivity().findViewById(R.id.tv_message_num);
        if (textView != null) {
          int num = 1;
          try {
            num = Integer.valueOf(textView.getText().toString()) + 1;
          } catch (Exception e) {
          }
          textView.setText("" + num);
          textView.setVisibility(View.VISIBLE);
        }
      }
    }
  }


  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_LOAD_MESSAGE:
        if (mMessageAdapter.getCount() > 0) {
          ChatMessage chatMessage = mMessageAdapter.getDataList().get(mMessageAdapter.getCount() - 1);
          mMessageAdapter.addToTop(DbHelper.getInstance().queryMessage(chatMessage.getTime()));
        } else {
          mMessageAdapter.addToTop(DbHelper.getInstance().queryMessage(0));
        }
        sendEmptyUiMessageDelayed(MSG_UI_LOAD_MESSAGE_SUCCESS, 800);
        break;
    }

  }

  @Override
  public void handleUiMessage(Message msg) {
    super.handleUiMessage(msg);
    switch (msg.what) {
      case MSG_UI_LOAD_MESSAGE_SUCCESS:
        mMessageAdapter.notifyDataSetChanged();
        if (isFistLoadData && mMessageAdapter.getCount() > 0) {
          isFistLoadData = false;
        } else {
          mLvMessage.setSelectionFromTop(mMessageAdapter.getCount() - 1, mLvMessage.getRefreshHeight());
        }
        mLvMessage.stopRefresh();
        mLvMessage.stopLoadMore();
        mLvMessage.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
        break;
    }
  }


  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.ll_baby_info) {
      goActivity(BabyInfoActivity.class);
    }
  }


  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (!isVisibleToUser) {
      if (mMessageAdapter != null && mMessageAdapter.getCount() > 0) {
        ChatMessage chatMessage = mMessageAdapter.getDataList().get(mMessageAdapter.getCount() - 1);
        if (chatMessage != null) {
          InitSharedData.setMessageTime(chatMessage.getTime());
        }
        mMessageAdapter.notifyDataSetChanged();
        if (getActivity() != null) {
          TextView textView = (TextView) getActivity().findViewById(R.id.tv_message_num);
          if (textView != null) {
            textView.setText("1");
            textView.setVisibility(View.GONE);
          }
        }
      }

    }
  }
}
