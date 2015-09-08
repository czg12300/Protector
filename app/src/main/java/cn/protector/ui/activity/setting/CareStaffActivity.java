
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.widgt.RoundImageView;
import cn.protector.R;
import cn.protector.data.BroadcastActions;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;

/**
 * 描述：监护人员页面
 * 
 * @author jakechen
 */
public class CareStaffActivity extends CommonTitleActivity {
    private ListView mLvCareStaff;

    private CareStaffAdapter mCareStaffAdapter;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_care_staff);
        setTitle(R.string.title_care_staff);
        mLvCareStaff = (ListView) findViewById(R.id.lv_care_staff);
        mCareStaffAdapter = new CareStaffAdapter(this);
        mLvCareStaff.setAdapter(mCareStaffAdapter);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            goActivity(ScanQACodeActivity.class);
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
