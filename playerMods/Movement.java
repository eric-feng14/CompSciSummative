package playerMods;
import becker.robots.*;

/**
 * Precise movement
 * @author Richard
 * @version 6/7/2025
 */
public class Movement {
	private Direction direction;
	private int distance;
	
	/**
	 * Initializes all fields of movement
	 * @param direction - direction of movement
	 * @param distance - distance of travel
	 */
	public Movement(Direction direction, int distance) {
		super();
		this.direction = direction;
		this.distance = distance;
	}

	/**
	 * Gets the direction of movement
	 * @return - direction of movement
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Sets the direction of movement
	 * @param direction - direction of movement
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Gets the distance of travel
	 * @return - distance of travel
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 * Sets the distance of travel
	 * @param distance - distance of travel
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
}
