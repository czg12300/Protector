
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.widget.ImageEditText;

/**
 * 描述:搜索区域页面
 *
 * @author jakechen
 * @since 2015/9/22 14:21
 */
public class SearchAreaActivity extends CommonTitleActivity {
    private ImageEditText mEvKey;

    private TextView mTvSearching;

    private ListView mLvResult;

    private ResultAdapter mResultAdapter;

    @Override
    protected void initView() {
        setTitle(R.string.title_search_area);
        setContentView(R.layout.activity_search_area);
        mEvKey = (ImageEditText) findViewById(R.id.ev_key);
        mTvSearching = (TextView) findViewById(R.id.tv_searching);
        mLvResult = (ListView) findViewById(R.id.lv_result);
        mEvKey.setHint(R.string.searching_hint);
        // mEvKey.setOnKeyListener(new View.OnKeyListener() {
        // @Override
        // public boolean onKey(View v, int keyCode, KeyEvent event) {
        // if (keyCode == KeyEvent.KEYCODE_ENTER) {
        // List<AMapLocation> list = new ArrayList<AMapLocation>();
        // for (int i = 0; i < 5; i++) {
        // list.add(new AMapLocation(""));
        // }
        // mResultAdapter.setData(list);
        // }
        // return true;
        // }
        // });
        mResultAdapter = new ResultAdapter(this);
        mLvResult.setAdapter(mResultAdapter);
        List<AMapLocation> list = new ArrayList<AMapLocation>();
        for (int i = 0; i < 5; i++) {
            list.add(new AMapLocation(""));
        }
        mResultAdapter.setData(list);
    }

    private class ResultAdapter extends BaseListAdapter<AMapLocation> {
        public ResultAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflate(R.layout.item_search_result);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AMapLocation location = mDataList.get(position);
            if (location != null) {
                // TODO
            }
            return convertView;
        }

        final class ViewHolder {
            TextView tvTitle;

            TextView tvContent;
        }
    }

}
