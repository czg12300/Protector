package cn.protector.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.common.bitmap.core.ImageLoader;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.widgt.RoundImageView;
import cn.protector.R;
import cn.protector.logic.entity.DeviceInfo;

public class DeviceManagerAdapter extends BaseListAdapter<DeviceInfo> {

  private View.OnClickListener listener;

  public DeviceManagerAdapter(Context context, View.OnClickListener listener) {
    super(context);
    this.listener = listener;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflate(R.layout.item_device_manager);
      holder.rivAvatar = (RoundImageView) convertView.findViewById(R.id.iv_avatar);
      holder.tvRelationship = (TextView) convertView.findViewById(R.id.tv_relationship);
      holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
      holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
      holder.btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
      holder.btnDelete.setOnClickListener(listener);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    DeviceInfo info = mDataList.get(position);
    holder.btnDelete.setTag(info);
    if (info != null) {
      holder.tvName.setText("" + info.getNikeName());
      holder.tvAddress.setText("" + info.getAddress());
      ImageLoader.getInstance().displayImage(info.getAvatar(), holder.rivAvatar);
      holder.tvRelationship.setText("(" + DeviceInfo.parseRelation(info.getRelation(), info.getOtherRelation()) + ")");
    } return convertView;
  }

  final class ViewHolder {
    RoundImageView rivAvatar;

    TextView tvRelationship;

    TextView tvName;
    TextView tvAddress;
    Button btnDelete;
  }
}