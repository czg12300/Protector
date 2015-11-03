
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cn.common.ui.BaseDialog;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.helper.SelectPhotoHelper;
import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：选择头像页面 Created by jakechen on 2015/8/27.
 */
public class ChooseAvatorActivity extends CommonTitleActivity implements View.OnClickListener {
    private GridView mGvAvators;

    private ImageButton mIbAdd;

    private BaseDialog mSelectDialog;

    private SelectPhotoHelper mSelectPhotoHelper;

    protected void hideDialog() {
        if (mSelectDialog != null) {
            mSelectDialog.dismiss();
        }
    }

    @Override
    protected View getTitleLayoutView() {
        View vTitle = getLayoutInflater().inflate(R.layout.title_choose_avator, null);
        mIvBack = (ImageView) vTitle.findViewById(R.id.iv_back);
        mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
        mIbAdd = (ImageButton) vTitle.findViewById(R.id.ib_add);
        return vTitle;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_choose_avator);
        setTitle(R.string.title_choose_avator);
        setBackgroundColor(getColor(R.color.background_white));
        mGvAvators = (GridView) findViewById(R.id.gv_avator);
    }

    @Override
    protected void initEvent() {
        mIvBack.setOnClickListener(this);
        mIbAdd.setOnClickListener(this);
        mGvAvators.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    if (imageView.getDrawable() instanceof BitmapDrawable) {
                        setResult(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                    }
                }

            }
        });
    }

    @Override
    protected void initData() {
        mGvAvators.setAdapter(new AvatorAdapter(this, getAvatorList()));
    }

    private List<Integer> getAvatorList() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(R.drawable.img_head_baby);
        list.add(R.drawable.img_head_boy1);
        list.add(R.drawable.img_head_boy2);
        list.add(R.drawable.img_head_girl1);
        list.add(R.drawable.img_head_girl2);
        list.add(R.drawable.img_head_mather);
        list.add(R.drawable.img_head_father);
        list.add(R.drawable.img_head_grandfa);
        list.add(R.drawable.img_head_grandma);
        return list;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_next) {
            goActivity(ChooseAvatorActivity.class);
        } else if (id == R.id.iv_back) {
            if (!isFinishing()) {
                finish();
            }
            onBack();
        } else if (id == R.id.ib_add) {
            showSelectDialog();
        } else if (id == R.id.tv_itme1) {
            if (mSelectPhotoHelper != null) {
                mSelectPhotoHelper.selectPhotoFromSystem();
            }
            hideSelectDialog();
        } else if (id == R.id.tv_itme2) {
            if (mSelectPhotoHelper != null) {
                mSelectPhotoHelper.takePhoto();
            }
            hideSelectDialog();
        }
    }

    private void showSelectDialog() {
        if (mSelectDialog == null) {
            mSelectDialog = new BaseDialog(this);
            mSelectDialog.setWindow(R.style.alpha_animation, 0.6f);
            mSelectDialog.setCanceledOnTouchOutside(true);
            mSelectDialog.setContentView(R.layout.dialog_select_avator);
            mSelectDialog.findViewById(R.id.tv_itme1).setOnClickListener(this);
            mSelectDialog.findViewById(R.id.tv_itme2).setOnClickListener(this);
            mSelectPhotoHelper = new SelectPhotoHelper(this, new SelectPhotoHelper.Callback() {
                @Override
                public void handleResult(Bitmap bitmap) {
                    setResult(bitmap);
                }
            });
            mSelectPhotoHelper.setCutWidth(100);
            mSelectPhotoHelper.setCutHeight(100);
            mSelectPhotoHelper.setPictureSavaPath("/avator");
            mSelectPhotoHelper.setIsSavaPhoto(true);
        }
        mSelectDialog.show();
    }

    private void setResult(Bitmap bitmap) {
        Intent intent = new Intent();
        intent.putExtra("bitmap_result", bitmap);
        setResult(RESULT_CODE, intent);
        finish();
    }

    private void hideSelectDialog() {
        if (mSelectDialog != null && mSelectDialog.isShowing()) {
            mSelectDialog.dismiss();
        }
    }

    class AvatorAdapter extends BaseListAdapter<Integer> {
        public AvatorAdapter(Context context, List<Integer> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = null;
            if (convertView == null) {
                iv = new ImageView(getContext());
                iv.setPadding(0, (int) getDimension(R.dimen.out_margin), 0,
                        (int) getDimension(R.dimen.out_margin));
                convertView = iv;
            } else {
                iv = (ImageView) convertView;
            }
            Integer id = mDataList.get(position);
            if (isAvailable(id)) {
                iv.setImageResource(id.intValue());
            }
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSelectPhotoHelper != null) {
            mSelectPhotoHelper.onActivityResult(requestCode, resultCode, data);
        }
    }
}
