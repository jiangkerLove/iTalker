package com.example.italker;


import com.example.common.app.BaseActivity;
import com.example.italker.activities.MainActivity;
import com.example.italker.frags.assist.PermissionsFragment;

public class LaunchActivity extends BaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(PermissionsFragment.haveAll(this, getSupportFragmentManager())){
            MainActivity.show(this);
            finish();
        }
    }
}
