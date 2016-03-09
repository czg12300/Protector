package cn.protector.ui.activity.usercenter;

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

import java.util.Calendar;
import java.util.List;

import cn.common.AppException;
import cn.common.bitmap.core.ImageLoader;
import cn.common.ui.BaseDialog;
import cn.common.ui.widgt.RoundImageView;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.WearInfoResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.MainActivity;
import cn.protector.ui.adapter.RelationshipAdapter;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

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
  private static final int MSG_BACK_SUBMIT = 1;

  private static final int MSG_UI_SUBMIT = 1;
  private static final int MSG_BACK_LOAD_DATA = 0;
  private static final int MSG_UI_LOAD_DATA = 0;
  private static final int MSG_BACK_DELETE = 1;
  private static final int MSG_UI_DELETE = 1;
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
    int type = TYPE_FINISH;
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
    } else {
      mRivAvatar.setImageResource(R.drawable.img_head_boy1);
      setTitle(R.string.title_finish_info);
      mStatusView.showContentView();
    }
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

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.ll_birthday) {
      showBirthdayDialog();
    } else if (id == R.id.btn_submit) {
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
        Message subMsg = obtainUiMessage();
        subMsg.what = MSG_UI_SUBMIT;
        subMsg.sendToTarget();
        break;
    }
  }


  private void loadDataTask() {
    HttpRequest<WearInfoResponse> request = new HttpRequest<>(AppConfig.GET_WEARERINFO, WearInfoResponse.class);
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
        goActivity(MainActivity.class);
        sendBroadcast(BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN);
        break;
    }
  }

  private void handleLoadData(WearInfoResponse response) {
    mStatusView.showContentView();
    ImageLoader.getInstance().displayImage(response.getAvatar(), mRivAvatar);
    evName.setText("" + response.getName());
    tvRelationship.setText(DeviceInfo.parseRelation(response.getRelationship(), response.getOtherRelationship()));
    tvSex.setText(TextUtils.equals(response.getSex(), MAN) ? MAN : WOMAN);
    tvBirthday.setText("" + response.getBirthday());
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
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
      dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String birthday = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
          tvBirthday.setText(birthday);
        }
      });
      dialog.show();
    }
  }

  private void showEditDialog() {
    if (!isFinishing()) {
      final BaseDialog dialog = new BaseDialog(this);
      dialog.setWindow(R.style.alpha_animation, 0.6f);
      dialog.setContentView(R.layout.dialog_select_birthday);
      final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
      dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String birthday = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
          tvBirthday.setText(birthday);
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
          if (relationship == 0) {
            showEditDialog();
          } else {
            relationship = position;
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
      Bitmap bitmap = data.getParcelableExtra("bitmap_result");
      if (bitmap != null) {
        mRivAvatar.setImageBitmap(bitmap);
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


}
