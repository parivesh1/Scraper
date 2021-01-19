package com.example.android.scraper;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    TextView description;
    ImageView image;
    ImageButton searchButton;
    EditText searchBar;
    String url, productName, link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();

        description = findViewById(R.id.descrip);
        image = findViewById(R.id.imag_view);
        searchButton = findViewById(R.id.searchBtn);
        searchBar = findViewById(R.id.searchBar);

        searchButton.setOnClickListener(v -> {
            productName = searchBar.getText().toString();
            url = String.format("https://amazon.com/s?k=%s&ref=nb_sb_noss", productName);
            searching mSearch = new searching();
            mSearch.execute();
        });
    }

    public class searching extends AsyncTask <Void, Void, Void> {

        private String data;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (url != null) {
                    Document document = Jsoup.connect(url).get();
                    Element d = document.select("[src^=https://m.media-amazon.com/images/I/]").first();
                    data = d.attr("alt");
                    data = data.replace("Sponsored Ad - ", " ");
                    link = d.attr("src");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (data != null) {
                description.setText((CharSequence) data);
            }
            Glide.with(searchButton).load(link).placeholder(R.drawable.loading)
                    .fitCenter()
                    .error(R.drawable.errorimage)
                    .into(image);
        }
    }
}