package com.example.babar.e_rev;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class my_PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public my_PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                tab1 tab1 = new tab1();
                return tab1;
            case 1:
                tab2 tab2 = new tab2();
                return tab2;
            case 2:
                tab3 tab3 = new tab3();
                return tab3;
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return mNumOfTabs;
    }
}
