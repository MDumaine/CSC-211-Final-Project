package GameObjects;

import java.awt.Color;
import java.awt.Graphics;

/**
 * The SpaceShip child class differentiates itself from Projectiles in that it
 * has various additions that allow greater control over the movement of the
 * GameObject, and graphical display.
 * 
 * @author Mason Dumaine
 *
 */
public class SpaceShip extends GameObject
{
	// These two booleans are used mainly for the enemy array, and control the
	// beginning spawning of the enemies.
	protected boolean isMoving = false, isVisible = false;

	// This is used for the thruster animation inside the drawObject() method.
	private int thrustCounter = 0;

	// This keeps track of how far down the screen the ship has moved.
	public int rowCounter = 0;

	/**
	 * Default constructor for the SpaceShip just uses the default super
	 * constructor to initialize the data members.
	 */
	public SpaceShip()
	{
		super();
	}

	/**
	 * The constructor for this child class just passes values up to the
	 * superclass.
	 * 
	 * @param xPos
	 *            X position where the ship is centered.
	 * @param yPos
	 *            Y position where the ship is centered.
	 * @param size
	 *            The size of the square ship.
	 * @param c
	 *            The color of the ship.
	 */
	public SpaceShip(int xPos, int yPos, int size, Color c)
	{
		super(xPos, yPos, size, c);
	}

	/**
	 * Simple setter for if the ship is visible or not.
	 * 
	 * @param vis
	 *            set visible or not
	 */
	public void setIsVisible(boolean vis)
	{
		isVisible = vis;
	}

	/**
	 * Simple setter for if the ship is moving or not.
	 * 
	 * @param mov
	 *            set moving or not
	 */
	public void setIsMoving(boolean mov)
	{
		isMoving = mov;
	}

	/**
	 * Getter for if the ship is moving or not.
	 * 
	 * @return moving or not?
	 */
	public boolean getIsMoving()
	{
		return isMoving;
	}

	/**
	 * move() both modifies the x,y position of the ship based on it's
	 * velocities, and updates the hitbox's position.
	 * 
	 */
	public void move()
	{
		// if the ship is supposed to be moving.
		if (isMoving)
		{
			super.xPos += xVel;
			super.yPos += yVel;

			// make sure to update the hitbox!
			updateHitBox();
		}
	}

	/**
	 * drawObject() is responsible for drawing the base of the ship (a square)
	 * and the thruster animation for the ship.
	 */
	public void drawObject(Graphics pen)
	{
		// Only draw if the ship is visible.
		if (isVisible)
		{
			// Choose a color for the frame to animate the thruster.
			switch (thrustCounter)
			{
			case 0:
				pen.setColor(Color.red);
				break;
			case 1:
				pen.setColor(Color.orange);
				break;
			case 2:
				pen.setColor(Color.yellow);
				break;
			case 3:
				pen.setColor(Color.white);
				break;
			case 4:
				thrustCounter = 0;
				break;
			default:
				thrustCounter = 0;
			}

			// This draws the thruster in the correct proportions and
			// in the correct direction.
			if (xVel > 0)
			{
				pen.fillOval(xPos - size - thrustCounter, yPos - (size / 4),
						size, size / 2);
			} else if (xVel < 0)
			{
				pen.fillOval(xPos + thrustCounter, yPos - (size / 4), size,
						size / 2);
			} else if (yVel > 0)
			{
				pen.fillOval(xPos - (size / 4), yPos - size - thrustCounter,
						size / 2, size);
			} else if (yVel < 0)
			{
				pen.fillOval(xPos - (size / 4), yPos + thrustCounter, size / 2,
						size);
			}
			thrustCounter++;

			// Draw the actual square ship.
			pen.setColor(objectColor);
			pen.fillRect(xPos - (size / 2), yPos - (size / 2), size, size);
		}
	}
}
