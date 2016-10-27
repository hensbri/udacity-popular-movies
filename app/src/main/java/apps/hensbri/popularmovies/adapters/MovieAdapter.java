package apps.hensbri.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.fragments.MovieGridViewFragment;
import apps.hensbri.popularmovies.helpers.OpenMovieConfig;

public class MovieAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView mPosterView;

        public ViewHolder(View view) {
            mPosterView = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_movie_grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mPosterView
                .setContentDescription(cursor.getString(MovieGridViewFragment.COL_MOVIE_TITLE));

        // Source: http://square.github.io/picasso/
        Picasso.with(context)
                .load(OpenMovieConfig.buildPosterURIString(
                        cursor.getString(MovieGridViewFragment.COL_MOVIE_POSTER_PATH),
                        context.getString(R.string.movie_poster_grid_size)))
                .fit()
                .centerInside()
                .into(viewHolder.mPosterView);
    }
}