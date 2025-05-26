package players;

/**
 * Record for player information
 * @author tuojs
 * @version 5/26/2025
 */
public class PlayerRecord {
	private int street;
	private int avenue;
	private final Player player;
	
	/**
	 * Constructor of PlayerRecord
	 * @param playerID - ID of player
	 * @param street - street of city
	 * @param avenue - avenue of city
	 */
	public PlayerRecord(Player player, int street, int avenue) {
		this.player = player;
		this.street = street;
		this.avenue = avenue;
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
	 * Gets playerID
	 * @return - ID of player
	 */
	public Player getPlayer() {
		return this.player;
	}
}
