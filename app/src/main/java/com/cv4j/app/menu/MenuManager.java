package com.cv4j.app.menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cv4j.app.R;
import com.cv4j.app.fragment.BinaryFragment;
import com.cv4j.app.fragment.FiltersFragment;
import com.cv4j.app.fragment.HistFragment;
import com.cv4j.app.fragment.HomeFragment;
import com.cv4j.app.fragment.IOFragment;
import com.cv4j.app.fragment.PixelOperatorFragment;
import com.cv4j.app.fragment.SpitalConvFragment;
import com.cv4j.app.fragment.TemplateMatchFragment;

/**
 * Created by tony on 2016/11/20.
 */

public class MenuManager {

    private static MenuManager instance = null;
    private FragmentManager fragmentManager;
    private MenuType curType;

    public enum MenuType {

        HOME("CV4J介绍",false),
        IO("io读写",true),
        FILTERS("常用过滤器",true),
        SPTIAL_CONV("空间卷积功能",true),
        BINARY("二值分析",true),
        HIST("直方图",true),
        TEMPLATE_MATCH("模版匹配",true),
        PIXEL_OPERATOR("模版匹配",true);

		/**
  		 * Menu's title
   		 */
        public final String title;
        
		/**
  		 * True if menu is removed, false otherwise
   		 */
        public final boolean removed;

		/**
	     * Set menu's title
	     *
	     * @param menu's title, removed or not
	     * @return the title
	     */
        MenuType(String title, boolean removed) {
            this.title = title;
            this.removed = removed;
        }

		/**
	     * Return menu's title
	     *
	     * @return the title
	     */
        public String getTitle() {
            return title;
        }

	    /**
	     * It is removed?
	     *
	     * @return 
	     */
        public boolean isRemoved() {
            return removed;
        }
    }

    private MenuManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        curType = MenuType.HOME;
    }

    /**
     * Return the instance
     *
     * @param fragment manager
     * @return MenuManager
     */
    public static MenuManager getInstance(FragmentManager fragmentManager) {
        if (instance == null) {
            instance = new MenuManager(fragmentManager);
        }

        return instance;
    }

    /**
     * Return menu type
     *
     * @param type
     * @return MenuType
     */
    public MenuType getCurType() {

        return curType;
    }

    /**
     * Show menu type
     *
     * @param type
     * @return
     */
    public boolean show(MenuType type) {
        if (curType.equals(type)) {
            return true;
        } else {
            hide(curType);
        }

        Fragment fragment = (Fragment) fragmentManager.findFragmentByTag(type.getTitle());
        if (fragment == null) {
            fragment = create(type);
            if (fragment == null) {
                return false;
            }
        }

        fragmentManager.beginTransaction().show(fragment).commit();
        curType = type;
        return true;
    }

    private Fragment create(MenuType type) {
        Fragment fragment = null;
        switch (type) {
            case HOME:
                fragment = new HomeFragment();
                break;

            case IO:
                fragment = new IOFragment();
                break;

            case FILTERS:
                fragment = new FiltersFragment();
                break;

            case SPTIAL_CONV:
                fragment = new SpitalConvFragment();
                break;

            case BINARY:
                fragment = new BinaryFragment();
                break;

            case HIST:
                fragment = new HistFragment();
                break;

            case TEMPLATE_MATCH:
                fragment = new TemplateMatchFragment();
                break;

            case PIXEL_OPERATOR:
                fragment = new PixelOperatorFragment();
                break;

            default:
                break;
        }
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment, type.getTitle()).commitAllowingStateLoss();
        return fragment;
    }

    private void hide(MenuType type) {
        Fragment fragment = (Fragment) fragmentManager.findFragmentByTag(type.getTitle());
        if (fragment != null) {
            if (type.isRemoved()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            } else {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                // ft.addToBackStack(type.getTitle());
                ft.hide(fragment);
                ft.commit();
            }
        }
    }

    /**
     * 判断某个fragment是否存在
     *
     * @param type
     * @return
     */
    public boolean isFragmentExist(MenuType type) {
        Fragment fragment = (Fragment) fragmentManager.findFragmentByTag(type.getTitle());
        return fragment != null;
    }

    /**
     * 返回菜单的总数
     *
     * @return
     */
    public int getMenuCount() {

        if (MenuType.values() != null) {
            return MenuType.values().length;
        }

        return 0;
    }

}
