package tetrisGame;

import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TetrisBoard extends JFrame{
	
	//Variables used to create the GUI--implement in main for now
	private JPanel topPanel;
	private JPanel drawingPanel;
	private JButton pause;
	private JTextField score;
	
	//Variables and objects used in process of the game
	private boolean gameRunning;
	private int linesRemoved;
	
	private Timer timer;
	private Sprite fallingPiece;
	private GridElement grid[][] = new GridElement[Constants.NUM_VBLOCKS][Constants.NUM_HBLOCKS];
	
	//Key Reader
	private class KeyReader extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();

            if (keycode == KeyEvent.VK_P) {
                gameRunning = !gameRunning;
                return;
            }

            switch (keycode) {

                case KeyEvent.VK_LEFT:
                    //move Sprite to left
                	if(checkIfCanMove(false)) {
                		fallingPiece.moveLeft();
                		drawingPanel.repaint();
                	}
                    break;

                case KeyEvent.VK_RIGHT:
                    //Move Sprite to right
                	if(checkIfCanMove(true)) {
                		fallingPiece.moveRight();
                		drawingPanel.repaint();
                	}
                    break;

                case KeyEvent.VK_DOWN:
                    //drop piece by a line
                	if(checkIfCanFall())
        				fallingPiece.dropShape();
        			else {
        				if(fallingPiece.getPieceYCoordinate() == 0)
        				{
        					gameRunning = false;
        					//Insert game over dialog box
        					String message = "GAME OVER!\nSCORE: " + linesRemoved * 30;
        					JOptionPane.showMessageDialog(drawingPanel, message, "Message Dialog", JOptionPane.PLAIN_MESSAGE);
        				} else {
	        				markOccupiedOnGrid(fallingPiece);
	        				checkFullLines();
	        				fallingPiece = new Sprite();
        				}
        			}
                	drawingPanel.repaint();
                    break;

                case KeyEvent.VK_UP:
                    //Left rotate
                	fallingPiece.rotateLeft();
                	drawingPanel.repaint();
                	System.out.println("Left rotate");
                    break;

                case KeyEvent.VK_SPACE:
                    //Drop shape to the end of board
                	if(fallingPiece.getPieceYCoordinate() == 0)
    				{
    					gameRunning = false;
    					gameRunning = false;
    					//Insert game over dialog box
    					String message = "GAME OVER!\nSCORE: " + linesRemoved * 30;
    					JOptionPane.showMessageDialog(drawingPanel, message, "Message Dialog", JOptionPane.PLAIN_MESSAGE);
    				} else {
    					while(checkIfCanFall())
                    		fallingPiece.dropShape();
        				markOccupiedOnGrid(fallingPiece);
        				checkFullLines();
        				fallingPiece = new Sprite();
    				}
                	drawingPanel.repaint();
                    break;

            }
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {
            //refresh board
        	refreshBoard();
        }
    }
	
	
	public TetrisBoard() {
	    
		gameRunning = true;
		linesRemoved = 0;
		
		fallingPiece = new Sprite();
		
		//Initialize the grid with NoShape initially--follows base coordinates and not absolute coordinates
	
		for(int i = 0; i < Constants.NUM_VBLOCKS; i++) {
			for(int j = 0; j < Constants.NUM_HBLOCKS; j++) {
				grid[i][j] = new GridElement();
			}
		}
		
		topPanel = new JPanel();
		score = new JTextField("SCORE", 10);
		drawingPanel = new JPanel() {
			
			@Override
			public void paintComponent(Graphics g) {
			    super.paintComponent(g);
			    Graphics2D myGraphics = (Graphics2D) g;
			    
			    drawTetrisBoard(myGraphics);
			    fallingPiece.drawShape(myGraphics);
			}
		};
		
		pause = new JButton("PAUSE");
		
		//This block of code is interfering with the KeyReader class function
		/*
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameRunning = !gameRunning;
	
				int option = JOptionPane.showConfirmDialog(drawingPanel, "Game Paused\nClick OK to continue, Cancel to end game", "Paused...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(option == 0)
					gameRunning = !gameRunning;
				else {
					//End game--Display Score and Ask for Name--Store in a serializable HashMap object for retrieval later
				}
				
			}
		});
		*/
		
		
		//Prepare GUI objects
		drawingPanel.setPreferredSize(new Dimension(Constants.NUM_HBLOCKS * Constants.PIXELS_PER_BLOCK, Constants.NUM_VBLOCKS * Constants.PIXELS_PER_BLOCK));
		Border raised = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		drawingPanel.setBorder(raised);
		
		topPanel.add(drawingPanel);
		topPanel.add(pause);
		topPanel.add(score);
		topPanel.setSize(500, 800);
		topPanel.setOpaque(true);	
		
		setContentPane(topPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle("Tetris");
		pack();
		setVisible(true);
		
		//Add event handlers for all keys
	    addKeyListener(new KeyReader());
	    setFocusable(true);
	    
	    //Set timer for dropping the piece
	    timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(),
                1000, 1000);
	}
	
	
	
	//Draw the tetris board in accordance with the grid matrix
	public void drawTetrisBoard(Graphics2D g) {
		
		for (int i = 0; i < Constants.NUM_VBLOCKS; i++ ) {
    		for (int j = 0; j < Constants.NUM_HBLOCKS; j++) {
    			g.draw(new Rectangle2D.Float(j * Constants.PIXELS_PER_BLOCK, i * Constants.PIXELS_PER_BLOCK, Constants.PIXELS_PER_BLOCK, Constants.PIXELS_PER_BLOCK));
    			
    			if(grid[i][j].occupied) {
    				int x = j * Constants.PIXELS_PER_BLOCK;
    	    		int y = i * Constants.PIXELS_PER_BLOCK;
    				Rectangle2D r = new Rectangle2D.Float(x, y, Constants.PIXELS_PER_BLOCK, Constants.PIXELS_PER_BLOCK);
    				GradientPaint gradient = new GradientPaint(x, y, grid[i][j].color, x + 50, y + 50, Color.BLACK);
    				g.setPaint(gradient);
    				g.fill(r);
    				g.setPaint(Color.BLACK);
    				g.draw(r);
    			}
    		}
    	}
	}
	
	//Drops moving Tetrominoe by a line and refreshes the drawingPanel
	public void refreshBoard() {
		if(gameRunning) {
			//try to drop piece by a line
			//check--new position in grid is not occupied 
			if(checkIfCanFall())
				fallingPiece.dropShape();
			else {
				//If Piece cannot fall any further from top of the board, end game
				if(fallingPiece.getPieceYCoordinate() == 0)
				{	
					gameRunning = false;
					//Insert game over dialog box
					String message = "GAME OVER!\nSCORE: " + linesRemoved * 30;
					JOptionPane.showMessageDialog(drawingPanel, message, "Message Dialog", JOptionPane.PLAIN_MESSAGE);
					
				} else {
    				markOccupiedOnGrid(fallingPiece);
    				checkFullLines();
    				fallingPiece = new Sprite();
				}
			}
			drawingPanel.repaint();
		}
		else
			return;
	}
	
	//Mark a position on grid matrix as occupied for fallen blocks
	public void markOccupiedOnGrid(Sprite fallingPiece) {
		//X coordinate will be column number in the matrix-corrected 
		int gridY = fallingPiece.getPieceXCoordinate()/Constants.PIXELS_PER_BLOCK;
		int gridX = fallingPiece.getPieceYCoordinate()/Constants.PIXELS_PER_BLOCK;
		

		//Mark all blocks on the grid occupied by the tetrominoe as occupied and record its original color
		for(int i = 0; i < 4; i++) {
			grid [gridX + fallingPiece.pieceBaseCoordinates[i][1]][gridY + fallingPiece.pieceBaseCoordinates[i][0]].occupied = true;
			grid [gridX + fallingPiece.pieceBaseCoordinates[i][1]][gridY + fallingPiece.pieceBaseCoordinates[i][0]].color = fallingPiece.piece.getColor();
		}
		
		//Debugging block
		/*
		for(int i = 0; i < Constants.NUM_VBLOCKS; i++) {
			for(int j = 0; j < Constants.NUM_HBLOCKS; j++) {
				
				if(grid[i][j].sprite.piece != Tetrominoe.NoShape || grid[i][j].occupied == true)
					System.out.print(grid[i][j].sprite.piece.getName() + " ");
				else {
					System.out.print("E ");
				}
			}
			System.out.println();
		}
		System.out.println("GRID OCCUPANCY");
		for(int i = 0; i < Constants.NUM_VBLOCKS; i++) {
			for(int j = 0; j < Constants.NUM_HBLOCKS; j++) {
				
				if(grid[i][j].occupied == true)
					System.out.print("X ");
				else {
					System.out.print("E ");
				}
			}
			System.out.println();
		}
		*/
		
	}
	
	//Check if new position of falling block is empty
	public boolean checkIfCanFall() {
		boolean canFall= true;
		
		int gridY = fallingPiece.getPieceXCoordinate()/Constants.PIXELS_PER_BLOCK;
		int newGridX = fallingPiece.getPieceYCoordinate()/Constants.PIXELS_PER_BLOCK + 1;
		
		if(fallingPiece.getShapeBottom()/30 == Constants.NUM_VBLOCKS) {
			return !canFall;
			
		}
		
		for(int i = 0; i < 4; i++) {		
			//To check if y is out of bounds 
			if (grid[newGridX + fallingPiece.pieceBaseCoordinates[i][1]][gridY + fallingPiece.pieceBaseCoordinates[i][0]].occupied == false) {
				continue;
			}
			else
				return !canFall;
		}
		return canFall;
	}
	
	//Check if new lateral position of falling block is empty. Parameter true is for moving to right, false for left
	public boolean checkIfCanMove(boolean direction) {
		boolean canMove= true;
		
		int newGridY = direction == true ? (fallingPiece.getPieceXCoordinate()/Constants.PIXELS_PER_BLOCK + 1) : (fallingPiece.getPieceXCoordinate()/Constants.PIXELS_PER_BLOCK - 1);
		int gridX = fallingPiece.getPieceYCoordinate()/Constants.PIXELS_PER_BLOCK ;
		
		//Check for left/right board boundaries
		if(direction && (fallingPiece.getRightExtremity()/30 == Constants.NUM_HBLOCKS))
		{
			return !canMove;
		}
		
		if(!direction && fallingPiece.getLeftExtremity()/30 == 0)
			return !canMove;
		
		//Check if position is occupied
		for(int i = 0; i < 4; i++) {		 
				if (grid[gridX + fallingPiece.pieceBaseCoordinates[i][1]][newGridY + fallingPiece.pieceBaseCoordinates[i][0]].occupied == false)
					continue;
				else
					return !canMove;
		}
		return canMove;
	}
	
	//Check for full lines
	public void checkFullLines() {
		int count = 0;
		for(int i = 0; i < Constants.NUM_VBLOCKS; i++ ) {
			for(int j = 0; j < Constants.NUM_HBLOCKS; j++) {
				if (grid[i][j].occupied == true)
					count++;
			}
			if (count == Constants.NUM_HBLOCKS) {
				//remove line 
				removeFullLine(i);
				linesRemoved++;
				score.setText("SCORE " + linesRemoved * Constants.PIXELS_PER_BLOCK);
			}
			count = 0;
		}
		
		//Debugging block
		/*
		System.out.println("updated board-removed lines");
		for(int i = 0; i < Constants.NUM_VBLOCKS; i++) {
			for(int j = 0; j < Constants.NUM_HBLOCKS; j++) {
				
				if(grid[i][j].occupied == true)
					System.out.print("X ");
				else {
					System.out.print("E ");
				}
			}
			System.out.println();
		}
		*/
	}
	
	//Remove full line from grid
	public void removeFullLine(int line) {
		System.out.println("Remove full line");
		
		for(int i = line; i >= 0; i--) {
			for(int j = 0; j < Constants.NUM_HBLOCKS; j++) {
				if(i != 0)
				{
					//grid[i][j].sprite = grid[i-1][j].sprite;
					grid[i][j].color = grid[i-1][j].color;
					grid[i][j].occupied = grid[i-1][j].occupied;
				}
				else
					grid[i][j] = new GridElement();
			}
		}
	}
	
	
	public static void main(String[] args) {
		TetrisBoard newGame = new TetrisBoard();	
	}
}
