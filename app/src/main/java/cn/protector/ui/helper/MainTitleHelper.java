
package cn.protector.ui.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.helper.PopupWindowHelper;
import cn.common.ui.widgt.ChangeThemeUtils;
import cn.common.utils.DisplayUtil;
import cn.protector.R;
import cn.protector.data.BroadcastActions;

/**
 * 描述：用于主页的title逻辑
 *
 * @author jakechen on 2015/9/4.
 */
public class MainTitleHelper implements View.OnClickListener {

    private View mVTitle;

    public static final String KEY_DEVICE_INFO = "key_device_info";

    public static final String KEY_DEVICE_LIST = "key_device_list";

    public static final int STYLE_LOCATE = 0;

    public static final int STYLE_HISTORY = 1;

    public static final int STYLE_MESSAGE = 3;

    public static final int STYLE_HEALTH = 4;

    public static final int STYLE_SETTING = 5;

    private int mStyle = STYLE_LOCATE;

    private TextView mTvTitle;

    private ImageView mIvTitleRight;

    private ImageView mIvTitleLeft;

    private DeviceAdapter mDeviceAdapter;

    private Context getContext() {
        return mVTitle.getContext();
    }

    private int getColor(int id) {
        return mVTitle.getResources().getColor(id);
    }

    public MainTitleHelper(View vTitle, int style) {
        mVTitle = vTitle;
        mStyle = style;
        mTvTitle = (TextView) mVTitle.findViewById(R.id.tv_title);
        mIvTitleRight = (ImageView) mVTitle.findViewById(R.id.iv_right);
        mIvTitleLeft = (ImageView) mVTitle.findViewById(R.id.iv_back);
        init();
        mDeviceAdapter = new DeviceAdapter(getContext());
        ChangeThemeUtils.adjustStatusBar(mVTitle, getContext());
        mTvTitle.setOnClickListener(this);
        mIvTitleLeft.setOnClickListener(this);
        mIvTitleRight.setOnClickListener(this);
    }

    /**
     * 初始化，根据类型来分类显示
     */

    private void init() {
        switch (mStyle) {
            case STYLE_LOCATE:
                mIvTitleLeft.setVisibility(View.GONE);
                mIvTitleRight.setVisibility(View.GONE);
                break;
            case STYLE_HISTORY:
                mIvTitleLeft.setVisibility(View.GONE);
                break;
            case STYLE_HEALTH:
                mIvTitleLeft.setVisibility(View.VISIBLE);
                mIvTitleRight.setVisibility(View.GONE);
                break;
            case STYLE_MESSAGE:
                mIvTitleLeft.setVisibility(View.GONE);
                break;
            case STYLE_SETTING:
                mIvTitleLeft.setVisibility(View.VISIBLE);
                mIvTitleRight.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 设置title
     *
     * @param title
     */
    public void setTitle(String title) {
        if (mTvTitle != null && !TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            sendBroadcast(new Intent(BroadcastActions.ACTION_MAIN_ACTIVITY_SELECT_TAB_LOCATE));
        } else if (id == R.id.iv_right) {
            if (mStyle == STYLE_HISTORY || mStyle == STYLE_MESSAGE) {
                showCalendarPop();
            }
        } else if (id == R.id.tv_title) {
            showDevicePop();
        }
    }

    private void sendBroadcast(Intent it) {
        getContext().sendBroadcast(it);
    }

    private PopupWindowHelper mCalendarPop;

    private CalendarHelper mCalendarHelper;

    /**
     * 显示日历控件
     */
    private void showCalendarPop() {
        if (mCalendarPop == null) {
            mCalendarPop = new PopupWindowHelper(getContext());
            mCalendarHelper = new CalendarHelper(getContext());
            mCalendarPop.setView(mCalendarHelper.getView(), mVTitle.getWidth(), 0);
        }
        mCalendarHelper.setDataList(2015, 9);
        mCalendarPop.setHeight(DisplayUtil.dip(313) + 2);
        mCalendarPop.showAsDropDown(mVTitle);
    }

    private PopupWindowHelper mDevicePop;

    private ListView mLvDevice;

    /**
     * 显示设备信息的弹窗
     */
    private void showDevicePop() {
        if (mDevicePop == null) {
            mDevicePop = new PopupWindowHelper(getContext());
            mLvDevice = new ListView(getContext());
            mLvDevice.setDivider(new ColorDrawable(getColor(R.color.divider_list)));
            mLvDevice.setDividerHeight(1);
            mLvDevice.setCacheColorHint(Color.TRANSPARENT);
            mLvDevice.setSelector(new ColorDrawable(Color.TRANSPARENT));
            mLvDevice.setAdapter(mDeviceAdapter);
            mLvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DeviceInfo info = (DeviceInfo) mDeviceAdapter.getItem(position);
                    Intent it = new Intent(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
                    Bundle b = new Bundle();
                    b.putSerializable(KEY_DEVICE_INFO, info);
                    it.putExtras(b);
                    sendBroadcast(it);
                    setTitle(info.name);
                    if (mDevicePop != null) {
                        mDevicePop.dismiss();
                    }
                }
            });
            mDevicePop.setView(mLvDevice, mVTitle.getWidth(), 0);
        }
        mDevicePop.setHeight(mDeviceAdapter.getCount() * (DisplayUtil.dip(60) + 1));
        mDevicePop.showAsDropDown(mVTitle);
    }

    /**
     * 设置设备数据
     *
     * @param list
     */
    public void setDevice(List<DeviceInfo> list) {
        if (list != null && list.size() > 0) {
            setTitle(list.get(0).name);
            mDeviceAdapter.setData(list);
        }
    }

    class DeviceAdapter extends BaseListAdapter<DeviceInfo> {

        public DeviceAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflate(R.layout.item_device);
                holder.ivAvator = (ImageView) convertView.findViewById(R.id.iv_avator);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DeviceInfo info = mDataList.get(position);
            if (info != null) {
                if (position == mDataList.size() - 1) {
                    holder.ivAvator.setImageResource(R.drawable.ico_equipment);
                } else {
                    holder.ivAvator.setImageResource(R.drawable.img_head_boy1);
                }
                holder.tvName.setText(info.name);
            }

            return convertView;
        }

        final class ViewHolder {
            TextView tvName;

            ImageView ivAvator;
        }
    }

    public static class DeviceInfo implements Serializable {
        public String avator;

        public String name;

        public int id;

        public DeviceInfo(int id, String avator, String name) {
            this.id = id;
            this.avator = avator;
            this.name = name;
        }
    }
}
