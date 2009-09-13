package org.longhorn.android.ikeacart;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class RowViewBinder implements ViewBinder {

    @Override
    public boolean setViewValue( View view, Cursor cursor, int col ) {
	
	int colUnitPrice = cursor.getColumnIndex( ItemDao.KEY_UNITPRICE );
	int colQuantity = cursor.getColumnIndex( ItemDao.KEY_QUANTITY );
	int colPriority = cursor.getColumnIndex( ItemDao.KEY_PRIORITY );
	int colLocation = cursor.getColumnIndex( ItemDao.KEY_LOCATION );
	if ( colUnitPrice == col ) {
	    TextView unitPriceTextView = (TextView)view;
	    unitPriceTextView.setText( Item.formatPrice( cursor.getFloat( col ) ) );
	    return true;
	} else if ( colQuantity == col ) {
	    TextView quantityTextView = (TextView)view;
	    quantityTextView.setText( " (" + cursor.getInt( col ) + ")");
	    return true;
	} else if ( colPriority == col ) {
	    setupPriorityView( cursor.getInt( col ), view );
	    return true;
	} else if ( colLocation == col ) {
	    setupLocationView( cursor.getString( col ), view );
	    return true;
	}
	
	return false;
    }
    private void setupPriorityView( int priority, View view ) {

	TextView priorityTextView = (TextView)view;
	StringBuilder buf = new StringBuilder( "");
	for ( int i = 0; i < priority; i++ ) {
	    buf.append( "* " );
	}
	priorityTextView.setText( buf.toString());
    }
    private void setupLocationView( String location, View view ) {

	
	TextView locationTextView = (TextView)view;

	if ( Item.isLocationAisleAndBin( location ) ) {
	    String[] tokens = 
		Item.getAisleAndBinLocation( location );
	    locationTextView.setText( "Aisle: " + tokens[0] + 
				      "    Bin: " + tokens[1] );
	} else {
	    locationTextView.setText( location );
	}
    }
    /**
    private void setupPriorityView( int priority, View view ) {

	ImageView priorityView = (ImageView)view;
	StringBuilder buf = new StringBuilder( "");
	for ( int i = 0; i < priority; i++ ) {
	    buf.append( "* " );
	}            
	priorityView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

	priorityView.setImageResource( R.drawable.rate_4 );
	
    }
    **/
    
}
