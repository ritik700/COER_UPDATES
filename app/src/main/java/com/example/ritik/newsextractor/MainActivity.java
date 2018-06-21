package com.example.ritik.newsextractor;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.spec.ECField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> news, dates, downloads;
    ListView listView;

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
        news = new ArrayList<>();
        dates = new ArrayList<>();
        downloads = new ArrayList<>();
        listView = findViewById(R.id.listview);
        fetchNews();
        loadNews();
    }


    public void fetchNews() {
        // EditText et = findViewById(R.id.editText);
        //TextView tv = findViewById(R.id.textViewdates);
        String url = "http://coer.ac.in";
        Worker task = new Worker();

        try {
            String result = task.execute(url).get();
            Pattern rule = Pattern.compile(" <div class=\"all-news-card\">(.*?)<div class=\"all-about-coer\">");
            Matcher m = rule.matcher(result);
            result = "";
            while (m.find()) {
                result += m.group(1);
            }
            rule = Pattern.compile("<li class=\"news-item\">(.*?)   </li>");
            m = rule.matcher(result);
            result = "";
            Pattern ruleNews = Pattern.compile("<p>(.*?)</p>");

            Pattern ruleDate = Pattern.compile("<small>(.*?)</small>");

            Pattern ruleDownloads = Pattern.compile("href=\"(.*?)\">");

            while (m.find()) {
                String temp = m.group(1);
                Matcher matNews = ruleNews.matcher(temp);
                Matcher matDate = ruleDate.matcher(temp);
                Matcher matDown = ruleDownloads.matcher(temp);

                if (matNews.find()) {
                    news.add(matNews.group(1));
                }
                if (matDate.find()) {
                    dates.add(matDate.group(1));
                }
                if (matDown.find()) {
                    downloads.add(matDown.group(1));
                }

            }
            while (m.find()) {
                result += m.group(1);
            }
            Log.i("result", result);
            System.out.println(news);
            System.out.println(dates);
            System.out.println(downloads);
            //tv.setText(result);
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public void loadNews() {
        CustomAdaptor customAdaptor = new CustomAdaptor();
        Toast.makeText(this, "Totale News "+customAdaptor.getCount()+" found", Toast.LENGTH_LONG).show();
        listView.setAdapter(customAdaptor);   //it will call getView method bydefault
    }

    class CustomAdaptor extends BaseAdapter {             //interface

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {

                convertView = getLayoutInflater().inflate(R.layout.customlayout, null);
                TextView tvnews = convertView.findViewById(R.id.textViewnews);
                TextView tvDate = convertView.findViewById(R.id.textViewdates);
                Button btnDownload = convertView.findViewById(R.id.buttondownload);
                String recvdate = dates.get(position);
                SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat showFormat = new SimpleDateFormat("dd-MM-yyyy");
                String tdate = showFormat.format(userFormat.parse(recvdate)).toString();
                tvDate.setText(tdate);
                //String temp = downloads.get(position);
                //TextView tvDownload = convertView.findViewById(R.id.textViewdownload);
                tvnews.setText(news.get(position));
                //tvDate.setText(dates.get(position));
                String temp = downloads.get(position);
                if (temp.trim().equals("")) {
                    btnDownload.setVisibility(View.GONE);
                } else {
                    btnDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = downloads.get(position);
                            url = url.replace("..", "http://coer.ac.in");
                            gotoUrl(url);
                        }
                    });

                    //tvDownload.setText(downloads.get(position));
                }
                return convertView;
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }
    }

    private void gotoUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}





