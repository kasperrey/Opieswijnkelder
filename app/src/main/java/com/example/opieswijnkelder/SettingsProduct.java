package com.example.opieswijnkelder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsProduct extends AppCompatActivity {

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            product = (Product) bundle.getSerializable("product");
        }

        TextView naam = findViewById(R.id.textView2);
        SeekBar seekBar = findViewById(R.id.seekBar2);
        EditText aantal = findViewById(R.id.editTextNumber3);
        Button delete = findViewById(R.id.button3);
        Button gedronken = findViewById(R.id.button4);
        Button gekocht = findViewById(R.id.button5);
        naam.setText(product.naam);
        seekBar.setMax(product.aantal);
        aantal.setText(String.valueOf(seekBar.getMax()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                aantal.setText(String.valueOf(progress));
            }
        });
        delete.setOnClickListener(v -> {
            product.aantal = null;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("verandert", product);
            startActivity(intent);
        });
        gedronken.setOnClickListener(v -> {
            product.aantal -= Integer.parseInt(String.valueOf(aantal.getText()));
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("verandert", product);
            startActivity(intent);
        });
        gekocht.setOnClickListener(v -> {
            product.aantal += Integer.parseInt(String.valueOf(aantal.getText()));
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("verandert", product);
            startActivity(intent);
        });
    }
}
