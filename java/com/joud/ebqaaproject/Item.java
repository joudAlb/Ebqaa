//When user adds an item in category
// facilitates the process of adding items to a category, managing their dates, and setting up alarms for notification purposes.
package com.joud.ebqaaproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Item extends AppCompatActivity {
    public static final int MIN_YEARS = 2023;// minimum year input by user
    public static final int MAX_YEARS = 2030;// maximum year input

    int noOfDateBoxes = 1, maxDateBoxes = 8;
    int category_id, category_days;
    String category_name;

    LinearLayout container;
    TextView title;
    ImageView icon, add_date_box;
    Button add_btn;
    EditText item_name;

    Bundle bundle;
    DatabaseHelper databaseHelper;
    DatePickerDialog datePickerDialog;
    AlarmManager alarmManager;


    //item class
    private int id;
    private String name;
    private String date;
    private boolean expired;
    private int categoryID;
    int year, month, day;

        public Item() {
            // Empty constructor
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);// display the design by referencing its' xml file

        bundle = getIntent().getExtras();//get category info to give back to item list
        category_id = Integer.parseInt((String) bundle.get("id"));    //get category id
        category_name = (String) bundle.get("name"); //get category name
        category_days = Integer.parseInt((String) bundle.get("days"));

        databaseHelper = new DatabaseHelper(Item.this);

        title = findViewById(R.id.page_title);// referencing each attribute with its id from the xml file
        icon = findViewById(R.id.toolbar_icon);
        add_btn = findViewById(R.id.add_item);
        add_date_box = findViewById(R.id.add_date_box);
        item_name = findViewById(R.id.item_name);
        container = findViewById(R.id.date_box_layout);

        title.setText(R.string.add_item);// set the title of the page

        icon.setImageResource(R.drawable.left_arrow);// set back button image
        icon.setContentDescription("Return");

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Item.this, ItemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //add item(s) to the item list
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the item name
                String name = item_name.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(Item.this, "Please enter item name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Item[] items = new Item[noOfDateBoxes];
                for (int i = 0; i < noOfDateBoxes; ++i) {
                    //get the dates
                    LinearLayout ll = (LinearLayout) container.getChildAt(i);
                    EditText et_view = (EditText) ll.getChildAt(0);
                    items[i] = new Item(-1, item_name.getText().toString(), et_view.getText().toString(), category_id);
                }

                //add item(s) to db
                int result = 0;
                for (int i = 0; i < noOfDateBoxes; ++i) {
                    int id = databaseHelper.addItem(items[i]);
                    if (id != -1) {
                        result++;
                        items[i].setId(id);
                        setAlarm(items[i]);
                    } else
                        result = noOfDateBoxes * -1;
                }

                Intent intent = new Intent(Item.this, ItemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void setDate(View view) {
        //date picker
        EditText et_view = (EditText) view;

        //get current date
        final Calendar cal = Calendar.getInstance();
        int cur_year = cal.get(Calendar.YEAR);
        int cur_month = cal.get(Calendar.MONTH);
        int cur_day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(Item.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                year = year1;
                month = month1;
                day = day1;
                if (year < MIN_YEARS || year > MAX_YEARS) {
                    Toast.makeText(Item.this, "Enter a valid date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                et_view.setText(day + "/" + (month + 1) + "/" + year);
            }
        }, cur_year, cur_month, cur_day);
        datePickerDialog.show();
    }

    public void removeDateBox(View view) {// this method is called when the user wants to remove a date box from the layout.
        noOfDateBoxes--;
        container.removeView((View) view.getParent());
    }

    public void addDateBox(View view) { // This method is called when the user wants to add a new date box to the layout. It is associated with the "Add Date" button.upr
        //add another date box to the layout, below current one
        if(noOfDateBoxes + 1 > maxDateBoxes) {
            Toast.makeText(this, "Cannot exceed 8 dates!", Toast.LENGTH_SHORT).show();
            return;
        }
        noOfDateBoxes++;
        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout date_box = (LinearLayout) inflater.inflate(R.layout.date_box, container, false);
        ImageView btn = date_box.findViewById(R.id.add_date_box);
        btn.setOnClickListener(this::removeDateBox);
        btn.setImageResource(R.drawable.ic_remove_blue);
        container.addView(date_box);
    }

    private void setAlarm(Item item) {
        //Format: DD/MM/YYYY or D/MM/YYYY or DD/M/YYYY or D/M/YYYY
        String date = item.getDate();
        int endIndex = (date.charAt(1) == '/') ? 1 : 2;
        int set_day = Integer.parseInt(date.substring(0, endIndex));
        int endIndex2 = (date.charAt(endIndex + 2) == '/') ? endIndex + 2 : endIndex + 3;
        int set_month = Integer.parseInt(date.substring(endIndex + 1, endIndex2));
        int set_year = Integer.parseInt(date.substring(endIndex2 + 1));

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.YEAR, set_year);
        c.set(Calendar.MONTH, set_month - 1);
        c.set(Calendar.DAY_OF_MONTH, set_day);

        Calendar cur = Calendar.getInstance();
        if(cur.after(c)) {
            //if current date is after expiry date
            //i.e., item has already expired
            item.setExpiry(true);
        }

        //subtract notify days
        c.add(Calendar.DAY_OF_MONTH, -category_days);
        c.add(Calendar.SECOND, 30);


        if (cur.before(c)) {
            //set alarm only if notify date is after current date
            alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), Receiver.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("days", category_days + "");
            int requestCode = item.getId();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }


    public Item(int id, String name, String date, int categoryID) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.categoryID = categoryID;
        this.expired = false;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id: " + id +
                ", name: " + name +
                ", date: " + date +
                ", categoryID: " + categoryID +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpiry(boolean expired) {
        this.expired = expired;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}

