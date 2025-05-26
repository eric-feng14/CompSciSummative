
import java.util.*;
import becker.robots.*;

/**
 * Abstract class for a player in the tag game
 * @author tuojs
 * @version 5/25/2025
 */
public abstract class Player extends EnhancedBot{
	private static int nextID = 0; // Next playerID of next created player; corresponds with index of playerList
	private static List<PlayerRecord> playerList = new ArrayList<PlayerRecord>();
	
	protected int playerID; // Used to identify player - unique to each Player object
	
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
	 * makes robot do the thing it is supposed to do
	 */
	public void doThing() {
		performAction();
		recordPlayer();
	}
	
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
		// Adds player info or sets player info on playerList
		if(playerID < nextID) {
			Player.playerList.get(playerID).setStreet(this.getStreet());
			Player.playerList.get(playerID).setAvenue(this.getAvenue());
		} else {
			Player.playerList.add(new PlayerRecord(this, this.getStreet(), this.getAvenue()));
			Player.nextID++; // Iterates playerID to create a unique player identification number
		}
	}
	
	/**
	 * Getter for PlayerRecord in the ArrayList
	 * @param playerID - ID of the player
	 * @return - PlayerRecord ID references
	 */
	public static PlayerRecord getPlayerRecord(int playerID){
		// Checks if index is out of bounds or not
		if (playerID >= 0 && playerID < playerList.size()) {
			return playerList.get(playerID);
		} else {
			System.out.println("ERROR");
			return new PlayerRecord(null, 0, 0);
		}
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
