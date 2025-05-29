package players;
import java.util.*;
import becker.robots.*;

/**
 * Abstract class for a player in the tag game
 * @author tuojs
 * @version 5/25/2025
 */
public abstract class Player extends EnhancedBot{
	private static int nextID = 0; // Next PLAYER_ID of next created player; corresponds with index of playerList
	private static List<PlayerRecord> playerList = new ArrayList<PlayerRecord>();
	
	protected PlayerRecord[] priorityList;
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
		this.updateList();
		
		Player.playerList.add(new PlayerRecord(type + ";" + speed + ";" + PLAYER_ID, this.getStreet(), this.getAvenue()));
		Player.nextID++; // Iterates PLAYER_ID to create a unique player identification number
	}
	
	/**
	 * makes robot do the thing it is supposed to do
	 */
	public void doThing() {
		updateList();
		performAction();
		recordPlayer();
	}
	
	/**
	 * Updates the priorityList of player
	 */
	private void updateList() {
		this.priorityList = new PlayerRecord[playerList.size()];
		// Sets all playerList Records to priorityList
		for (int i = 0; i < Player.playerList.size(); i++) {
			this.priorityList[i] = Player.playerList.get(i);
		}
		this.sortPriority();
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
	
	/**
	 * Records all player information
	 */
	private void recordPlayer() {
			Player.playerList.get(PLAYER_ID).setStreet(this.getStreet());
			Player.playerList.get(PLAYER_ID).setAvenue(this.getAvenue());
	}
	
	/**
	 * Getter for PlayerRecord in the ArrayList
	 * @param PLAYER_ID - ID of the player
	 * @return - PlayerRecord ID references
	 */
	public static PlayerRecord getPlayerRecord(int PLAYER_ID){
		// Checks if index is out of bounds or not
		if (PLAYER_ID >= 0 && PLAYER_ID < playerList.size()) {
			return playerList.get(PLAYER_ID);
		} else {
			System.out.println("ERROR");
			return new PlayerRecord(null, 0, 0);
		}
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
	 * Getter for the player list
	 * pre: accessed by other player classes
	 * @return - the list of player records
	 */
	protected static List<PlayerRecord> getPlayerList(){
		return Player.playerList;
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
