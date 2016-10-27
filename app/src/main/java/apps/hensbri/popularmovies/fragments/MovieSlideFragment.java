package apps.hensbri.popularmovies.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.adapters.ViewPagerAdapter;
import apps.hensbri.popularmovies.helpers.Utilities;

public class MovieSlideFragment extends Fragment {

    private ViewPagerAdapter mViewPagerAdapter;
    public ViewPager mViewPager;

    public MovieSlideFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.movie_slide_fragment, container, false);

        mViewPagerAdapter =
                new ViewPagerAdapter(
                        getContext(),
                        getFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Utilities.setLastTabPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( mViewPager != null )
            mViewPager.setCurrentItem(Utilities.getLastTabPosition());
    }
}
