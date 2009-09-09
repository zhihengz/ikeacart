package org.longhorn.android.ikeacart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ItemDao {

    public static final String
	KEY_NAME = "name",
	KEY_ROWID = "_id",
	KEY_PRIORITY = "priority",
	KEY_LOCATION = "location",
	KEY_UNITPRICE = "unitprice",
	KEY_QUANTITY = "quantity";

    private static final String[] ALL_KEYS = {
	KEY_ROWID, 
	KEY_NAME,
	KEY_LOCATION,
	KEY_UNITPRICE,
	KEY_QUANTITY,
	KEY_PRIORITY
    };    
    private static final String[] ALL_KEYS_W_PRICE = {
	KEY_ROWID, 
	KEY_NAME,
	KEY_LOCATION,
	KEY_UNITPRICE,
	KEY_QUANTITY,
	KEY_PRIORITY,
	KEY_UNITPRICE + " * " + KEY_QUANTITY + " as price", 
    };

    private static final String TAG = "ItemDao";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqlite;
    
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "ikeacart";
    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE + 
	" (_id integer primary key autoincrement, " +
	"name text not null, " +
	"location text not null, " +
	"priority integer not null, " +
	"unitprice float not null, " +
	"quantity integer not null);";
    private static final String DATABASE_DROP = 
	"DROP TABLE IF EXISTS " + DATABASE_TABLE;

    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
	
        @Override
	public void onCreate(SQLiteDatabase db) {
	    
            db.execSQL(DATABASE_CREATE);
        }

        @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + 
		  " to " + newVersion + ", which will destroy all old data");
	    db.execSQL( DATABASE_DROP );
	    onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ItemDao(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ItemDao open() throws SQLException {
        dbHelper = new DatabaseHelper(mCtx);
        sqlite = dbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        dbHelper.close();
    }


    /**
     * Create a new item. If the item is successfully created 
     * return the new rowId for that item, otherwise return
     * a -1 to indicate failure.
     * 
     * @return updated Item upon success, otherwise null
     */
    public Item createItem( Item item ) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, item.getName() );
        initialValues.put(KEY_LOCATION, item.getLocation() );
        initialValues.put(KEY_UNITPRICE, item.getUnitPrice() );
        initialValues.put(KEY_QUANTITY, item.getQuantity() );
        initialValues.put(KEY_PRIORITY, item.getPriority() );

        long ret = sqlite.insert(DATABASE_TABLE, null, initialValues);
	if ( ret < 0 ) {
	    return null;
	}
	item.setId( ret );
	return item;
    }

    /**
     * Delete the item with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteItem( Long rowId) {

        return sqlite.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all items in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor getCursorOfAllItems() {

        return sqlite.query( DATABASE_TABLE, ALL_KEYS,
			     null, null, null, null, null);
    }
    
    /**
     * Return a Cursor over the list of all items in the database by priority
     * 
     * @return Cursor over all notes
     */
    public Cursor getCursorOfAllItemsByPriority( ) {
	return sqlite.query( DATABASE_TABLE, ALL_KEYS,
			     null, null, null, null,
			     KEY_PRIORITY + " desc" + ", " + 
			     KEY_NAME );
    }
    /**
     * Return a Cursor over the list of all items in the database by price
     * 
     * @return Cursor over all notes
     */
    public Cursor getCursorOfAllItemsByPrice( ) {
	return sqlite.query( DATABASE_TABLE, ALL_KEYS_W_PRICE,
			     null, null, null, null,
			     "price desc" + ", " + KEY_NAME );
    }
    /**
     * Return a Cursor positioned at the item that matches the given rowId
     * 
     * @param rowId id of item to retrieve
     * @return Cursor positioned to matching item, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor getCursorOfItem(long rowId) throws SQLException {

        Cursor mCursor =
                sqlite.query( true, 
			      DATABASE_TABLE, ALL_KEYS,
			      KEY_ROWID + "=" + rowId, 
			      null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return an item at positioned cursor
     */
    public Item getItem( Cursor cursor ) {

	Item item = new Item();
	item.setId( cursor.getLong( getColIndex( cursor, KEY_ROWID ) ) );
	item.setName( cursor.getString( getColIndex( cursor, KEY_NAME ) ) );
	item.setLocation( cursor.getString( getColIndex( cursor, KEY_LOCATION ) ) );
	item.setUnitPrice( cursor.getFloat( getColIndex( cursor, KEY_UNITPRICE ) ) );
	item.setQuantity( cursor.getInt( getColIndex( cursor, KEY_QUANTITY ) ) );
	item.setPriority( cursor.getInt( getColIndex( cursor, KEY_PRIORITY ) ) );
	return item;
    }
    
    private int getColIndex( Cursor cursor, String colName ) {
	return cursor.getColumnIndexOrThrow( colName );
    }
    /**
     * Update the item using the details provided. The item to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateItem( Item item ) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, item.getName() );
        args.put(KEY_LOCATION, item.getLocation() );
        args.put(KEY_UNITPRICE, item.getUnitPrice() );
        args.put(KEY_QUANTITY, item.getQuantity() );
        args.put(KEY_PRIORITY, item.getPriority() );
        return sqlite .update( DATABASE_TABLE, args, 
			       KEY_ROWID + "=" + item.getId(), null) > 0;
    }
}
