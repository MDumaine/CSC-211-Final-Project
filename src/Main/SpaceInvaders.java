package Main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;

import GameObjects.GameObject;
import GameObjects.Projectile;
import GameObjects.SpaceShip;

@SuppressWarnings("serial")
public class SpaceInvaders extends Canvas
{
	// DO NOT MODIFY: these change based on the game state.
	private boolean runGame = false, initComplete = false, showStart = true,
			endGame = false, playerWon = false;

	// Allows for smoother animation by drawing to
	// an off-screen buffer first.
	private BufferStrategy strategy;

	// Keeps track of the number of enemy kills, and the time between each shot.
	private int killCounter = 0, lastShotTime = 0;

	// The default colors of all the objects.
	private final Color SHOT_COLOR = Color.blue, PLAYER_COLOR = Color.white,
			ENEMY_COLOR = Color.red;

	// The font used for the text elements of the game.
	private Font gameFont = new Font("TimesRoman", Font.PLAIN, 32);

	private final int FRAME_SIZE = 1000, SLEEP_TIME = 10, // The number of ms
															// between frames.
			TOTAL_ENEMIES = 200, // The total size of the wave of enemies
			PLAYER_SPEED = FRAME_SIZE / 250, // Must be positive integer
			ENEMY_SPEED = FRAME_SIZE / 500, // Must be positive integer
			PROJECTILE_SPEED = FRAME_SIZE / 200, // Must be positive integer
			SHIP_SIZE = 25, // The physical size of all ships
			SHOT_SIZE = 8, // The physical size of projectiles
			RATE_OF_FIRE = 650, // Time between shots in milliseconds
			// Triggers the lose condition
			GAME_OVER_TRIGGER_YPOS = FRAME_SIZE - SHIP_SIZE * 4,
			// The location of the x axis on which the player moves.
			PLAYER_YAXIS_POS = FRAME_SIZE - SHIP_SIZE * 2;

	// Keyboard object that handles user input.
	public KeyBoard keyboard = new KeyBoard();

	// Instantiation of our game objects.
	private GameObject player = new SpaceShip(FRAME_SIZE / 2, PLAYER_YAXIS_POS,
			SHIP_SIZE, PLAYER_COLOR);

	private GameObject[] enemies = new SpaceShip[TOTAL_ENEMIES];

	private GameObject[] shots = new Projectile[0];

	/**
	 * The constructor for the game is responsible for setting up the frame,
	 * adding a new panel to it, and setting up various other boilerplate code
	 * for smooth animation.
	 * 
	 */
	public SpaceInvaders()
	{
		// Set up all the frame stuff.
		JFrame frame = new JFrame("Space Invaders");

		JPanel gamePanel = (JPanel) frame.getContentPane();
		gamePanel.setPreferredSize(new Dimension(FRAME_SIZE, FRAME_SIZE));

		setBounds(0, 0, FRAME_SIZE, FRAME_SIZE);
		gamePanel.add(this);
		setIgnoreRepaint(true);

		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add the keyAdapter object to the canvas.
		addKeyListener(keyboard);
		requestFocus();

		// Double buffer
		createBufferStrategy(2);
		// Instantiates the bufferStrategy.
		strategy = getBufferStrategy();

		// The player should be both visible and moveable.
		((SpaceShip) player).setIsMoving(true);
		((SpaceShip) player).setIsVisible(true);

		// Fill the enemy array with enemies.
		enemies = fillEnemyArray();

		// We're done, the gameLoop can run safely.
		initComplete = true;
	}

	/**
	 * gameLoop() loops through all of the updating and drawing of the game for
	 * each frame.
	 */
	public void gameLoop()
	{
		// If everything is ready.
		while (initComplete)
		{
			// Creates a new graphics2d object using the bufferStrategy.
			Graphics2D pen = (Graphics2D) strategy.getDrawGraphics();

			// Blanks the frame with black to allow us to draw new objects.
			pen.setColor(Color.black);
			pen.fillRect(0, 0, FRAME_SIZE, FRAME_SIZE);

			// Shows the first screen.
			if (!runGame && showStart)
			{
				showScreenMessage("PRESS \'R\' TO START", pen, FRAME_SIZE / 2);
				if (keyboard.go)
				{
					runGame = true;
					showStart = false;
				}
			}

			// Determines whether to show a lose or win screen.
			if (!runGame && endGame)
			{
				if (keyboard.go)
				{
					killCounter = 0;
					enemies = fillEnemyArray();
					player = new SpaceShip(FRAME_SIZE / 2, PLAYER_YAXIS_POS,
							SHIP_SIZE, PLAYER_COLOR);
					((SpaceShip) player).setIsVisible(true);
					((SpaceShip) player).setIsMoving(true);

					runGame = true;
					showStart = false;
					endGame = false;
				} else if (playerWon)
				{
					showScreenMessage("YOU WIN", pen, FRAME_SIZE / 2);
					showScreenMessage("PRESS \'R\' TO RESTART", pen,
							FRAME_SIZE / 2 + (FRAME_SIZE / 10));
				} else
				{
					showScreenMessage("YOU LOSE", pen, FRAME_SIZE / 2);
					showScreenMessage("PRESS \'R\' TO RESTART", pen,
							FRAME_SIZE / 2 + (FRAME_SIZE / 10));
				}
			}

			// The actual updating of the game.
			if (runGame && !endGame)
			{
				checkForEnemyHits();
				updateGame();
				drawGame(pen);
				searchForWinner();
			}

			// Clear the graphics object.
			pen.dispose();

			// Show me what you got! I want to see what you got!
			strategy.show();

			// Wait for SLEEP_TIME between frames.
			sleep();
		}
	}

	/**
	 * updateGame() modifies all of the game data based on the current state of
	 * the game.
	 */
	private void updateGame()
	{
		// Only run these if the game is actually running.
		if (runGame)
		{
			moveEnemies();
			playerAction();
			player.move();
		}

		// This ensures that the player does not leave the game area.
		if (checkBoundaries(player.getX(), player.getY()))
		{
			player.setX(player.getX() - player.getXVel());
		}

		// This checks if any of the shots have left the frame, if so
		// it removes them. Otherwise, it moves them.
		for (int i = 0; i < shots.length; i++)
		{
			if (checkBoundaries(shots[i].getX(), shots[i].getY()))
			{
				shots = removeObject(i, shots);
			} else
			{
				shots[i].move();
			}
		}
	}

	/**
	 * This modifies the game state based on what the keyboard object says the
	 * user has entered.
	 */
	private void playerAction()
	{
		if (keyboard.left)
		{
			player.setXVel(-PLAYER_SPEED);
		} else if (keyboard.right)
		{
			player.setXVel(PLAYER_SPEED);
		} else if (keyboard.fire)
		{
			shootIfAble();
			// Since we can only get one keypress at a time
			// the player's speed has to be zeroed here.
			player.setXVel(0);
		} else
		{
			player.setXVel(0);
		}
	}

	/**
	 * This method fills the enemy array with enemies at a position off screen.
	 * They move left until they hit the edge, then enter the game screen and
	 * continue their behavior.
	 * 
	 * @return Returns a new gameObject array with enemies.
	 */
	private GameObject[] fillEnemyArray()
	{
		SpaceShip[] tempEnemies = new SpaceShip[TOTAL_ENEMIES];
		for (int i = 0; i < TOTAL_ENEMIES; i++)
		{
			tempEnemies[i] = new SpaceShip(SHIP_SIZE * 4, -SHIP_SIZE, SHIP_SIZE,
					ENEMY_COLOR);
			tempEnemies[i].setXVel(-ENEMY_SPEED);

			// If it is the first enemy in the array, allow it to move and
			// be visible. This is done for the recursive check of on the
			// next ship. This allows every ship to have the same distance
			// from one another, yet still independently move.
			if (i == 0)
			{
				((SpaceShip) tempEnemies[i]).setIsMoving(true);
				((SpaceShip) tempEnemies[i]).setIsVisible(true);
			}
		}
		return tempEnemies;
	}

	/**
	 * This method goes through the entire array and moves each enemy. It also
	 * does the recursive check for each enemy in the game, ensuring that they
	 * all start in the same place off screen, but start at different times.
	 * 
	 */
	private void moveEnemies()
	{
		for (int i = 0; i < enemies.length; i++)
		{

			// If it isn't the first ship, which is already moving.
			if (i > 0)
			{
				// Then if the ship before this is at least one ship's
				// length away from the edge of the screen, then start
				// the current ship moving.
				if (enemies[i - 1].getX() < SHIP_SIZE)
				{
					((SpaceShip) enemies[i]).setIsMoving(true);
					((SpaceShip) enemies[i]).setIsVisible(true);
				}
			}

			// This does the checks on where to make the enemies
			// start traveling downwards.
			if (enemies[i].getX() > FRAME_SIZE - SHIP_SIZE
					|| enemies[i].getX() < SHIP_SIZE)
			{
				enemies[i].setYVel(ENEMY_SPEED);

				enemies[i].setXVel(0);
			}

			// This does the check to see if the ship should start
			// traveling left or right.
			if (enemies[i].getY() >= ((((SpaceShip) enemies[i]).rowCounter
					* SHIP_SIZE * 2) + SHIP_SIZE * 2))
			{

				// If it is an odd row, then move left.
				if (((SpaceShip) enemies[i]).rowCounter % 2 == 1)
				{
					enemies[i].setYVel(0);
					enemies[i].setXVel(-ENEMY_SPEED);
				}

				// If it is an even row, then move right.
				else if (((SpaceShip) enemies[i]).rowCounter % 2 == 0)
				{
					enemies[i].setYVel(0);
					enemies[i].setXVel(ENEMY_SPEED);
				}

				// Since it has completed a row, increment the rowCounter.
				((SpaceShip) enemies[i]).rowCounter++;
			}

			// Finally move all the enemies based on their
			// new velocities.
			enemies[i].move();
		}

	}

	/**
	 * This method checks each frame to see if any of the win/loss conditions
	 * have been met.
	 * 
	 */
	private void searchForWinner()
	{
		// Loop through all of the enemies.
		for (int i = 0; i < enemies.length; i++)
		{
			// If any of the enemies have reached the bottom of the screen.
			if (enemies[i].getY() >= GAME_OVER_TRIGGER_YPOS)
			{
				// Then modify the game state.
				runGame = playerWon = false;
				endGame = true;

				// And clear out our objects.
				enemies = clearObjectArray();
				shots = clearObjectArray();
				player = new SpaceShip();

			}
		}

		// If at any time the number of kills = the total number of enemies.
		if (killCounter == TOTAL_ENEMIES)
		{
			// Then modify the game state.
			runGame = false;
			endGame = playerWon = true;

			// And clear out the objects.
			enemies = clearObjectArray();
			shots = clearObjectArray();
			player = new SpaceShip();
		}
	}

	/**
	 * drawGame() is responsible for passing the graphics object to all the
	 * necessary drawing methods, as well as drawing various UI elements.
	 * 
	 * @param pen
	 *            The graphics object to which is drawn
	 */
	private void drawGame(Graphics pen)
	{

		String killCounterString = killCounter + "/" + TOTAL_ENEMIES;
		pen.setColor(Color.white);
		pen.setFont(gameFont);

		// Draws the kill counter in the top right.
		int labelWidth = pen.getFontMetrics().stringWidth(killCounterString);
		int labelHeight = pen.getFontMetrics().getAscent();
		pen.drawString(killCounterString, FRAME_SIZE - labelWidth, labelHeight);

		// Draw the game over line
		pen.drawLine(0, GAME_OVER_TRIGGER_YPOS, FRAME_SIZE,
				GAME_OVER_TRIGGER_YPOS);

		// Draw all the objects
		drawGameObjects(pen, enemies);
		drawGameObjects(pen, shots);
		player.drawObject(pen);
	}

	/**
	 * This is a general purpose method to loop through all of the game object
	 * arrays and call each element's draw method.
	 * 
	 * @param pen
	 *            What is being drawn to.
	 * @param gameObjects
	 *            The array of passed GameObjects
	 */
	private void drawGameObjects(Graphics pen, GameObject[] gameObjects)
	{
		for (int i = 0; i < gameObjects.length; i++)
		{
			gameObjects[i].drawObject(pen);
		}
	}

	/**
	 * shootIfAble() checks whenever the spacebar is pressed, if the ship can
	 * add another shot to the projectile array. Ability to shoot is determined
	 * by the time between shots and if the shot is the first shot or not.
	 * 
	 */
	private void shootIfAble()
	{
		if (getShotTime() > RATE_OF_FIRE || shots.length == 0)
		{
			shots = addNewShotToArray(shots);
		}

	}

	/**
	 * addNewShotToArray() takes the original array of projectiles, and
	 * increases its size by 1, then it adds a new shot at the last element in
	 * the new array. Each element from the old array is copied over to the new
	 * one.
	 * 
	 * @param originalObjects
	 *            The original array of shots.
	 * @return The array of shots + one more.
	 */
	private GameObject[] addNewShotToArray(GameObject[] originalObjects)
	{
		GameObject[] tempObjects = new GameObject[originalObjects.length + 1];
		for (int i = 0; i < tempObjects.length; i++)
		{
			if (i == originalObjects.length)
			{
				tempObjects[i] = createNewShot();
			} else
			{
				tempObjects[i] = originalObjects[i];
			}
		}

		return tempObjects;
	}

	/**
	 * This method creates and returns shots for addNewShotToArray() it also
	 * logs the time the shot was created to keep track of time between shots.
	 * 
	 * @return Returns a new projectile object.
	 */
	private GameObject createNewShot()
	{
		Projectile shot = new Projectile(player.getX(), player.getY(),
				SHOT_SIZE, SHOT_COLOR);
		shot.setYVel(-PROJECTILE_SPEED);
		lastShotTime = (int) System.currentTimeMillis();

		return shot;
	}

	/**
	 * clearObjectArray() just returns an empty object array.
	 * 
	 * @return Returns a new empty array
	 */
	private GameObject[] clearObjectArray()
	{
		return new GameObject[0];
	}

	/**
	 * removeObject() is used whenever an item in an array needs to be deleted.
	 * It creates an array of size - 1, then copies over every element except
	 * for the index into the new array.
	 * 
	 * @param index
	 *            The index of the item to be deleted.
	 * @param originalObjects
	 *            The array of original objects.
	 * @return A new array of objects.
	 */
	private GameObject[] removeObject(int index, GameObject[] originalObjects)
	{
		GameObject[] tempObjects = new GameObject[originalObjects.length - 1];

		int j = 0;
		for (int i = 0; i < tempObjects.length; i++)
		{
			if (i == index)
			{
				j++;
			}
			tempObjects[i] = originalObjects[j];
			j++;
		}
		return tempObjects;
	}

	/**
	 * Calculates the change in time since the last shot.
	 * 
	 * @return The amount of time since the last shot.
	 */
	private int getShotTime()
	{
		long deltaT = (System.currentTimeMillis() - lastShotTime);
		return (int) deltaT;
	}

	/**
	 * Checks each shot against each enemy to see if there are any
	 * intersections, if there is, then remove those objects from the screen.
	 */
	private void checkForEnemyHits()
	{
		GameObject currentShot;

		// All the shots
		for (int i = 0; i < shots.length; i++)
		{
			currentShot = shots[i];
			// All the enemies
			for (int j = 0; j < enemies.length; j++)
			{
				if (enemies[j].intersects(currentShot.getHitbox()))
				{
					// Enemy kill confirmed.
					killCounter++;
					// Remove the enemy object at the current index.
					enemies = removeObject(j, enemies);
					// Remove the projectle object at the current index.
					shots = removeObject(i, shots);
				}
			}
		}
	}

	/**
	 * This method returns true if the passed x,y position exists outside of the
	 * frame. It is used to delete shots as they leave the frame.
	 * 
	 * @param xPos
	 *            The x position to check
	 * @param yPos
	 *            The y position to check
	 * @return Whether or not the point is outside.
	 */
	private boolean checkBoundaries(int xPos, int yPos)
	{
		boolean isOutside = false;
		if ((xPos >= FRAME_SIZE || xPos <= 0)
				|| (yPos >= FRAME_SIZE || yPos <= 0))
		{
			isOutside = true;
		}
		return isOutside;
	}

	/**
	 * This method is used for displaying messages to the screen (for starting,
	 * and restarting).
	 * 
	 * @param screenMessage
	 *            Message to be shown
	 * @param pen
	 *            Graphics object to which is drawn
	 * @param yPos
	 *            The y postion where the message is placed.
	 */
	private void showScreenMessage(String screenMessage, Graphics pen, int yPos)
	{
		pen.setColor(Color.white);
		pen.setFont(gameFont);
		int labelWidth = pen.getFontMetrics().stringWidth(screenMessage);
		int labelHeight = pen.getFontMetrics().getAscent();

		pen.drawString(screenMessage, (FRAME_SIZE / 2) - (labelWidth / 2),
				yPos + (labelHeight / 2));
	}

	/**
	 * This sleeps the loop for SLEEP_TIME to give the illusion of motion.
	 * 
	 */
	private void sleep()
	{
		try
		{
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
