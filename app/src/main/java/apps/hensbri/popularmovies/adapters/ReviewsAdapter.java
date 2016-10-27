package apps.hensbri.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.fragments.MovieReviewsFragment;
import apps.hensbri.popularmovies.helpers.Utilities;

public class ReviewsAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView mAuthorView;
        public final TextView mContentView;
        public final TextView mUrlView;

        public ViewHolder(View view) {
            mAuthorView = (TextView) view.findViewById(R.id.movie_review_author);
            mContentView = (TextView) view.findViewById(R.id.movie_review_content);
            mUrlView = (TextView) view.findViewById(R.id.movie_review_url);
        }
    }

    public ReviewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

            viewHolder.mAuthorView.setText(Utilities.getFormattedReviewAuthor(context,
                    cursor.getString(MovieReviewsFragment.COL_REVIEW_AUTHOR)));
            viewHolder.mContentView.setText(cursor.getString(MovieReviewsFragment.COL_REVIEW_CONTENT));
            viewHolder.mUrlView.setText(cursor.getString(MovieReviewsFragment.COL_REVIEW_URL));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }
}
