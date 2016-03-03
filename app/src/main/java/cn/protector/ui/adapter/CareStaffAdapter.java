package cn.protector.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.widgt.RoundImageView;
import cn.protector.R;
import cn.protector.logic.entity.CareStaffInfo;

public class CareStaffAdapter extends BaseListAdapter<CareStaffInfo> {
  private View.OnClickListener listener;

  public CareStaffAdapter(Context context,View.OnClickListener listener) {
    super(context);
    this.listener = listener;
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflate(R.layout.item_care_staff);
      holder.rivAvatar = (RoundImageView) convertView.findViewById(R.id.riv_avator);
      holder.tvRelationship = (TextView) convertView.findViewById(R.id.tv_relationship);
      holder.tvManager = (TextView) convertView.findViewById(R.id.tv_manager);
      holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
      convertView.setOnClickListener(listener);
      convertView.setTag(R.layout.item_care_staff, holder);
    } else {
      holder = (ViewHolder) convertView.getTag(R.layout.item_care_staff);
    }
    CareStaffInfo info = mDataList.get(position);
    if (info != null) {
      convertView.setTag(info);
      if (info.isManager()) {
        convertView.setEnabled(false);
        holder.tvManager.setVisibility(View.VISIBLE);
      } else {
        convertView.setEnabled(true);
        holder.tvManager.setVisibility(View.GONE);
      }
      holder.tvRelationship.setText("（" + info.getRelation() + "）");
      if (!TextUtils.isEmpty(info.getNickName())) {
        holder.tvNickName.setText(info.getNickName());
      }
    }
    return convertView;
  }

  final class ViewHolder {
    RoundImageView rivAvatar;

    TextView tvRelationship;

    TextView tvManager;
    TextView tvNickName;
  }
}