package cn.protector.ui.activity.setting;

import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import cn.common.AppException;
import cn.common.ui.BaseDialog;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.logic.http.response.ModeStateResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.utils.ToastUtil;

/**
 * 描述：定位模式页面
 *
 * @author jakechen
 */
public class LocateModeActivity extends CommonTitleActivity implements View.OnClickListener {
    private static final int MSG_UI_LOAD_DATA = 0;
    private static final int MSG_UI_SWITCH_MODE = 1;
    private static final int MSG_UI_CANCEL_SWITCH_MODE = 2;


    private static final int MSG_BACK_LOAD_DATA = 0;
    private static final int MSG_BACK_SWITCH_MODE = 1;
    private static final int MSG_BACK_CANCEL_SWITCH_MODE = 2;
    private static final String CANCEL = "取消";
    private static final String SWITCH = "切换";
    private RotateAnimation reFreshAnimation;
    private ImageView ivRefresh;
    private RadioGroup radioGroup;
    private RadioButton rbSave;
    private RadioButton rbNormal;
    private RadioButton rbFollow;
    private String mode;
    private Button btnSwitch;
    private TextView tvMode;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_locate_mode);
        setTitle(R.string.title_locate_mode);
        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        radioGroup = (RadioGroup) findViewById(R.id.rg_mode);
        btnSwitch = (Button) findViewById(R.id.btn_switch);
        tvMode = (TextView) findViewById(R.id.tv_mode);
        rbSave = (RadioButton) findViewById(R.id.rb_save);
        rbNormal = (RadioButton) findViewById(R.id.rb_normal);
        rbFollow = (RadioButton) findViewById(R.id.rb_follow);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_follow) {
                    mode = "5";
                } else if (checkedId == R.id.rb_save) {
                    mode = "3";
                } else if (checkedId == R.id.rb_normal) {
                    mode = "4";
                }
            }
        });
        btnSwitch.setOnClickListener(this);
        ivRefresh.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        refresh();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_refresh) {
            refresh();
        } else if (v.getId() == R.id.btn_switch) {
            RadioButton b=(RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
            if (!TextUtils.equals(b.getText(),tvMode.getText())){
            if (TextUtils.equals(btnSwitch.getText(), CANCEL)) {
                cancelMode();
            } else {
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_save) {
                    showDialog();
                } else {
                    switchMode();
                }
            }}else{
                ToastUtil.show("你还没选择想要切换的模式呢");
            }
        }
    }

    private void cancelMode() {
        btnSwitch.setText("正在取消...");
        btnSwitch.setEnabled(false);
        sendEmptyBackgroundMessage(MSG_BACK_CANCEL_SWITCH_MODE);
    }

    private void switchMode() {
        btnSwitch.setText("切换中...");
        btnSwitch.setEnabled(false);
        setRadioEnable(false);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_follow) {
            rbFollow.setText(Html.fromHtml("跟踪模式  <font color=\"#ff5b5b\">    切换中</font>"));
        } else if (checkedId == R.id.rb_save) {
            rbSave.setText(Html.fromHtml("省电模式  <font color=\"#ff5b5b\">     切换中</font>"));
        } else if (checkedId == R.id.rb_normal) {
            rbNormal.setText(Html.fromHtml("正常模式  <font color=\"#ff5b5b\">     切换中</font>"));
        }
        sendEmptyBackgroundMessage(MSG_BACK_SWITCH_MODE);
    }

    private void resetModeSelected() {
        radioGroup.setEnabled(true);
        rbFollow.setText("跟踪模式");
        rbSave.setText("省电模式");
        rbNormal.setText("正常模式");
    }

    private void refresh() {
        showLoad();
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_LOAD_DATA:
                loadDataTask();
                break;
            case MSG_BACK_SWITCH_MODE:
                switchModeTask();
                break;
            case MSG_BACK_CANCEL_SWITCH_MODE:
                cancelSwitchModeTask();
                break;
        }
    }

    private void loadDataTask() {
        HttpRequest<ModeStateResponse> sportRequest = new HttpRequest<>(AppConfig.COM_GETEQUIPMENTSTATE, ModeStateResponse.class);
        sportRequest.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            sportRequest.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message msg = obtainUiMessage();
        msg.what = MSG_UI_LOAD_DATA;
        try {
            msg.obj = sportRequest.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        msg.sendToTarget();
    }

    private void switchModeTask() {
        HttpRequest<CommonResponse> sportRequest = new HttpRequest<>(AppConfig.COM_SETUPLOADMODE, CommonResponse.class);
        sportRequest.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            sportRequest.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        sportRequest.addParam("mode", mode);
        Message msg = obtainUiMessage();
        msg.what = MSG_UI_SWITCH_MODE;
        try {
            msg.obj = sportRequest.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        msg.sendToTarget();
    }

    private void showDialog() {
        if (!isFinishing()) {
            final BaseDialog dialog = new BaseDialog(this);
            dialog.setWindow(R.style.alpha_animation, 0.3f);
            dialog.setContentView(R.layout.dialog_delete_care_staff);
            TextView text = (TextView) dialog.findViewById(R.id.tv_title);
            text.setText("省电模式下，设备每2小时唤醒一次，其余时间均在休眠状态。休眠期间实时定位、远程监听、状态切换等功能不能使用。是否确认切换至省电模式？");
            dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchMode();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void cancelSwitchModeTask() {
        HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.CANCEL_UPLOADMODE, CommonResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message msg = obtainUiMessage();
        msg.what = MSG_UI_CANCEL_SWITCH_MODE;
        try {
            msg.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        msg.sendToTarget();
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_LOAD_DATA:
                handleLoadData(msg.obj);
                break;
            case MSG_UI_SWITCH_MODE:
                handleSwitchMode(msg.obj);
                break;
            case MSG_UI_CANCEL_SWITCH_MODE:
                handleCancelSwitchMode(msg.obj);
                break;
        }
    }

    private void handleLoadData(Object obj) {
        hideLoad();
        resetModeSelected();
        if (obj != null) {
            ModeStateResponse response = (ModeStateResponse) obj;
            switch (response.getCurrentState()) {
                case ModeStateResponse.MODE_SAVE:
                    radioGroup.check(R.id.rb_save);
                    tvMode.setText("省电模式");
                    break;
                case ModeStateResponse.MODE_NORMAL:
                    tvMode.setText("正常模式");
                    radioGroup.check(R.id.rb_normal);
                    break;
                case ModeStateResponse.MODE_FOLLOW:
                    tvMode.setText("跟踪模式");
                    radioGroup.check(R.id.rb_follow);
                    break;
            }
            if (response.getSwitchState() > 0) {
                switch (response.getSwitchState()) {
                    case ModeStateResponse.MODE_SAVE:
                        radioGroup.check(R.id.rb_save);
                        rbSave.setText(Html.fromHtml("省电模式  <font color=\"#ff5b5b\">     切换中</font>"));
                        break;
                    case ModeStateResponse.MODE_NORMAL:
                        rbNormal.setText(Html.fromHtml("正常模式  <font color=\"#ff5b5b\">     切换中</font>"));
                        radioGroup.check(R.id.rb_normal);
                        break;
                    case ModeStateResponse.MODE_FOLLOW:
                        rbFollow.setText(Html.fromHtml("跟踪模式  <font color=\"#ff5b5b\">    切换中</font>"));
                        radioGroup.check(R.id.rb_follow);
                        break;
                }
                setRadioEnable(false);
                btnSwitch.setText(CANCEL);
            } else {
                setRadioEnable(true);
                btnSwitch.setText(SWITCH);
            }
        } else {
            ToastUtil.showError();
            if (TextUtils.equals(tvMode.getText(), getString(R.string.loading_position_mode))) {
                tvMode.setText("加载失败，点击右侧按钮重试");
            }
        }
    }

    private void setRadioEnable(boolean enable) {
       rbFollow.setEnabled(enable);
       rbSave.setEnabled(enable);
       rbNormal.setEnabled(enable);
    }

    private void handleSwitchMode(Object obj) {
        btnSwitch.setEnabled(true);
        if (obj != null) {
            CommonResponse response = (CommonResponse) obj;
            if (response.getResult() > 0) {
                refresh();
            } else {
                setRadioEnable(true);
                if (TextUtils.isEmpty(response.getInfo())) {
                    ToastUtil.showError();
                } else {
                    ToastUtil.show(response.getInfo());
                }
            }
        } else {
            ToastUtil.showError();
            setRadioEnable(true);
        }
    }

    private void handleCancelSwitchMode(Object obj) {
        btnSwitch.setEnabled(true);
        if (obj != null) {
            CommonResponse response = (CommonResponse) obj;
            if (response.getResult() > 0) {
                refresh();
            } else {
                btnSwitch.setText(CANCEL);
                if (TextUtils.isEmpty(response.getInfo())) {
                    ToastUtil.showError();
                } else {
                    ToastUtil.show(response.getInfo());
                }
            }
        } else {
            btnSwitch.setText(CANCEL);
            ToastUtil.showError();
        }
    }


    private void showLoad() {
        if (reFreshAnimation == null) {
            reFreshAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            reFreshAnimation.setDuration(800);
            reFreshAnimation.setInterpolator(new LinearInterpolator());
        }
        reFreshAnimation.setRepeatCount(-1);
        ivRefresh.clearAnimation();
        ivRefresh.startAnimation(reFreshAnimation);
    }

    private void hideLoad() {
        if (reFreshAnimation != null) {
            reFreshAnimation.setRepeatCount(0);
        }
    }


}
