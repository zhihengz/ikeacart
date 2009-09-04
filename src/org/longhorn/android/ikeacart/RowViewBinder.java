package org.longhorn.android.ikeacart;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class RowViewBinder implements ViewBinder {

    @Override
    public boolean setViewValue( View view, Cursor cursor, int col ) {
	
	int colUnitPrice = cursor.getColumnIndex( ItemDao.KEY_UNITPRICE );
	int colQuantity = cursor.getColumnIndex( ItemDao.KEY_QUANTITY );
	int colPriority = cursor.getColumnIndex( ItemDao.KEY_PRIORITY );
	if ( colUnitPrice == col ) {
	    TextView unitPriceTextView = (TextView)view;
	    unitPriceTextView.setText( Item.formatPrice( cursor.getFloat( col ) ) );
	    return true;
	} else if ( colQuantity == col ) {
	    TextView quantityTextView = (TextView)view;
	    quantityTextView.setText( "(" + cursor.getInt( col ) + ")");
	    return true;
	} else if ( colPriority == col ) {
	    TextView priorityTextView = (TextView)view;
	    int priority = cursor.getInt( col );
	    StringBuilder buf = new StringBuilder( "");
	    for ( int i = 0; i < priority; i++ ) {
		buf.append( "* " );
	    }
	    priorityTextView.setText( buf.toString());
	    return true;
	}
	
	return false;
    }
				     
}
