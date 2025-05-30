package players;
import java.util.*;

import app.Main;
import becker.robots.*;

/**
 * Abstract class for a player in the tag game
 * @author tuojs
 * @version 5/25/2025
 */
public abstract class Player extends EnhancedBot{
	private static int nextID = 0; // Next PLAYER_ID of next created player; corresponds with index of playerList
	
	protected PlayerRecord[] priorityList = new PlayerRecord[Main.numOfPlayers];
	private int speed, stamina;
	private final int PLAYER_ID;
	
	/**
	 * Constructor of player
	 * post: records all information of player when initialized
	 * @param city - city of player
	 * @param s - street of player
	 * @param a - avenue of player
	 * @param d - Direction of player
	 */
	public Player(City city, int s, int a, Direction d, int speed, int stamina, String type) {
		super(city, s, a, d);
		this.PLAYER_ID = nextID;
		this.speed = speed;
		this.stamina = stamina;
		
		this.priorityList[nextID] = new PlayerRecord(type, s, a, speed);
		Player.nextID++; // Iterates playerID to create a unique player identification number
		this.sortPriority();
	}
	
	/**
	 * makes robot do the thing it is supposed to do
	 */
	public void doThing() {
		performAction();
		recordPlayer();
	}
	
	/**
	 * Sorts the priority list of the player
	 */
	protected abstract void sortPriority();
	
	/**
	 * The player's function is performed when this method is called
	 * pre: It is called upon in the application class
	 * post: The action determined by helper methods will be performed
	 */
	protected abstract void performAction();
	
	public PlayerRecord getPlayerRecord(int PLAYER_ID){
		// Checks if index is out of bounds or not
		if (PLAYER_ID >= 0 && PLAYER_ID < this.priorityList.length) {
			return priorityList[PLAYER_ID];
		}
		System.out.println("ERROR");
		return new PlayerRecord(null, -1, 0, 0);
	}
	/**
	 * Records all player information
	 */
	private void recordPlayer() {
		this.priorityList[PLAYER_ID].setStreet(this.getStreet());
		this.priorityList[PLAYER_ID].setAvenue(this.getAvenue());
	}
	
	/**
	 * Getter for speed
	 * @return - speed of robot in turn
	 */
	public int acquireSpeed() {
		return speed;
	}

	/**
	 * Sets speed of robot 
	 * @param speed - speed of robot
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	/**
	 * Gets stamina of robot
	 * @return - stamina of robot
	 */
	public int getStamina() {
		return stamina;
	}

	/**
	 * Sets stamina of robot
	 * @param stamina - stamina of robot
	 */
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}
	
	/**
	 * Gets the player ID of Player
	 * @return - PLAYER_ID
	 */
	public int getPLAYER_ID() {
		return this.PLAYER_ID;
	}
	
	/**
	 * Gets the next step's direction 
	 * @return - Direction of its next step
	 */
	protected abstract Direction getNextDirection();
	
	/**
	 * Determines the depth of next movement of player
	 * @return - the number of moves the player is required to make
	 */
	protected abstract int getNextMovement();
	
}
