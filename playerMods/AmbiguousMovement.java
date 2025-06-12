package playerMods;
import becker.robots.*;

public class AmbiguousMovement {
	// DISTANCE / DEPTH OF MOVEMENT
	private int distance;
	
	// DIRECTIONS DENOTED BY INTEGERS
	
	/*
	 * Horizontal Direction States:
	 * 0 - None
	 * 1 - EAST
	 * 2 - WEST
	 * 3 - Both
	 */
	private int directionH;
	
	/*
	 * Vertical Direction States
	 * 0 - None
	 * 1 - NORTH
	 * 2 - SOUTH
	 * 3 - Both
	 */
	private int directionV;
	// Other numbers make direction undetermined
	
	
	/**
	 * Movement defined by state of horizontal and vertical directions and scalar distance
	 * @param directionH - state of horizontal direction
	 * @param directionV - state of vertical direction
	 * @param distance - depth of movement
	 */
	public AmbiguousMovement(int directionH, int directionV, int distance) {
		super();
		this.directionH = directionH;
		this.directionV = directionV;
		this.distance = distance;
	}
	

	/**
	 * Gets direction horizontal
	 * @return - horizontal direction
	 */
	public int getDirectionH() {
		return directionH;
	}

	/**
	 * Sets direction horizontal
	 * @param directionH - horizontal direction
	 */
	public void setDirectionH(int directionH) {
		this.directionH = directionH;
	}

	/**
	 * Gets direction vertical
	 * @return - vertical direction
	 */
	public int getDirectionV() {
		return directionV;
	}

	/**
	 * Sets direction vertical
	 * @param directionV - vertical direction
	 */
	public void setDirectionV(int directionV) {
		this.directionV = directionV;
	}
	
	/**
	 * Gets distance of movement
	 * @return - distance of travel
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Sets distance of movement
	 * @param distance - distance of travel
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
}
