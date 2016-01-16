package GameObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The GameObject class is the basis from which all other game objects inherit
 * their basic item properties: x,y position, size, velocities, hitbox and
 * color.
 * 
 * @author Mason Dumaine
 *
 */
public abstract class GameObject
{
	// To keep track of the game entity's various properties.
	protected int xPos, yPos, size, xVel, yVel;
	protected Color objectColor;

	// Used for hit detection.
	protected Rectangle hitbox;

	/**
	 * The default constructor for this class initializes the data members with
	 * default values.
	 */
	public GameObject()
	{
		xPos = yPos = size = xVel = yVel = 0;
		hitbox = new Rectangle();
	}

	/**
	 * This is the constructor that is used for the actual instantiation of most
	 * game objects.
	 * 
	 * @param xPos
	 *            The x position that the object is centered on.
	 * @param yPos
	 *            The y position that the object is centered on.
	 * @param size
	 *            The width of the square that represents the object.
	 * @param color
	 *            The color of the object.
	 */
	public GameObject(int xPos, int yPos, int size, Color color)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.size = size;
		this.objectColor = color;

		this.hitbox = new Rectangle(xPos - size, yPos - size, size, size);
	}

	/**
	 * A simple getter for the x position.
	 * 
	 * @return The object's current x position.
	 */
	public int getX()
	{
		return xPos;
	}

	/**
	 * A simple getter for the y position.
	 * 
	 * @return The object's current y position.
	 */
	public int getY()
	{
		return yPos;
	}

	/**
	 * A simple setter for the x position.
	 * 
	 * @param x
	 *            Sets the object's current x position.
	 */
	public void setX(int x)
	{
		this.xPos = x;
	}

	/**
	 * A simple setter for the y position.
	 * 
	 * @param y
	 *            Sets the object's current y position.
	 */
	public void setY(int y)
	{
		this.yPos = y;
	}

	/**
	 * A simple getter for the x velocity.
	 * 
	 * @return Gets the object's current x velocity.
	 */
	public int getXVel()
	{
		return this.xVel;
	}

	/**
	 * A simple getter for the y velocity.
	 * 
	 * @return Gets the object's current y velocity.
	 */
	public int getYVel()
	{
		return this.yVel;
	}

	/**
	 * A simple setter for the x velocity.
	 * 
	 * @param xVel
	 *            Sets the object's current x velocity.
	 */
	public void setXVel(int xVel)
	{
		this.xVel = xVel;
	}

	/**
	 * A simple setter for the y velocity.
	 * 
	 * @param yVel
	 *            Sets the object's current y velocity.
	 */
	public void setYVel(int yVel)
	{
		this.yVel = yVel;
	}

	/**
	 * A simple getter for the ship color.
	 * 
	 * @return Gets the object's current color.
	 */
	public Color getColor()
	{
		return objectColor;
	}

	/**
	 * Move() is defined in each child class. Move both modifies the object's
	 * x,y position based off of the x,y velocity, and updates the location of
	 * the hitbox.
	 */
	public abstract void move();

	/**
	 * updateHitBox() is used to realign the hitbox of the object each time
	 * move() is called.
	 * 
	 */
	public void updateHitBox()
	{
		hitbox.setLocation(xPos - (size / 2), yPos - (size / 2));
	}

	/**
	 * intersects() returns true when the passed rectangle is within
	 * this.hitbox.
	 * 
	 * @param r
	 *            The rectangle to be checked against.
	 * 
	 * @return Whether or not the passed rectangle intersects the hitbox.
	 */
	public boolean intersects(Rectangle r)
	{
		return hitbox.intersects(r);
	}

	/**
	 * Returns the rectangle hitbox.
	 * 
	 * @return The hitbox rectangle.
	 */
	public Rectangle getHitbox()
	{
		return hitbox;
	}

	/**
	 * Drawing is different between SpaceShip and Projectile, however, it is a
	 * method that both classes need.
	 * 
	 * @param pen
	 *            The graphics object to which we draw.
	 */
	public abstract void drawObject(Graphics pen);

}
