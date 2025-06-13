package players;
import becker.robots.*;

/**
 * Robot with enhanced movement methods
 * @author Richard, Eric
 * @version 5/15/2025
 */
public class EnhancedBot extends RobotSE {
	
	/**
	 * Constructor for EnhancedBot
	 * @param city - city of the robot
	 * @param s - street of the city
	 * @param a - avenue of the city
	 * @param d - direction of the robot
	 */
	public EnhancedBot(City city, int s, int a, Direction d) {
		super(city, s, a, d);
	}
	
	/**
	 * 
	 */
	public void destroy() {
		try {
			this.breakRobot("Robot has been destroyed!");
		} catch (Exception e) {
			System.out.print("Destroy failed!");
		}
	}
	
	/**
	 * Turns to specified direction
	 * @param direction - direction to turn to
	 */
	public void turnTo(Direction direction) {
		// Getting difference of direction
		int current = this.directionToInt(this.getDirection());
		int newDirection = this.directionToInt(direction);
		int difference = current - newDirection;
		
		// Turns robot for difference of direction
		this.turnFor(difference);
	}
	
	/**
	 * Moves robot to specified location
	 * @param street - street of target position
	 * @param avenue - avenue of target position
	 */
	public void moveTo(int street, int avenue) {
		this.moveTo(street, avenue, false);
	}
	
	/**
	 * Moves robot to specified location
	 * @param street - street of target position
	 * @param avenue - avenue of target position
	 * @param reversedOrder - if true, proceeds street movement first
	 */
	public void moveTo(int street, int avenue, boolean reversedOrder) {
		int streetDif = street - this.getStreet();
		int avenueDif = avenue - this.getAvenue();
		// Checks if the order is reversed or not
		if (reversedOrder) {
			this.streetMove(streetDif);
			this.avenueMove(avenueDif);
		}else {
			this.avenueMove(avenueDif);
			this.streetMove(streetDif);
		}
	}
	
	/**
	 * Moves along avenue axis across streets
	 * @param streetDif - Difference of streets
	 */
	private void streetMove(int streetDif) {
		int movement = Math.abs(streetDif);
		// Turns North if negative and South if positive
		if (streetDif < 0) {
			this.turnTo(Direction.NORTH);
		} else if (streetDif > 0) {
			this.turnTo(Direction.SOUTH);
		}
		this.move(movement);
	}
	
	/**
	 * Moves along street axis across avenues
	 * @param avenueDif - Difference of avenues
	 */
	private void avenueMove(int avenueDif) {
		int movement = Math.abs(avenueDif);
		// Turns West if negative and EAST if positive
		if (avenueDif < 0) {
			this.turnTo(Direction.WEST);
		} else if (avenueDif > 0){
			this.turnTo(Direction.EAST);
		}
		this.move(movement);
	}
	
	/**
	 * Turn for this many times, but more efficient
	 * @param turns - the number of left turns robot will take
	 */
	private void turnFor (int turns) {
		// Ensures that difference is positive
		if (turns < 0)
			turns += 4;
		// Turns number of lefts using most efficient method
		switch (turns % 4) {
		case 1:
			this.turnLeft();
			break;
		case 2:
			this.turnAround();
			break;
		case 3:
			this.turnRight();
		}
	}
	
	/**
	 * Makes the direction enum into an integer
	 * @param direction - the direction of the robot
	 * @return - returns a number from 0-3 depending on the direction
	 */
	private int directionToInt(Direction direction) {
		// Determines number from direction
		switch(direction) {
		case EAST: 
			return 0;
		case SOUTH:
			return 1;
		case WEST:
			return 2;
		case NORTH: 
			return 3;
		default:
			return 0;
		}
	}
	
	/**
	 * Moves to next barrier
	 */
	public void moveToNext() {
		// Moves while the front is clear
		while(this.frontIsClear()) {
			this.move();
		}
	}
}
