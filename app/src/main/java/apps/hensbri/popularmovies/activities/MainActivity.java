package apps.hensbri.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.fragments.MovieDetailFragment;
import apps.hensbri.popularmovies.fragments.MovieGridViewFragment;
import apps.hensbri.popularmovies.fragments.MovieSlideFragment;
import apps.hensbri.popularmovies.sync.MovieSyncAdapter;

/*
    The majority of this code was modeled using the Udacity lessons
    related to the Udacity Sunshine App as a template
 */
public class MainActivity extends AppCompatActivity
        implements MovieGridViewFragment.MovieSelectedCallback
{
    private static final String MOVIESLIDEFRAG_TAG = "MSFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_movie_slider, new MovieSlideFragment(), MOVIESLIDEFRAG_TAG)
                    .commit();
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }

        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onMovieSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, contentUri);

            MovieDetailFragment f = new MovieDetailFragment();
            f.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, f, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
