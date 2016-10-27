package apps.hensbri.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.adapters.ReviewsAdapter;
import apps.hensbri.popularmovies.data.MovieContract;
import apps.hensbri.popularmovies.helpers.Utilities;
import apps.hensbri.popularmovies.sync.FetchReviewsTask;

public class MovieReviewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REVIEWS_LOADER = 1;

    // Review Columns
    private static final String[] REVIEWS_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_URL
    };

    public static final int COL_ID = 0;
    public static final int COL_REVIEW_CONTENT = 1;
    public static final int COL_REVIEW_AUTHOR = 2;
    public static final int COL_REVIEW_URL = 3;

    private Uri mUri;
    private ReviewsAdapter mReviewsAdapter;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        try {
            Uri detailUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
            Long movieId = MovieContract.MovieEntry.getMovieIdFromUri(detailUri);
            mUri = MovieContract.ReviewEntry.buildReviewUri(movieId);
        } catch (Exception e) {
            mUri = null;
        }

        mReviewsAdapter = new ReviewsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.movie_details_reviews_fragment, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_movie_reviews);
        lv.setAdapter(mReviewsAdapter);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.movie_reviews_progressbar);

        return rootView;
    }

    public void updateReviews(Long movieId) {
        mProgressBar.setVisibility(View.VISIBLE);
        FetchReviewsTask reviewsTask = new FetchReviewsTask(getActivity());
        reviewsTask.execute(movieId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            String sortOrder = MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " DESC";
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    REVIEWS_COLUMNS,
                    null,
                    null,
                    sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mReviewsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReviewsAdapter.swapCursor(null);
    }
}
