package com.example.ritik.newsextractor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    class Worker extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String results = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder html = new StringBuilder();

                // to read line by line

                // int data = reader.read();
                //while (data != -1) {
                //  char current = (char) data;
                //results += current;
                // System.out.println(results);
                //data = reader.read();

                //}
                for (String line; (line = reader.readLine()) != null; ) {
                    html.append(line);
                }
                in.close();
                urlConnection.disconnect();
                System.out.println(results);
                return html.toString();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void btnClick(View view) {
        EditText et = findViewById(R.id.editText);
        TextView tv = findViewById(R.id.textView2);
        String url = "http://" + et.getText();
        Worker task = new Worker();
        try {
            String result = task.execute(url).get();
            Pattern rule = Pattern.compile(" <div class=\"all-news-card\">(.*?)<div class=\"all-about-coer\">");
            Matcher m = rule.matcher(result);
            result="";
            while(m.find())
            {
                result += m.group(1);
            }
            rule = Pattern.compile("<li class=\"news-item\">(.*?)   </li>");
            m = rule.matcher(result);
            result="";
            while(m.find())
            {
                result += m.group(1);
            }
            Log.i("result",result);
            tv.setText(result);
        } catch (Exception e) {
            System.out.println(e);
        }


    }
}

