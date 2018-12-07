package plant;
/**
 * Sunflower Class:
 * @author Sirak Berhane (101030433)
 */
public class Sunflower extends PassivePlant{
	public static final int SUNFLOWER_HIT_THRESHOLD = 5;
	public static final int SUNFLOWER_BUY_THRESHOLD = 50;
	public static final int SUN = 50;
	public static final int SUN_GENERATION_COUNTDOWN_LENGTH = 4;
	
	private int countDownStart;
	/**
	 * Creates a new Sunflower Plant type.
	 * @param x grid position x
	 * @param y grid position y
	 */
	public Sunflower(int x, int y) {
		super(SUNFLOWER_HIT_THRESHOLD, SUNFLOWER_BUY_THRESHOLD, x, y);
		countDownStart = SUN_GENERATION_COUNTDOWN_LENGTH;
	}
	
	/**
	 * Generates one sunflower every second turn. If the count down is 
	 * zero the sunflower instance object can generate a sun (every 
	 * second-turn). Every Sunflower will generate sun at different turns.
	 * @return a sunflower seed
	 */
	public int generateSun() {
		int currentCountDown = countDownToGenerateSun(countDownStart);
		if (currentCountDown == 0) {
			countDownStart = SUN_GENERATION_COUNTDOWN_LENGTH;
			return SUN;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Decrements by one until reaches 0. 
	 * @param start counter integer starter. 
	 * @return
	 */
	private int countDownToGenerateSun(int start) {
		if (start != 0) {
			countDownStart--;
			start = countDownStart;
		}
		return start;
	}
	
	/**
	 * Print Sunflower on to the board
	 */
	public String toString() {
		String Status= "";
		Status = "S = " + this.getHitThreshold();
		return Status;
	}
}
