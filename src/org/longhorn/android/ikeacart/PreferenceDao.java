package org.longhorn.android.ikeacart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PreferenceDao {

    public static final String
	KEY_TAX = "tax",
	KEY_ROWID = "_id";

    private static final String TAG = "PreferenceDao";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqlite;
    
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "ikeacartPref";
    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE + 
	" (_id integer primary key, " +
	"tax float not null );";
    private static final String DATABASE_DROP = 
	"DROP TABLE IF EXISTS " + DATABASE_TABLE;

    private static final String DATABASE_INIT =
	"insert into " + DATABASE_TABLE + " (_id, tax ) " + 
	"values( 1, 0.0)";

    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
	
        @Override
	public void onCreate(SQLiteDatabase db) {
	    
            db.execSQL(DATABASE_CREATE);
	    db.execSQL(DATABASE_INIT);
        }

        @Override
	public void onUpgrade(SQLiteDatabase db, 
			      int oldVersion, 
			      int newVersion) {
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
    public PreferenceDao(Context ctx) {
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
    public PreferenceDao open() throws SQLException {
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
    public boolean savePreference( Preference pref ) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TAX, pref.getTaxPercent() );
        initialValues.put(KEY_ROWID, 1 );

        return sqlite.update( DATABASE_TABLE, initialValues,
			      KEY_ROWID + "= 1", null ) > 0 ;
    }

    /**
     * Return a Cursor positioned at the item that matches the given rowId
     * 
     * @param rowId id of item to retrieve
     * @return Cursor positioned to matching item, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor getCursorOfPreference( ) throws SQLException {

        Cursor mCursor =
                sqlite.query( true, 
			      DATABASE_TABLE, 
			      new String[] { KEY_TAX },
			      KEY_ROWID + "= 1", 
			      null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return an item at positioned cursor
     */
    public Preference getPreference( ) {
	
	Cursor cursor = getCursorOfPreference();
	Preference pref = new Preference();
	pref.setTaxPercent( cursor.getFloat( getColIndex( cursor, KEY_TAX ) ) );
	cursor.close();
	return pref;
    }
    
    private int getColIndex( Cursor cursor, String colName ) {
	return cursor.getColumnIndexOrThrow( colName );
    }
}
