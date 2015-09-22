package cn.protector.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.widgt.RoundImageView;
import cn.protector.R;
import cn.protector.data.BroadcastActions;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.AddDeviceActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：设备管理
 *
 * @author jake
 * @since 2015/9/21 22:07
 */
public class DeviceManageActivity extends CommonTitleActivity {


    private ListView mLvContent;
    private CareStaffAdapter mCareStaffAdapter;

    @Override
    protected View getTitleLayoutView() {
        View vTitle = getLayoutInflater().inflate(R.layout.title_choose_avator, null);
        vTitle.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
        vTitle.findViewById(R.id.ib_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(AddDeviceActivity.class);
            }
        });
        return vTitle;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device_manage);
        setTitle(R.string.title_device_manage);
        mLvContent = (ListView) findViewById(R.id.lv_content);
        mCareStaffAdapter = new CareStaffAdapter(this);
        mLvContent.setAdapter(mCareStaffAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mCareStaffAdapter.setData(getList());
    }

    private List<CareStaff> getList() {
        List<CareStaff> list = new ArrayList<CareStaff>();
        list.add(new CareStaff("爸爸", true));
        list.add(new CareStaff("妈妈", false));
        list.add(new CareStaff("哥哥", false));
        list.add(new CareStaff("姐姐", false));
        list.add(new CareStaff("爷爷", false));
        list.add(new CareStaff("奶奶", false));
        return list;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            goActivity(ScanQACodeActivity.class);
        }
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
//        actions.add(BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN)) {
            finish();
        }
    }

    private class CareStaff {
        public String relationship;

        public boolean isManager;

        public CareStaff(String relationship, boolean isManager) {
            this.relationship = relationship;
            this.isManager = isManager;
        }
    }

    private class CareStaffAdapter extends BaseListAdapter<CareStaff> {
        public CareStaffAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflate(R.layout.item_care_staff);
                holder.rivAvator = (RoundImageView) convertView.findViewById(R.id.riv_avator);
                holder.tvRelationship = (TextView) convertView.findViewById(R.id.tv_relationship);
                holder.tvManager = (TextView) convertView.findViewById(R.id.tv_manager);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CareStaff careStaff = mDataList.get(position);
            if (careStaff != null) {
                if (careStaff.isManager) {
                    holder.tvManager.setVisibility(View.VISIBLE);
                } else {
                    holder.tvManager.setVisibility(View.GONE);
                }
                holder.tvRelationship.setText(careStaff.relationship);
                holder.rivAvator.setImageResource(R.drawable.img_head_father);
            }

            return convertView;
        }

        final class ViewHolder {
            RoundImageView rivAvator;

            TextView tvRelationship;

            TextView tvManager;
        }
    }

}
