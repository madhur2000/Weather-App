package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText2);
        textView = findViewById(R.id.textView5);
    }

    class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                String result = "";
                while(data != -1){
                    result += (char) data;
                    data = reader.read();
                }
                return result;
            }
            catch(Exception e){
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Could Not Find Weather!", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weatherInfo);

                String currentWeather = "";

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject obj = jsonArray.getJSONObject(i);
//                    Log.i("main", obj.getString("main"));
//                    Log.i("description", obj.getString("description"));

                    currentWeather += obj.getString("main") + ": " + obj.getString("description") + "\n";

                }

                textView.setText(currentWeather);
                //Log.i("Weather", weatherInfo);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could Not Find Weather!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void getWeather(View view) {

        DownloadTask downloadTask = new DownloadTask();

        String encodedCityName = "";
        try {
            encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could Not Find Weather!", Toast.LENGTH_SHORT).show();
        }
        downloadTask.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName +"&appid=439d4b804bc8187953eb36d2a8c26a02");

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }
}
