package apps.hensbri.popularmovies.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import apps.hensbri.popularmovies.R;

/**
 * Created by hensbri on 10/25/16.
 */
public class Utilities {
    private static int mLastTabPosition = 0;

    public static void setLastTabPosition(int tabPosition) {
        mLastTabPosition = tabPosition;
    }

    public static int getLastTabPosition() {
        return mLastTabPosition;
    }

    public static void showMessage(Context c, String t) {
        Toast.makeText(c, t, Toast.LENGTH_SHORT).show();
    }

    public static String getFormattedRating(Context c, float rating) {
        return String.format(c.getString(R.string.movie_max_rating_format), rating);
    }

    public static String getFormattedReleaseDate(Context c, String releaseDate) {
        return String.format(c.getString(R.string.movie_release_date_format), releaseDate);
    }

    public static String getFormattedReviewAuthor(Context c, String author) {
        return String.format(c.getString(R.string.review_author_format), author);
    }

    public static Uri buildYouTubeURI(Context c, String videoSource) {
        String s = c.getString(R.string.youtube_app_string);
        if (null != c.getContentResolver().getType(Uri.parse(s))) {
            return Uri.parse(s + ":" + videoSource);
        }

        return Uri.parse(c.getString(R.string.youtube_url_string)+ videoSource);
    }
}
