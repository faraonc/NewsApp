package edu.udacity.faraonc.newsapp;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * The News object.
 *
 * @author ConardJames
 * @version 010918-01
 */
class News implements Parcelable {

    private String title, contributors, date, url;

    /**
     * Create a News object.
     *
     * @param title        news title
     * @param contributors contributors of the news
     * @param date         published date
     * @param url          the url of the news
     */
    News(String title, String contributors, String date, String url) {
        this.title = title;
        this.contributors = contributors;
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
        this.contributors = in.readString();
        this.date = in.readString();
        this.url = in.readString();
    }

    /**
     * Get the title of the news.
     *
     * @return the title
     */
    String getTitle() {
        return this.title;
    }

    /**
     * Get the contributors of the news.
     *
     * @return the contributors
     */
    String getContributors() {
        return this.contributors;
    }

    /**
     * Allocate an array of authors only if needed.
     *
     * @return the array of authors
     */
    String[] getAuthors() {
        return this.contributors.split(",");
    }

    /**
     * Get the published date of the news.
     *
     * @return the published date
     */
    String getDate() {
        return this.date;
    }

    /**
     * Get the url of the news.
     *
     * @return the url
     */
    String getUrl() {
        return this.url;
    }

    @Override
    /**
     * Not used for now.
     *
     * @return an int
     */
    public int describeContents() {
        return 0;
    }

    @Override
    /**
     * Serialized the object.
     *
     * @param parcel contains the object
     * @param   i    not currntly used
     */
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.contributors);
        parcel.writeString(this.date);
        parcel.writeString(this.url);
    }

    /**
     * Creates a parcel creator for the News.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        /**
         * Construct a News
         *
         * @param in the parcel containing the News
         * @return the News
         */
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        /**
         * Allocate an array of serializable for News.
         *
         * @param size the number of allocations
         * @return an array for News
         */
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
