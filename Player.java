
import java.util.*;
import becker.robots.*;

/**
 * Abstract class for a player in the tag game
 * @author tuojs
 * @version 5/25/2025
 */
public abstract class Player extends EnhancedBot{
	private static final int PLAYER_INDEX_LENGTH = 3; // Length of each index of playerList
	private static int nextID = 0; // Next playerID of next created player; corresponds with index of playerList
	private static List<int[]> playerList = new ArrayList<int[]>();
	
	private int playerID; // Used to identify player - unique to each Player object
	
	/**
	 * Constructor of player
	 * post: records all information of player when initialized
	 * @param city - city of player
	 * @param s - street of player
	 * @param a - avenue of player
	 * @param d - Direction of player
	 */
	public Player(City city, int s, int a, Direction d) {
		super(city, s, a, d);
		this.playerID = Player.nextID;
		this.recordPlayer();
	}
	
	/**
	 * The player's function is performed when this method is called
	 * pre: It is called upon in the application class
	 * post: The action determined by helper methods will be performed
	 */
	public void performAction() {
		this.recordPlayer();
	}
	
	/**
	 * Records all player information
	 */
	private void recordPlayer() {
		// Adds player info or sets player info on playerList
		if(playerID < nextID) {
			Player.playerList.set(playerID, new int[]{playerID, this.getStreet(), this.getAvenue()});
		} else {
			Player.playerList.add(new int[]{playerID, this.getStreet(), this.getAvenue()});
			Player.nextID++; // Iterates playerID to create a unique player identification number
		}
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
