package com.example.italker.frags.main;



import com.example.common.app.BaseFragment;
import com.example.common.widget.GalleryView;
import com.example.italker.R;

import butterknife.BindView;


public class ActiveFragment extends BaseFragment {

    @BindView(R.id.galleryView)
    GalleryView mGalley;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();
        mGalley.setup(getLoaderManager(), (count) -> {

        });
    }
}
