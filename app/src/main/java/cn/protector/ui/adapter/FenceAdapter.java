package cn.protector.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;
import cn.protector.logic.entity.FenceInfo;

public class FenceAdapter extends BaseListAdapter<FenceInfo> {
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
            if (!TextUtils.isEmpty(info.getName())) {
                holder.tvIcon.setText(info.getName().substring(0, 1));
                holder.tvName.setText(info.getName());
            }
            holder.tvRange.setText(
                    getContext().getString(R.string.fence_range_string).replace("#", "" + info.getRadius()));
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