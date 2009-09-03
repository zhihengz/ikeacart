package org.longhorn.android.ikeacart;

public class Item {

    private String name;
    private int quantity;
    private String location;
    private int priority;
    private Long id;
    private double unitPrice;
    
    public Item() {

    }
    public Item( String name, String location,
		 double unitPrice, int quantity, int priority ) {
	this.name = name;
	this.location = location;
	this.unitPrice = unitPrice;
	this.quantity = quantity;
	this.priority = priority;
    }

    
    public void setUnitPrice(double UnitPrice) {
    this.unitPrice = UnitPrice;
    }
    
    public double getUnitPrice() {
    return unitPrice;
    }
    
    public void setId(Long Id) {
    this.id = Id;
    }
    
    public Long getId() {
    return id;
    }
    
    public void setPriority(int Priority) {
    this.priority = Priority;
    }
    
    public int getPriority() {
    return priority;
    }
    
    public void setName(String Name) {
    this.name = Name;
    }
    
    public String getName() {
    return name;
    }
    
    
    public void setLocation(String Location) {
    this.location = Location;
    }
    
    public String getLocation() {
    return location;
    }
    
    
    public void setQuantity(int Quantity) {
    this.quantity = Quantity;
    }
    
    public int getQuantity() {
    return quantity;
    }
    
}
