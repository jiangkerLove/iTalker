package com.example.italker.frags.account;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.common.app.BaseApplication;
import com.example.common.app.BaseFragment;
import com.example.common.widget.PortraitView;
import com.example.italker.R;
import com.example.italker.frags.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更新信息的界面
 */
public class UpdateInfoFragment extends BaseFragment {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        new GalleryFragment().setListener((path) -> {
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setCompressionQuality(96);

            File dPath = BaseApplication.getPortraitTmpFile();

            UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(520, 520)
                    .withOptions(options)
                    .start(getActivity());
            //show的时候建议使用getChildFragmentManager
        }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从Activity传递过来的回调，然后取出其中的值进行图片加载
        //如果是我能够处理的类型
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //通过Ucrop得到对应的Uri
            final Uri resultUri = UCrop.getOutput(data);

            if (resultUri != null) {
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * 加载Uri到当前的头像中
     * @param uri
     */
    private void loadPortrait(Uri uri) {
        Glide.with(getContext())
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }
}
