package com.example.opieswijnkelder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for managing individual product settings.
 * Allows users to modify quantity, delete, or mark products as consumed/purchased.
 */
public class SettingsProduct extends AppCompatActivity {
    private static final String EXTRA_PRODUCT = "product";
    private static final String EXTRA_CHANGED = "verandert";

    private Product product;
    private EditText aantalEditText;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);

        initializeViews();
        loadProduct();
        setupListeners();
    }

    private void initializeViews() {
        aantalEditText = findViewById(R.id.editTextNumber3);
        seekBar = findViewById(R.id.seekBar2);
        TextView naamTextView = findViewById(R.id.textView2);
        Button deleteButton = findViewById(R.id.button3);
        Button gedronkenButton = findViewById(R.id.button4);
        Button gekochtButton = findViewById(R.id.button5);

        if (product != null) {
            naamTextView.setText(String.format("%s  %s", product.getNaam(), product.getVervaldatum()));
            seekBar.setMax(product.getAantal());
            aantalEditText.setText(String.valueOf(product.getAantal()));
        }
    }

    private void loadProduct() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            product = (Product) bundle.getSerializable(EXTRA_PRODUCT);
            if (product == null) {
                finish();
                return;
            }
        } else {
            finish();
        }
    }

    private void setupListeners() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    aantalEditText.setText(String.valueOf(progress));
                }
            }
        });

        findViewById(R.id.button3).setOnClickListener(v -> handleDelete());
        findViewById(R.id.button4).setOnClickListener(v -> handleConsumed());
        findViewById(R.id.button5).setOnClickListener(v -> handlePurchased());
    }

    private void handleDelete() {
        if (product != null) {
            product.setAantal(null);
            returnToMainActivity();
        }
    }

    private void handleConsumed() {
        if (product != null) {
            try {
                int currentAmount = Integer.parseInt(aantalEditText.getText().toString());
                int newAmount = product.getAantal() - currentAmount;
                if (newAmount >= 0) {
                    product.setAantal(newAmount);
                    returnToMainActivity();
                } else {
                    showError("Cannot consume more than available");
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount entered");
            }
        }
    }

    private void handlePurchased() {
        if (product != null) {
            try {
                int currentAmount = Integer.parseInt(aantalEditText.getText().toString());
                int newAmount = product.getAantal() + currentAmount;
                product.setAantal(newAmount);
                returnToMainActivity();
            } catch (NumberFormatException e) {
                showError("Invalid amount entered");
            }
        }
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(EXTRA_CHANGED, product);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
