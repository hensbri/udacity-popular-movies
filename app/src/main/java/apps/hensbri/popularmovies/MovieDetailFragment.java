package apps.hensbri.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.text.DecimalFormat;
import com.squareup.picasso.Picasso;

import apps.hensbri.popularmovies.data.ParcelableMovie;

/**
 * Created by hensbri on 8/11/16.
 */
public class MovieDetailFragment extends Fragment{
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private ParcelableMovie mMovie;

    public MovieDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null) {

            mMovie = intent.getExtras().getParcelable("movie");

            if (mMovie != null) {
                getActivity().setTitle(mMovie.getTitle());

                ((TextView) rootView.findViewById(R.id.movie_title))
                        .setText(mMovie.getTitle());

                ((TextView) rootView.findViewById(R.id.movie_overview))
                        .setText(mMovie.getOverview());

                ((TextView) rootView.findViewById(R.id.move_release_date))
                        .setText(mMovie.getReleaseDate());

                ImageView moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);

                // Source: http://square.github.io/picasso/
                Picasso.with(getContext())
                        .load(mMovie.getPosterURLString(getString(R.string.movie_poster_detail_size)))
                        .fit()
                        .centerInside()
                        .into(moviePoster);

                float ratingScale = Float.parseFloat(getString(R.string.rating_scale_factor));

                // Source: https://developer.android.com/reference/android/widget/RatingBar.html
                ((RatingBar) rootView.findViewById(R.id.movie_ratingbar))
                        .setRating(mMovie.getRating() / ratingScale);

                DecimalFormat decFmt = new DecimalFormat("#0.##");

                ((TextView) rootView.findViewById(R.id.movie_rating))
                        .setText(decFmt.format(mMovie.getRating()));
            }
        }
        return rootView;
    }
}
