package cn.protector.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;
import cn.protector.logic.entity.ChatMessage;

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
            holder.ivAvator = (ImageView) convertView.findViewById(R.id.iv_avator);
            holder.ivVoiceNew = (ImageView) convertView.findViewById(R.id.iv_new_voice);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvMessage = (TextView) convertView.findViewById(R.id.tv_message);
            holder.tvVoice = (TextView) convertView.findViewById(R.id.tv_voice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChatMessage message = mDataList.get(position);
        if (message != null) {
            if (message.type == ChatMessage.TYPE_MESSAGE) {
                holder.tvMessage.setVisibility(View.VISIBLE);
                holder.tvVoice.setVisibility(View.GONE);
                holder.ivVoiceNew.setVisibility(View.GONE);
                holder.ivAvator.setImageResource(R.drawable.img_head_boy1);
                holder.tvMessage.setText(message.message);
            } else if (message.type == ChatMessage.TYPE_VOICE) {
                holder.tvMessage.setVisibility(View.GONE);
                holder.tvVoice.setVisibility(View.VISIBLE);
                holder.ivVoiceNew.setVisibility(View.VISIBLE);
                holder.ivAvator.setImageResource(R.drawable.img_head_girl1);
                holder.tvVoice.setText(message.message);
            }
            holder.tvName.setText(message.name);
            holder.tvTime.setText(message.time);

        }
        return convertView;
    }

    static final class ViewHolder {
        public TextView tvName;
        public TextView tvTime;
        public TextView tvMessage;
        public TextView tvVoice;
        public ImageView ivAvator;
        public ImageView ivVoiceNew;
    }
}
