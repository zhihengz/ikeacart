package org.longhorn.android.ikeacart;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class IkeaCart extends ListActivity {
    public static final int INSERT_ID = Menu.FIRST;
    private int itemIndex = 1;
    private ItemDao itemDao;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView( R.layout.item_list );
	itemDao = new ItemDao( this );
	itemDao.open();
	fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
	menu.add( 0, INSERT_ID, 0, R.string.menu_insert );
	return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
	case INSERT_ID:
	    createItem();
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    private void createItem() {
	Item item = new Item( "Item " + (itemIndex++),
			      "Home Improvement",
			      10.99,
			      2,
			      5 );
	itemDao.createItem( item );
	fillData();
    }

    private void fillData() {
	Cursor c = itemDao.getAllItems();
	startManagingCursor( c );
	String[] from = new String[] { 
	    itemDao.KEY_NAME,
	    itemDao.KEY_LOCATION,
	    itemDao.KEY_QUANTITY,
	    itemDao.KEY_UNITPRICE,
	    ItemDao.KEY_PRIORITY
	};
	int[] to = new int[] { 
	    R.id.name, 
	    R.id.location,
	    R.id.quantity,
	    R.id.unitprice,
	    R.id.priority
	};
	SimpleCursorAdapter items =
	    new SimpleCursorAdapter( this, R.layout.items_row, c,
				     from, to );
	items.setViewBinder( new RowViewBinder() );
	setListAdapter( items );
    }
}
