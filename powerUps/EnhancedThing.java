package powerUps;
import becker.robots.*;
import players.*;

/**
 * EnhancedThing that represents a powerup, inheriting from Robot's Thing
 * @author Eric Feng
 * @version Due date: June 13 2025
 */
public abstract class EnhancedThing extends Thing{
	private static int nextID = 0;
	final private String type;
	final private int street, avenue;
	final private int ID;
	
	/**
	 * Constructor 
	 * @param c c is the city
	 * @param street street is the y-axis location
	 * @param avenue avenue is the x-axis location
	 * @param type type is the type of powerup
	 */
	public EnhancedThing(City c, int street, int avenue, String type) {
		super(c,street,avenue);
		this.street = street;
		this.avenue = avenue;
		this.type = type;
		this.ID = nextID;
		nextID++;
	}
	
	/**
	 * abstract method that passes the effect of the powerup to the player
	 * @param p p is the player
	 */
	public abstract void applyTo(Player p);
	
	/**
	 * Getter for location about powerup
	 * @return returns the powerups street
	 */
	public int getStreet() {
		return this.street;
	}
	
	/**
	 * Getter for location about powerup
	 * @return returns the powerup's avenue
	 */
	public int getAvenue() {
		return this.avenue;
	}
	
	
	/**
	 * Getter for the type of powerup
	 * @return returns the type of the current powerup
	 */
	public String getType() {
		return this.type;
	}

	public int getID() {
		return ID;
	}
}
