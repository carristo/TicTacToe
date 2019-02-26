package input;

import java.awt.event.*;

public class NewGameListener implements ActionListener{
	
	private String command = null;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		command = e.getActionCommand();
		System.out.println(command);
	}
//	public void mouseClicked(MouseEvent e) {
//		isClicked = (e.getButton() == e.BUTTON1);
//	}
	
	public boolean isNewGame() {
		return command != null;
	}
	
	public void refresh() {
		command = null;
	}
	
	public boolean isWithComp() {
		if(command == null) {
			return false;
		}
		return (command.compareTo("New game with computer") == 0);
		
	}
}
