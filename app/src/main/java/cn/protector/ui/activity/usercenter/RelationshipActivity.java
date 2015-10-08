
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.util.List;

import cn.protector.ProtectorApplication;
import cn.protector.R;
import cn.protector.data.BroadcastActions;
import cn.protector.data.InitSharedData;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.MainActivity;

/**
 * 描述：我与宝贝关系
 *
 * @author Created by jakechen on 2015/8/27.
 */
public class RelationshipActivity extends CommonTitleActivity implements View.OnClickListener {
    private Button btnSubmit;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_relationship);
        setTitle(R.string.title_relationship);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
    }

    @Override
    protected void initEvent() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            ProtectorApplication app = (ProtectorApplication) ProtectorApplication.getInstance();
            if (!app.isShowMain()) {
                InitSharedData.setUserId(21);
                goActivity(MainActivity.class);
            }
            sendBroadcast(new Intent(BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN));
        }
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN)) {
            finish();
        }
    }
}
