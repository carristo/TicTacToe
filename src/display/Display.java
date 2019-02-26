package display;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import game.Difficulty;
import input.*;

public class Display {
	private static boolean created = false;
	private static JFrame window;
	private static Canvas content;
	private static BufferStrategy bufferStrategy;
	private static BufferedImage buffer;
	private static int[] bufferData;
	private static int clearColor;
	private static Graphics bufferGraphics;
	private static JMenuBar menu;
	private static NewGameListener ngl;
	private static Difficulty difficulty = Difficulty.EASY;
	private static boolean newGame;
	
	
	public static void create(int width, int height, String title, int clearColor, int numBuffers) {
		if(created) {
			return;
		}
		
		window = new JFrame("Title");
		ngl = new NewGameListener();
		createMenuBar();
		content = new Canvas();
		Display.clearColor = clearColor;
		
		content.setPreferredSize(new Dimension(width, height));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		window.add(menu, BorderLayout.NORTH);
		window.getContentPane().add(content);
		window.setFocusable(true);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bufferData = ((DataBufferInt)buffer.getRaster().getDataBuffer()).getData();
		bufferGraphics = buffer.getGraphics();
		
		content.createBufferStrategy(numBuffers);
		bufferStrategy = content.getBufferStrategy();
		
		created = true;
		
	}
	
	//creates popup menu
	private static void createMenuBar() {
		menu = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGameWithComp = new JMenuItem("New game with computer");
		JMenuItem newGameWithFriend = new JMenuItem("New game with friend");
		JMenuItem exit = new JMenuItem("Exit");
		
		
		newGameWithFriend.addActionListener(ngl);
		newGameWithComp.addActionListener(ngl);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		JMenu difficultyMenu = new JMenu("Difficulty");
		JRadioButtonMenuItem easy = new JRadioButtonMenuItem("Easy");
		easy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficulty = difficulty.EASY;
			}
		});
		JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
		medium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficulty = difficulty.MEDIUM;
			}
		});
		JRadioButtonMenuItem hard = new JRadioButtonMenuItem("Hard");
		hard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficulty = difficulty.HARD;
			}
		});
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
	
	
	public static Graphics2D getGraphics() {
		return (Graphics2D)bufferGraphics;
	}
	
	public static void destroy() {
		if(!created) {
			return;
		}
		
		window.dispose();
	}
	
	public static void clear() {
		Arrays.fill(bufferData, clearColor);
		bufferGraphics.setColor(Color.RED);
		
		bufferGraphics.drawLine(0, 100, 300, 100);
		bufferGraphics.drawLine(0, 200, 300, 200);
		bufferGraphics.drawLine(100, 0, 100, 300);
		bufferGraphics.drawLine(200, 0, 200, 300);
	}
	
	public static void swapBuffers() {
		Graphics g = bufferStrategy.getDrawGraphics();
		g.drawImage(buffer, 0, 0, null);
		bufferStrategy.show();
	}
	
	public static void addInputListener(Input input) {
		content.addMouseListener(input);
	}
	
	public static void setTitle(String title) {
		window.setTitle(title);
	}
	
	public static boolean isNewGame() {
		return ngl.isNewGame() || newGame;
	}
	
	public static boolean isWithComp() {
		return ngl.isWithComp();
	}
	
	public static void refresh() {
		newGame = false;
		ngl.refresh();
	}
	
	public static Difficulty getDifficulty() {
		return difficulty;
	}
	
	public static void showResult(String result) {
		swapBuffers();
		String message;
		if(result.equals("Draw") || result == "Draw" || result.compareTo("Draw")==0) {
			message = "No winner. Try again?";
		} else { message = result + " wins. Try again?"; }
		switch(JOptionPane.showConfirmDialog(window, message, "Game over" , JOptionPane.YES_NO_OPTION)) {
		case JOptionPane.YES_OPTION:
			newGame = true;
			break;
		case JOptionPane.NO_OPTION:
			System.exit(0);
		} 
	}
}
