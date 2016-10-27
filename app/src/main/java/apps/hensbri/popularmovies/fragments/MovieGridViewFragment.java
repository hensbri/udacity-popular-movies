package apps.hensbri.popularmovies.fragments;

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
import android.widget.AdapterView;
import android.widget.GridView;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.adapters.MovieAdapter;
import apps.hensbri.popularmovies.data.MovieContract;
import apps.hensbri.popularmovies.helpers.Utilities;

/**
 * Fragment to provide main grid view for Popular Movies
 * and option to change view by
 *
 * Used https://developer.android.com/training/implementing-navigation/lateral.html to
 * add Slider Tab support for changing Movie Categories
 */
public class MovieGridViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MovieGridViewFragment.class.getSimpleName();

    public static final String ARG_ID = "MF_ID";
    public static final String ARG_REQUEST_TYPE = "ARG_REQUEST_TYPE";

    private MovieAdapter mMoviesAdapter;

    private String mRequestType;
    private GridView mGridView;
    private int mSelectedGridItem = mGridView.INVALID_POSITION;
    private static final String SELECTED_MOVIE_KEY = "selected_movie_key";

    private static int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    public static final int COL_MOVIE_TITLE = 3;

    //https://developer.android.com/training/basics/fragments/communicating.html#DefineInterface
    public interface MovieSelectedCallback {
        public void onMovieSelected(Uri contentUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        int id = args.getInt(ARG_ID);
        mRequestType = args.getString(ARG_REQUEST_TYPE);

        MOVIE_LOADER = id;

        final View rootView = inflater.inflate(R.layout.movie_fragment, container, false);

        mMoviesAdapter = new MovieAdapter(getContext(), null, 0);

        // Source: https://developer.android.com/guide/topics/ui/layout/gridview.html
        // Setup gridView
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        mGridView.setAdapter(mMoviesAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((MovieSelectedCallback) getContext())
                            .onMovieSelected(
                                    MovieContract.MovieEntry.buildMovieUri(
                                            cursor.getLong(COL_MOVIE_ID)
                                    ));
                    mSelectedGridItem = position;
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_MOVIE_KEY)) {
            mSelectedGridItem = savedInstanceState.getInt(SELECTED_MOVIE_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedGridItem != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_MOVIE_KEY, mSelectedGridItem);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri movieUri =
                MovieContract.MovieListEntry
                        .buildMovieListWithRequestTypeUri(mRequestType);
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        if (mSelectedGridItem != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mSelectedGridItem);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }
}