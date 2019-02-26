package input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {
	private int x;
	private int y;
	
	public Input() {
		x = -1;
		y = -1;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		System.out.println("x = " + x + "; y = " + y);
	}
	
	public int getX() { return x; }
	
	public int getY() { return y; }
	
	public void refresh() {
		x = -1;
		y = -1;
	}
}
