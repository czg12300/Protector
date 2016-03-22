
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.common.ui.BaseDialog;
import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;

/**
 * 描述：选择头像页面 Created by jakechen on 2015/8/27.
 */
public class ChooseAvatorActivity extends CommonTitleActivity implements View.OnClickListener {
    private static final int RESULT_CAMERA = 3211;

    private static final int RESULT_LOAD_IMAGE = 3212;

    private static final int PHOTO_REQUEST_CUT = 3213;

    private static File dir;

    static {
        dir = new File(Environment.getExternalStorageDirectory() + "/avatar");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private Uri takePhoto = Uri.fromFile(new File(dir, "big.jpg"));

    private Uri cutPhoto = Uri.fromFile(new File(dir, "small.jpg"));

    private GridView mGvAvators;

    private ImageButton mIbAdd;

    private BaseDialog mSelectDialog;

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
        if (id == R.id.iv_back) {
            if (!isFinishing()) {
                finish();
            }
            onBack();
        } else if (id == R.id.ib_add) {
            showSelectDialog();
        } else if (id == R.id.tv_itme1) {
            selectPhotoFromSystem();
            hideSelectDialog();
        } else if (id == R.id.tv_itme2) {
            takePhoto();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CAMERA:// 当选择拍照时调用
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(takePhoto);
                }
                break;
            case RESULT_LOAD_IMAGE:// 当选择从本地获取图片时
                // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    }
                }
                break;
            case PHOTO_REQUEST_CUT:// 返回的结果
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap photo =decodeFile(cutPhoto.getPath(),100,100);
                        setResult(photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }catch (Error error){}
                }

                break;
        }
    }

    public static Bitmap decodeFile(String pathName, int width, int height) {
        Bitmap bitmap = null;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, width * height);
            opts.inJustDecodeBounds = false;

            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Bitmap.Config.ALPHA_8;
            opts.inDither = true;
            try {
                bitmap = BitmapFactory.decodeFile(pathName, opts);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    bitmap = BitmapFactory.decodeFile(pathName, opts);
                } catch (OutOfMemoryError e1) {
                }
            } catch (Exception e2) {
            }
        return bitmap;
    }
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
                                        int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
                                               int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
                / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cutPhoto);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        i.putExtra(MediaStore.EXTRA_OUTPUT, takePhoto);
        startActivityForResult(i, RESULT_CAMERA);
    }

    /**
     * 从相册选择头像
     */
    public void selectPhotoFromSystem() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
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

}
