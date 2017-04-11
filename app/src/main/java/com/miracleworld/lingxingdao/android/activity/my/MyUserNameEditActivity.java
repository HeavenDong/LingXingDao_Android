package com.miracleworld.lingxingdao.android.activity.my;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;


/**
 * 用户名编辑页面
 */
public class MyUserNameEditActivity extends BaseActivity {
    private EditText my_name_edit;
    @Override
    protected void initView() {
        my_name_edit = (EditText) findViewById(R.id.my_name_edit);
        findViewById(R.id.name_title_left).setOnClickListener(this);
        findViewById(R.id.name_title_right).setOnClickListener(this);
        findViewById(R.id.my_name_delect).setOnClickListener(this);
    }
    @Override
    public void setContentLayout() {
        setContentView(R.layout.my_username_edit);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.name_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.name_title_right:
                if (TextUtils.isEmpty(my_name_edit.getText().toString())) {
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.my_text15));
                    return;
                }else if(!CommanUtil.isUserName(my_name_edit.getText().toString())){
                    final AlertDialog dialog=  new AlertDialog.Builder(MyUserNameEditActivity.this).create();
                    dialog.show();
                    dialog.setCancelable(false);
                    Window window=dialog.getWindow();
                    View dialogView=View .inflate(MyUserNameEditActivity.this, R.layout.my_username_dialog, null);
                    window.setContentView(dialogView);
                    dialogView.findViewById(R.id.username_dialog_sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    return;
                }else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("userName", my_name_edit.getText().toString());
                    intent.putExtras(bundle);
                    setResult(544, intent);
                     finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                break;
            case R.id.my_name_delect:
                my_name_edit.setText("");
                break;
        }
    }
}
