package apps.hensbri.popularmovies.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.fragments.MovieGridViewFragment;

/**
 * Sourced: https://developer.android.com/samples/SlidingTabsBasic/index.html
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final String LOG_TAG = ViewPagerAdapter.class.getSimpleName();

    private Context mContext;
    private static final int FRAGMENT_COUNT = 3;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new MovieGridViewFragment();
        Bundle args = new Bundle();
        args.putInt(MovieGridViewFragment.ARG_ID, i);
        args.putString(MovieGridViewFragment.ARG_REQUEST_TYPE,
                mContext.getResources().getStringArray(R.array.tab_keys)[i]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getStringArray(R.array.tab_labels)[position];
    }
}
