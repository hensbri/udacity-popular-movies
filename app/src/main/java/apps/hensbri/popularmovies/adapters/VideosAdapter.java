package apps.hensbri.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.fragments.MovieVideosFragment;

public class VideosAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView mNameView;

        public ViewHolder(View view) {
            mNameView = (TextView) view.findViewById(R.id.movie_video_name);
        }
    }

    public VideosAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mNameView.setText(cursor.getString(MovieVideosFragment.COL_VIDEO_NAME));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_video, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }
}
