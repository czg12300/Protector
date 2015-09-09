
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.common.ui.BaseDialog;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.widgt.RoundImageView;
import cn.common.utils.CommonUtil;
import cn.common.utils.DisplayUtil;
import cn.protector.R;
import cn.protector.data.BroadcastActions;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.widget.ImageEditText;
import cn.protector.utils.ToastUtil;

/**
 * 描述:宝贝信息页面
 *
 * @author Created by jakechen on 2015/8/27.
 */
public class BabyInfoActivity extends CommonTitleActivity implements View.OnClickListener {

    private RoundImageView mRivAvator;


    private TextView mTvBabyBirthday;

    private TextView mTvShoeSize;
    private TextView mTvBabySex;
    private TextView mTvBabyName;

    private BaseDialog mBirthdayDialog;
    private BaseDialog mNameDialog;
    private BaseDialog mSexDialog;

    private BaseDialog mShoeSizeDialog;

    private DatePicker mDatePicker;
    private ImageEditText mEvBabyNickName;
    private Button mBtnSexMale;
    private Button mBtnSexFemale;

    @Override
    protected void hideDialog() {
        super.hideDialog();
        if (mBirthdayDialog != null) {
            mBirthdayDialog.dismiss();
        }
        if (mNameDialog != null) {
            mNameDialog.dismiss();
        }
        if (mSexDialog != null) {
            mSexDialog.dismiss();
        }
        if (mShoeSizeDialog != null) {
            mShoeSizeDialog.dismiss();
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_baby_info);
        setTitle(R.string.title_baby_info);
        mRivAvator = (RoundImageView) findViewById(R.id.riv_avator);
        mTvBabyName = (TextView) findViewById(R.id.tv_baby_name);
        mTvBabyBirthday = (TextView) findViewById(R.id.tv_baby_birthday);
        mTvBabySex = (TextView) findViewById(R.id.tv_baby_sex);
        mTvShoeSize = (TextView) findViewById(R.id.tv_shoe_size);
//        mRivAvator.setBorderColor(getColor(R.color.gray_999999));
//        mRivAvator.setBorderWidth(3, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.ll_avator).setOnClickListener(this);
        findViewById(R.id.ll_baby_name).setOnClickListener(this);
        findViewById(R.id.ll_baby_birthday).setOnClickListener(this);
        findViewById(R.id.ll_baby_sex).setOnClickListener(this);
        findViewById(R.id.ll_shoe_size).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mRivAvator.setImageResource(R.drawable.img_head_boy1);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_baby_name) {
            showNameDialog();
        } else if (id == R.id.ll_baby_birthday) {
            showBirthdayDialog();
        } else if (id == R.id.ll_baby_sex) {
            showSexDialog();
        } else if (id == R.id.ll_shoe_size) {
            showShoeSizeDialog();
        } else if (id == R.id.ll_avator) {
            goActivityForResult(ChooseAvatorActivity.class);
        }
    }

    /**
     * 显示选择宝贝生日的弹窗
     */
    private void showBirthdayDialog() {
        if (mBirthdayDialog == null) {
            mBirthdayDialog = new BaseDialog(this);
            mBirthdayDialog.setWindow(R.style.alpha_animation, 0.3f);
            mBirthdayDialog.setContentView(R.layout.dialog_select_birthday);
            mDatePicker = (DatePicker) mBirthdayDialog.findViewById(R.id.date_picker);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            mDatePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), null);
            mBirthdayDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDatePicker != null) {
                        String birthday = String.format("%d年%02d月%02d日", mDatePicker.getYear(),
                                mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth());
                        mTvBabyBirthday.setText(birthday);
                    }
                    if (mBirthdayDialog != null) {
                        mBirthdayDialog.dismiss();
                    }
                }
            });
        }
        mBirthdayDialog.show();
    }

    /**
     * 显示编辑宝贝昵称的弹窗
     */
    private void showNameDialog() {
        if (mNameDialog == null) {
            mNameDialog = new BaseDialog(this);
            mNameDialog.setWindow(R.style.alpha_animation, 0.3f);
            mNameDialog.setContentView(R.layout.dialog_edit_baby_nikename);
            mEvBabyNickName = (ImageEditText) mNameDialog.findViewById(R.id.ev_baby_nickname);
            mNameDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(mEvBabyNickName.getText())) {
                        ToastUtil.show(R.string.baby_nickname_edit_hint);
                    } else {
                        mTvBabyName.setText(mEvBabyNickName.getText());
                        if (mNameDialog != null) {
                            mNameDialog.dismiss();
                        }
                    }
                }
            });
            mNameDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNameDialog != null) {
                        mNameDialog.dismiss();
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(mTvBabyName.getText())) {
            mEvBabyNickName.setText(mTvBabyName.getText());
            mEvBabyNickName.setSelection(mEvBabyNickName.getText().length());
        }
        CommonUtil.showSoftInput(this);
        mNameDialog.show();
    }

    /**
     * 显示选择鞋子尺码的弹窗
     */
    private void showShoeSizeDialog() {
        if (mShoeSizeDialog == null) {
            mShoeSizeDialog = new BaseDialog(this);
            mShoeSizeDialog.setWindow(R.style.alpha_animation, 0.3f);
            mShoeSizeDialog.setContentView(R.layout.dialog_select_shoe_size);
            ListView lv = (ListView) mShoeSizeDialog.findViewById(R.id.lv_shoe_size);
            lv.setAdapter(new ShoeSizeAdapter(this, getShoeSizeList()));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mTvShoeSize.setText((String) parent.getAdapter().getItem(position));
                    if (mShoeSizeDialog != null && mShoeSizeDialog.isShowing()) {
                        mShoeSizeDialog.dismiss();
                    }
                }
            });
        }
        mShoeSizeDialog.show();
    }

    /**
     * 显示选择性别的弹窗
     */
    private void showSexDialog() {
        if (mSexDialog == null) {
            mSexDialog = new BaseDialog(this);
            mSexDialog.setWindow(R.style.alpha_animation, 0.3f);
            mSexDialog.setContentView(R.layout.dialog_select_sex);
            mBtnSexMale = (Button) mSexDialog.findViewById(R.id.btn_male);
            mBtnSexFemale = (Button) mSexDialog.findViewById(R.id.btn_female);
            mBtnSexMale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTvBabySex.setText(R.string.male);
                    if (mSexDialog != null) {
                        mSexDialog.dismiss();
                    }
                }
            });

            mBtnSexFemale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTvBabySex.setText(R.string.female);
                    if (mSexDialog != null) {
                        mSexDialog.dismiss();
                    }
                }
            });
        }
        if (TextUtils.equals(mTvBabySex.getText().toString(), getString(R.string.male))) {
            mBtnSexMale.setSelected(true);
            mBtnSexFemale.setSelected(false);
        } else if (TextUtils.equals(mTvBabySex.getText().toString(), getString(R.string.female))) {
            mBtnSexMale.setSelected(false);
            mBtnSexFemale.setSelected(true);
        } else {
            mBtnSexMale.setSelected(false);
            mBtnSexFemale.setSelected(false);
        }
        mSexDialog.show();
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

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN)) {
            finish();
        }
    }
}
