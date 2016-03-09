package cn.protector.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;

public class RelationshipAdapter extends BaseListAdapter<String> {
  public RelationshipAdapter(Context context) {
    super(context, getData());
  }
  private static final String[] data = {"其他", "爸爸", "妈妈", "哥哥", "姐姐", "爷爷", "奶奶", "公公", "婆婆", "叔叔", "阿姨"};

  private static ArrayList<String> getData() {
    ArrayList<String> list = new ArrayList<>();
    for (String str : data) {
      list.add(str);
    }
    return list;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView;
    if (convertView == null) {
      convertView = inflate(R.layout.item_textview);
      textView = (TextView) convertView.findViewById(R.id.tv_content);
      convertView.setTag(textView);
    } else {
      textView = (TextView) convertView.getTag();
    }
    textView.setText("" + mDataList.get(position));
    return convertView;
  }
}