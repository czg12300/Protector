
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cn.common.ui.BaseDialog;
import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;
import cn.protector.logic.entity.FenceInfo;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：设置围栏页面
 *
 * @author jakechen
 */
public class FenceSetActivity extends CommonTitleActivity {
    private ListView mLvFence;

    private FenceAdapter mFenceAdapter;
    private BaseDialog mOperatingDialog;

    @Override
    protected void hideDialog() {
        super.hideDialog();
        if (mOperatingDialog != null) {
            mOperatingDialog.dismiss();
        }
    }

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
                goActivity(AddFenceActivity.class);
            }
        });
        return vTitle;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_care_staff);
        setTitle(R.string.title_fence_set);
        mLvFence = (ListView) findViewById(R.id.lv_care_staff);
        mFenceAdapter = new FenceAdapter(this);
        mLvFence.setAdapter(mFenceAdapter);
        mLvFence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showOperatingDialog((FenceInfo) mFenceAdapter.getItem(position));
            }
        });
    }

    /**
     * 显示操作的弹窗
     */
    private void showOperatingDialog(final FenceInfo info) {
        if (mOperatingDialog == null) {
            mOperatingDialog = new BaseDialog(this);
            mOperatingDialog.setWindow(R.style.alpha_animation, 0.3f);
            mOperatingDialog.setContentView(R.layout.dialog_fence_operating);
            mOperatingDialog.findViewById(R.id.btn_edit_info).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOperatingDialog != null) {
                        mOperatingDialog.dismiss();
                    }
                }
            });

            mOperatingDialog.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFenceAdapter.remove(info);
                    //TODO 删除信息
                    if (mOperatingDialog != null) {
                        mOperatingDialog.dismiss();
                    }
                }
            });
        }
        mOperatingDialog.show();
    }

    @Override
    protected void initData() {
        super.initData();
        mFenceAdapter.setData(getList());
    }

    private List<FenceInfo> getList() {
        List<FenceInfo> list = new ArrayList<FenceInfo>();
        FenceInfo info;
        info = new FenceInfo();
        info.name = "老师";
        info.address = "广州市天河区岑村红花岗";
        info.range = 500;
        list.add(info);
        info = new FenceInfo();
        info.name = "姐姐家";
        info.address = "广州市天河区上社";
        info.range = 500;
        list.add(info);
        info = new FenceInfo();
        info.name = "爷爷家";
        info.address = "广州市天河区五山路口";
        info.range = 500;
        list.add(info);
        info = new FenceInfo();
        info.name = "外婆家";
        info.address = "广州市白云区上下九";
        info.range = 500;
        list.add(info);
        info = new FenceInfo();
        info.name = "家";
        info.address = "广州市天河区岗顶";
        info.range = 1500;
        list.add(info);
        return list;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            goActivity(ScanQACodeActivity.class);
        }
    }


    private class FenceAdapter extends BaseListAdapter<FenceInfo> {
        public FenceAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflate(R.layout.item_fence);
                holder.tvIcon = (TextView) convertView.findViewById(R.id.tv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                holder.tvRange = (TextView) convertView.findViewById(R.id.tv_range);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            FenceInfo info = mDataList.get(position);
            if (info != null) {
                if (!TextUtils.isEmpty(info.name)) {
                    holder.tvIcon.setText(info.name.substring(0, 1));
                    holder.tvName.setText(info.name);
                }
                if (!TextUtils.isEmpty(info.address)) {
                    holder.tvAddress.setText(info.address);
                }
                holder.tvRange.setText(getString(R.string.fence_range_string).replace("#", "" + info.range));
            }

            return convertView;
        }

        final class ViewHolder {

            TextView tvIcon;
            TextView tvName;
            TextView tvAddress;
            TextView tvRange;

        }
    }

}
