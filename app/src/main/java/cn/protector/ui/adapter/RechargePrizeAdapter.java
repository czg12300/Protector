package cn.protector.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;
import cn.protector.logic.entity.PrizeInfo;

/**
 * 描述：
 * 作者：jake on 2016/3/12 18:34
 */
public class RechargePrizeAdapter extends BaseListAdapter<PrizeInfo> {
    public RechargePrizeAdapter(Context context) {
        super(context, getPrizeData());
    }

    private static ArrayList<PrizeInfo> getPrizeData() {
        ArrayList<PrizeInfo> list = new ArrayList<>();
        list.add(new PrizeInfo("1个月", "5元", 5, 1));
        list.add(new PrizeInfo("2个月", "10元", 10, 2));
        list.add(new PrizeInfo("3个月(9.8折)", "14.7元", 14.7, 3));
        list.add(new PrizeInfo("6个月(9.5折)", "28.5元", 28.5, 6));
        list.add(new PrizeInfo("1年(9折)", "54元", 54, 12));
        list.add(new PrizeInfo("2年(8折)", "96元", 96, 24));
        return list;
    }

    private int select = -1;


    public void setSelect(int select) {
        this.select = select;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.item_prize);
            holder = new ViewHolder();
            holder.vContent = convertView.findViewById(R.id.ll_content);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(mDataList.get(position).getTitle());
        holder.tvContent.setText(mDataList.get(position).getContent());
        if (position == select) {
            holder.vContent.setSelected(true);
        } else {
            holder.vContent.setSelected(false);
        }
        return convertView;
    }

    final class ViewHolder {
        View vContent;
        TextView tvTitle;
        TextView tvContent;
    }
}

