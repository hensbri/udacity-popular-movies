package apps.hensbri.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.data.MovieContract;
import apps.hensbri.popularmovies.helpers.OpenMovieConfig;
import apps.hensbri.popularmovies.helpers.Utilities;

public class MovieDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieWithFavoriteView.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieWithFavoriteView.COLUMN_MOVIE_ID,
            MovieContract.MovieWithFavoriteView.COLUMN_TITLE,
            MovieContract.MovieWithFavoriteView.COLUMN_OVERVIEW,
            MovieContract.MovieWithFavoriteView.COLUMN_RELEASE_DATE,
            MovieContract.MovieWithFavoriteView.COLUMN_POSTER_PATH,
            MovieContract.MovieWithFavoriteView.COLUMN_VOTE_AVG,
            MovieContract.MovieWithFavoriteView.COLUMN_IS_FAVORITE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_OVERVIEW = 3;
    public static final int COL_MOVIE_RELEASE_DATE = 4;
    public static final int COL_MOVIE_POSTER_PATH = 5;
    public static final int COL_MOVIE_VOTE_AVG = 6;
    public static final int COL_MOVIE_IS_FAVORITE = 7;

    private Uri mUri;

    private Long      mMovieId;
    private boolean   mIsFavorite = false;
    private boolean   mIsValid;
    private TextView  mTitleView;
    private TextView  mOverviewView;
    private TextView  mReleaseDateView;
    private ImageView mPosterView;
    private RatingBar mRatingBar;
    private TextView  mRatingTextView;
    private ImageButton mFavoriteButton;
    private MovieReviewsFragment mReviewsFragment;
    private MovieVideosFragment mVideosFragment;

    public MovieDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);
        Intent intent = getActivity().getIntent();
        mIsValid = (intent != null && mUri != null);

        if (mIsValid) {
            mTitleView = (TextView) rootView.findViewById(R.id.movie_title);
            mOverviewView = (TextView) rootView.findViewById(R.id.movie_overview);
            mReleaseDateView = (TextView) rootView.findViewById(R.id.movie_release_date);
            mPosterView = (ImageView) rootView.findViewById(R.id.movie_poster);

            // Source: https://developer.android.com/reference/android/widget/RatingBar.html
            mRatingBar = (RatingBar) rootView.findViewById(R.id.movie_ratingbar);
            mRatingTextView = (TextView) rootView.findViewById(R.id.movie_rating);

            mFavoriteButton = (ImageButton) rootView.findViewById(R.id.movie_favorite_button);
            mFavoriteButton.setVisibility(View.VISIBLE);

            mReviewsFragment = new MovieReviewsFragment();
            mReviewsFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_reviews, mReviewsFragment)
                    .commit();

            mVideosFragment = new MovieVideosFragment();
            mVideosFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_videos, mVideosFragment)
                    .commit();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst() && mIsValid) {

            mMovieId = new Long(data.getInt(COL_MOVIE_ID));

            mReviewsFragment.updateReviews(mMovieId);
            mVideosFragment.updateVideos(mMovieId);

            mIsFavorite = (data.getInt(COL_MOVIE_IS_FAVORITE) == 1);

            String title = data.getString(COL_MOVIE_TITLE);
            mTitleView.setText(title);
            getActivity().setTitle(title);

            mOverviewView.setText(data.getString(COL_MOVIE_OVERVIEW));
            mReleaseDateView.setText(
                    Utilities.getFormattedReleaseDate(getContext(),
                            data.getString(COL_MOVIE_RELEASE_DATE)));

            mOverviewView.setText(data.getString(COL_MOVIE_OVERVIEW));

            // Source: http://square.github.io/picasso/
            Picasso.with(getContext())
                    .load(OpenMovieConfig.buildPosterURIString(
                            data.getString(COL_MOVIE_POSTER_PATH),
                            getString(R.string.movie_poster_detail_size)))
                    .fit()
                    .centerInside()
                    .into(mPosterView);
            mPosterView.setContentDescription(title);

            float rating = data.getFloat(COL_MOVIE_VOTE_AVG);
            float ratingScale = Float.parseFloat(getString(R.string.rating_scale_factor));
            String ratingText = Utilities.getFormattedRating(getContext(), rating);

            mRatingBar.setRating(rating / ratingScale );
            mRatingBar.setContentDescription(ratingText);
            mRatingTextView.setText(ratingText);

            mFavoriteButton.setSelected(mIsFavorite);
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int changed = 0;
                    if (mFavoriteButton.isSelected()) {
                        changed = getContext().getContentResolver().delete(
                                MovieContract.MovieListEntry.CONTENT_URI,
                                MovieContract.MovieListEntry.COLUMN_REQUEST_TYPE + " = ? AND " +
                                MovieContract.MovieListEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{getString(R.string.movie_favorites_key),
                                        Long.toString(mMovieId)});
                    } else {
                        ContentValues cv = new ContentValues();
                        cv.put(MovieContract.MovieListEntry.COLUMN_MOVIE_ID, mMovieId);
                        cv.put(MovieContract.MovieListEntry.COLUMN_REQUEST_TYPE,
                                getString(R.string.movie_favorites_key));
                        try {
                            getContext().getContentResolver().insert(
                                    MovieContract.MovieListEntry.CONTENT_URI,
                                    cv);
                            changed = 1;
                        } catch (Exception e) {
                            Utilities.showMessage(getContext(), "Could not mark as favorite");
                        }
                    }

                    if (changed > 0)
                        mIsFavorite = !mIsFavorite;

                    mFavoriteButton.setSelected(mIsFavorite);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
