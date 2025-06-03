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
	
	protected ArrayList<PlayerRecord> priorityList = new ArrayList<PlayerRecord>();
	private int speed;
	private final int PLAYER_ID;
	public  boolean isDefeated;
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
	 * makes robot do the thing it is supposed to do
	 */
	public void doThing(PlayerRecord[] players) {
		this.sortPriority(players); //update other player priority
		this.performAction(); //do what the robot is supposed to do
	}
	
	/**
	 * Sorts the priority list of the player
	 */
	protected abstract void sortPriority(PlayerRecord[] players);
	
	/**
	 * The player's function is performed when this method is called
	 * pre: It is called upon in the application class
	 * post: The action determined by helper methods will be performed
	 */
	protected abstract void performAction();
	
	public PlayerRecord getPlayerRecord(int PLAYER_ID){
		// Checks if index is out of bounds or not
		if (PLAYER_ID >= 0 && PLAYER_ID < this.priorityList.size()) {
			return priorityList.get(PLAYER_ID);
		}
		System.out.println("ERROR");
		return new PlayerRecord(null, -1, 0, 0, 0);
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
