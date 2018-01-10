package edu.udacity.faraonc.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by faraonc on 1/9/18.
 */

class News implements Parcelable {

    private String title, author, date, url;

    News(String title, String author, String date, String url) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.url = url;
    }

    /**
     * Construct a News from a Parcel.
     *
     * @param in the parcel.
     */
    News(Parcel in) {
        this.title = in.readString();
        this.author = in.readString();
        this.date = in.readString();
        this.url = in.readString();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    /**
     * Not used for now.
     *
     * @return an int.
     */
    public int describeContents() {
        return 0;
    }

    @Override
    /**
     * Serialized the object.
     *
     * @param parcel contains the object.
     * @param   i    not currntly used.
     */
    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(this.entryName);
//        parcel.writeString(this.entryLocation);
//        parcel.writeString(this.entryDescription);
//        parcel.writeInt(this.entryImageId);
    }

    /**
     * Creates a parcel creator for the News.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        /**
         * Construct a News
         *
         * @param in the parcel containing the News.
         * @return the News.
         */
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        /**
         * Allocate an array of serializable for News.
         *
         * @param size the number of allocations
         * @return an array for News.
         */
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
