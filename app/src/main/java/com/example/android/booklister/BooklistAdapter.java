package com.example.android.booklister;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
public class BooklistAdapter extends ArrayAdapter<Booklist> {
    List<Booklist> booklistList = new ArrayList<>();
    public void addEarthquake(List<Booklist> booklists){
        this.booklistList = booklists;
        notifyDataSetChanged();
    }
    public BooklistAdapter(Activity context, List<Booklist> booklists) {
         super(context, 0, booklists);
        booklistList = booklists;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_view, parent, false);
        }
        // Find the booklist at the given position in the list of earthquakes
        Booklist currentBooklist = getItem(position);
        // Find the TitleView
        TextView titleView = (TextView) listItemView.findViewById(R.id.TitleView);
        // Display the title
        titleView.setText(currentBooklist.getmTitle());
        // Find the DateView
        TextView dateView = (TextView) listItemView.findViewById(R.id.DateView);
        // Display the date
       dateView.setText(currentBooklist.getmDate());
        // Find the AuthorView
        TextView authorView = (TextView) listItemView.findViewById(R.id.AuthorView);
        authorView.setText(currentBooklist.getmAuthor());
        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
    @Override
    public int getCount() {
        return booklistList.size();
    }
}

