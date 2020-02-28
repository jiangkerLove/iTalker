package com.example.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.common.R;
import com.example.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * TODO: document your custom view class.
 */
public class GalleryView extends RecyclerView {

    private static final int LOADER_ID = 0x0100;
    private static final int MAX_IMAGE_COUNT = 3;//最大的选中图片数量
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024;//最大的选中图片数量
    private LoaderCallback mLoaderCallback = new LoaderCallback();
    private Adapter mAdapter = new Adapter();
    private List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mListener;

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //Cell点击操作，如果说我们的的点击是允许的，那么更新对应的Cell状态并更新界面
                //如果我们不能允许点击,已经达到最大的选中数量，那么就不刷新界面
                if (onItemSelectClick(image)) {
                    //noinspection unchecked
                    holder.updateData(image);
                }
            }
        });
    }

    /**
     * Cell点击的具体逻辑
     *
     * @param image
     * @return True，代表我进行了数据更改，你要刷新，反之则不用
     */
    private boolean onItemSelectClick(Image image) {
        //是否需要进行更新
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            //如果之前在那么现在就移除
            mSelectedImages.remove(image);
            image.isSelect = false;
            //状态已经改变则需要更新
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {
                //得到提示文字
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                //格式化填充
                str = String.format(str,MAX_IMAGE_COUNT);
                Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }
        //如果数据有更改，那么我们需要通知外面的监听者我们的数据有改变
        if (notifyRefresh)
            notifySelectChanged();
        return true;
    }

    /**
     * 初始化方法
     *
     * @param loaderManager Loader管理器
     * @return 任何一个LOADER_ID 可用于销毁Loader
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        this.mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * 得到选中的图片的全部地址
     *
     * @return 返回一个数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    //可以进行清空选中的图片
    public void clear() {
        for (Image image : mSelectedImages) {
            //一定要先重置状态
            image.isSelect = false;
        }
        mSelectedImages.clear();
        //通知更新
        mAdapter.notifyDataSetChanged();
    }

    //通知选中状态改变
    private void notifySelectChanged() {
        //判断是否有监听者，有则通知
        SelectedChangeListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    //通知Adapter数据更改的方法
    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }

    //用于实际的数据加载的Loader Callback
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,//Id
                MediaStore.Images.Media.DATA,//路径
                MediaStore.Images.Media.DATE_ADDED//图片的创建时间
        };

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            //创建一个Loader
            if (id == LOADER_ID) {
                //如果是我们的ID则可以进行初始化
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");//倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            //当Loader加载完成时
            List<Image> images = new ArrayList<>();
            //判断是否有数据
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    //移动游标到开始位置
                    data.moveToFirst();
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        //循环读取，知道没有下一条数据
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long dateTime = data.getLong(indexDate);
                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            //如果没有图片，或者图片大小太小，则跳过
                            continue;
                        }
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dateTime;
                        images.add(image);
                    } while (data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            //当Loader销毁或者重置,界面清空操作
            updateSource(null);
        }
    }

    //内部的数据结构
    private static class Image {
        int id;//数据的id
        String path;//图片的路径
        long date;//创建的日期
        boolean isSelect;//是否选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Image image = (Image) o;
            return path != null ? Objects.equals(path, image.path) : image.path == null;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }

    }

    /**
     * Cell 对应的Holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)//加载路径
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存，直接重原图加载
                    .centerCrop()//居中剪切
                    .placeholder(R.color.grey_200)//默认颜色
                    .into(mPic);

            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }


    /**
     * 对外的一个监听器
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }
}