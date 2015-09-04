package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.utils.DisplayUtil;
import cn.protector.R;
import cn.protector.data.BroadcastActions;
import cn.protector.ui.activity.usercenter.BabyInfoActivity;
import cn.protector.ui.helper.MainTitleHelper;

/**
 * Created by Administrator on 2015/8/13.
 */
public class SettingFragment extends BaseWorkerFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private MainTitleHelper mTitleHelper;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    private GridView mGvSetting;
    private View mVBabyInfo;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_setting);
        mGvSetting = (GridView) findViewById(R.id.gv_setting);
        mVBabyInfo = findViewById(R.id.ll_baby_info);
        mTitleHelper = new MainTitleHelper(findViewById(R.id.fl_title), MainTitleHelper.STYLE_SETTING);
    }

    @Override
    protected void initEvent() {
        mGvSetting.setOnItemClickListener(this);
        mVBabyInfo.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mGvSetting.setAdapter(new SettingAdapter(getActivity(), getListSetting()));
    }

    /**
     * 设置grid view 的设置选项
     *
     * @return
     */
    private List<ItemInfo> getListSetting() {
        List<ItemInfo> list = new ArrayList<ItemInfo>();
        list.add(new ItemInfo(R.drawable.ico_family_setting, getString(R.string.family_setting)));
        list.add(new ItemInfo(R.drawable.ico_saftey_setting, getString(R.string.saftey_setting)));
        list.add(new ItemInfo(R.drawable.ico_qrcode_setting, getString(R.string.qrcode_setting)));
        list.add(new ItemInfo(R.drawable.ico_position_setting, getString(R.string.position_setting)));
        list.add(new ItemInfo(R.drawable.ico_location_setting, getString(R.string.location_setting)));
        list.add(new ItemInfo(R.drawable.ico_shutdown_setting, getString(R.string.shutdown_setting)));
        return list;
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
        actions.add(BroadcastActions.ACTION_GET_ALL_DEVICES);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_MAIN_DEVICE_CHANGE)) {
            //TODO 切换设备
            MainTitleHelper.DeviceInfo info = (MainTitleHelper.DeviceInfo) intent.getSerializableExtra(MainTitleHelper.KEY_DEVICE_INFO);
            mTitleHelper.setTitle(info.name);
        } else if (TextUtils.equals(action, BroadcastActions.ACTION_GET_ALL_DEVICES)) {
            List<MainTitleHelper.DeviceInfo> infos = (List<MainTitleHelper.DeviceInfo>) intent.getSerializableExtra(MainTitleHelper.KEY_DEVICE_LIST);
            mTitleHelper.setDevice(infos);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO
    }

    /**
     * 用于设置grid view
     */
    class ItemInfo {
        int imgId;
        String name;

        public ItemInfo(int imgId, String name) {
            this.imgId = imgId;
            this.name = name;
        }
    }

    /**
     * grid view 的适配器
     */
    class SettingAdapter extends BaseListAdapter<ItemInfo> {
        public SettingAdapter(Context context, List<ItemInfo> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView == null) {
                tv = new TextView(getContext());
                int padding = DisplayUtil.dip(10);
                tv.setPadding(0, padding, 0, padding);
                tv.setGravity(Gravity.CENTER);
                tv.setCompoundDrawablePadding(padding);
                tv.setTextColor(getColor(R.color.gray_757575));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                convertView = tv;
            } else {
                tv = (TextView) convertView;
            }
            ItemInfo info = mDataList.get(position);
            if (info != null) {
                tv.setText(info.name);
                tv.setCompoundDrawablesWithIntrinsicBounds(0, info.imgId, 0, 0);
            }
            return convertView;
        }
    }
}
