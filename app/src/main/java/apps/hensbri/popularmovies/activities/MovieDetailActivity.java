package apps.hensbri.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.fragments.MovieDetailFragment;
import apps.hensbri.popularmovies.fragments.MovieReviewsFragment;
import apps.hensbri.popularmovies.fragments.MovieVideosFragment;

/**
 * Created by hensbri on 8/11/16.
 */
public class MovieDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_URI, getIntent().getData());

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}
