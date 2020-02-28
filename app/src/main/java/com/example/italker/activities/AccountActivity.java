package com.example.italker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.common.app.BaseActivity;
import com.example.italker.R;
import com.example.italker.frags.account.UpdateInfoFragment;
import com.yalantis.ucrop.UCrop;

public class AccountActivity extends BaseActivity {

    private Fragment mCurFragment;

    /**
     * 账户Activity显示的入口
     * @param context
     */
    public static void show(Context context){
        context.startActivity(new Intent(context,AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mCurFragment = new UpdateInfoFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container,mCurFragment)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode,resultCode,data);
    }
}
