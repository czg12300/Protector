
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.common.AppException;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.widgt.RoundImageView;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CareStaffListResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;

/**
 * 描述：监护人员页面
 *
 * @author jakechen
 */
public class CareStaffActivity extends CommonTitleActivity {
    private static final int MSG_BACK_LOAD_DATA=0;
    private static final int MSG_UI_LOAD_DATA=0;
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

    @Override
    protected void initData() {
        super.initData();
        mCareStaffAdapter.setData(getList());
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what){
            case MSG_BACK_LOAD_DATA:
                loadDataTask();
                break;
        }
    }

    private void loadDataTask() {
        HttpRequest<CareStaffListResponse> request = new HttpRequest<>(AppConfig.GET_EQUI_USER_LIST,
                CareStaffListResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid",
                    DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message uiMsg = obtainUiMessage();
        uiMsg.what = MSG_UI_LOAD_DATA;
        try {
            uiMsg.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        uiMsg.sendToTarget();
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
