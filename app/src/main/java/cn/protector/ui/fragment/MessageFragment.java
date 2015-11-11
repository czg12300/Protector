
package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.common.ui.fragment.BaseWorkerFragment;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.entity.ChatMessage;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.ui.activity.usercenter.BabyInfoActivity;
import cn.protector.ui.adapter.MessageAdapter;
import cn.protector.ui.helper.MainTitleHelper;

/**
 * 描述：消息页面数据适配器
 *
 * @author Created by jakechen on 2015/8/13.
 */
public class MessageFragment extends BaseWorkerFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final int MSG_UI_START = 0;

    private static final int MSG_UI_LOAD_MESSAGE_SUCCESS = MSG_UI_START + 1;

    private static final int MSG_BACK_START = 100;

    private static final int MSG_BACK_LOAD_MESSAGE = MSG_BACK_START + 1;

    private MainTitleHelper mTitleHelper;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    private ListView mLvMessage;

    private MessageAdapter mMessageAdapter;

    private TextView mTvDate;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_message);
        mLvMessage = (ListView) findViewById(R.id.lv_message);
        View header = inflate(R.layout.header_message);
        mLvMessage.addHeaderView(header);
        mTvDate = (TextView) header.findViewById(R.id.tv_date);
        mTitleHelper = new MainTitleHelper(findViewById(R.id.fl_title),
                MainTitleHelper.STYLE_MESSAGE);
    }

    private void setDate(String date) {
        if (mTvDate != null && !TextUtils.isEmpty(date)) {
            mTvDate.setText(date);
        }
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
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_MAIN_DEVICE_CHANGE)) {
            DeviceInfo info = DeviceInfoHelper.getInstance().getPositionDeviceInfo();
            if (info!=null&&!TextUtils.isEmpty(info.getNikeName())){
                mTitleHelper.setTitle(info.getNikeName());
            }
        }
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_LOAD_MESSAGE:
                sendEmptyUiMessageDelayed(MSG_UI_LOAD_MESSAGE_SUCCESS, 1000);
                break;
        }

    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_LOAD_MESSAGE_SUCCESS:
                mMessageAdapter.setData(getTestList());
                break;
        }
    }

    private List<ChatMessage> getTestList() {
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        for (int i = 0; i < 15; i++) {
            ChatMessage message = new ChatMessage();
            if (i % 5 == 0) {
                message.type = ChatMessage.TYPE_VOICE;
                message.name = "小妮儿";
                message.message = "录音 15’";
            } else {
                message.name = "大宝儿";
                message.type = ChatMessage.TYPE_MESSAGE;
                message.message = "我的鞋子湿了，记得帮我烘干哦";
            }
            if (i < 10) {
                message.time = "9:0" + i;
            } else {
                message.time = "9:" + i;
            }
            list.add(message);
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_baby_info) {
            goActivity(BabyInfoActivity.class);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO
    }

}
