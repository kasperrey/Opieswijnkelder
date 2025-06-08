package com.example.opieswijnkelder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "overdatum";
    private static final String PREFS_NAME = "shared preferences";
    private static final String PRODUCTS_KEY = "producten";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final int EXPIRY_WARNING_DAYS = 2;

    private List<Product> producten = new ArrayList<>();
    private List<String> productenNaam = new ArrayList<>();
    private List<String> productenNaamEnAantal = new ArrayList<>();
    private int notificationId = 0;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        createNotificationChannel();
        loadData();
        handleIntent();
        updateProductLists();
        checkExpiryDates();
        setupListView();
        setupAddButton();
    }

    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;

        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            producten.add(product);
            productenNaam.add(product.getNaam());
        } else {
            product = (Product) getIntent().getSerializableExtra("verandert");
            if (product != null) {
                int index = productenNaam.indexOf(product.getNaam());
                if (product.getAantal() == null) {
                    producten.remove(index);
                } else {
                    producten.set(index, product);
                }
            }
        }
        saveData();
    }

    private void updateProductLists() {
        productenNaamEnAantal.clear();
        producten.forEach(v -> productenNaamEnAantal.add(v.getNaam() + " " + v.getAantal()));
        producten.sort(Comparator.comparing(Product::getNaam));
        Collections.sort(productenNaam);
        Collections.sort(productenNaamEnAantal);
    }

    private void checkExpiryDates() {
        String currentDate = dateFormat.format(new Date());
        
        for (Product product : producten) {
            if (product.getVervaldatum() == null || product.getVervaldatum().isEmpty()) {
                continue;
            }

            try {
                if (!product.getVervaldatum().matches("\\d{2}-\\d{2}-\\d{4}")) {
                    continue;
                }

                Date expiryDate = dateFormat.parse(product.getVervaldatum());
                Date today = dateFormat.parse(currentDate);
                
                if (expiryDate != null && today != null) {
                    long diffInMillis = expiryDate.getTime() - today.getTime();
                    int diffInDays = (int) (diffInMillis / (24 * 60 * 60 * 1000));
                    
                    if (diffInDays <= EXPIRY_WARNING_DAYS && diffInDays >= 0) {
                        createNotification(product.getNaam(), 
                            "Je " + product.getNaam() + " is over " + diffInDays + " dag(en) overdatum.");
                        int index = productenNaam.indexOf(product.getNaam());
                        if (index >= 0) {
                            productenNaamEnAantal.set(index, 
                                productenNaamEnAantal.get(index) + " OVER " + diffInDays + " DAG(EN) OVERDATUM");
                        }
                    }
                }
            } catch (ParseException e) {
                System.err.println("Error processing date for product " + product.getNaam() + ": " + e.getMessage());
            }
        }
    }

    private void setupListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, productenNaamEnAantal);
        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            saveData();
            Intent intent = new Intent(getApplicationContext(), SettingsProduct.class);
            intent.putExtra("product", producten.get(productenNaamEnAantal.indexOf(
                (String) parent.getItemAtPosition(position))));
            startActivity(intent);
        });
    }

    private void setupAddButton() {
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(getApplicationContext(), VoegToe.class);
            startActivity(intent);
        });
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PRODUCTS_KEY, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        
        producten = gson.fromJson(json, type);
        if (producten == null) {
            producten = new ArrayList<>();
        }
        
        producten.forEach(v -> productenNaam.add(v.getNaam()));
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(producten);
        editor.putString(PRODUCTS_KEY, json);
        editor.apply();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "Notifications for expiring products";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(notificationId++, builder.build());
    }
}
