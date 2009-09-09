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

public class PreferenceEdit extends Activity {
    
    private EditText taxText;
    private PreferenceDao prefDao;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView( R.layout.pref_edit );
	initDao();
	initEditFields();
	populateFields();
	setConfirmTrigger();
    }

    private void initDao() {
	prefDao = new PreferenceDao( this );
	prefDao.open();
    }
    private void initEditFields( ) {
	taxText = (EditText) findViewById( R.id.tax );
    }
    
    private void populateFields() {
	Preference pref = prefDao.getPreference();
	taxText.setText( pref.formatTaxPercent() );
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
    }
    
    @Override
    protected void onPause() {
	super.onPause();
	saveState();
    }
    
    private void saveState() {
	Preference pref = new Preference();
	pref.setTaxPercent( Double.parseDouble( taxText.getText().toString() ) );
	prefDao.savePreference( pref );
    }

    @Override
    protected void onResume() {
	super.onResume( );
	populateFields();
    }
}
