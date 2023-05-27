package com.joud.ebqaaproject;
//The purpose of the `DateComparator` class is to provide a custom comparator for sorting `Item` objects based on their dates. It implements the `Comparator<Item>` interface, which allows objects of the `Item` class to be compared and sorted using this comparator.
//The `compare()` method is overridden to define the comparison logic. In this case, the method compares the dates of two `Item` objects (`i1` and `i2`). It uses a `SimpleDateFormat` to parse the date strings stored in the `Item` objects into `Date` objects.
//The comparison is performed by calling the `compareTo()` method on the `Date` objects. The result of the comparison is then returned by using `Integer.compare()` to convert the result to -1, 0, or 1, as expected by the `compare()` method's contract.
//The comparator can be used to sort a list of `Item` objects based on their dates, for example, by using `Collections.sort()` and passing an instance of `DateComparator` as the comparator argument.
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<Item> {
    private SimpleDateFormat dateFormat; // used for parsing and formatting dates
    @Override
    public int compare(Item i1, Item i2) {

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date date1 = null, date2 = null;
        try {
            date1 = dateFormat.parse(i1.getDate());
            date2 = dateFormat.parse(i2.getDate());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int result = date1.compareTo(date2); // compare method takes two Item objects as parameters and returns an integer value indicating the comparison result.
        //compare method expects a negative, zero, or positive integer as the return value to indicate the order of the compared objects.

        return Integer.compare(result, 0);
        //result:
        // If result is negative, it means date1 is before date2.
        //If result is zero, it means date1 is equal to date2.
        //If result is positive, it means date1 is after date2.
        //0:
    }
}
