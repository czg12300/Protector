package cn.protector.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.common.bitmap.core.ImageLoader;
import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.ChatMessage;
import cn.protector.ui.activity.WebActivity;

/**
 * 描述：消息页面的适配器
 *
 * @author Created by jakechen on 2015/9/4.
 */
public class MessageAdapter extends BaseListAdapter<ChatMessage> {
  public MessageAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflate(R.layout.item_message);
      holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avator);
      holder.ivVoiceNew = (ImageView) convertView.findViewById(R.id.iv_new_voice);
      holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
      holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
      holder.tvMessage = (TextView) convertView.findViewById(R.id.tv_message);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    final ChatMessage message = mDataList.get(position);
    if (message != null) {
      holder.tvMessage.setVisibility(View.VISIBLE);
      if (message.getTime() > InitSharedData.getMessageTime()) {
        holder.ivVoiceNew.setVisibility(View.VISIBLE);
      } else {
        holder.ivVoiceNew.setVisibility(View.GONE);
      }
      ImageLoader.getInstance().displayImage(message.getImage(), holder.ivAvatar);
      holder.tvMessage.setText(message.getContent());
      holder.tvName.setText(message.getSender());
      holder.tvTime.setText(ChatMessage.getFormatTime(message.getTime()));
      if (TextUtils.isEmpty(message.getUrl())) {
        convertView.setEnabled(false);
      } else {
        convertView.setEnabled(true);
      }
      convertView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (message != null && !TextUtils.isEmpty(message.getUrl())) {
            Bundle bundle = new Bundle();
            bundle.putString(WebActivity.KEY_TITLE, message.getContent());
            bundle.putString(WebActivity.KEY_URL, message.getUrl());
            WebActivity.start(v.getContext(), bundle);
          }
        }
      });

    }
    return convertView;
  }


  public void addToLast(ChatMessage message) {
    if (message != null) {
      mDataList.add(message);
    }
  }

  public void addToTop(ArrayList<ChatMessage> messages) {
    if (messages != null && messages.size() > 0) {
      mDataList.addAll(0, messages);
    }
  }

  static final class ViewHolder {
    public TextView tvName;
    public TextView tvTime;
    public TextView tvMessage;
    public ImageView ivAvatar;
    public ImageView ivVoiceNew;
  }
}
