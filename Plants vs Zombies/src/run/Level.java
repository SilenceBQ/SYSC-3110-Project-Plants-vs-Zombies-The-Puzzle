/**
 * @author Jolar Tabungar 101030060
 * 
 * Implementation of the Level class
 * 
 */		
package run;
import java.util.ArrayList;
import java.util.Random;
import plant.*;
import zombie.*;

public class Level {
	
	//Constants for the coordinate bounds
	public static final int X_MIN = 0;
	public static final int X_MAX = 8;
	public static final int Y_MIN = 0;
	public static final int Y_MAX = 4;
	
	//Constant for minimum turnCountdown till next wave
	public static final int MINIMUM_TURN_COUNTDOWN_LENGTH = 7;
	//Sun generation amount
	public static final int PLAYER_GENERATED_SUN_AMOUNT = 25;
	//Sun generation countdown length
	public static final int SUN_GENERATION_COUNTDOWN_LENGTH = 4;
	
	//The player's suntotal
    private int sunTotal;
    //Countdown till player generates sun
  	private int generateSunCountdown;
	
	//Level's array of lawns
	private Lawn[] lawns;
	//Level's sizes for waves
	private ArrayList<Integer> waveSizes;
	//Level's turn countdown for next wave
	private int turnCountdown;
	//The amount of zombies currently spawned in the wave
	private int spawnCount;
	//The amount of zombies remaining in the wave
	private int remainingCount;
	//The wave count
	private int waveCount;
	
	private GameGUI game;
	/*
	//The player playing the level
	private Player player;
	//Level's printState class
	private PrintState printState;
	*/
	
	//Constructs a new Level
	public Level(ArrayList<Integer> waveSizes, GameGUI game) {
		this.game = game;
		
		lawns = new Lawn[5];
		this.waveSizes = waveSizes;
		turnCountdown = MINIMUM_TURN_COUNTDOWN_LENGTH;
		spawnCount = 0;
		waveCount = 1;
		remainingCount = waveSizes.get(0);
		
		//Starting suntotal
		sunTotal = 100; 
				
		//Start countdown to sun generation
	    generateSunCountdown = SUN_GENERATION_COUNTDOWN_LENGTH;
		
		//Initialize the 5 lawns
		for (int i = 0; i < 5; i ++) {
			lawns[i] = new Lawn();
		}
		
		//player = new Player(this);
		//printState = new PrintState(this, player);
		
		
	}
	
	/**]
	 * Adds a new zombie to the level
	 * @param zombieType the type of zombie to be spawned
	 * @param y the row the zombie will be spawned in
	 */
	public void addZombie(String zombieType, int y) {
		Zombie zombie = null;
		
		if (zombieType.equalsIgnoreCase("zombie")) {
			zombie = new BasicZombie(y);
		}
		
		lawns[y].getZombies().add(zombie);
	}
	
	/**
	 * Adds a new plant to the level
	 * @param plantType the type of plant to be spawned
	 * @param x the column of placement
	 * @param y the row of placement
	 * @return true if the plant can be placed (due to sun cost and if space is empty)
	 */
	public boolean addPlant(int x, int y) {
		Plant plant = null;
		
		if (game.sunflowerSelected()) {
			plant = new Sunflower(x, y);
		}
		else if (game.peashooterSelected()) {
			plant = new Peashooter(x, y);
		}
		
		for (Plant aPlant: lawns[y].getPlants()) {
			if (aPlant.getxPos() == x && aPlant.getyPos() == y)
				return false;
		}
		
		if (getSunTotal() >= plant.getBuyThreshold()) {
			lawns[y].getPlants().add(plant);
			setSunTotal(getSunTotal() - plant.getBuyThreshold());
			return true;
		}
		return false;
	}
	
	/**
	 * Removes and deletes a plant at (x, y) position
	 * @param x the row in question
	 * @param y the column in question
	 * @return true if plant exists at (x, y) position and therefore can be removed, false otherwise
	 */
	public boolean removePlant(int x, int y) {
		for (Plant plant: lawns[y].getPlants()) {
			if (plant.getxPos() == x && plant.getyPos() == y) {
				lawns[y].getPlants().remove(plant);
				//plants.remove(plant);
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Spawns n = zombieCount zombies (simulates a wave of zombies being added)
	 * @param zombieCount
	 */
	public void zombieWave() {
		//The number of zombies spawned in this call
		int currentCount = 0;
		Random randomY = new Random();
		//Spawn about a fifth of the wave size every call
		int yPos = randomY.nextInt(Y_MAX + 1);
		while (currentCount < waveSizes.get(0) / 5 && spawnCount < waveSizes.get(0)) {
			//Hold Variable to make sure yPos is unique every time
			int tempYPos = randomY.nextInt(Y_MAX + 1);
			
			while (tempYPos == yPos) {
				tempYPos = randomY.nextInt(Y_MAX + 1);
			}
			
			yPos = tempYPos;
			
			addZombie("zombie", yPos);
			currentCount ++;
			spawnCount ++;
		}
		
	}
	
	/**
	 * Returns the closest zombie in the given row
	 * @param yPos the given row
	 * @return the closest zombie
	 */
	public Zombie closestZombie(int yPos, Plant plant) {
		Zombie closest = null;
		
		if (!lawns[yPos].getZombies().isEmpty()) {
			
			//Find a potential closest zombie
			int i = 0;
			closest = lawns[yPos].getZombies().get(i);
			while (closest.getCurrentX() < plant.getxPos() && lawns[yPos].getZombies().size() > 1) {
				closest = lawns[yPos].getZombies().get(i);
				i ++;
			}
	
			//For each loop to visit all zombies
			for (Zombie zombie: lawns[yPos].getZombies()) {
				//If current zombie is closer than our current closest, update closest
				if (zombie.getCurrentX() < closest.getCurrentX() && zombie.getCurrentX() >= plant.getxPos())
					closest = zombie;
		
			}
		}

		return closest;
	}

	
	/**
	 * 
	 */
	public Plant closestPlant(int yPos, Zombie zombie) {
		Plant closest = null;
		if (!lawns[yPos].getPlants().isEmpty()) {
			
			//Find a potential closest plant
			int i = 0;
			closest = lawns[yPos].getPlants().get(i);
			while (closest.getxPos() > zombie.getCurrentX() && lawns[yPos].getPlants().size() > 1) {
				closest = lawns[yPos].getPlants().get(i);
				i ++;
			}
		
			//For each loop to visit all plants in the row
			for (Plant plant: lawns[yPos].getPlants()) {
				//If current plant is closer than our current closest, update closest
				if (plant.getxPos() > closest.getxPos() && plant.getxPos() <= closestZombie(yPos, plant).getCurrentX())
					closest = plant;
			}

		}
		return closest;
	}
	
	/**
	 * All plants do their respective actions
	 */
	public void plantAction() {
		//Visit all plants
		for (int i = 0; i < lawns.length; i ++) {
			
			for (Plant plant: lawns[i].getPlants()) {
				//Sunflower action
				if (plant instanceof Sunflower) {
					//Cast plant as sunflower
					Sunflower sunflower = (Sunflower) plant;
					//Give the player more sun
					setSunTotal(getSunTotal() + sunflower.generateSun());
				}
				//Peashooter action
				else if (plant instanceof Peashooter) {
					//Cast plant as peashooter
					Peashooter peashooter = (Peashooter) plant;
				
					//Find closest zombie in the row of the peashooter
					Zombie targetZombie = closestZombie(peashooter.getyPos(), peashooter);
					if (targetZombie != null) {
						//Deal damage to zombie
						targetZombie.hit(peashooter.getHitValue());
						//If zombie is dead, remove it
						if (targetZombie.isDead()) {
							lawns[i].getZombies().remove(targetZombie);
							remainingCount --;
						}
					}
				}
			}
		}
	}

	public void zombieAction() {
		
		//Visit all zombies
		for (int i = 0; i < lawns.length; i ++) {
			
			//Flag if this lawn's lawn mower gets activated
			boolean lawnMowerActivate = false;
			
			for (Zombie zombie: lawns[i].getZombies()) {
				
				zombie.setMoving(true);
				
				//Determine whether zombie needs to stop due to plant collision
				if (!lawns[i].getPlants().isEmpty()) {
					if (zombie.getCurrentX() == closestPlant(i, zombie).getxPos())
						zombie.setMoving(false);
					else
						zombie.setMoving(true);
				}
				
				if (zombie.isMoving()) {
					// If the zombies reaches the last tile and lawn mower is 
					// already activated, then it is game over.
					if (zombie.getCurrentX() < 0 && lawns[i].isLawnMowerActivated()) {
						gameLost();
					}
					
					// If the zombies reach the last tile activate lawn mower 
					if (zombie.getCurrentX() < 0) {
						lawnMowerActivate = true;
					}
					
					//
					if (zombie.isMoving()) {
						zombie.setCurrentX(zombie.getCurrentX() - zombie.getMovementSpeed());
					}
				}
				else {
					// Attack until plant is dead
					Plant targetPlant = closestPlant(i, zombie);
					if (targetPlant != null) {
						targetPlant.setHitThreshold(targetPlant.getHitThreshold() - zombie.attack());
						if (targetPlant.isPlantDead()) {
							lawns[i].getPlants().remove(targetPlant);
							zombie.setMoving(true);
						} 

					}
					
				}
			}
			
			//Activate the lawnmower once all the zombies have done their actions
			if (lawnMowerActivate) {
				activateLawnMower(i);
			}
		}
	}
	
	public Lawn getLawn(int y) {
		return lawns[y];
	}
	
	/**
	 * Activates the lawn mower in the given lawn
	 * @param yPos
	 */
	public void activateLawnMower(int yPos) {
		lawns[yPos].setLawnMower(true);
		remainingCount -= lawns[yPos].getZombies().size();
		lawns[yPos].getZombies().clear();
	}
	
	/**
	 * Gets the next move from the player and continues the level simulation.
	 */
	public void NextTurn() {
		//Get player's action
		//getPlayerAction();
		
		//Generate sun for the player
		gameGenerateSun();
		
		//Continue to spawn waves until there are no more waves left
		if (!waveSizes.isEmpty()) {
			if (spawnCount < waveSizes.get(0)) {
				zombieWave();
			}
		}
		
		//Zombies do actions
		zombieAction();
		
		//Plants do actions
		plantAction();
		
		//Decrement the turn countdown for the next wave
		turnCountdown --;
		
		//Reset for next wave
		if (turnCountdown <= 0 && !waveSizes.isEmpty() && waveSizes.size() != 1 && remainingCount == 0) {
			//Remove the size of completed wave from list
			waveSizes.remove(waveSizes.get(0));
			//Reset the turnCountdown
			turnCountdown = MINIMUM_TURN_COUNTDOWN_LENGTH;
			//Reset the remainingCount
			if (!waveSizes.isEmpty())
				remainingCount = waveSizes.get(0);
			//Reset the spawn count
			spawnCount = 0;
			//Increment the wave counter
			waveCount ++;
		}
		
		/*
		//Update the state
		printState.updateState(lawns);
		//Print the Current State
		printState.print();
		*/
		/*
		//Check if win condition is met
		if (checkWinCondition()) {
			//Player wins
			gameWin();
		}
		else {
			//Proceed to the next turn
			NextTurn();
		}
		*/
	
	}
	
	/**
	 * Returns the total number of zombies remaining currently in the wave;
	 * @return the total number of zombies remaining currently in the wave
	 */
	public int zombieCount() {
		return remainingCount;
	}
	
	/**
	 * Returns the current wave number
	 * @return the current wave number
	 */
	public int currentWave() {
		return waveCount;
	}
	
	
	/**
	 * Return true if the win condition is met, false otherwise 
	 * @return true if the win condition is met, false otherwise
	 */
	public boolean checkWinCondition() {
		boolean win = false;
		//Only check if there are no more zombies to be spawned
		if (waveSizes.size() == 1 && remainingCount == 0) {
			for (int i = 0; i < lawns.length; i ++) {
				//If no more zombies in this row, win condition is met so far
				if (lawns[i].getZombies().isEmpty()) {
					win = true;
				}
				//If there are zombies in the row then win condition is not met
				else {
					win = false;
				}
			}
		}
		
		return win;
	}
	
	/**
	 * @return the current suntotal of player
	 */
	public int getSunTotal() {
		return sunTotal;
	}
	
	/**
	 * Generate sun for the player when countdown is up
	 */
	public void gameGenerateSun() {
		//Decrement countdown
		countDownToGenerateSun();
		
		//If countdown is over, generate sun
		if (generateSunCountdown == 0) {
			generateSunCountdown = SUN_GENERATION_COUNTDOWN_LENGTH;
			setSunTotal(getSunTotal() + PLAYER_GENERATED_SUN_AMOUNT);
		} 
	}
	
	/**
	 * Decrement the sun generation countdown
	 */
	private void countDownToGenerateSun() {
		generateSunCountdown --;
	}

	/**
	 * Sets the suntotal of the player
	 * @param newtotal the new suntotal of the player
	 */
	public void setSunTotal(int newtotal) {
		sunTotal = newtotal;
	}
	
	/**
	 * Prints the text for completing the level
	 */
	public void gameWin() {
		System.out.println("Level Complete!");
		System.out.println("YOU WIN!");
		System.exit(0);	
	}
	
	/**
	 * Prints the text for failing the level
	 */
	public void gameLost() {
		System.out.println("Zombies Ate Your Brains!");
		System.out.println("GAME OVER!");
		System.exit(0);	
	}
	/**
	 * 
	 * @param y
	 * @return returs  a list of lawns on the y coordinate
	 */
	
	public Lawn getLawns(int y){
		return lawns[y];
	}
}
