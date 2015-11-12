
package cn.protector.ui.helper;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.common.ui.BasePopupWindow;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.helper.PopupWindowHelper;
import cn.common.utils.DisplayUtil;
import cn.protector.R;

/**
 * 描述：用于日历逻辑
 *
 * @author jakechen on 2015/9/4.
 */
public class CalendarHelper {
    private GridView mGvCalendar;

    private CalendarAdapter mCalendarAdapter;

    private View mContentView;
    private Context mContext;

    public CalendarHelper(Context context) {
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(R.layout.layout_calendar, null);
        mGvCalendar = (GridView) findViewById(R.id.gv_calendar);
        mCalendarAdapter = new CalendarAdapter(context);
        mGvCalendar.setAdapter(mCalendarAdapter);
    }

    public void setDataList(int year, int month) {
        List<DateUtil.DayInfo> dayList = DateUtil.getDayListOfMonth(year, month);
        int[] today = DateUtil.getDateInt();
        List<TimeInfo> list = new ArrayList<TimeInfo>();
        for (int i = 0; i < dayList.size(); i++) {
            TimeInfo info = new TimeInfo();
            DateUtil.DayInfo dayInfo = dayList.get(i);
            if (dayInfo.type != DateUtil.DayInfo.TYPE_POSITION_MONTH) {
                info.status = TimeInfo.STATUS_NOT_POSITION_MONTH;
            } else {
                info.status = TimeInfo.STATUS_POSITION_MONTH;
            }
            if (dayInfo.day == 1) {
                if (dayInfo.type == DateUtil.DayInfo.TYPE_POSITION_MONTH) {
                    info.tip = month + "月";
                } else {
                    if (dayInfo.type == DateUtil.DayInfo.TYPE_NEXT_MONTH) {
                        info.tip = (month + 1) + "月";
                    }
                }
            }
            if (dayInfo.type == DateUtil.DayInfo.TYPE_POSITION_MONTH && i % 10 == 0) {
                info.hasData = true;
            } else {
                info.hasData = false;
            }
            if (dayInfo.type == DateUtil.DayInfo.TYPE_POSITION_MONTH && dayInfo.day == today[2]) {
                info.status = TimeInfo.STATUS_TODAY;
            }
            info.date = dayInfo.day + "";
            list.add(info);
        }
        mCalendarAdapter.setData(list);
    }

    private View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    private class CalendarAdapter extends BaseListAdapter<TimeInfo> {
        public CalendarAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflate(R.layout.item_calendar);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tvMonthTip = (TextView) convertView.findViewById(R.id.tv_month_tip);
                holder.tvTodayTip = (TextView) convertView.findViewById(R.id.tv_today);
                holder.ivTip = (ImageView) convertView.findViewById(R.id.iv_tip);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TimeInfo info = mDataList.get(position);
            if (info != null) {
                if (!TextUtils.isEmpty(info.date)) {
                    holder.tvDate.setText(info.date);
                }
                if (!TextUtils.isEmpty(info.tip)) {
                    holder.tvMonthTip.setVisibility(View.VISIBLE);
                    holder.tvMonthTip.setText(info.tip);
                } else {
                    holder.tvMonthTip.setVisibility(View.GONE);
                }
                if (info.hasData) {
                    holder.ivTip.setVisibility(View.VISIBLE);
                } else {
                    holder.ivTip.setVisibility(View.GONE);
                }
                switch (info.status) {
                    case TimeInfo.STATUS_TODAY:
                        holder.tvDate.setTextColor(getColor(R.color.white));
                        holder.tvDate.setBackgroundColor(getColor(R.color.blue_b3e5fc));
                        holder.tvTodayTip.setVisibility(View.VISIBLE);
                        break;
                    case TimeInfo.STATUS_NOT_POSITION_MONTH:
                        holder.tvDate.setTextColor(getColor(R.color.gray_999999));
                        holder.tvDate.setBackgroundColor(getColor(R.color.white));
                        holder.tvTodayTip.setVisibility(View.GONE);
                        break;
                    case TimeInfo.STATUS_POSITION_MONTH:
                        holder.tvDate.setTextColor(getColor(R.color.black_333333));
                        holder.tvDate.setBackgroundColor(getColor(R.color.white));
                        holder.tvTodayTip.setVisibility(View.GONE);
                        break;
                }

            }
            return convertView;
        }

        final class ViewHolder {
            public TextView tvDate;

            public TextView tvMonthTip;

            public TextView tvTodayTip;

            public ImageView ivTip;

        }
    }

    private class TimeInfo {
        public static final int STATUS_TODAY = 1;

        public static final int STATUS_NOT_POSITION_MONTH = 2;

        public static final int STATUS_POSITION_MONTH = 3;

        int status;

        String tip;

        String date;

        boolean hasData;
    }

    public View getView() {
        return mContentView;
    }

    private BasePopupWindow mCalendarPop;

    /**
     * 显示日历控件
     */
    public void showCalendar(View parent) {
        if (mCalendarPop == null) {
            mCalendarPop = new BasePopupWindow(mContext);
            mCalendarPop.setContentView(mContentView);
            mCalendarPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mCalendarPop.showAsDropDown(parent);
    }

}
