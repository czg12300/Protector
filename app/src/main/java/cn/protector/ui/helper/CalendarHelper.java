
package cn.protector.ui.helper;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import cn.common.ui.BasePopupWindow;
import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;

import java.util.ArrayList;
import java.util.List;

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

    private BasePopupWindow mCalendarPop;

    private OnCalendarListener mOnCalendarListener;

    private TextView mTvCenter;

    public CalendarHelper(Context context) {
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(R.layout.layout_calendar, null);
        mGvCalendar = (GridView) findViewById(R.id.gv_calendar);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mCalendarAdapter = new CalendarAdapter(context);
        mGvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCalendarAdapter.setSelectItem(position);
                TimeInfo info = (TimeInfo) parent.getAdapter().getItem(position);
                if (info != null) {
                    mTvCenter.setText(info.month + "月" + info.day + "日");
                    if (mOnCalendarListener != null && info != null) {
                        mOnCalendarListener.onItemClick(position,
                                formatDate(info.year, info.month, info.day));
                    }
                }
                if (view != null) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCalendarPop.dismiss();
                        }
                    }, 500);
                } else {
                    mCalendarPop.dismiss();
                }
            }
        });
        mGvCalendar.setAdapter(mCalendarAdapter);
        int[] today = DateUtil.getDateInt();
        mTvCenter.setText(today[1] + "月" + today[2] + "日");
    }

    public void setSelectItem(int year, int month, int day) {
        if (mCalendarAdapter != null && mCalendarAdapter.getCount() > 0) {
            for (int i = 0; i < mCalendarAdapter.getCount(); i++) {

                TimeInfo info = mCalendarAdapter.getDataList().get(i);
                if (info != null && info.year == year && info.month == month && info.day == day) {
                    mCalendarAdapter.setSelectItem(i);
                    return;
                }
            }
        }
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
            info.day = dayInfo.day;
            info.year = year;
            info.month = month;
            list.add(info);
        }
        mCalendarAdapter.setData(list);
    }

    public String formatDate(int year, int month, int day) {
        String monthStr = "" + month;
        String dayStr = "" + day;
        if (month < 10) {
            monthStr = "0" + month;
        }
        if (day < 10) {
            dayStr = "0" + day;
        }
        return year + "-" + monthStr + "-" + dayStr;
    }

    private View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    private class CalendarAdapter extends BaseListAdapter<TimeInfo> {
        public CalendarAdapter(Context context) {
            super(context);
        }

        private int selectIndex;

        public void setSelectItem(int position) {
            selectIndex = position;
            notifyDataSetChanged();
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
                // holder.ivTip = (ImageView)
                // convertView.findViewById(R.id.iv_tip);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TimeInfo info = mDataList.get(position);
            if (info != null) {
                holder.tvDate.setText("" + info.day);
                if (!TextUtils.isEmpty(info.tip)) {
                    holder.tvMonthTip.setVisibility(View.VISIBLE);
                    holder.tvMonthTip.setText(info.tip);
                } else {
                    holder.tvMonthTip.setVisibility(View.GONE);
                }
                // if (info.hasData) {
                // holder.ivTip.setVisibility(View.VISIBLE);
                // } else {
                // holder.ivTip.setVisibility(View.GONE);
                // }
                holder.tvDate.setTextColor(mContext.getResources()
                        .getColorStateList(R.drawable.text_selector_calendar_item));
                holder.tvDate.setBackgroundResource(R.drawable.bg_selector_calendar_item);
                switch (info.status) {
                    case TimeInfo.STATUS_TODAY:
                        holder.tvTodayTip.setVisibility(View.VISIBLE);
                        break;
                    case TimeInfo.STATUS_NOT_POSITION_MONTH:
                        holder.tvDate.setTextColor(getColor(R.color.gray_999999));
                        holder.tvDate.setBackgroundColor(getColor(R.color.white));
                        holder.tvTodayTip.setVisibility(View.GONE);
                        break;
                    case TimeInfo.STATUS_POSITION_MONTH:
                        holder.tvTodayTip.setVisibility(View.GONE);
                        break;
                }
                if (selectIndex == position) {
                    holder.tvDate.setSelected(true);
                } else {
                    holder.tvDate.setSelected(false);
                }

            }
            return convertView;
        }

        final class ViewHolder {
            public TextView tvDate;

            public TextView tvMonthTip;

            public TextView tvTodayTip;

            // public ImageView ivTip;

        }
    }

    private static class TimeInfo {
        public static final int STATUS_TODAY = 1;

        public static final int STATUS_NOT_POSITION_MONTH = 2;

        public static final int STATUS_POSITION_MONTH = 3;

        int status;

        String tip;

        int year;

        int month;

        int day;

        boolean hasData;
    }

    public View getView() {
        return mContentView;
    }

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

    public void setOnCalendarListener(OnCalendarListener listener) {
        if (listener != null) {
            mOnCalendarListener = listener;
        }
    }

    public static interface OnCalendarListener {
        void onItemClick(int position, String date);
    }
}
