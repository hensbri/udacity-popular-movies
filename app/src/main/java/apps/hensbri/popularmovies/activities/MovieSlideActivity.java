package apps.hensbri.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.fragments.MovieSlideFragment;

public class MovieSlideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_slide_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_slide_container, new MovieSlideFragment())
                    .commit();
        }
    }

}
