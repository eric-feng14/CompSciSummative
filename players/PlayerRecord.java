package players;

/**
 * Record for player information
 * @author tuojs
 * @version 5/26/2025
 */
public class PlayerRecord {
	private int street, avenue;
	private int speed, PLAYER_ID;

	private final String TYPE;
	
	/**
	 * Constructor of PlayerRecod
	 * @param playerTYPE - TYPE of player
	 * @param street - street of city
	 * @param avenue - avenue of city
	 */
	public PlayerRecord(String TYPE, int street, int avenue, int speed) {
		this.TYPE = TYPE;
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
	
	public int getPLAYER_ID() {
		return PLAYER_ID;
	}

	public void setPLAYER_ID(int PLAYER_ID) {
		this.PLAYER_ID = PLAYER_ID;
	}
	/**
	 * Gets playerTYPE
	 * @return - TYPE of player
	 */
	public String getTYPE() {
		return this.TYPE;
	}
}
