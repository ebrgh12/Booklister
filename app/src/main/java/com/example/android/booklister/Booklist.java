package com.example.android.booklister;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by giris on 8/11/2016.
 */
public class Booklist implements Parcelable {
    private String mTitle;
    private String mAuthor;
    private String mDate;
    public Booklist(String Title, String Author, String Date)
    {
        mTitle = Title;
        mAuthor = Author;
        mDate= Date;
    }
    public String getmTitle() {
        return mTitle;
    }
    public String getmAuthor()
    {
        return mAuthor;
    }
    public String getmDate(){
        return mDate;
    }
    protected Booklist(Parcel in) {
        mTitle = in.readString();
        mAuthor = in.readString();
        mDate = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mAuthor);
        dest.writeString(mDate);
    }
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Booklist> CREATOR = new Parcelable.Creator<Booklist>() {
        @Override
        public Booklist createFromParcel(Parcel in) {
            return new Booklist(in);
        }
        @Override
        public Booklist[] newArray(int size) {
            return new Booklist[size];
        }
    };
}