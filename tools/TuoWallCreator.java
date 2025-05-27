package tools;
import becker.robots.*;
import java.util.ArrayList;

/**
 * Makes walls with methods
 * @author tuojs
 * @version 4/9/2025 
 */
public class TuoWallCreator {
	private ArrayList<Wall> walls;
	private City city;
	/**
	 * Constructor - initializes empty wall List
	 * @param city - City
	 */
	public TuoWallCreator(City city) {
		this.city = city;
		this.walls = new ArrayList<Wall>();
	}
	
	/**
	 * Makes a singular wall
	 * @param x - x position of wall
	 * @param y - y position of wall
	 * @param d - direction of wall
	 */
	public void createWall(int x, int y, Direction d) {
		walls.add(new Wall(city, y, x, d));
	}
	
	/**
	 * Creates rectangular shape with walls facing outwards
	 * @param x - starting x position
	 * @param y - starting y position
	 * @param w - width
	 * @param h - height
	 * @return - 2D array with spots with walls filled with Walls
	 */
	public void createWallRect(int x, int y, int w, int h) {
		this.createWallDoubleH(x, y, w, h);
		this.createWallDoubleV(x, y, w, h);
	}
	
	/**
	 * Makes the horizontal sides of a rectangle
	 * @param x - x start position of the shape
	 * @param y - y start position of the shape
	 * @param w - width of the shape
	 * @param h - height of the shape
	 */
	protected void createWallDoubleH(int x, int y, int w, int h) {
		// Makes walls of the ceiling and ground
		for (int i = 0; i < w; i++) {
			new Wall(city, y, x+i, Direction.NORTH);
			new Wall(city, h+y-1, x+i, Direction.SOUTH);
		}
	}
	
	/**
	 * Makes the horizontal sides of a rectangle
	 * @param x - x start position of the shape
	 * @param y - y start position of the shape
	 * @param w - width of the shape
	 * @param h - height of the shape
	 */
	protected void createWallDoubleV(int x, int y, int w, int h) {
		// Makes walls of sides
		for (int i = 0; i < h; i++) {
			new Wall(city, y+i, x, Direction.WEST);
			new Wall(city, y+i, w+x-1, Direction.EAST);
		}
	}
	
	/**
	 * Creates line of walls
	 * pre: Length must be positive
	 * @param x - x starting position
	 * @param y - y starting position
	 * @param length - length of line
	 * @param extendVector - direction of extension
	 * @param d - direction of walls of the line
	 */
	public void createWallLine(int x, int y, int length, Direction extendVector, Direction d) {
		int yChange = 0;
		int xChange = 0;
		// Determines which direction the walls will extend
		switch(extendVector) {
			case EAST: 
				xChange = 1;
				break;
			case WEST:
				xChange = -1;
				break;
			case SOUTH:
				yChange = 1;
				break;
			case NORTH:
				yChange = -1;
				break;
			default:
		}
		// Creates line of walls according to parameters
		for (int i = 0; i < length; i++) {
			createWall(x + xChange * i, y + yChange * i, d);
		}
	}
	
	/**
	 * Getter for city
	 * @return - city
	 */
	public City getCity() {
		return this.city;
	}
}
