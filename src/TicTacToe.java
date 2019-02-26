import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class TicTacToe extends JFrame{
	private MyPanel canvas;
	private JPanel status;
	private JMenuBar menu;
	
	private TTCcell[][] map = new TTCcell[3][3];
	private boolean isCrossTurn = true;
	public TicTacToe() {
		super("Tic Tac Toe");
	}
	
	
	//initializes window
	private void init() {
		setBounds(400, 200, 500, 370);
		canvas = new MyPanel(new FlowLayout());
		status = new JPanel(new FlowLayout());
		createMenuBar();
		add(menu, BorderLayout.NORTH);
		add(canvas, BorderLayout.WEST);
		add(status, BorderLayout.EAST);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map.length; j++) {
				map[i][j] = TTCcell.EMPTY;
			}
		}
		
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX()/100;
				int y = e.getY()/100;
				if(map[x][y] == TTCcell.EMPTY) {
					map[x][y] = isCrossTurn ?  TTCcell.CROSS :TTCcell.ZERO;
					isCrossTurn = !isCrossTurn;
					canvas.repaint();
				}
			}
		});
		
	}
	
	//creates popup menu
	private void createMenuBar() {
		menu = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGameWithComp = new JMenuItem("New game with computer");
		JMenuItem newGameWithFriend = new JMenuItem("New game with friend");
		JMenuItem exit = new JMenuItem("Exit");
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		JMenu difficultyMenu = new JMenu("Difficulty");
		JRadioButtonMenuItem easy = new JRadioButtonMenuItem("Easy");
		JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
		JRadioButtonMenuItem hard = new JRadioButtonMenuItem("Hard");
		ButtonGroup diffBG = new ButtonGroup();
		diffBG.add(easy);
		diffBG.add(medium);
		diffBG.add(hard);
		diffBG.setSelected(easy.getModel(), true);
		
		JMenu sideMenu = new JMenu("Side");
		JRadioButtonMenuItem cross = new JRadioButtonMenuItem("X");
		JRadioButtonMenuItem zero = new JRadioButtonMenuItem("O");
		ButtonGroup sideBG = new ButtonGroup();
		sideBG.add(cross);
		sideBG.add(zero);
		sideBG.setSelected(cross.getModel(), true);
		
		gameMenu.add(newGameWithComp);
		gameMenu.add(newGameWithFriend);
		gameMenu.add(exit);
		
		difficultyMenu.add(easy);
		difficultyMenu.add(medium);
		difficultyMenu.add(hard);
		
		sideMenu.add(cross);
		sideMenu.add(zero);
		
		menu.add(gameMenu);
		menu.add(difficultyMenu);
		menu.add(sideMenu);
		
	}
	
	public static void main(String[] args) {
		TicTacToe ttc = new TicTacToe();
		ttc.init();
	}
}


class MyPanel extends JPanel {
	
	public MyPanel(LayoutManager lm) {
		super(lm);
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(300, 300));
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
		BufferedImage emptyField = ImageIO.read(new File("\\res\\emptyField.png"));
		g.drawImage(emptyField, getHeight()/2, getWidth()/2 , this);
		} catch(IOException e) {
			e.printStackTrace();
		}
//		g.drawLine(100, 0, 100, 400);
//		g.drawLine(200, 0, 200, 400);
//		g.drawLine(0, 100, 400, 100);
//		g.drawLine(0, 200, 400, 200);
	}
}