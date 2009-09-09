package org.longhorn.android.ikeacart;

import android.database.Cursor;
public class CartService {

    private static final float tax = (float)0.0825;
    private int totalItems;
    private double totalCost;

    public CartService( ItemDao itemDao, Preference pref ) {
	
	Cursor cursor = itemDao.getCursorOfAllItems();
	totalItems = cursor.getCount();
	totalCost = 0;
	if ( totalItems > 0 ) {
	    calcCost( cursor, pref );
	}
	cursor.close();
    }

    private void calcCost( Cursor cursor, Preference pref ) {
	if ( cursor.getCount() <= 0 )  {
	    return;
	}
	while( cursor.moveToNext() ) {
	    totalCost += 
		cursor.getFloat( cursor.getColumnIndexOrThrow( ItemDao.KEY_UNITPRICE ) ) 
		* 
		cursor.getInt( cursor.getColumnIndexOrThrow( ItemDao.KEY_QUANTITY ) );
	} ;
	totalCost = totalCost * ( 1 + pref.getTaxPercent() / 100.0 );
    }
    public int getTotalItems(){
	return totalItems;
    }

    public double getTotalCost() {
	return totalCost;
    }
}
