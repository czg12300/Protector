package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.ui.widgt.PullDragHelper;
import cn.common.ui.widgt.PullEnableListView;
import cn.common.ui.widgt.PullListener;
import cn.common.ui.widgt.PullToRefreshLayout;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.ChatMessage;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.push.DbHelper;
import cn.protector.push.PushMessageReceiver;
import cn.protector.ui.adapter.MessageAdapter;
import cn.protector.ui.helper.MainTitleHelper;

/**
 * 描述：消息页面数据适配器
 *
 * @author Created by jakechen on 2015/8/13.
 */
public class MessageFragment extends BaseWorkerFragment {
  private static final int MSG_UI_START = 0;

  private static final int MSG_UI_LOAD_MESSAGE_SUCCESS = 1;

  private static final int MSG_BACK_START = 100;

  private static final int MSG_BACK_LOAD_MESSAGE = MSG_BACK_START + 1;

  private MainTitleHelper mTitleHelper;
  private PullToRefreshLayout pullLayout;

  public static MessageFragment newInstance() {
    return new MessageFragment();
  }

  private PullEnableListView mLvMessage;

  private MessageAdapter mMessageAdapter;
  private TextView tvNoData;

  @Override
  public void initView() {
    setContentView(R.layout.fragment_message);
    tvNoData = (TextView) findViewById(R.id.tv_no_data);
    mLvMessage = new PullEnableListView(getActivity());
    mLvMessage.setDividerHeight(0);
    mLvMessage.setCanScrollUp(false);
    pullLayout = (PullToRefreshLayout) findViewById(R.id.pull);
    pullLayout.setContentView(mLvMessage);
    mTitleHelper = new MainTitleHelper(findViewById(R.id.fl_title), MainTitleHelper.STYLE_MESSAGE);
    showMessageList(false);
    pullLayout.setPullListener(new PullListener() {
      @Override
      public void onLoadMore(PullDragHelper pullDragHelper) {
      }

      @Override
      public void onRefresh(PullDragHelper pullDragHelper) {
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_MESSAGE);
      }
    });
    setRedDot();
  }

  private void setRedDot() {
    int num = InitSharedData.getMessageNum();
    if (getActivity() != null) {
      TextView textView = (TextView) getActivity().findViewById(R.id.tv_message_num);
      if (num > 0) {
        textView.setText("" + num);
        textView.setVisibility(View.VISIBLE);
      } else {
        textView.setVisibility(View.GONE);
      }
    }
  }

  private void showMessageList(boolean isShow) {
    tvNoData.setVisibility(isShow ? View.GONE : View.VISIBLE);
    pullLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
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
        mLvMessage.setSelection(mMessageAdapter.getCount());
        showMessageList(true);
      }
      InitSharedData.setMessageNum(InitSharedData.getMessageNum() + 1);
      setRedDot();
    }
  }


  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_LOAD_MESSAGE:
        ArrayList<ChatMessage> list = null;
        if (mMessageAdapter.getCount() > 0) {
          ChatMessage chatMessage = mMessageAdapter.getDataList().get(0);
          list = DbHelper.getInstance().queryMessage(chatMessage.getTime());
          if (list != null && list.size() > 0) {
            list = changeDesc(list);
            if (list.size() < DbHelper.PAGE_SIZE) {
              mLvMessage.setCanScrollDown(false);
            }
          } else {
            mLvMessage.setCanScrollDown(false);
          }
        } else {
          list = DbHelper.getInstance().queryMessage(0);
          list = changeDesc(list);
          if (list.size() < DbHelper.PAGE_SIZE) {
            mLvMessage.setCanScrollDown(false);
          }
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_LOAD_MESSAGE_SUCCESS;
        message.obj = list;
        message.sendToTarget();
        break;
    }

  }

  private ArrayList<ChatMessage> changeDesc(ArrayList<ChatMessage> list) {
    if (list != null && list.size() > 0) {
      ArrayList<ChatMessage> result = new ArrayList<>();
      for (int i = list.size() - 1; i >= 0; i--) {
        result.add(list.get(i));
      }
      return result;
    }
    return list;
  }

  @Override
  public void handleUiMessage(Message msg) {
    super.handleUiMessage(msg);
    switch (msg.what) {
      case MSG_UI_LOAD_MESSAGE_SUCCESS:
        if (msg.obj != null) {
          if (mMessageAdapter.getCount() > 0) {
            ArrayList<ChatMessage> list = (ArrayList<ChatMessage>) msg.obj;
            if (list.size() > 0) {
              mMessageAdapter.addToTop(list);
              mMessageAdapter.notifyDataSetChanged();
              pullLayout.finishTask(true);
              mLvMessage.setSelection(list.size());
            } else {
              pullLayout.finishTask(false);
            }
          } else {
            ArrayList<ChatMessage> list = (ArrayList<ChatMessage>) msg.obj;
            if (list.size() > 0) {
              mMessageAdapter.setData(list);
              mMessageAdapter.notifyDataSetChanged();
              showMessageList(true);
            }
          }
        } else {
          if (mMessageAdapter.getCount() > 0) {
            pullLayout.finishTask(false);
          }
        }
        break;
    }
  }


  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (!isVisibleToUser) {
      hideRedDot();

    }
  }

  private void hideRedDot() {
    if (mMessageAdapter != null && mMessageAdapter.getCount() > 0) {
      ChatMessage chatMessage = mMessageAdapter.getDataList().get(mMessageAdapter.getCount() - 1);
      if (chatMessage != null && InitSharedData.getMessageTime() < chatMessage.getTime()) {
        InitSharedData.setMessageTime(chatMessage.getTime());
        mMessageAdapter.notifyDataSetChanged();
        InitSharedData.setMessageNum(0);
        setRedDot();
      }

    }
  }

  @Override
  public void onPause() {
    super.onPause();
    hideRedDot();
  }
}
