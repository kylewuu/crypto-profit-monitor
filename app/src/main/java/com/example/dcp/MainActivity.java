
package com.example.dcp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.DecimalFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String dogePriceString = "--";
    private double dogePriceDouble = 0.1;
    private DecimalFormat df;
    private DecimalFormat df2;
    private double cadConversion = 1;

    private double dogeCount;
    private double cadSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        df = new DecimalFormat("0.0000");
        df2 = new DecimalFormat("0.00");
        dogeCount = 2327;
        cadSpent = 650;

        displayPrice();
        getDogePrice();
    }

    protected void displayPrice() {
        TextView currentPriceTV = (TextView) findViewById(R.id.current_price);
        TextView profitTV = (TextView) findViewById(R.id.profit);
        currentPriceTV.post(new Runnable() {
            @Override
            public void run() {
                getDogePrice();
                getCadConversion();
                currentPriceTV.setText(String.valueOf(dogePriceString));
                profitTV.setText(df2.format(((dogeCount * dogePriceDouble) * cadConversion) - cadSpent));
                currentPriceTV.postDelayed(this, 1000);
            }
        });
    }

    protected void getDogePrice() {
        ExecutorService service = Executors.newFixedThreadPool(7);
        service.submit(new Runnable() {
            public void run() {
                try {
                    StringBuilder result = new StringBuilder();
                    URL url = new URL("https://api.binance.com/api/v3/ticker/price?symbol=DOGEUSDT");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()))) {
                        for (String line; (line = reader.readLine()) != null; ) {
                            result.append(line);
                        }
                    }
                    dogePriceDouble = Double.parseDouble((new JSONObject(result.toString())).getString("price"));

                    dogePriceDouble = Double.parseDouble(df.format(dogePriceDouble));
                    dogePriceString = String.valueOf(dogePriceDouble);
                    while (dogePriceString.length() - dogePriceString.indexOf('.') < 5 ) {
                        dogePriceString += "0";
                    }
                    System.out.println(dogePriceString);

                }
                catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    protected void getCadConversion() {
        ExecutorService service = Executors.newFixedThreadPool(7);
        service.submit(new Runnable() {
            public void run() {
                try {
                    StringBuilder result = new StringBuilder();
                    URL url = new URL("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/usd/cad.json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()))) {
                        for (String line; (line = reader.readLine()) != null; ) {
                            result.append(line);
                        }
                    }
                    cadConversion = Double.parseDouble((new JSONObject(result.toString())).getString("cad"));

                    System.out.println(String.valueOf(cadConversion));

                }
                catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}