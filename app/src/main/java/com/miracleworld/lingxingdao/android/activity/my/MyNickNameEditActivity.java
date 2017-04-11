package com.miracleworld.lingxingdao.android.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;


/**
 * 昵称编辑页面
 */
public class MyNickNameEditActivity extends BaseActivity {
    private EditText my_nick_edit;
    @Override
    protected void initView() {
        my_nick_edit = (EditText) findViewById(R.id.my_nick_edit);
        String nick=getIntent().getExtras().getString("oldeName");
        if (!nick.equals(getResources().getString(R.string.my_text13))) {
            my_nick_edit.setText(nick);
        }
        findViewById(R.id.nick_title_left).setOnClickListener(this);
        findViewById(R.id.nick_title_right).setOnClickListener(this);
        findViewById(R.id.my_nick_delect).setOnClickListener(this);
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.my_nickname_edit);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.nick_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.nick_title_right:
                if (TextUtils.isEmpty(my_nick_edit.getText().toString())) {
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.my_text15));
                    return;
                }else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("nickName", my_nick_edit.getText().toString());
                    intent.putExtras(bundle);
                    setResult(541, intent);
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                break;
            case R.id.my_nick_delect:
                my_nick_edit.setText("");
                break;
        }
    }
}
