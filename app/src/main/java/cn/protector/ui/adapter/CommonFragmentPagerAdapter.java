package cn.protector.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {
	public CommonFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		mDataList = new ArrayList<Fragment>();
	}

	protected List<Fragment> mDataList;

	public void setData(List<Fragment> list) {
		if (list != null && list.size() > 0) {
			mDataList = list;
			notifyDataSetChanged();
		}
	}

	public void addAll(List<Fragment> list) {
		if (list != null && list.size() > 0) {
			mDataList.addAll(list);
			notifyDataSetChanged();
		}
	}

	public void addData(Fragment t) {
		if (t != null) {
			mDataList.add(t);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public android.support.v4.app.Fragment getItem(int position) {
		return mDataList.get(position);
	}

}
