package players;
import java.util.*;
import app.Main;
import becker.robots.*;

/**
 * Abstract class for a player in the tag game
 * @author Richard Tuo, Eric Feng, Felix Wang
 * @version 5/25/2025
 */
public abstract class Player extends EnhancedBot{
	private static int nextID = 0; // Next PLAYER_ID of next created player; corresponds with index of playerList
	
	protected static Random generator = new Random();
	private int speed;
	private final int PLAYER_ID;
	private boolean isDefeated;
	private final String TYPE;
	
	/**
	 * Constructor of player
	 * post: records all information of player when initialized
	 * @param city - city of player
	 * @param s - street of player
	 * @param a - avenue of player
	 * @param d - Direction of player
	 */
	public Player(City city, int s, int a, Direction d, int speed, String TYPE, boolean defeated) {
		super(city, s, a, d);
		this.PLAYER_ID = nextID;
		this.speed = speed;
		this.isDefeated = defeated;
		this.TYPE = TYPE;
		
		Player.nextID++; // Iterates playerID to create a unique player identification number
	}
	
	/**
	 * The player's function is performed when this method is called
	 * pre: It is called upon in the application class
	 * post: The action determined by helper methods will be performed
	 */
	public abstract void performAction(PlayerRecord[] players);
	
	/**
	 * Determines if is defeated
	 * @return - isDefeated
	 */
	public boolean isDefeated() {
		return isDefeated;
	}
	
	/**
	 * Sets player's defeat status
	 * @param isDefeated - robot is defeated/tagged
	 */
	public void setDefeated(boolean isDefeated) {
		this.isDefeated = isDefeated;
	}

	/**
	 * Gets stamina of robot
	 * @return - stamina of robot
	 */
	public int obtainSpeed() {
		return this.speed;
	}

	/**
	 * Sets stamina of robot
	 * @param stamina - stamina of robot
	 */
	public void setStamina(int speed) {
		this.speed = speed;
	}
	
	/**
	 * Gets the player ID of Player
	 * @return - PLAYER_ID
	 */
	public int getPLAYER_ID() {
		return this.PLAYER_ID;
	}
	
	public String getTYPE() {
		return this.TYPE;
	}
}
