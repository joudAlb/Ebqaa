package com.joud.ebqaaproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ItemActivity extends AppCompatActivity {
    TextView title;
    LinearLayout item_list;
    ImageView icon;
    FloatingActionButton fab;
    DatabaseHelper databaseHelper;
    List<Item> items;
    AlarmManager alarmManager;

    String category_name;
    int category_id, category_days;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //get category details
        Bundle bundle = getIntent().getExtras();
        Log.d("Item List", "bundle - "+ bundle.get("id") + bundle.get("name"));
        category_id = Integer.parseInt((String) bundle.get("id"));    //get category id
        category_name = (String) bundle.get("name"); //get category name
        category_days = Integer.parseInt((String) bundle.get("days"));

        title = findViewById(R.id.page_title);
        fab = findViewById(R.id.fab_item);
        icon = findViewById(R.id.toolbar_icon);
        item_list = findViewById(R.id.item_list);

        databaseHelper = new DatabaseHelper(ItemActivity.this);

        //Change the page title
        title = findViewById(R.id.page_title);
        title.setText(getResources().getString(R.string.itemsPage_title) + " " + category_name);

        //Change the top left icon to a back button
        icon = findViewById(R.id.toolbar_icon);
        icon.setImageResource(R.drawable.left_arrow);
        icon.setContentDescription("Go Back");

        //go back to category list
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //go to add item activity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, Item.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //Populate the list by adding rows dynamically
        LayoutInflater inflater = this.getLayoutInflater();
        items = databaseHelper.getAllItems(category_id);

        Collections.sort(items, new DateComparator());  //sort in ascending order of dates

        for(int i = 0; i < items.size(); ++i) {
            RelativeLayout rowView = (RelativeLayout) inflater.inflate(R.layout.items_row_item, item_list, false);
            //customise the title and date
            Item item = items.get(i);
            TextView item_title = rowView.findViewById(R.id.item_title);
            item_title.setText(item.getName());
            TextView item_date = rowView.findViewById(R.id.item_date);
            if(item.isExpired() || checkIfExpired(item)) {
                item_date.setText("Item expired");
            } else {
                item_date.setText("Expire at: " + item.getDate());
            }
            ImageView item_del = rowView.findViewById(R.id.item_del);
            item_del.setOnClickListener(this::deleteItem);
            rowView.setId(i);
            rowView.setOnClickListener(this::onClickItem);
            //add to list
            item_list.addView(rowView);
        }
    }

    private boolean checkIfExpired(Item item) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String cur_date = format.format(new Date());
        Date date1 = null, date2 = null;
        try {
            date1 = format.parse(item.getDate());
            date2 = format.parse(cur_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date1 != null;
        if(date1.before(date2)) {
            item.setExpiry(true);
        }
        return item.isExpired();
    }

    private void onClickItem(View view) {
        int id = view.getId();
        Item clicked_item = items.get(id);
    }

    private void deleteItem(View view) {
        RelativeLayout rowView = (RelativeLayout) view.getParent();
        int id = rowView.getId();
        int result = databaseHelper.removeItem(items.get(id));
        if(result == 1) {
            Toast.makeText(this, "Item successfully deleted!", Toast.LENGTH_SHORT).show();
            item_list.removeView(rowView);

            //Delete alarm of the item
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), Receiver.class);
            int requestCode = items.get(id).getId();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
            alarmManager.cancel(pendingIntent);

            items.remove(id);
        } else { //delete?
            Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show();
        }
    }
}