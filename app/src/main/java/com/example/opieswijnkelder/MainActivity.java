package com.example.opieswijnkelder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> producten = new ArrayList<>();
    ArrayList<String> productenNaam = new ArrayList<>();
    ArrayList<String> productenNaamEnAantal = new ArrayList<>();
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        loadData();

        producten.forEach(e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String currentDate = sdf.format(new Date());
            if (String.valueOf(e.vervaldatum).length() != 0) {
                int verschil = Integer.parseInt(e.vervaldatum.substring(0, 2)) - Integer.parseInt(currentDate.substring(0, 2));
                if (e.vervaldatum.substring(5, 9).equals(currentDate.substring(5, 9))) {
                    if (e.vervaldatum.substring(3, 5).equals(currentDate.substring(3, 5))) {
                        if (verschil <= 2) {
                            createNotification(e.naam, "Je "+e.naam+" is over "+verschil+" dag overdatum.");
                        }
                    }
                }
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (getIntent().getSerializableExtra("product") != null) {
                Product product = (Product) getIntent().getSerializableExtra("product");
                producten.add(product);
                productenNaam.add(product.naam);
            } else {
                Product product = (Product) getIntent().getSerializableExtra("verandert");
                if (product.aantal == null) {
                    System.out.println(product.naam);
                    System.out.println(productenNaam.indexOf(product.naam));
                    producten.remove(productenNaam.indexOf(product.naam));
                } else {
                    producten.set(productenNaam.indexOf(product.naam), product);
                }
            }
            saveData();
        }
        producten.forEach(v -> productenNaamEnAantal.add(v.naam+" "+v.aantal));
        producten.sort(Comparator.comparing(o -> o.naam));
        Collections.sort(productenNaam);
        Collections.sort(productenNaamEnAantal);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productenNaamEnAantal);
        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            saveData();
            Intent intent = new Intent(getApplicationContext(), SettingsProduct.class);
            intent.putExtra("product", producten.get(productenNaamEnAantal.indexOf((String) parent.getItemAtPosition(position))));
            startActivity(intent);
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(getApplicationContext(), VoegToe.class);
            startActivity(intent);
        });
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("producten", null);

        Type type = new TypeToken<ArrayList<Product>>() {}.getType();

        producten = gson.fromJson(json, type);

        if (producten == null) {
            producten = new ArrayList<>();
        }

        producten.forEach(v -> productenNaam.add(v.naam));
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(producten);

        editor.putString("producten", json);
        editor.apply();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("overdatum", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "overdatum")
                .setSmallIcon(R.mipmap.)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(id, builder.build());
        id += 1;
    }
}
