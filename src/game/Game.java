package game;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import display.Display;
import graphics.Sprite;
import input.Input;


public class Game implements Runnable{
	public static final String TITLE = "Tic Tac Toe";
	public static final int TILE_SIZE = 100;
	public static final int SECOND = 1000;
	public static final float UPDATE_RATE = 60.0f;
	public static final float UPDATE_INTERVAL = SECOND / UPDATE_RATE;
	public static final int IDLE_TIME = 1;
	public static final int CLEAR_COLOR = 0x11FFFFFF;
	public static final int NUM_BUFFERS = 3;
	
	
	private boolean running;
	private Thread gameThread;
	private Difficulty difficulty;
	private static Graphics2D graphics;
	private Input input;
	private Tile[][] map = new Tile[3][3];
	private int[][] priorityMap = new int[3][3];
	private boolean isCrossTurn = true;
	private boolean isPlayerCross = true;
	private boolean isWithComp;
	private static Sprite crossSprite;
	private static Sprite zeroSprite;
	private static BufferedImage emptyField;
	private byte emptyTiles;
	
	
	static {
		crossSprite = new Sprite("cross.png");
		zeroSprite = new Sprite("zero.png");
		try {
			emptyField = ImageIO.read(new File("res\\emptyfield.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Game() {
		Display.create(TILE_SIZE * 3, TILE_SIZE * 3, TITLE, CLEAR_COLOR, NUM_BUFFERS);
		initMap();
		graphics = Display.getGraphics();
		input = new Input();
		Display.addInputListener(input);
	}
	
	public static void main(String args[]) {
		new Game().start();
	}
	
	public synchronized void start() {
		if(running) {
			return;
		}
		
		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public synchronized void stop() {
		running  = false;
	}
	
	public void update() {
		if(Display.isNewGame()) {
			newGame();
			Display.refresh();
		}
		
		if(emptyTiles <= 0) {
			Display.showResult("Draw");
		}
		
		int x = input.getX();
		int y = input.getY();
		
		if(isWithComp & !isPlayerCross) {
			performTurn();
		}
		
		if(x >= 0 && y >= 0) {
			x /= TILE_SIZE;
			y /= TILE_SIZE;
			
			if(map[y][x] == Tile.EMPTY) {
				map[y][x] = isCrossTurn ? Tile.CROSS : Tile.ZERO;
				emptyTiles--;
				render();
				if(checkVictory()) {
					if(isCrossTurn) {
						Display.showResult("Crosses");
					} else {
						Display.showResult("Zeros");
					}
				}
				changePriority(y, x);
				isCrossTurn = !isCrossTurn;
				if(isWithComp & isPlayerCross) {
					performTurn();
					render();
					if(checkVictory()) {
						if(isCrossTurn) {
							Display.showResult("Crosses");
						} else {
							Display.showResult("Zeros");
						}
					}
				}
			}
			
			x = -1;
			y = -1;
		}
		
		
	}
	
	public void render() {
		Display.clear();
		
		graphics.drawImage(emptyField, 0, 0, null);
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map.length; j++) {
				switch(map[i][j]) {
				case EMPTY: 
					break;
				case ZERO:
					zeroSprite.render(graphics, j, i);
					break;
				case CROSS:
					crossSprite.render(graphics, j, i);
					break;
				default:
					break;
				}
			}
		}
		Display.swapBuffers();
	}
	
	public void run() {
		long lastTime = System.currentTimeMillis();
		long currentTime;
		float delta = 0.0f;
		long count = 0;
		int fps = 0;
		int upd = 0;
		int updl = 0;
		
		while(running) {
			currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - lastTime;
			lastTime = currentTime;
			delta += elapsedTime / UPDATE_INTERVAL;
			count += elapsedTime;
			
			boolean render = false;
			
			while(delta > 1) {
				update();
				delta--;
				upd++;
				if(render) {
					updl++;
				} else { render = true; }
			}
			
			if(render) {
				render();
				fps++;
			} else {
				try {
					Thread.sleep(IDLE_TIME);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(count > SECOND) {
				Display.setTitle(TITLE + " | fps = " + fps + " | upd = " + upd + " | updl = " + updl);
				fps = 0;
				upd = 0;
				updl = 0;
				count -= SECOND;
			}
		}
		
	}
	
	private void checkNextTurnVictory() {
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map.length; j++) {
				if(map[i][j] == Tile.EMPTY) {
					map[i][j] = Tile.CROSS;
					if(checkVictory()) {
						priorityMap[i][j] = isPlayerCross ? Integer.MAX_VALUE/2 : Integer.MAX_VALUE;
					}
					map[i][j] = Tile.ZERO;
					if(checkVictory()) {
						priorityMap[i][j] = isPlayerCross ? Integer.MAX_VALUE: Integer.MAX_VALUE/2;
					}
					map[i][j] = Tile.EMPTY;
				}
			}
		}
	}
	
	public boolean checkVictory() {
		return checkRows() | checkColumns() | checkDiagonals();

	}
	
	public boolean checkRows() {
		for(int i = 0; i < map.length; i++) {
			if(map[i][0] != Tile.EMPTY & map[i][0] == map[i][1] & map[i][0] == map[i][2]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkColumns() {
		for(int i = 0; i < map.length; i++) {
			if(map[0][i] != Tile.EMPTY & map[0][i] == map[1][i] & map[0][i] == map[2][i]) {
				return true;
			}
		}
		return false;

	}
	
	public boolean checkDiagonals() {
		if(map[0][0] != Tile.EMPTY & map[0][0] == map[1][1] & map[0][0] == map[2][2]) {
			return true;
		}
		if(map[2][0] != Tile.EMPTY & map[2][0] == map[1][1] & map[2][0] == map[0][2]) {
			return true;
		}
		return false;
	}
	
	public void newGame() {
		Display.clear();
		initMap();
		input.refresh();
		isCrossTurn = true;
		difficulty = Display.getDifficulty();
		isWithComp = Display.isWithComp();
		start();

	}
	
	public void performTurn() {
		int x = 0;
		int y = 0;
		switch(difficulty) {
		case EASY:
			Random rand = new Random();
			do {
				x = rand.nextInt(2);
				y = rand.nextInt(2);
				if(map[x][y] == Tile.EMPTY) {
					map[x][y] = isCrossTurn ? Tile.CROSS : Tile.ZERO;
					isCrossTurn = !isCrossTurn;
					break;
				}
			} while(true);
			break;
		case MEDIUM:
			x = 0;
			y = 0;
			for(int i = 0; i < map.length; i++) {
				for(int j = 0; j < map.length; j++) {
					if(priorityMap[i][j] > priorityMap[x][y]) {
						x = i;
						y = j;
					}
				}
			}
			map[x][y] = isCrossTurn ? Tile.CROSS : Tile.ZERO;
			priorityMap[x][y] = Integer.MIN_VALUE;
			checkNextTurnVictory();
			isCrossTurn = !isCrossTurn;
			break;
		case HARD:
			x = 0;
			y = 0;
			for(int i = 0; i < map.length; i++) {
				for(int j = 0; j < map.length; j++) {
					if(priorityMap[i][j] > priorityMap[x][y]) {
						x = i;
						y = j;
					}
				}
			}
			map[x][y] = isCrossTurn ? Tile.CROSS : Tile.ZERO;
			changePriority(x, y);
			checkNextTurnVictory();
			isCrossTurn = !isCrossTurn;
			break;
		}
		emptyTiles--;
		System.out.println("Turn: (" + x + ", " + y + ")");
	}
	
	public void initMap() {
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map.length; j++) {
				map[i][j] = Tile.EMPTY;
			}
		}
		emptyTiles = (byte) (map.length * map.length);
		priorityMap[1][1] = 4;
		priorityMap[0][0] = 3;
		priorityMap[2][0] = 3;
		priorityMap[0][2] = 3;
		priorityMap[2][2] = 3;
		priorityMap[0][1] = 2;
		priorityMap[1][0] = 2;
		priorityMap[1][2] = 2;
		priorityMap[2][1] = 2;
	}
	
	
	/*There are three different strategies to change tiles priority for three different tile places
	 * 1) when user put cross in center of map, priority of all tiles increases by 2. It doesn't changes anything,
	 * so method does nothing
	 * 2) when user put cross in corner, he changes priority of all tiles, which lays on same row, column and diagonal
	 * 3) when user put cross in the center of any line, he changes priority of tiles, which lays on same row and column
	 * Priority of nearest tiles increases by 2, and further -- by 1.
	 */
	public void changePriority(int i, int j) {
		
		//2) in corner
		if((i == 0 & j == 0) | (i == 0 & j == 2) | (i == 2 & j == 0) | (i == 2 & j == 2)) {
			try {
				//cols
				for(int ii = i - 2; ii <= i + 2; ii++) {
					try {
						priorityMap[ii][j]++;
					} catch(ArrayIndexOutOfBoundsException ignore) { }
				}
				//rows
				for(int jj = j - 2; jj <= j + 2; jj++) {
					try {
						priorityMap[i][jj]++;
					} catch(ArrayIndexOutOfBoundsException ignore) { }
				}
				//diags
				for(int ii = i - 1; ii <= i + 2; ii++) {
					int n = map.length - 1;
					try {
						priorityMap[ii][ii-i+j]++;
					} catch (ArrayIndexOutOfBoundsException ignore) { }
					try {
						priorityMap[ii][i-ii+j]++;
					} catch (ArrayIndexOutOfBoundsException ignore) { }
				}
				//nearest tiles
				for(int ii = i-1; ii <= i+1; ii++) {
					for(int jj= j - 1; jj <= j+1; jj++) {
						try {
							priorityMap[ii][jj]++;
						} catch(ArrayIndexOutOfBoundsException ignore) { }
					}
				}
				
			} catch(ArrayIndexOutOfBoundsException e) {}
		}
		//3) in the center of a line
		if((i == 0 & j == 1) | (i == 1 & j == 0) | (i == 1 & j == 2) | (i == 2 & j == 1)) {
			//cols
			for(int ii = i - 2; ii <= i + 2; ii++) {
				try {
					priorityMap[ii][j]++;
				} catch(ArrayIndexOutOfBoundsException ignore) { }
			}
			//rows
			for(int jj = j - 2; jj <= j + 2; jj++) {
				try {
					priorityMap[i][jj]++;
				} catch(ArrayIndexOutOfBoundsException ignore) { }
			}
			//nearest tiles
			for(int ii = i-1; ii <= i+1; ii++) {
				for(int jj= j - 1; jj <= j+1; jj++) {
					try {
						if(jj != ii-i+j && jj != i-ii+j) { // if not diag
							priorityMap[ii][jj]++;
						}
					} catch(ArrayIndexOutOfBoundsException ignore) { }
				}
			}
		}
		checkVictory();
		priorityMap[i][j] = Integer.MIN_VALUE;
		for(int ii = 0; ii < map.length; ii++) {
			for(int jj = 0; jj < map.length; jj++) {
				System.out.print(priorityMap[ii][jj] + " ");
			}
			System.out.println();
		}
	}
}
