package GameObjects;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Projectile contain's code for the unique animating and movements of the shot.
 * Projectiles only travel in the y direction and follow a wave - like path,
 * unlike ships which move in all directions.
 * 
 * @author Mason Dumaine
 *
 */
public class Projectile extends GameObject
{
	// How far left or right the shot goes when it swings.
	private final int SWING_X_LENGTH = 6;

	// How large the size of the pulse animation is.
	private int pulseSize = 0;

	// Initializes the data member that keeps track of how
	// far the swing is with the lowest value.
	private int swingAmp = -SWING_X_LENGTH;

	// The direction of the swing.
	private boolean swingRight = true;

	// Whether or not the pulse size is increasing.
	private boolean pulseSizeIsIncreasing = true;

	/**
	 * The default constructor for projectiles uses the superclass' default
	 * constructor to give default values.
	 */
	public Projectile()
	{
		super();
	}

	/**
	 * This constructor uses the super constructor to initialize the object's
	 * data members.
	 * 
	 * @param xPos
	 *            The x position of the projectile.
	 * @param yPos
	 *            The y position of the projectile.
	 * @param size
	 *            The size of the projectile.
	 * @param c
	 *            The color of the projectile.
	 */
	public Projectile(int xPos, int yPos, int size, Color c)
	{
		super(xPos, yPos, size, c);
	}

	/**
	 * The unique drawing method for the projectile features, a pulsing
	 * animation as well as drawing the projectile in the correct location.
	 * 
	 * @param pen
	 *            The graphics object to which is drawn.
	 */
	public void drawObject(Graphics pen)
	{
		// Tracks the size of the pulse.
		int sizeThisFrame = size + pulseSize;

		// This if-statement structure increments the size of
		// the pulse until it reaches a certain size. Then it
		// begins decreasing until a certain size.
		if (pulseSize > size * 2 || pulseSize < 0)
		{
			pulseSizeIsIncreasing = !pulseSizeIsIncreasing;
		}

		if (pulseSizeIsIncreasing)
		{
			pulseSize++;
		} else
		{
			pulseSize--;
		}

		// This draws the actual base shot.
		pen.setColor(objectColor);
		pen.fillOval(xPos - (size / 2), yPos - (size / 2), size, size);
		pen.setColor(Color.white);

		// This draws the pulse.
		pen.drawOval(xPos - (sizeThisFrame / 2), yPos - (sizeThisFrame / 2),
				sizeThisFrame, sizeThisFrame);
	}

	/**
	 * I wanted the shot's movement to be unique, so I made it travel along a
	 * wave. It also has a side effect of testing the collision detection in
	 * areas of the enemy ships that wouldn't normally be hit (ie the sides).
	 */
	public void move()
	{
		// This follows a similar formula to the pulsing of the shot.
		if (swingRight)
		{
			swingAmp++;
			if (swingAmp == SWING_X_LENGTH)
			{
				swingRight = !swingRight;
			}
		} else if (!swingRight)
		{
			swingAmp--;
			if (swingAmp == -SWING_X_LENGTH)
			{
				swingRight = !swingRight;
			}
		}

		// Finally modify the x,y position of the shot.
		xPos += swingAmp;
		yPos += yVel;

		// Then make sure that the hitbox is in the right place!
		updateHitBox();
	}
}
