package players;

/**
 * Record for player information
 * @author tuojs
 * @version 5/26/2025
 */
public class PlayerRecord {
	private int street;
	private int avenue;
	private int speed;
	private final String ID;
	
	/**
	 * Constructor of PlayerRecord
	 * @param playerID - ID of player
	 * @param street - street of city
	 * @param avenue - avenue of city
	 */
	public PlayerRecord(String ID, int speed, int street, int avenue) {
		this.ID = ID;
		this.street = street;
		this.avenue = avenue;
		this.speed = speed;
	}
	
	/**
	 * Gets the street value
	 * @return - street number
	 */
	public int getStreet() {
		return street;
	}
	
	/**
	 * Sets street value
	 * @param street - street number
	 */
	public void setStreet(int street) {
		this.street = street;
	}
	
	/**
	 * Gets avenue value
	 * @return - avenue number
	 */
	public int getAvenue() {
		return avenue;
	}

	/**
	 * Sets avenue value
	 * @param avenue - avenue number
	 */
	public void setAvenue(int avenue) {
		this.avenue = avenue;
	}
	
	/**
	 * Gets the speed of player
	 * @return - speed of player
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed of player
	 * @param speed - speed of player
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * Gets playerID
	 * @return - ID of player
	 */
	public String getID() {
		return this.ID;
	}
}
