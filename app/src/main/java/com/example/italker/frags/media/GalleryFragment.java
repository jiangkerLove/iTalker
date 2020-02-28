package com.example.italker.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.common.app.BaseFragment;
import com.example.common.tools.UiTool;
import com.example.common.widget.GalleryView;
import com.example.italker.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.qiujuer.genius.ui.Ui;

import java.util.Objects;

/**
 * 图片选择Fragment
 */
public class GalleryFragment extends BottomSheetDialogFragment
        implements GalleryView.SelectedChangeListener {

    private GalleryView mGallery;
    private OnselectedListener mListener;

    public GalleryFragment() { }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //我们先使用默认的dialog
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //获取GalleryView
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = root.findViewById(R.id.galleryView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        //如果选中了一张图片
        if (count > 0) {
            //隐藏自己
            dismiss();
            if (mListener != null) {
                //得到所有的选中的图片的路径
                String[] paths = mGallery.getSelectedPath();
                //返回第一张
                mListener.onSelectedImage(paths[0]);
                //取消和唤起者之间的引用，加快内存回收
                mListener = null;
            }
        }
    }

    /**
     * 设置事件监听并返回自己
     *
     * @param listener
     * @return
     */
    public GalleryFragment setListener(OnselectedListener listener) {
        mListener = listener;
        return this;
    }

    public interface OnselectedListener {
        void onSelectedImage(String path);
    }

    private static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if (window == null) return;
            //得到屏幕的高度
            int screenHeight = UiTool.getScreenHeight(Objects.requireNonNull(getOwnerActivity()));
            //得到状态拦的高度
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());

            //计算dialog的高度并设置
            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        }
    }
}
