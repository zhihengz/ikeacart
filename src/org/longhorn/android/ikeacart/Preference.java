package org.longhorn.android.ikeacart;

import java.text.DecimalFormat;

public class Preference {

    private double taxPercent;
    public Preference() {
	
    }
    
    public void setTaxPercent(double taxPercent) {
	this.taxPercent = taxPercent;
    }
    
    public double getTaxPercent() {
	return taxPercent;
    }
    
    public String formatTaxPercent( ) {
	return new DecimalFormat( "0.000" ).format( taxPercent );
    }
}

