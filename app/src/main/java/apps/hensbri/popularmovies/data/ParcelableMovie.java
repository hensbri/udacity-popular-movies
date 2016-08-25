package apps.hensbri.popularmovies.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import apps.hensbri.popularmovies.BuildConfig;
import apps.hensbri.popularmovies.R;

/**
 * Source: Used Udacity Webinar on Parcelables
 * Created by hensbri on 8/11/16.
 */
public class ParcelableMovie implements Parcelable{
    private static final String LOG_TAG = ParcelableMovie.class.getSimpleName();

    private String mTitle;
    private String mOverview;
    private float mRating;
    private String mPoster;
    private String mReleaseDate;

    public ParcelableMovie(String title,
                           String overview,
                           String poster,
                           float rating,
                           String release_date) {
        mTitle       = title;
        mOverview    = overview;
        mPoster      = poster;
        mRating      = rating;
        mReleaseDate = release_date;
    }

    public String getTitle() { return mTitle; }
    public String getOverview() { return mOverview; }
    public float getRating() { return mRating; }
    public String getPoster() { return mPoster; }
    public String getReleaseDate() { return mReleaseDate; }

    public String getPosterURLString(String poster_size) {
        return OpenMovieConfig.buildPosterURIString(mPoster, poster_size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mOverview);
        parcel.writeFloat(mRating);
        parcel.writeString(mPoster);
        parcel.writeString(mReleaseDate);
    }

    public static final Parcelable.Creator<ParcelableMovie> CREATOR
            = new Parcelable.Creator<ParcelableMovie>() {
        public ParcelableMovie createFromParcel(Parcel in) {
            return new ParcelableMovie(in);
        }
        public ParcelableMovie[] newArray(int size) {
            return new ParcelableMovie[size];
        }
    };

    private ParcelableMovie(Parcel in) {
        mTitle       = in.readString();
        mOverview    = in.readString();
        mRating      = in.readFloat();
        mPoster      = in.readString();
        mReleaseDate = in.readString();
    }
}