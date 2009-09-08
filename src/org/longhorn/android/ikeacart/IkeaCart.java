package org.longhorn.android.ikeacart;

import android.app.TabActivity;
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
import android.widget.TextView;
import android.widget.TabHost;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class IkeaCart extends TabActivity {
    
    public static final int INSERT_ID = Menu.FIRST;
    public static final int DELETE_ID = Menu.FIRST + 1;

    private static final int
	ACTIVITY_CREATE = 0,
	ACTIVITY_EDIT = 1;

    private static final String[][] TABS = {
	{"tab_default", "sort by" },
	{"tab_priority", "priority" },
	{"tab_price", "price" }
    };
    private int itemIndex = 1;
    private ItemDao itemDao;
    private int sortOrder = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView( R.layout.item_list_tab );
	itemDao = new ItemDao( this );
	itemDao.open();
	initTab( );
	fillData();
	registerForContextMenu( getListView() );
    }

    private void initTab( ) {
	TabHost tabHost = getTabHost();
	for ( int i = 0; i < TABS.length; i++ ) {
	    
	    tabHost.addTab( tabHost.newTabSpec( TABS[i][0] )
			    .setIndicator( TABS[i][1])
			    .setContent( R.id.main_table ) );
	}
	tabHost.setCurrentTab( 1 );
	tabHost.setCurrentTab( 0 );
	tabHost.setOnTabChangedListener( new TabHost.OnTabChangeListener() {
		public void onTabChanged( String tabId ) {
		    onSortTabChanged( tabId );
		}
	    } );
    }

    private void onSortTabChanged( String tabId ) {
	
	if ( TABS[1][0].equals( tabId ) ) {
	    sortOrder = 1;
	} else if ( TABS[2][0].equals( tabId ) ) {
	    sortOrder = 2;
	} else {
	    sortOrder = 0;
	}
	fillData();
    }
    private ListView getListView() {
	return (ListView) findViewById( android.R.id.list );
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
    public void onCreateContextMenu(ContextMenu menu, View v,
				    ContextMenuInfo menuInfo) {
	menu.add( 0, DELETE_ID, 0, R.string.menu_delete);
	super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	switch( item.getItemId() ) {
	case DELETE_ID:
	    AdapterContextMenuInfo info =
		(AdapterContextMenuInfo) item.getMenuInfo();
	    itemDao.deleteItem( info.id );
	    fillData();
	    return true;
	}
	return super.onContextItemSelected(item);
	
    }
    /**
    @Override
    protected void onListItemClick( ListView l, View v, int pos, long id ) {
	super.onListItemClick( l, v, pos, id );
	Intent intent = new Intent( this, ItemEdit.class );
	intent.putExtra( ItemDao.KEY_ROWID, id );
	startActivityForResult( intent, ACTIVITY_EDIT );
    }
    **/
    @Override
    protected void onActivityResult( int req, int res, Intent intent ) {

	super.onActivityResult( req, res, intent );
	fillData( );
    }

    private void fillData( ) {
	fillSummary();
	fillListView();
    }

    private void fillSummary() {
	CartService cartService = new CartService( itemDao );

	TextView miscText = ( TextView ) findViewById( R.id.misc_summary );
	TextView costText= ( TextView ) findViewById( R.id.cost_summary );
	String miscInfo = "No item yet";
	int totalItems = cartService.getTotalItems();
	if ( totalItems > 0 ) {
	    miscInfo = totalItems + " item";
	    if ( totalItems > 1 )
		miscInfo += "s";
	} 

	miscText.setText( miscInfo );

	double totalCost = cartService.getTotalCost();
	if ( totalCost > 0 ) {
	    costText.setText( Item.formatPrice( totalCost ) );
	} else {
	    costText.setText( "" );
	}
    }
    private void fillListView() {
	
	Cursor c = null;

	if ( sortOrder == 1 ) { 
	    c = itemDao.getCursorOfAllItemsByPriority();
	} else if ( sortOrder == 2 ) { 
	    c = itemDao.getCursorOfAllItemsByPrice();
	} else {
	    c = itemDao.getCursorOfAllItems();
	}
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
	    new SimpleCursorAdapter( this, R.layout.item_table_row, c,
				     from, to );
	items.setViewBinder( new RowViewBinder() );
	ListView list = getListView();
	list.setAdapter( items );
	list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
		public void onItemClick( AdapterView<?>parent,
					 View view,
					 int position,
					 long id ) {
		    Intent intent = new Intent( IkeaCart.this, ItemEdit.class );
		    intent.putExtra( ItemDao.KEY_ROWID, id );
		    startActivityForResult( intent, ACTIVITY_EDIT );
		}
	    } );
    }
}
