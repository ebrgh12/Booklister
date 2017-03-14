package com.example.android.booklister;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class BooklistActivity extends AppCompatActivity {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BooklistActivity.class.getSimpleName();
    EditText answerField;
    Button searchButton;
    Editable nameEditable;
    String answer;
    private ListView listView;
    private TextView empty_list_item;
    private static final String TAG = BooklistActivity.class.getSimpleName();
    private String LIST_INSTANCE_STATE;
    private  String mListInstanceState;
    private Parcelable p;
    private ArrayList<Booklist> bList = new ArrayList<>();
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView2);
        answerField = (EditText) findViewById(R.id.editText);
        searchButton = (Button) findViewById(R.id.button1);
        empty_list_item = (TextView) findViewById(R.id.empty_list_item);
        if(savedInstanceState == null || !savedInstanceState.containsKey("books")) {
            bList = new ArrayList<Booklist>();
        }
        else {
            bList = savedInstanceState.getParcelableArrayList("books");
            BooklistAdapter adapter = new BooklistAdapter(BooklistActivity.this, bList);
            listView.setAdapter(adapter);
            answerField.setText("");
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditable = answerField.getText();
                answer = nameEditable.toString();
                empty_list_item.setVisibility(View.GONE);
                //listView.setVisibility(View.GONE);
                ConnectivityManager connectivity = (ConnectivityManager) BooklistActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = connectivity.getActiveNetworkInfo();
                if (nInfo != null && nInfo.isConnected()) {
                    Toast.makeText(getApplicationContext(), "Good internet connection", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "You clicked search button" + " " + answer, Toast.LENGTH_SHORT).show();
                    String USGS_REQUEST_URL1 = "https://www.googleapis.com/books/v1/volumes?q=";
                    String USGS_REQUEST_URL2 = answer.replace(" ", "%20");
                    String USGS_REQUEST_URL3 = "&maxResults=10";
                    String USGS_REQUEST_URL = USGS_REQUEST_URL1 + USGS_REQUEST_URL2 + USGS_REQUEST_URL3;
                    Log.d(TAG, USGS_REQUEST_URL);
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(USGS_REQUEST_URL);
                } else if (nInfo == null) {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("books", bList);
        super.onSaveInstanceState(outState);
    }
    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with  the response.
     */
    private class BookAsyncTask extends AsyncTask<String, Void, List<Booklist>> {
        @Override
        protected List<Booklist> doInBackground(String... urls) {
            // Create URL object
            URL url = createUrl(urls[0]);
            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
            }
            if (jsonResponse != null) {
                // Extract relevant fields from the JSON response and create an {@link Event} object
                List<Booklist> booklists = extractFeatureFromJson(jsonResponse);
                // Return the {@link Event} object as the result fo the {@link BookAsyncTask}
                return booklists;
            }
            return null;
        }
        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }
        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = null;
           if(url==null){
                return  jsonResponse;
           }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                // Check for server related problems
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else if(urlConnection.getResponseCode() ==401){
                    return jsonResponse;
                }
            }
                catch (IOException e) {
                } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }
        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
        private List<Booklist> extractFeatureFromJson(String earthquakeJSON) {
            Log.d(TAG, "Response from server = " + earthquakeJSON);
            String mytitle;
            String myauthor;
            String mydate;

            if(TextUtils.isEmpty(earthquakeJSON)){
                return null;
            }
            bList = new ArrayList<>();
            try {
                // build up a list of Booklist objects with the corresponding data.
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
                JSONArray earthquakeArray = baseJsonResponse.getJSONArray("items");
                for (int i = 0; i < earthquakeArray.length(); i++) {
                    JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                    JSONObject properties = currentEarthquake.getJSONObject("volumeInfo");
                    // Extract the value for the key called "mag"
                    mytitle = properties.getString("title");
                    myauthor = properties.getString("authors");
                    mydate = properties.getString("publishedDate");
                    Log.d(TAG,"author = "+ myauthor);
                    Log.d(TAG,"title="+ mytitle);
                    Log.d(TAG, "date="+mydate);
                    Booklist booklist = new Booklist(mytitle, myauthor, mydate);
                    bList.add(booklist);
                }
                return bList;
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<Booklist> booklist) {
            super.onPostExecute(booklist);

            if(booklist !=null) {
                Log.d(TAG, "size = " + booklist.size());
                BooklistAdapter adapter = new BooklistAdapter(BooklistActivity.this, booklist);
                listView.setAdapter(adapter);
                answerField.setText("");
            }
            else{
                listView.setVisibility(View.GONE);
                empty_list_item.setVisibility(View.VISIBLE);
                empty_list_item.setText("No data, Please use other search term");
            }

            }
        }
    }





