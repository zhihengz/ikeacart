package org.longhorn.android.ikeacart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.database.Cursor;

public class ItemEdit extends Activity {
    
    private EditText nameText, quantityText, unitPriceText;
    private Long rowId;
    private ItemDao itemDao;
    private Item currItem;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView( R.layout.item_edit );
	initDao();
	initEditFields();
	findRowId( savedInstanceState );
	populateFields();
	setConfirmTrigger();
    }

    private void initDao( ) {
	itemDao = new ItemDao( this );
	itemDao.open();
    }

    private void initEditFields( ) {
	nameText = (EditText) findViewById( R.id.name );
	quantityText = (EditText) findViewById( R.id.quantity );
	unitPriceText = (EditText) findViewById( R.id.unitprice );
	
    }
    private void findRowId( Bundle savedInstanceState) {
	
	rowId = savedInstanceState != null ?
	    savedInstanceState.getLong( ItemDao.KEY_ROWID ):
	    null;
	if ( rowId == null ) {
	    Bundle extras = getIntent( ).getExtras();
	    rowId = extras != null ? 
		extras.getLong( ItemDao.KEY_ROWID ):
		null;
	}
    }

    private Item getItemToPopulate( ) {

	Item item = null;
	if ( rowId != null ) {
	    Cursor cursor = itemDao.getCursorOfItem( rowId );
	    startManagingCursor( cursor );
	    item = itemDao.getItem( cursor );
	} else {
	    item = new Item( );
	    item.setName( "item name" );
	    item.setQuantity( 1 );
	    item.setLocation( "unknown" );
	    item.setPriority( 1 );
	    item.setUnitPrice( 0.0 );
	}
	return item;
    }
    private void populateFields() {
	Item item = getItemToPopulate( );
	currItem = item;
	nameText.setText( item.getName( ) );
	quantityText.setText( "" + item.getQuantity( ) );
	unitPriceText.setText( "" + item.getUnitPrice( ) );
    }

    
    private void setConfirmTrigger( ) {
	Button confirm = (Button) findViewById( R.id.confirm );
	confirm.setOnClickListener( new View.OnClickListener() {

		public void onClick( View view ) {
		    setResult( RESULT_OK );
		    finish();
		}
	    } );
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
	
	super.onSaveInstanceState( outState );
	outState.putLong( ItemDao.KEY_ROWID, rowId );
    }
    
    @Override
    protected void onPause() {
	super.onPause();
	saveState();
    }
    
    private void saveState() {
	Item item = currItem;
	item.setName( nameText.getText().toString() );
	item.setQuantity( Integer.parseInt( quantityText.getText().toString() ) );
	item.setUnitPrice(Float.parseFloat( unitPriceText.getText().toString() ) );
	if ( rowId == null ) {
	    Item savedItem = itemDao.createItem( item );
	    if ( savedItem.getId() != null )
		rowId = savedItem.getId( );
	} else {
	    item.setId( rowId );
	    itemDao.updateItem( item );
	}
    }

    @Override
    protected void onResume() {
	super.onResume( );
	populateFields();
    }
}
