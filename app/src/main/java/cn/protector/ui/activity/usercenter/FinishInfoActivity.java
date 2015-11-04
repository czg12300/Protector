
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.common.ui.BaseDialog;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.widgt.RoundImageView;
import cn.common.utils.DisplayUtil;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.ui.activity.CommonTitleActivity;

/**
 * 完善信息页面
 *
 * @auther Created by jakechen on 2015/8/27.
 */
public class FinishInfoActivity extends CommonTitleActivity implements View.OnClickListener {
    private static final int MSG_BACK_SUBMIT = 0;

    private static final int MSG_UI_SUBMIT = 0;

    private Button mBtnSubmit;

    private RoundImageView mRivAvator;

    private EditText mEvBabyName;

    private EditText mEvNikeName;

    private EditText mEvRelationship;

    private TextView mTvBabyBirthday;

    private TextView mTvShoeSize;

    private View mVAvator;

    private View mVShoeSize;

    private View mVBirthday;

    private BaseDialog mBirthdayDialog;

    private BaseDialog mShoeSizeDialog;

    private DatePicker mDatePicker;

    protected void hideDialog() {
        if (mBirthdayDialog != null) {
            mBirthdayDialog.dismiss();
        }
        if (mShoeSizeDialog != null) {
            mShoeSizeDialog.dismiss();
        }
    }

    private String code;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_finish_info);
        setTitle(R.string.title_finish_info);
        mVAvator = findViewById(R.id.ll_avator);
        mVShoeSize = findViewById(R.id.ll_shoe_size);
        mVBirthday = findViewById(R.id.ll_birthday);
        mTvShoeSize = (TextView) findViewById(R.id.tv_shoe_size);
        mTvBabyBirthday = (TextView) findViewById(R.id.tv_birthday);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mEvBabyName = (EditText) findViewById(R.id.ev_baby_name);
        mRivAvator = (RoundImageView) findViewById(R.id.riv_avator);
        // mRivAvator.setBorderColor(getColor(R.color.gray_999999));
        // mRivAvator.setBorderWidth(3, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    protected void initEvent() {
        mBtnSubmit.setOnClickListener(this);
        mVAvator.setOnClickListener(this);
        mVShoeSize.setOnClickListener(this);
        mVBirthday.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mRivAvator.setImageResource(R.drawable.img_head_boy1);
        code = getIntent().getStringExtra("Code");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_birthday) {
            showBirthdayDialog();
        } else if (id == R.id.ll_shoe_size) {
            showShoeSizeDialog();
        } else if (id == R.id.btn_submit) {
            sendEmptyBackgroundMessage(MSG_BACK_SUBMIT);
        } else if (id == R.id.ll_avator) {
            goActivityForResult(ChooseAvatorActivity.class);
        } else if (id == R.id.btn_ok) {
            if (mDatePicker != null) {
                String birthday = String.format("%d年%02d月%02d日", mDatePicker.getYear(),
                        mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth());
                mTvBabyBirthday.setText(birthday);
            }
            if (mBirthdayDialog != null && mBirthdayDialog.isShowing()) {
                mBirthdayDialog.dismiss();
            }
        }
    }

    private void showBirthdayDialog() {
        if (mBirthdayDialog == null) {
            mBirthdayDialog = new BaseDialog(this);
            mBirthdayDialog.setWindow(R.style.alpha_animation, 0.6f);
            mBirthdayDialog.setContentView(R.layout.dialog_select_birthday);
            mDatePicker = (DatePicker) mBirthdayDialog.findViewById(R.id.date_picker);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            mDatePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), null);
            mBirthdayDialog.findViewById(R.id.btn_ok).setOnClickListener(this);
        }
        mBirthdayDialog.show();
    }

    private void showShoeSizeDialog() {
        if (mShoeSizeDialog == null) {
            mShoeSizeDialog = new BaseDialog(this);
            mShoeSizeDialog.setWindow(R.style.alpha_animation, 0.6f);
            mShoeSizeDialog.setContentView(R.layout.dialog_select_shoe_size);
            ListView lv = (ListView) mShoeSizeDialog.findViewById(R.id.lv_shoe_size);
            lv.setAdapter(new ShoeSizeAdapter(this, getShoeSizeList()));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mTvShoeSize.setText((String) parent.getAdapter().getItem(position));
                    if (mShoeSizeDialog != null) {
                        mShoeSizeDialog.dismiss();
                    }
                }
            });
        }
        mShoeSizeDialog.show();
    }

    private List<String> getShoeSizeList() {
        List<String> list = new ArrayList<String>();
        list.add("33");
        list.add("34");
        list.add("35");
        list.add("36");
        list.add("37");
        list.add("38");
        list.add("39");
        list.add("40");
        list.add("41");
        list.add("42");
        list.add("43");
        list.add("44");
        list.add("45");
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE) {
            Bitmap bitmap = data.getParcelableExtra("bitmap_result");
            if (bitmap != null) {
                mRivAvator.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_SUBMIT:
                break;
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

    private class ShoeSizeAdapter extends BaseListAdapter<String> {
        public ShoeSizeAdapter(Context context, List<String> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null) {
                textView = new TextView(getContext());
                textView.setTextColor(getColor(R.color.black_333333));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getDimension(R.dimen.text_content));
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(MATCH_PARENT,
                        DisplayUtil.dip(44));
                textView.setLayoutParams(params);
                textView.setPadding(DisplayUtil.dip(10), 0, 0, 0);
                textView.setGravity(Gravity.CENTER);
                convertView = textView;
            } else {
                textView = (TextView) convertView;
            }
            if (!TextUtils.isEmpty(mDataList.get(position))) {
                textView.setText(mDataList.get(position) + "码");
            }
            return convertView;
        }
    }
}
