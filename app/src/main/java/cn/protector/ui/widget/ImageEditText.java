
package cn.protector.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.protector.R;

/**
 * 左边有图片的输入框 Created by jakechen on 2015/8/10.
 */
public class ImageEditText extends LinearLayout {

    public ImageEditText(Context context) {
        this(context, null);
    }

    public ImageEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.bg_selector_input_select);
        setOrientation(HORIZONTAL);
        final View root = LayoutInflater.from(getContext()).inflate(R.layout.layout_input_box,
                this);
        mIvleft = (ImageView) root.findViewById(R.id.iv_left);
        mEvCenter = (EditText) root.findViewById(R.id.ev_center);
        mEvCenter.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                root.setSelected(hasFocus);
                mIvleft.setSelected(hasFocus);
            }
        });
    }

    private ImageView mIvleft;

    private EditText mEvCenter;

    public void setInputType(int type) {
        mEvCenter.setInputType(type);
    }

    public void setLeftDrawable(int resId) {
        mIvleft.setImageResource(resId);
    }

    public void addTextChangedListener(TextWatcher listener) {
        mEvCenter.addTextChangedListener(listener);
    }

    public void setHint(int resId) {

        mEvCenter.setHint(resId);
    }

    public void setHint(CharSequence cs) {
        mEvCenter.setHint(cs);
    }

    public Editable getText() {
        return mEvCenter.getText();
    }

}
