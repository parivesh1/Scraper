package com.example.android.scraper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

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
            productName = getName();
            closeKeyboard();
            if (productName != null) {
                url = String.format("https://amazon.com/s?k=%s&ref=nb_sb_noss_2", productName);
                searching mSearch = new searching();
                mSearch.execute();
            }
        });
    }

    public String getName() {
        if (searchBar.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter the Product Name first!", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            return searchBar.getText().toString();
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class searching extends AsyncTask<Void, Void, Void> {

        String data;
        Document document;
        Element d;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36")
                        .get();
                d = document.select("[src^=https://m.media-amazon.com/images/I/]").first();
                data = d.attr("alt");
                data = data.replace("Sponsored Ad - ", " ");
                link = d.attr("src");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (data != null) {
                description.setText((CharSequence) data);
            } else {
                description.setText("Check Product Name!!");
            }
            if (link != null) {
                Glide.with(searchButton)
                        .load(link)
                        .placeholder(R.drawable.loading)
                        .fitCenter()
                        .into(image);
            } else {
                image.setImageResource(R.drawable.errorimage);
            }
        }
    }
}
