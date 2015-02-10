/**
 * TronGame.java
 *
 * Roman Seviaryn
 *
 */
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.JOptionPane.*;

public class TronGame extends JFrame implements ActionListener{
	Timer clock;
	GamePanel arena;
	
    public TronGame() {
    	super("Tron");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setSize(607, 510);

    	clock = new Timer(10, this);
    	    	
    	arena = new GamePanel(this);
    	add(arena);

    	setResizable(false);
    	setVisible(true);
    }
    public void start(){
    	clock.start();
    }

    public void actionPerformed(ActionEvent evt){
    	if(arena.getWinner() != arena.NEUTRAL){
    		if(arena.getWinner() == arena.BLUE){
    			JOptionPane.showMessageDialog(null, "Player 1 Wins!", "Winner!", JOptionPane.PLAIN_MESSAGE);
    			System.exit(0);
    		}else{
    			JOptionPane.showMessageDialog(null, "Player 2 Wins!", "Winner!", JOptionPane.PLAIN_MESSAGE);
    			System.exit(0);

    		}
    	}
    	arena.move();
    	arena.repaint();
    }

    public static void main(String[] args){
    	TronGame frame = new TronGame();
    }
}

enum Direction{
	// enum constants - pretty simple
	UP, DOWN, LEFT, RIGHT
}

class GamePanel extends JPanel implements KeyListener{
	private int blueX, blueY, orangeX, orangeY;
	private boolean[] keys;
	private ArrayList<int[]> path;
	private TronGame mainFrame;
	private Direction direction_blue, direction_orange;
	private int players;
	private int winner = 0; // default is 0. If the winner is ORANGE winner = 1 if the winner is BLUE, winner = 2

	public final int NEUTRAL = 0;
	public final int BLUE = 1;
	public final int ORANGE = 2;

	public GamePanel(TronGame m){
		keys = new boolean[KeyEvent.KEY_LAST + 1];
		mainFrame = m;

		players = 2;
		// for single player game, change this to 1

		blueX = 50;
		blueY = 240;
		direction_blue = Direction.RIGHT;

		orangeX = 550;
		orangeY = 240;
		direction_orange = Direction.LEFT;

		path = new ArrayList<int[]>();
		setUpBoundry();

		setSize(600, 480);
		addKeyListener(this);
	}

	public void addNotify(){
		super.addNotify();
		requestFocus();
		mainFrame.start();
	}

	private void setUpBoundry(){
		System.out.println("pass");
		for(int i = 0; i < 600; i++){
			path.add(new int[]{i, 0, NEUTRAL});
			path.add(new int[]{i, 480, NEUTRAL});
		}
		for(int i = 0; i < 480; i++){
			path.add(new int[]{0, i, NEUTRAL});
			path.add(new int[]{600, i, NEUTRAL});
		}
	}

	private void moveBikeBlue(){
		for(int num = 0; num < 2; num++){
			// num was used because i was already taken and j seemed wierd
			// moves the bike 2 blocks at a time to speed up the game
			path.add(new int[]{blueX, blueY, BLUE});
			if(direction_blue == Direction.UP){
				blueY -= 1;
			}else if(direction_blue == Direction.DOWN){
				blueY += 1;
			}else if(direction_blue == Direction.LEFT){
				blueX -= 1;
			}else if(direction_blue == Direction.RIGHT){
				blueX += 1;
			}
			for(int[] i: path){
				if(i[0] == blueX && i[1] == blueY){
					// if the bike crashes into a previously made path
					winner = ORANGE;
				}
			}
		}
	}

	private void moveBikeOrange(){
		for(int num = 0; num < 2; num++){
			// see above method
			// moves the bike 2 blocks at a time to speed up the game
			path.add(new int[]{orangeX, orangeY, ORANGE});
			if(direction_orange == Direction.UP){
				orangeY -= 1;
			}else if(direction_orange == Direction.DOWN){
				orangeY += 1;
			}else if(direction_orange == Direction.LEFT){
				orangeX -= 1;
			}else if(direction_orange == Direction.RIGHT){
				orangeX += 1;
			}
			for(int[] i: path){
				if(i[0] == orangeX && i[1] == orangeY){
					// if the orange bike crashes into someone's path
					winner = BLUE;
				}
				if(players == 1){
					// now the brain:
					// if the bot would crash next loop, the bot will make a left turn
					// ai portion is only on if the players field of GamePanel is 1(single player game)
					// due to the nature of movement, the AI "thinks" twice during one move - now that's pretty smart!
					if(direction_orange == Direction.UP && i[0] == orangeX && i[1] == orangeY -1){
						direction_orange = Direction.LEFT;
					}
					else if(direction_orange == Direction.DOWN && i[0] == orangeX && i[1] == orangeY +1){
						direction_orange = Direction.RIGHT;
					}
					else if(direction_orange == Direction.LEFT && i[0] == orangeX -1 && i[1] == orangeY){
						direction_orange = Direction.DOWN;
					}
					else if(direction_orange == Direction.RIGHT && i[0] == orangeX +1 && i[1] == orangeY){
						direction_orange = Direction.UP;
					}
				}
			}
		}
	}

	public void move(){
		if(keys[KeyEvent.VK_W]){
			direction_blue = Direction.UP;
		}
		if(keys[KeyEvent.VK_S]){
			direction_blue = Direction.DOWN;
		}
		if(keys[KeyEvent.VK_A]){
			direction_blue = Direction.LEFT;
		}
		if(keys[KeyEvent.VK_D]){
			direction_blue = Direction.RIGHT;
		}
		if(players == 2){
			//if the game is a 2 player game the second player controls the orange bike
			if(keys[KeyEvent.VK_UP]){
			direction_orange = Direction.UP;
			}
			if(keys[KeyEvent.VK_DOWN]){
				direction_orange = Direction.DOWN;
			}
			if(keys[KeyEvent.VK_LEFT]){
				direction_orange = Direction.LEFT;
			}
			if(keys[KeyEvent.VK_RIGHT]){
				direction_orange = Direction.RIGHT;
			}
		}

		moveBikeBlue();
		moveBikeOrange();
	}

	public void paintComponent(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLUE);
		for(int i = 0; i <= getWidth(); i += 40){
			g.fillRect(i, 0, 1, getHeight());
			if(i <= getHeight()){
				g.fillRect(0, i, getWidth(), 1);
			}
		}

		for(int[] i: path){
				if(i[2] == BLUE){
					g.setColor(new Color(0, 75, 250));
					g.fillRect(i[0], i[1], 1, 1);
				}
				if(i[2] == ORANGE){
					g.setColor(new Color(175, 75, 0));
					g.fillRect(i[0], i[1], 1, 1);
				}
		}

		g.setColor(new Color(0, 75, 175));
		g.fillRect(blueX -5, blueY -5, 10, 10);
		g.setColor(new Color(200, 100, 0));
		g.fillRect(orangeX -5, orangeY -5, 10, 10);
	}

	public void keyTyped(KeyEvent e){}

	public void keyPressed(KeyEvent e){
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e){
		keys[e.getKeyCode()] = false;
	}

	public int getWinner(){
		return winner;
	}
}