package com.example.italker.helper;

import android.content.Context;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 解决对Fragment的调度与重用问题
 * 达到最优的fragment切换
 */

public class NavHelper<T> {

    //用于初始化一些必须要的参数
    private final Context mContext;
    private final int mContainerId;
    private final FragmentManager mFragmentManager;
    private final Tab.OnTabChangedListener<T> mListener;
    //所有的Tab集合
    private final SparseArray<Tab<T>> mtabs = new SparseArray<>();
    //当前的一个选中的Tab
    private Tab<T> mCurrentTab;

    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, Tab.OnTabChangedListener<T> listener) {
        mContext = context;
        mContainerId = containerId;
        mFragmentManager = fragmentManager;
        mListener = listener;
    }

    /**
     * 添加Tab
     * @param menuId Tab对应的菜单Id
     * @param tab
     */
    public NavHelper<T> add(int menuId,Tab<T> tab){
        mtabs.put(menuId,tab);
        return this;
    }

    /**
     * 获取当前显示的Tab
     * @return 当前的Tab
     */
    public Tab<T> gerCurrentTab(){
        return mCurrentTab;
    }

    /**
     * 执行点击菜单的操作
     * @param menuId 菜单的Id
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int menuId){
        //集合中寻找点击的菜单对应的Tab，如果有则进行处理
        Tab<T> tab = mtabs.get(menuId);
        if(tab != null){
            doSelect(tab);
        }
        return true;
    }

    /**
     * 进行真实的Tab选择操作
     * @param tab
     */
    private void doSelect(Tab<T> tab){
        Tab<T> oldTab = null;
        if(mCurrentTab != null){
            oldTab = mCurrentTab;
            if(oldTab == tab){
                //如果说当前的tab就是点击的tab，不做操作
                notifyTabReselect(tab);
                return;
            }
        }
        //赋值并调用切换方法
        mCurrentTab = tab;
        doTabChanged(mCurrentTab,oldTab);
    }

    /**
     * Fragment的真实操作
     * @param newTab
     * @param oldTab
     */
    private void doTabChanged(Tab<T> newTab,Tab<T> oldTab){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if(oldTab != null){
            if(oldTab.fragment != null){
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab != null) {
            if(newTab.fragment==null){
                //首次新建
                Fragment fragment = Fragment.instantiate(mContext, newTab.clx.getName(), null);
                //缓存起来
                newTab.fragment = fragment;
                //提交到FragmentManager
                ft.add(mContainerId,fragment,newTab.clx.getName());
            }else {
                //从提交到FragmentManager的缓存空间中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        //提交事务
        ft.commit();
        notifyTabSelect(newTab,oldTab);
    }

    //会调我们的监听器
    private void notifyTabSelect(Tab<T> newTab,Tab<T> oldTab){
        if (mListener != null) {
            mListener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab){
        //TODO 二次点击Tab所做的操作
    }

    /**
     * 我们的所有的Tab基础属性
     * @param <T> 范型的额外参数
     */
    public static class Tab<T>{

        //Fragment对应的class信息
        public Class<?> clx;
        //额外的字段，用户自己设定需要使用
        public T extra;
        //内部缓存的对应的Fragment，Package权限
        Fragment fragment;

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        public interface OnTabChangedListener<T>{
            void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
        }
    }
}
