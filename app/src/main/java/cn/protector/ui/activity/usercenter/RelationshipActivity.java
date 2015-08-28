
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.util.List;

import cn.protector.R;
import cn.protector.data.BroadcastActivions;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.MainActivity;

/**
 * Created by jakechen on 2015/8/27.
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
            goActivity(MainActivity.class);
            sendBroadcast(new Intent(BroadcastActivions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN));
        }
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActivions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActivions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN)) {
            finish();
        }
    }
}
