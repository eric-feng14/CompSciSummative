package players;

/**
 * Record for player information
 * @author tuojs
 * @version 5/26/2025
 */
public class PlayerRecord {
	private int street, avenue;
	private int speed, hp;
	private final int PLAYER_ID;
	private final static int DEFAULT_SPEED = 1;
	private boolean isDefeated;
	private final String TYPE;
	private PlayerRecord currentTarget;
	//Note: the robots will not be attempting to learn each others stamina due to  potential inaccuracy (e.g. strategies could affect it)
	
	/**
	 * Constructor of PlayerRecod
	 * @param playerTYPE - TYPE of player
	 * @param street - street of city
	 * @param avenue - avenue of city
	 */
	public PlayerRecord(String TYPE, int PLAYER_ID, int street, int avenue, int speed, boolean isDefeated, int hp, 
			PlayerRecord currentTarget) {
		this.TYPE = TYPE;
		this.street = street;
		this.avenue = avenue;
		this.speed = speed;
		this.PLAYER_ID = PLAYER_ID;
		this.isDefeated = isDefeated;
		this.hp = hp;
		this.currentTarget = currentTarget;
	}
	
	/**
	 * Player constructor
	 * @param player - Player
	 */
	public PlayerRecord(Player player) {
		this(player.getTYPE(), player.getPLAYER_ID(), player.getStreet(), player.getAvenue(), 
				PlayerRecord.DEFAULT_SPEED, player.isDefeated(), player.getHp(), player.getCurrentTarget());
	}
	
	public PlayerRecord getCurrentTarget() {
		return currentTarget;
	}

	public void setCurrentTarget(PlayerRecord currentTarget) {
		this.currentTarget = currentTarget;
	}

	public int getHP() {
		return this.hp;
	}

	public void setHP(int hP) {
		this.hp = hP;
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
	 * Gets player ID
	 * @return - player id
	 */
	public int getPLAYER_ID() {
		return PLAYER_ID;
	}
	
	/**
	 * Gets playerTYPE
	 * @return - TYPE of player
	 */
	public String getTYPE() {
		return this.TYPE;
	}

	/**
	 * Gets isDefeated
	 * @return isDefeated
	 */
	public boolean isDefeated() {
		return isDefeated;
	}

	/**
	 * Sets isDefeated
	 * @param isDefeated - robot is defeated
	 */
	public void setDefeated(boolean isDefeated) {
		this.isDefeated = isDefeated;
	}
	
	/**
	 * Overridden toString
	 * @return - Returns the string representation of PlayerRecord
	 */
	@Override
	public String toString() {
		return "Street: " + this.getStreet() + ", Avenue: " + this.getAvenue() + ", Type: " + this.getTYPE();
	}
}
