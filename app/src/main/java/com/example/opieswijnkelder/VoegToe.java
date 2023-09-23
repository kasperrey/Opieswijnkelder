package com.example.opieswijnkelder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class VoegToe  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voeg_toe);

        EditText dateEdt = findViewById(R.id.editTextDate);
        Button button = findViewById(R.id.button2);
        Button min = findViewById(R.id.min);
        Button plus = findViewById(R.id.plus);
        TextView aantal = findViewById(R.id.textView);
        EditText naam = findViewById(R.id.textInputEditText);
        CheckBox checkBox = findViewById(R.id.checkBox);

        dateEdt.setOnClickListener(v -> {
            // on below line we are getting
            // the instance of our calendar.
            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    // on below line we are passing context.
                    VoegToe.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // on below line we are setting date to our edit text.
                        if (String.valueOf(monthOfYear).length()<2) {
                            dateEdt.setText(dayOfMonth + "-" + "0" + (monthOfYear + 1) + "-" + year1);
                        } else {
                            dateEdt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);
                        }

                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("product", new Product(Integer.parseInt((String) aantal.getText()), String.valueOf(naam.getText()), String.valueOf(dateEdt.getText())));
            startActivity(intent);
        });
        min.setOnClickListener(v -> aantal.setText(String.valueOf(Integer.parseInt((String) aantal.getText())-1)));
        plus.setOnClickListener(v -> aantal.setText(String.valueOf(Integer.parseInt((String) aantal.getText())+1)));
        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                dateEdt.setVisibility(View.VISIBLE);
            } else {
                dateEdt.setVisibility(View.INVISIBLE);
            }
        });
    }
}
