package apps.hensbri.popularmovies.fragments;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.adapters.VideosAdapter;
import apps.hensbri.popularmovies.data.MovieContract;
import apps.hensbri.popularmovies.helpers.Utilities;
import apps.hensbri.popularmovies.sync.FetchVideosTask;

public class MovieVideosFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIDEOS_LOADER = 2;

    // Video Columns
    private static final String[] VIDEOS_COLUMNS = {
            MovieContract.VideoEntry.TABLE_NAME + "." + MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.COLUMN_NAME,
            MovieContract.VideoEntry.COLUMN_SOURCE
    };

    public static final int COL_ID = 0;
    public static final int COL_VIDEO_NAME = 1;
    public static final int COL_VIDEO_SOURCE = 2;

    private Uri mUri;
    private VideosAdapter mVideosAdapter;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        try {
            Uri detailUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
            Long movieId = MovieContract.MovieEntry.getMovieIdFromUri(detailUri);
            mUri = MovieContract.VideoEntry.buildVideoUri(movieId);
        } catch (Exception e) {
            mUri = null;
        }

        mVideosAdapter = new VideosAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.movie_details_videos_fragment, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_movie_videos);
        lv.setAdapter(mVideosAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Utilities.buildYouTubeURI(getContext(),
                                    cursor.getString(COL_VIDEO_SOURCE))));
                }
            }
        });

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.movie_videos_progressbar);

        return rootView;
    }

    public void updateVideos(Long movieId) {
        mProgressBar.setVisibility(View.VISIBLE);
        FetchVideosTask reviewsTask = new FetchVideosTask(getActivity());
        reviewsTask.execute(movieId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(VIDEOS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            String sortOrder = MovieContract.VideoEntry.COLUMN_NAME + " DESC";
            return new CursorLoader(getActivity(),
                mUri,
                VIDEOS_COLUMNS,
                null,
                null,
                sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mVideosAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVideosAdapter.swapCursor(null);
    }
}

