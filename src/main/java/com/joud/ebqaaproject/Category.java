//When user adds a category
package com.joud.ebqaaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Category extends AppCompatActivity {
    public static final int MIN_NOTIFICATION = 1; // user chooses when to get the notification
    public static final int MAX_NOTIFICATION = 10;// highest number of days a user can choose
    int imgCounter = 1; //to move to the next image
    int imageNum = 2; //number of images available in the program
    Button addButton;
    TextView pageTitle;
    EditText categoryName, notificationDays; // EditText for user input of category name and notification days
    ImageView returnButton, previousButton, nextButton, img;
    DatabaseHelper databaseHelper;// helper class for database operations


//category class
    private int id;
    private String name;
    private int days;
    private int imageNo;
    public Category() {
        // Empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category);// display the design by referencing its' xml file

        pageTitle = findViewById(R.id.page_title);// initialize UI elements by referencing the id from xml file
        returnButton = findViewById(R.id.toolbar_icon);
        previousButton = findViewById(R.id.prev);
        img = findViewById(R.id.img);
        nextButton = findViewById(R.id.next);
        addButton = findViewById(R.id.add_category);
        categoryName = findViewById(R.id.name);
        notificationDays = findViewById(R.id.days);

        databaseHelper = new DatabaseHelper(Category.this);// initialize the database helper

        pageTitle.setText(R.string.add_category);// set the title of the page

        returnButton.setImageResource(R.drawable.left_arrow);// set the image resource for the return button
        returnButton.setContentDescription("Return");// set the content description for accessibility

        returnButton.setOnClickListener(new View.OnClickListener() {//if the user clicks on the return button, go to the category list
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Category.this, MainActivity.class);// when user clicks on the return button, go to MainActivity
                startActivity(intent);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {// when user clicks previous button, show the previous image
            @Override
            public void onClick(View view) {
                imgCounter--;//decrement to previous image
                if (imgCounter != 0)//check if user isn't on the first image
                    setImage(imgCounter);
                else
                    imgCounter = 1;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {//if user clicks on next button, view next image
            @Override
            public void onClick(View view) {
                imgCounter++;//increment/forward to next image
                if (imgCounter <= imageNum)// check if counter is within the image range
                    setImage(imgCounter);
                else
                    imgCounter = imageNum;
            }
        });

        //✅️ when user clicks on the add button, add the category to the database
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryNameText = categoryName.getText().toString();
                String notificationDaysText = notificationDays.getText().toString();

                if (categoryNameText.isEmpty() || notificationDaysText.isEmpty()) {
                    Toast.makeText(Category.this, "Please enter category name and notification days!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int notificationDaysValue;
                try {
                    notificationDaysValue = Integer.parseInt(notificationDaysText);
                } catch (NumberFormatException e) {// handle invalid input for notification days
                    Toast.makeText(Category.this, "Invalid notification days!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (notificationDaysValue < MIN_NOTIFICATION || notificationDaysValue > MAX_NOTIFICATION) {// check if notification days are within the valid range
                    Toast.makeText(Category.this, "Notification days must be between 1 and 10.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ‼️ add the category to the database with these details
                Category category = new Category(-1, categoryNameText, notificationDaysValue, imgCounter);
                boolean result = databaseHelper.addCategory(category);
                if (result) {
                    // if adding the category is successful
                    Toast.makeText(Category.this, "Category Added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Category.this, MainActivity.class);
                    startActivity(intent);
                } else { // if an error occurs while adding the category, show an error message
                    Toast.makeText(Category.this, "An Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setImage(int counter) { //display the chosen image by user
        switch (counter) {
            case 1:
                img.setImageResource(R.drawable.food_img);
                break;
            case 2:
                img.setImageResource(R.drawable.medicine_img);
                break;
        }
    }



    //category class
    public Category(int id, String name, int days, int imageNo) {
        this.id = id;
        this.name = name;
        this.days = days;
        this.imageNo = imageNo;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id: " + id +
                ", name: " + name +
                ", days: " + days +
                ", imageName: " + imageNo +
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

    public int getDays() {
        return days;
    }

    public void setdDays(int days) {
        this.days = days;
    }

    public int getImageNo() {
        return imageNo;
    }

    public void setImageNo(int imageNo) {
        this.imageNo = imageNo;
    }
}