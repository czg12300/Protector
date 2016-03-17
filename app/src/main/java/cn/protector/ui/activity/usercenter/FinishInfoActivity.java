
package cn.protector.ui.activity.usercenter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import cn.common.AppException;
import cn.common.bitmap.core.ImageLoader;
import cn.common.ui.BaseDialog;
import cn.common.ui.widgt.RoundImageView;
import cn.common.utils.CommonUtil;
import cn.common.utils.UploadUtil;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.logic.http.response.GetBaseListResponse;
import cn.protector.logic.http.response.UpdateImageResponse;
import cn.protector.logic.http.response.WearInfoResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.MainActivity;
import cn.protector.ui.adapter.RelationshipAdapter;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.ui.widget.ImageEditText;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * 完善信息页面
 *
 * @auther Created by jakechen on 2015/8/27.
 */
public class FinishInfoActivity extends CommonTitleActivity implements View.OnClickListener {
    public static final String KEY_TYPE = "key_type";

    public static final String KEY_EID = "key_code";

    public static final String MAN = "男";

    public static final String WOMAN = "女";

    public static final int TYPE_FINISH = 0;

    public static final int TYPE_MODIFY = 1;

    public static final int TYPE_BABY_INFO = 2;

    private static final int MSG_BACK_SUBMIT = 1;

    private static final int MSG_BACK_UPLOAD = 2;

    private static final int MSG_BACK_GET_DEVICE_LIST = 3;

    private static final int MSG_BACK_LOAD_DATA = 0;

    private static final int MSG_UI_SUBMIT = 1;

    private static final int MSG_UI_LOAD_DATA = 0;

    private static final int MSG_UI_UPDATE_SUCCESS = 2;

    private static final int MSG_UI_UPDATE_FAIL = 3;

    private static final int MSG_UI_HIDE_TIP_DIALOG = 4;

    private static final int MSG_UI_GET_DEVICE_LIST = 5;

    TipDialogHelper mTipDialogHelper;

    private Button mBtnSubmit;

    private RoundImageView mRivAvatar;

    private EditText evName;

    private EditText evWeight;

    private EditText evShoeSize;

    private TextView tvBirthday;

    private TextView tvRelationship;

    private TextView tvSex;

    private String eId;

    private StatusView mStatusView;

    private int relationship = 0;

    private String avatarUrl;

    private int type;

    private String otherRelationship = "";

    private WearInfoResponse mWearInfoResponse;

    @Override
    protected void initView() {
        mStatusView = new StatusView(this);
        setContentView(mStatusView);
        mStatusView.setContentView(R.layout.activity_finish_info);
        mRivAvatar = (RoundImageView) findViewById(R.id.riv_avator);
        evName = (EditText) findViewById(R.id.ev_baby_nickname);
        evShoeSize = (EditText) findViewById(R.id.ev_shoe_size);
        evWeight = (EditText) findViewById(R.id.ev_weight);
        tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        tvRelationship = (TextView) findViewById(R.id.tv_relationship);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        type = TYPE_FINISH;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getInt(KEY_TYPE, TYPE_FINISH);
            eId = bundle.getString(KEY_EID);
        }
        if (type == TYPE_MODIFY) {
            setTitle("修改设备信息");
            if (TextUtils.isEmpty(eId)) {
                ToastUtil.show("初始化失败");
                finish();
            }
            loadData();
        } else if (type == TYPE_BABY_INFO) {
            mBtnSubmit.setText("保存");
            mBtnSubmit.setBackgroundDrawable(
                    getResources().getDrawable(R.drawable.btn_selector_blue));
            mBtnSubmit.setTextColor(
                    getResources().getColorStateList(R.drawable.text_selector_btn_blue));
            setTitle("宝贝信息");
            if (TextUtils.isEmpty(eId)) {
                ToastUtil.show("初始化失败");
                finish();
            }
            loadData();

        } else {
            setTitle(R.string.title_finish_info);
            mStatusView.showContentView();
        }
        mTipDialogHelper = new TipDialogHelper(this);
    }

    @Override
    protected void initEvent() {
        mStatusView.setStatusListener(new StatusView.StatusListener() {
            @Override
            public void reLoadData() {
                loadData();
            }
        });
        mBtnSubmit.setOnClickListener(this);
        findViewById(R.id.ll_avator).setOnClickListener(this);
        findViewById(R.id.ll_birthday).setOnClickListener(this);
        findViewById(R.id.ll_relationship).setOnClickListener(this);
        findViewById(R.id.ll_sex).setOnClickListener(this);
    }

    private boolean isChangeData() {
        boolean result = false;
        if (mWearInfoResponse == null) {
            return false;
        }
        if (!TextUtils.isEmpty(avatarUrl)
                && !TextUtils.equals(mWearInfoResponse.getAvatar(), avatarUrl)) {
            result = true;
        }
        if (!TextUtils.equals(mWearInfoResponse.getName(), evName.getText().toString())) {
            result = true;
        }
        if (!TextUtils.equals(mWearInfoResponse.getBirthday(), tvBirthday.getText().toString())) {
            result = true;
        }
        if (mWearInfoResponse.getRelationship() != relationship) {
            result = true;
        }
        if (mWearInfoResponse.getSex() != WearInfoResponse.parseSex(tvSex.getText().toString())) {
            result = true;
        }
        if (mWearInfoResponse.getShoeSize() != Integer.valueOf(evShoeSize.getText().toString())) {
            result = true;
        }
        if (mWearInfoResponse.getWeight() != Integer.valueOf(evWeight.getText().toString())) {
            result = true;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_birthday) {
            showBirthdayDialog();
        } else if (id == R.id.btn_submit) {
            if (type != TYPE_FINISH) {
                if (!isChangeData()) {
                    finish();
                    return;
                }
            }
            if (TextUtils.isEmpty(evName.getText())) {
                ToastUtil.show("请输入昵称");
                return;
            }
            if (TextUtils.isEmpty(tvRelationship.getText())) {
                ToastUtil.show("请选择关系");
                return;
            }
            if (type == TYPE_BABY_INFO) {
                mTipDialogHelper.showLoadingTip("正在努力保存");
            } else {
                mTipDialogHelper.showLoadingTip("正在努力提交");
            }
            sendEmptyBackgroundMessage(MSG_BACK_SUBMIT);
        } else if (id == R.id.ll_avator) {
            goActivityForResult(ChooseAvatorActivity.class);
        } else if (id == R.id.ll_sex) {
            showSexDialog();
        } else if (id == R.id.ll_relationship) {
            showRelationshipDialog();
        }
    }

    private void loadData() {
        mStatusView.showLoadingView();
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_LOAD_DATA:
                loadDataTask();
                break;
            case MSG_BACK_SUBMIT:
                submitTask();
                break;
            case MSG_BACK_GET_DEVICE_LIST:
                getBaseListTask();
                break;
            case MSG_BACK_UPLOAD:
                if (msg.obj != null) {
                    uploadTask((Bitmap) msg.obj);
                } else {
                    sendEmptyUiMessage(MSG_UI_UPDATE_FAIL);
                }
                break;
        }
    }

    /**
     * 获取设备列表请求
     */
    private void getBaseListTask() {
        HttpRequest<GetBaseListResponse> request = new HttpRequest<>(AppConfig.GET_BASE_LIST,
                GetBaseListResponse.class);
        if (!TextUtils.isEmpty(InitSharedData.getUserCode())) {
            request.addParam("uc", InitSharedData.getUserCode());
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_GET_DEVICE_LIST;
        try {
            message.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
    }

    private void uploadTask(Bitmap bitmap) {
        String filePath = UploadUtil.saveBitmapFile(bitmap);
        String uploadUrl = AppConfig.SERVER + AppConfig.UPLOAD_IMAGE + "&uc="
                + InitSharedData.getUserCode() + "&=eid" + eId + "&file=";
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(uploadUrl);
        MultipartEntity entity = new MultipartEntity();
        File file = new File(filePath);
        FileBody fileBody = new FileBody(file);
        entity.addPart("uploadedfile", fileBody);
        httppost.setEntity(entity);
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity);
            UpdateImageResponse us = new UpdateImageResponse();
            us.parse(result);
            if (!TextUtils.isEmpty(us.getImageUrl())) {
                avatarUrl = us.getImageUrl();
                Message uiMsg = obtainUiMessage();
                uiMsg.what = MSG_UI_UPDATE_SUCCESS;
                uiMsg.obj = bitmap;
                uiMsg.sendToTarget();
            } else {
                sendEmptyUiMessage(MSG_UI_UPDATE_FAIL);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendEmptyUiMessage(MSG_UI_UPDATE_FAIL);
        }
        httpclient.getConnectionManager().shutdown();
    }

    private void submitTask() {
        HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.SET_WEARERINFO,
                CommonResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        request.addParam("eid", eId);
        if (!TextUtils.isEmpty(avatarUrl)) {
            request.addParam("saveType", "1");
            request.addParam("Image", avatarUrl);
        } else {
            request.addParam("saveType", "2");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{\"Name\":\"").append(evName.getText().toString()).append("\",");
        builder.append("\"Sex\":\"").append(WearInfoResponse.parseSex(tvSex.getText().toString()))
                .append("\",");
        builder.append("\"Birthday\":\"").append(tvBirthday.getText().toString()).append("\",");
        builder.append("\"Relation\":\"").append(relationship).append("\",");
        builder.append("\"OtherRelation\":\"").append(otherRelationship).append("\",");
        builder.append("\"Birthday\":\"").append(tvBirthday.getText().toString()).append("\",");
        builder.append("\"Weight\":\"").append(evWeight.getText().toString()).append("\",");
        builder.append("\"ShoeSize\":\"").append(evShoeSize.getText().toString()).append("\"}");
        request.addParam("jsonInfo", builder.toString());
        Message uiMsg = obtainUiMessage();
        uiMsg.what = MSG_UI_SUBMIT;
        try {
            uiMsg.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        uiMsg.sendToTarget();
    }

    private void loadDataTask() {
        HttpRequest<WearInfoResponse> request = new HttpRequest<>(AppConfig.GET_WEARERINFO,
                WearInfoResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        request.addParam("eid", eId);
        Message uiMsg = obtainUiMessage();
        uiMsg.what = MSG_UI_LOAD_DATA;
        try {
            uiMsg.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        uiMsg.sendToTarget();
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_LOAD_DATA:
                if (msg.obj != null) {
                    handleLoadData((WearInfoResponse) msg.obj);
                } else {
                    mStatusView.showFailView();
                }
                break;
            case MSG_UI_SUBMIT:
                if (msg.obj != null && ((CommonResponse) msg.obj).getResult() > 0) {
                    if (type == TYPE_MODIFY) {
                        sendEmptyUiMessage(MSG_UI_HIDE_TIP_DIALOG);
                        sendBroadcast(BroadcastActions.ACTION_MODIFY_WEAR_INFO_SUCCESS);
                        finish();
                    } else if (type == TYPE_BABY_INFO) {
                        sendEmptyUiMessage(MSG_UI_HIDE_TIP_DIALOG);
                        sendBroadcast(BroadcastActions.ACTION_UPDATE_DEVICE_LIST_INFO);
                        finish();
                    } else {
                        sendEmptyBackgroundMessage(MSG_BACK_GET_DEVICE_LIST);
                    }
                } else {
                    sendEmptyUiMessage(MSG_UI_HIDE_TIP_DIALOG);
                    if (msg.obj != null
                            && !TextUtils.isEmpty(((CommonResponse) msg.obj).getInfo())) {
                        ToastUtil.show(((CommonResponse) msg.obj).getInfo());
                    } else {
                        if (type == TYPE_BABY_INFO) {
                            ToastUtil.show("提交失败");
                        } else {
                            ToastUtil.show("保存失败");
                        }
                    }
                }
                break;
            case MSG_UI_UPDATE_FAIL:
                ToastUtil.show("头像上传失败");
                sendEmptyUiMessage(MSG_UI_HIDE_TIP_DIALOG);
                break;
            case MSG_UI_UPDATE_SUCCESS:
                ToastUtil.show("头像上传成功");
                sendEmptyUiMessage(MSG_UI_HIDE_TIP_DIALOG);
                if (msg.obj != null && mRivAvatar != null) {
                    mRivAvatar.setImageBitmap((Bitmap) msg.obj);
                }
                break;
            case MSG_UI_HIDE_TIP_DIALOG:
                if (mTipDialogHelper != null) {
                    mTipDialogHelper.hideDialog();
                }
                break;
            case MSG_UI_GET_DEVICE_LIST:
                sendEmptyUiMessage(MSG_UI_HIDE_TIP_DIALOG);
                GetBaseListResponse response = (GetBaseListResponse) msg.obj;
                if (response != null && response.isOk()) {
                    InitSharedData.setDeviceData(response.getJson());
                    DeviceInfoHelper.getInstance().refreshDeviceList();
                    if (AppConfig.isDebug) {
                        ToastUtil.show(response.getJson());
                    }
                    if (!TextUtils.isEmpty(InitSharedData.getDeviceData())) {
                        sendBroadcast(BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN);
                        goActivity(MainActivity.class);
                    } else {
                        ToastUtil.show("提交失败");
                    }
                }
                break;
        }
    }

    private void handleLoadData(WearInfoResponse response) {
        mWearInfoResponse = response;
        mStatusView.showContentView();
        if (!TextUtils.isEmpty(response.getAvatar())) {
            ImageLoader.getInstance().displayImage(response.getAvatar(), mRivAvatar);
        }
        evName.setText("" + response.getName());
        relationship = response.getRelationship();
        otherRelationship = response.getOtherRelationship();
        tvRelationship.setText(DeviceInfo.parseRelation(response.getRelationship(),
                response.getOtherRelationship()));
        tvSex.setText(response.getSex() == WearInfoResponse.MAN ? MAN : WOMAN);
        if (!TextUtils.isEmpty(response.getBirthday())) {
            tvBirthday.setText(response.getBirthday());
        }
        evWeight.setText("" + response.getWeight());
        evShoeSize.setText("" + response.getShoeSize());
    }

    private void showSexDialog() {
        if (!isFinishing()) {
            final BaseDialog dialog = new BaseDialog(this);
            dialog.setWindow(R.style.alpha_animation, 0.3f);
            dialog.setContentView(R.layout.dialog_select_sex);
            Button btnSexMale = (Button) dialog.findViewById(R.id.btn_male);
            Button btnSexFemale = (Button) dialog.findViewById(R.id.btn_female);
            btnSexMale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSex.setText(MAN);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            btnSexFemale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSex.setText(WOMAN);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            if (TextUtils.equals(tvSex.getText().toString(), MAN)) {
                btnSexMale.setSelected(true);
                btnSexFemale.setSelected(false);
            } else if (TextUtils.equals(tvSex.getText().toString(), WOMAN)) {
                btnSexMale.setSelected(false);
                btnSexFemale.setSelected(true);
            } else {
                btnSexMale.setSelected(false);
                btnSexFemale.setSelected(false);
            }
            dialog.show();
        }
    }

    private void showBirthdayDialog() {
        if (!isFinishing()) {
            final BaseDialog dialog = new BaseDialog(this);
            dialog.setWindow(R.style.alpha_animation, 0.6f);
            dialog.setContentView(R.layout.dialog_select_birthday);
            final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
            if (tvBirthday != null && !TextUtils.isEmpty(tvBirthday.getText())) {
                int[] times = parseTime(tvBirthday.getText().toString().trim());
                datePicker.init(times[0], times[1] - 1, times[2], null);
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH), null);
            }
            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String birthday = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-"
                            + datePicker.getDayOfMonth();
                    tvBirthday.setText(birthday);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void showEditDialog() {
        if (!isFinishing()) {
            final BaseDialog dialog = new BaseDialog(this);
            dialog.setWindow(R.style.alpha_animation, 0.3f);
            dialog.setContentView(R.layout.dialog_edit_baby_nikename);
            TextView text = (TextView) dialog.findViewById(R.id.tv_title);
            text.setText("请输入关系");
            final ImageEditText ev = (ImageEditText) dialog.findViewById(R.id.ev_baby_nickname);
            ev.setHint("请输入关系");
            dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.hideSoftInput(FinishInfoActivity.this);
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(ev.getText())) {
                        ToastUtil.show("请输入关系");
                    } else {
                        otherRelationship = ev.getText().toString();
                        tvRelationship.setText(otherRelationship);
                        CommonUtil.hideSoftInput(FinishInfoActivity.this);
                        dialog.dismiss();
                    }

                }
            });
            dialog.show();
        }
    }

    private void showRelationshipDialog() {
        if (!isFinishing()) {
            final BaseDialog dialog = new BaseDialog(this);
            dialog.setWindow(R.style.alpha_animation, 0.6f);
            dialog.setContentView(R.layout.dialog_select_relationship);
            ListView listView = (ListView) dialog.findViewById(R.id.lv_relationship);
            listView.setAdapter(new RelationshipAdapter(this));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    relationship = position;
                    if (position == 0) {
                        showEditDialog();
                    } else {
                        tvRelationship.setText(DeviceInfo.parseRelation(relationship, "其他"));
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE) {
            mTipDialogHelper.showLoadingTip("正在上传头像");
            final Bitmap bitmap = data.getParcelableExtra("bitmap_result");
            if (bitmap != null) {
                Message message = obtainBackgroundMessage();
                message.obj = bitmap;
                message.what = MSG_BACK_UPLOAD;
                message.sendToTarget();
            }
        }
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN)) {
            finish();
        }
    }

    private int[] parseTime(String time) {
        int[] result = new int[3];
        try {
            result[0] = Integer.valueOf(time.substring(0, 4));
            result[1] = Integer.valueOf(time.substring(5, 7));
            result[2] = Integer.valueOf(time.substring(8, 10));
        } catch (Exception e) {
        } catch (Error error) {
        }
        return result;
    }

}
