package org.longhorn.android.ikeacart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.database.Cursor;

public class ItemEdit extends Activity {
    
    private static int[] RadioButtonIds = new int[] {
	R.id.priority_1,
	R.id.priority_2,
	R.id.priority_3,
	R.id.priority_4,
	R.id.priority_5,
    };
    private EditText nameText, quantityText, unitPriceText;
    private EditText aisleText, binText;
    private Spinner locationSpinner;
    private Long rowId;
    private ItemDao itemDao;
    private Item currItem;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView( R.layout.item_edit_table );
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
	aisleText = (EditText) findViewById( R.id.aisle );
	binText = (EditText) findViewById( R.id.bin );
	locationSpinner = (Spinner ) findViewById( R.id.location );
	ArrayAdapter adapter = 
	    ArrayAdapter.createFromResource( this,
					     R.array.locations,
					     android.R.layout.simple_spinner_item );
	adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
	locationSpinner.setAdapter( adapter );
	
	View.OnClickListener radioListener = new View.OnClickListener() {
		public void onClick( View v ) {
		    String selected = ((RadioButton)v).getText().toString();
		    currItem.setPriority( Integer.parseInt( selected ) );
		}
	    };

	for ( int i = 0 ; i < RadioButtonIds.length; i++ ) {
	    RadioButton radio = ( RadioButton ) 
		findViewById( RadioButtonIds[i] );
	    radio.setOnClickListener( radioListener );
	}
    }
    
    private String[] getAllLocations( ) {
	return getResources().getStringArray( R.array.locations );
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
	unitPriceText.setText( item.getFormattedUnitPrice( ) );
	if ( item.getLocation() == null ) {
	    item.setLocation( getFirstChoiceOfLocation( ) );
	} else {
	    setLocation( item.getLocation( ) );
	}
	for ( int i = 0 ; i < RadioButtonIds.length; i++ ) {
	    RadioButton radio = ( RadioButton ) 
		findViewById( RadioButtonIds[i] );
	    int value = Integer.parseInt( radio.getText().toString() );
	    if ( value == item.getPriority() ) {
		radio.setChecked( true );
		break;
	    }
	}
    }

    private void setLocation( String location ) {
	
	String aisleBinLoc = getFirstChoiceOfLocation( );

	if ( Item.isLocationAisleAndBin( location ) ) {
	    setSpinnerSelected( aisleBinLoc );
	    setAisleAndBin( location );
	} else {
	    setSpinnerSelected( location );
	}
    }

    private void setAisleAndBin( String location ) {
	String[] tokens = Item.getAisleAndBinLocation( location );
	aisleText.setText( tokens[0] );
	binText.setText( tokens[1] );
    }
    private String getFirstChoiceOfLocation( ) {
	return getAllLocations()[0];
    }
    
    private void setSpinnerSelected( String location ) {
	String[] allLocations = getAllLocations();
	
	for ( int i = 0; i < allLocations.length; i++ ) {
	    if ( location.equals( allLocations[i] ) ) {
		locationSpinner.setSelection( i );
		return;
	    }
	}
	locationSpinner.setSelection( 0 );
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
	item.setLocation( formatLocation() );
	if ( rowId == null ) {
	    Item savedItem = itemDao.createItem( item );
	    if ( savedItem.getId() != null )
		rowId = savedItem.getId( );
	} else {
	    item.setId( rowId );
	    itemDao.updateItem( item );
	}
    }

    private String formatLocation( ) {
	
	if ( isValidInputOfAisleAndBin() ) {

	    String aisle = aisleText.getText().toString();
	    String bin = binText.getText().toString();
	    return aisle + ":" + bin;

	} else {
	    return locationSpinner.getSelectedItem().toString();
	}
    }

    private boolean isValidInputOfAisleAndBin( ) {
	return isNotEmptyInput( aisleText ) && 
	    isNotEmptyInput( binText );
    }

    private boolean isNotEmptyInput( EditText textInput ) {
	String input = textInput.getText().toString();
	return input != null && ! "".equals( input.trim() );
    }
    @Override
    protected void onResume() {
	super.onResume( );
	populateFields();
    }
}
