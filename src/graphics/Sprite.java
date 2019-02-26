package graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	public static final String SPRITE_PATH = "res\\";
	public static final int SPRITE_SIZE = 76;
	public static final int X_OFFSET = 15;
	public static final int Y_OFFSET = 12;
	BufferedImage image;
	
	private String spriteName;
	
	public Sprite(String spriteName) {
		this.spriteName = spriteName;
		try {
			System.out.println(image);
			image = ImageIO.read(new File(SPRITE_PATH + spriteName));
			System.out.println(image);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render(Graphics2D g, int x, int y) {
		g.drawImage(image, x * SPRITE_SIZE + (2* x + 1) * X_OFFSET, y * SPRITE_SIZE + (2 * y + 1) * Y_OFFSET, null);
	}
}
