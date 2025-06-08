package com.example.opieswijnkelder;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for adding new products to the inventory.
 * Allows users to specify product name, quantity, and optional expiry date.
 */
public class VoegToe extends AppCompatActivity {
    private static final String EXTRA_PRODUCT = "product";
    private static final String DATE_FORMAT = "%02d-%02d-%d";

    private EditText dateEditText;
    private EditText nameEditText;
    private TextView quantityTextView;
    private CheckBox expiryCheckBox;
    private int currentQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voeg_toe);

        initializeViews();
        setupDatePicker();
        setupQuantityControls();
        setupAddButton();
        setupExpiryCheckBox();
    }

    private void initializeViews() {
        dateEditText = findViewById(R.id.editTextDate);
        nameEditText = findViewById(R.id.textInputEditText);
        quantityTextView = findViewById(R.id.textView);
        expiryCheckBox = findViewById(R.id.checkBox);
        Button addButton = findViewById(R.id.button2);
        Button minusButton = findViewById(R.id.min);
        Button plusButton = findViewById(R.id.plus);

        dateEditText.setVisibility(View.INVISIBLE);
        quantityTextView.setText(String.valueOf(currentQuantity));
    }

    private void setupDatePicker() {
        dateEditText.setOnClickListener(v -> showDatePicker());
        dateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                closeKeyboard();
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                String formattedDate = String.format(Locale.getDefault(), 
                    DATE_FORMAT, selectedDay, selectedMonth + 1, selectedYear);
                dateEditText.setText(formattedDate);
            },
            year, month, day
        );
        datePickerDialog.show();
    }

    private void setupQuantityControls() {
        findViewById(R.id.min).setOnClickListener(v -> {
            if (currentQuantity > 0) {
                currentQuantity--;
                quantityTextView.setText(String.valueOf(currentQuantity));
            }
        });

        findViewById(R.id.plus).setOnClickListener(v -> {
            currentQuantity++;
            quantityTextView.setText(String.valueOf(currentQuantity));
        });
    }

    private void setupAddButton() {
        findViewById(R.id.button2).setOnClickListener(v -> {
            String productName = nameEditText.getText().toString().trim();
            if (productName.isEmpty()) {
                showError("Please enter a product name");
                return;
            }

            if (currentQuantity <= 0) {
                showError("Please enter a valid quantity");
                return;
            }

            String expiryDate = expiryCheckBox.isChecked() ? 
                dateEditText.getText().toString() : null;

            Product product = new Product(currentQuantity, productName, expiryDate);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(EXTRA_PRODUCT, product);
            startActivity(intent);
            finish();
        });
    }

    private void setupExpiryCheckBox() {
        expiryCheckBox.setOnClickListener(v -> {
            if (expiryCheckBox.isChecked()) {
                dateEditText.setVisibility(View.VISIBLE);
                dateEditText.requestFocus();
            } else {
                dateEditText.setVisibility(View.INVISIBLE);
                dateEditText.setText("");
            }
        });
    }

    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) 
                getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
