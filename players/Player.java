package players;
import java.util.*;
import app.Main;
import becker.robots.*;
import powerUps.*;

/**
 * Abstract class for a player in the tag game
 * @author Richard Tuo, Eric Feng, Felix Wang
 * @version 5/25/2025
 */
public abstract class Player extends EnhancedBot{
	private static int nextID = 0; // Next PLAYER_ID of next created player; corresponds with index of playerList
	
	protected static Random generator = new Random();
	private int speed, hp;
	private final int PLAYER_ID;
	private boolean isDefeated;
	private final String TYPE;
	private PlayerRecord currentTarget;
	private int defense, strength, stamina;
	private final static int DEFAULT_HP = 100, DEFAULT_DEFENCE = 6, DEFAULT_STRENGTH = 10, DEFAULT_STAMINA = 5;
	private final static int NORMAL_HIT = 20, CRITICAL_HIT = 40, KNOCKOUT = 100;


	/**
	 * Constructor of player
	 * post: records all information of player when initialized
	 * @param city - city of player
	 * @param s - street of player
	 * @param a - avenue of player
	 * @param d - Direction of player
	 * @param speed - speed of player
	 * @param TYPE - type of player
	 * @param defeated - player isDefeated
	 * @param hp - Health of player
	 * @param currentTarget - target of the current player
	 */
	public Player(City city, int s, int a, Direction d, int speed, String TYPE, boolean defeated, int hp, 
			PlayerRecord currentTarget, int defence, int strength, int stamina) {
		super(city, s, a, d);
		this.PLAYER_ID = nextID;
		this.speed = speed;
		this.isDefeated = defeated;
		this.TYPE = TYPE;
		this.hp = hp;
		this.currentTarget = currentTarget;
		this.defense = defence;
		this.strength = strength;
		this.stamina = stamina;
		Player.nextID++; // Iterates playerID to create a unique player identification number
	}
	
	/**
	 * Constructor with default health and a default target (target is mainly used for attackers)
	 * @param city - city
	 * @param s - street 
	 * @param a avenue 
	 * @param d - Direction
	 * @param speed - speed of player
	 * @param TYPE - type of player
	 * @param defeated - isDefeated
	 */
	public Player(City city, int s, int a, Direction d, int speed, String TYPE, boolean defeated) {
		this(city, s, a, d, speed, TYPE, defeated, DEFAULT_HP, null, DEFAULT_DEFENCE, DEFAULT_STRENGTH, DEFAULT_STAMINA);
	}
	
	public abstract void sendSignal();
	
	public void sendInfo(int damageDealt, int victimID) {};
	
	public static int getNormalHit() {
		return NORMAL_HIT;
	}

	public static int getCriticalHit() {
		return CRITICAL_HIT;
	}

	public static int getKnockout() {
		return KNOCKOUT;
	}
	public int getStamina() {
		return stamina;
	}

	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public PlayerRecord getCurrentTarget() {
		return currentTarget;
	}

	public void setCurrentTarget(PlayerRecord currentTarget) {
		this.currentTarget = currentTarget;
	}

	/**
	 * Initializes playerRecords
	 * @param players - players
	 */
	public void initialize(PlayerRecord[] players) {}
	
	
	/**
	 * Gets health of player
	 * @return - health
	 */
	public int getHp() {
		return this.hp;
	}

	/**
	 * Sets hp of player
	 * @param hp - health
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}

	/**
	 * The player's function is performed when this method is called
	 * pre: It is called upon in the application class
	 * post: The action determined by helper methods will be performed
	 */
	public abstract void performAction(PlayerRecord[] players, ArrayList<EnhancedThing> powerUps);
	
	public abstract void pickPowerUp(EnhancedThing powerup);
	
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
	 * Sets speed of robot
	 * @param speed - speed of robot
	 */
	public void setSpeed(int speed) {
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
	
	/**
	 * Gets the defense value of Player
	 * @return - defense
	 */
	public int getDefense() {
		return this.defense;
	}
	
	/**
	 * Gets the strength of Player
	 * @return - strength
	 */
	public int getStrength() {
		return this.strength;
	}
}
