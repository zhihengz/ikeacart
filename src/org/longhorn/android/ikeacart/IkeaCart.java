package org.longhorn.android.ikeacart;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.content.Intent;
import android.view.View;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;

public class IkeaCart extends ListActivity {
    
    public static final int INSERT_ID = Menu.FIRST;

    private static final int
	ACTIVITY_CREATE = 0,
	ACTIVITY_EDIT = 1;

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
	registerForContextMenu( getListView() );
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

	Intent intent = new Intent( this, ItemEdit.class );
	startActivityForResult( intent, ACTIVITY_CREATE );
    }

    @Override
    protected void onListItemClick( ListView l, View v, int pos, long id ) {
	super.onListItemClick( l, v, pos, id );
	Intent intent = new Intent( this, ItemEdit.class );
	intent.putExtra( ItemDao.KEY_ROWID, id );
	startActivityForResult( intent, ACTIVITY_EDIT );
    }

    @Override
    protected void onActivityResult( int req, int res, Intent intent ) {

	super.onActivityResult( req, res, intent );
	fillData( );
    }
    private void fillData() {
	Cursor c = itemDao.getCursorOfAllItems();
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
