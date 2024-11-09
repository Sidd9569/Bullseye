package com.example.stockmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private ImageView niftyChartImageView, bankNiftyChartImageView, bseChartImageView;
    private TextView stockNameTextView, stockValueTextView;
    private EditText quantityEditText, stopLossEditText;
    private ListView searchResultListView;
    private Button buyButton, sellButton, submitButton;

    private List<String> filteredItems;
    private CustomAdapter adapter;
    private Handler handler = new Handler();
    private Runnable priceUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // Initialize views
        niftyChartImageView = findViewById(R.id.niftyChartImageView);
        bankNiftyChartImageView = findViewById(R.id.bankNiftyChartImageView);
        bseChartImageView = findViewById(R.id.bseChartImageView);
        stockNameTextView = findViewById(R.id.stockNameTextView);
        stockValueTextView = findViewById(R.id.stockValueTextView);
        quantityEditText = findViewById(R.id.quantityEditText);
        stopLossEditText = findViewById(R.id.stopLossEditText);
        searchResultListView = findViewById(R.id.searchResultListView);
        buyButton = findViewById(R.id.buyButton);
        sellButton = findViewById(R.id.sellButton);
        submitButton = findViewById(R.id.submitButton);

        filteredItems = new ArrayList<>();
        adapter = new CustomAdapter(this, filteredItems);
        searchResultListView.setAdapter(adapter);

        String searchQuery = getIntent().getStringExtra("searchQuery");
        filterSearchResults(searchQuery);

        priceUpdater = new Runnable() {
            @Override
            public void run() {
                updatePrices();
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(priceUpdater);

        buyButton.setOnClickListener(v -> handleTransaction("buy"));
        sellButton.setOnClickListener(v -> handleTransaction("sell"));
        submitButton.setOnClickListener(v -> handleTransaction("submit"));
    }

    private void filterSearchResults(String query) {
        stockNameTextView.setText("No results found");
        switch (query.toLowerCase()) {
            case "nifty":
                displayStock("Nifty", niftyChartImageView);
                break;
            case "bank nifty":
                displayStock("Bank Nifty", bankNiftyChartImageView);
                break;
            case "bse":
                displayStock("BSE", bseChartImageView);
                break;
            default:
                filteredItems.add("No results found for " + query);
                adapter.notifyDataSetChanged();
        }
    }

    private void displayStock(String stockName, ImageView chartImageView) {
        stockNameTextView.setText(stockName);
        chartImageView.setVisibility(View.VISIBLE);
    }

    private void updatePrices() {
        stockValueTextView.setText("$" + String.format("%.2f", Math.random() * 1000));
    }

    private void handleTransaction(String action) {
        int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        double stockPrice = Double.parseDouble(stockValueTextView.getText().toString().substring(1));
        String stockName = stockNameTextView.getText().toString();

        if (action.equals("buy")) {
            String purchasedStock = "Bought " + quantity + " shares of " + stockName + " at $" + stockPrice;
            Intent resultIntent = new Intent();
            resultIntent.putExtra("purchasedStock", purchasedStock);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else if (action.equals("sell")) {
            filteredItems.add("Sold " + quantity + " shares of " + stockName + " at $" + stockPrice);
            adapter.notifyDataSetChanged();
        } else if (action.equals("submit")) {
            filteredItems.add("Stop Loss set for " + stockName + " at $" + stockPrice);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(priceUpdater);
    }
}