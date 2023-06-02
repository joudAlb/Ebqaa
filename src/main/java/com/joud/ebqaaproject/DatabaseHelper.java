package com.joud.ebqaaproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "trackerDB.db";
    public static final String CAT_TABLE_NAME = "Category";
    public static final String ITEM_TABLE_NAME = "Item";
    public static final String COLUMN_1 = "ID";
    public static final String COLUMN_2 = "Name";
    public static final String ITEM_COLUMN_3 = "Date";
    public static final String ITEM_COLUMN_4 = "CategoryID";
    public static final String CAT_COLUMN_3 = "Days";
    public static final String CAT_COLUMN_4 = "ImageNo";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //called the 1st time db is accessed
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEM_TABLE = "CREATE TABLE " + ITEM_TABLE_NAME + "( " +
                COLUMN_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_2 + " TEXT NOT NULL, " +
                ITEM_COLUMN_3 + " TEXT NOT NULL, " +
                ITEM_COLUMN_4 + " REFERENCES " + CAT_TABLE_NAME + "(" + COLUMN_1 + ") ON DELETE CASCADE);";
        String CREATE_CAT_TABLE = "CREATE TABLE " + CAT_TABLE_NAME + "( " +
                COLUMN_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_2 + " TEXT NOT NULL, " +
                CAT_COLUMN_3 + " INTEGER NOT NULL, " +
                CAT_COLUMN_4 + " INTEGER NOT NULL);";
        db.execSQL(CREATE_CAT_TABLE);
        db.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + CAT_TABLE_NAME);
        db.execSQL("DROP TABLE " + ITEM_TABLE_NAME);
    }

    //View>Tool Windows>Dev File Explorer>com.blah.>get the db
    public Boolean addCategory(Category category) {
        //to add_btn a category to the db
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_2, category.getName());
        cv.put(CAT_COLUMN_3, category.getDays());
        cv.put(CAT_COLUMN_4, category.getImageNo());

        long result = db.insert(CAT_TABLE_NAME, null, cv);
        db.close();
        return result != -1;
    }

    public int removeCategory(Category category) {
        //to delete a category in the db
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CAT_TABLE_NAME, COLUMN_1 + "=" + category.getId(), null);
    }

    public List<Category> getAllCategories() {
        //to return a list of all categories
        List<Category> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + CAT_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()) {
            //loop through the cursor
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int days = cursor.getInt(2);
                int imageNo = cursor.getInt(3);

                Category category = new Category(id, name, days, imageNo);
                returnList.add(category);
            } while(cursor.moveToNext());
        }
        //close the cursor and db once done
        cursor.close();
        db.close();
        return returnList;
    }

    public int addItem(Item item) {
        //to add_btn an item to the db
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_2, item.getName());
        cv.put(ITEM_COLUMN_3, item.getDate());
        cv.put(ITEM_COLUMN_4, item.getCategoryID());

        int result = (int) db.insert(ITEM_TABLE_NAME, null, cv);
        db.close();
        return result;
    }

    public int removeItem(Item item) {
        //to delete an item from the db
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ITEM_TABLE_NAME, COLUMN_1 + "=" + item.getId(), null);
    }

    public List<Item> getAllItems(int categoryID) {
        //to return a list of all items in that category
        List<Item> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + ITEM_TABLE_NAME + " WHERE " + ITEM_COLUMN_4 + " = " + categoryID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()) {
            //loop through the cursor
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String date = cursor.getString(2);

                Item item = new Item(id, name, date, categoryID);
                returnList.add(item);
            } while(cursor.moveToNext());
        }
        //close the cursor and db once done
        cursor.close();
        db.close();
        return returnList;
    }

    public int[] getAllItemIds(int categoryID) {
        //to return a list of all items in that category
        String queryString = "SELECT * FROM " + ITEM_TABLE_NAME + " WHERE " + ITEM_COLUMN_4 + " = " + categoryID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        int[] returnList = new int[cursor.getCount()];
        int i = 0;
        if(cursor.moveToFirst()) {
            //loop through the cursor
            do {
                returnList[i++] = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        //close the cursor and db once done
        cursor.close();
        db.close();
        return returnList;
    }
}
