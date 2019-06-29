package tetrisGame;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import com.programwithjava.basic.DrawingKit;

public class Sprite {
        
    protected Tetrominoe piece;
    
    //Base coordinates on X/Y axes
    protected int pieceBaseCoordinates[][];
    
    private int[][][] coordinateTable;
    
    //These are X and Y coordinates in absolute pixel units
    private int pieceXCoordinate;
    private int pieceYCoordinate;
    
    //Default Constructor
    public Sprite() {
    	getNewSprite();
    }
    
    
    public int getPieceXCoordinate() {
    	return pieceXCoordinate;
    }
    
    public int getPieceYCoordinate() {
    	return pieceYCoordinate;
    }
    
    public void setPieceXCoordinate(int pieceXCoordinate) {
    	this.pieceXCoordinate = pieceXCoordinate;
    }
    
    public void setPieceYCoordinate(int pieceYCoordinate) {
    	this.pieceYCoordinate = pieceYCoordinate;
    }
    
    //Initialize coordinateTable and pieceBaseCoordinates
    public void getNewSprite() {
    	pieceBaseCoordinates = new int[4][2];
    	piece = Tetrominoe.getRandomTetrominoe();
    	coordinateTable = new int [][][] {{{0,0},  {0,0}, {0,0}, {0,0}},
										  {{1,0},  {1,1}, {0,1}, {2,0}}, 					//S-shape
										  {{0,0},  {1,0}, {1,1}, {2,1}}, 					//Z-Shape 
										  {{0,0},  {0,1}, {0,2}, {0,3}}, 					//Line Shape
										  {{0,0},  {0,1}, {1,1}, {0,2}},					//T-shape 
										  {{0,0},  {0,1}, {1,1}, {1,0}}, 					//Square Shape
										  {{0,0},  {0,1}, {0,2}, {1,2}}, 					//L-Shape
										  {{1,0},  {1,1}, {1,2}, {0,2}}};					//J Shape
    	assignShape(piece);
    	pieceXCoordinate = getRandomXPosition(Constants.NUM_HBLOCKS - getPieceWidth() + 1);
    	pieceYCoordinate = 0;
    }
    
    //Initialize Shape to Tetrominoe.NoShape
    public void initializeToNoShape() {
    	pieceBaseCoordinates = new int[4][2];
    	piece = Tetrominoe.NoShape;
    	coordinateTable = new int [][][] {{{0,0},  {0,0}, {0,0}, {0,0}}, 
			  							  {{1,0},  {1,1}, {0,1}, {2,0}}, 					//S-shape
			                              {{0,0},  {1,0}, {1,1}, {2,1}}, 					//Z-Shape 
			                              {{0,0},  {0,1}, {0,2}, {0,3}}, 					//Line Shape
			                              {{0,0},  {0,1}, {1,1}, {0,2}},					//T-shape 
			                              {{0,0},  {0,1}, {1,1}, {1,0}}, 					//Square Shape
			                              {{0,0},  {0,1}, {0,2}, {1,2}}, 					//L-Shape
			                              {{1,0},  {1,1}, {1,2}, {0,2}}};					//J Shape
		assignShape(piece);
		pieceXCoordinate = 0;
		pieceYCoordinate = 0;
    }
    
    
    
    //Populates pieceBaseCoordinates for the piece from CoordinateTable
    public void assignShape(Tetrominoe t) {
    	piece = t;
    	for(int i = 0; i < 4; i++) {
    		for(int j = 0; j < 2; j++) {
    			pieceBaseCoordinates[i][j] = coordinateTable[t.ordinal()][i][j];
    		}
    	}
    	
    }
    
    //Used to generate random  starting position of the piece in the game
    private int getRandomXPosition(int range) {
    	Random random = new Random();
    	int position = random.nextInt(range);
    	return (position * Constants.PIXELS_PER_BLOCK);
    }
    
    //Used to limit random X position of the new piece to the limits of the board width
    private int getPieceWidth(){
    	int minX = 10000;
    	int maxX = -1;
    	for(int i = 0; i < 4; i++) {
			minX = (pieceBaseCoordinates[i][0] < minX) ? pieceBaseCoordinates[i][0] : minX;
			maxX = (pieceBaseCoordinates[i][0] > maxX) ? pieceBaseCoordinates[i][0] : maxX;
    	}
    	return (maxX - minX + 1);
    }
    
    

    //Used to get lowest Y coordinate of the shape 
    public int getShapeBottom() {
    	//Get y-coordinate of first point in shape
    	int bottom = pieceBaseCoordinates[0][1];
    	
    	for(int i =0; i < 4; i++) {
    		bottom = Math.max(bottom, pieceBaseCoordinates[i][1]);
    	}
    	
    	//Offset bottom with current X and Y coordinates of the shape
    	bottom = pieceYCoordinate + (bottom + 1) * Constants.PIXELS_PER_BLOCK;
    	return bottom;
    }
    
    //Returns absolute Left extremity of the sprite
    public int getLeftExtremity() {
    	int leftExtremity = pieceBaseCoordinates[0][0];
    	for(int i = 0; i < 4; i++) {
    		leftExtremity = Math.min(leftExtremity, pieceBaseCoordinates[i][0]);
    	}
    	
    	return pieceXCoordinate + leftExtremity * Constants.PIXELS_PER_BLOCK;
    }
    
    //returns absolute right extremity of the sprite
    public int getRightExtremity() {
    	int rightExtremity = pieceBaseCoordinates[0][0];
    	for(int i = 0; i < 4; i++) {
    		rightExtremity = Math.max(rightExtremity, pieceBaseCoordinates[i][0]);
    	}
    	
    	return pieceXCoordinate + (rightExtremity + 1) * Constants.PIXELS_PER_BLOCK;
    }
    
    //Rotates the piece and checks for board boundaries while rotation
    public void rotateLeft() {
    	if (piece == Tetrominoe.NoShape || piece == Tetrominoe.SquareShape) 
    		return;

    	//For all other pieces:
		int temp;
		int minX, minY;
		
		//Basic Rotation
		for (int i = 0; i < 4; ++i) {
            temp = pieceBaseCoordinates[i][0];
            pieceBaseCoordinates[i][0] = pieceBaseCoordinates[i][1];
            pieceBaseCoordinates[i][1] = -temp;
            
        }
		
		//Offset any negative coordinates to ensure pieces stay on board 
		minX = pieceBaseCoordinates[0][0];
		minY = pieceBaseCoordinates[0][1];
		
		for(int i = 0; i < 4; i++) {
			minX = pieceBaseCoordinates[i][0] < minX ? pieceBaseCoordinates[i][0]: minX;
			minY = pieceBaseCoordinates[i][1] < minY ? pieceBaseCoordinates[i][1]: minY;
		}
		
		if(minX < 0) {
			while(minX != 0 ) {
				for(int i = 0; i < 4; i++) {
					pieceBaseCoordinates[i][0]++;
				}
				minX++;
			}
		}
		
		if(minY < 0) {
			while(minY != 0 ) {
				for(int i = 0; i < 4; i++) {
					pieceBaseCoordinates[i][1]++;
				}
				minY++;
			}
		}
		
		//Offset actual X/Y coordinates to ensure pieces stay on board
		if(getRightExtremity()/Constants.PIXELS_PER_BLOCK > Constants.NUM_HBLOCKS) {
			while(getRightExtremity()/Constants.PIXELS_PER_BLOCK != Constants.NUM_HBLOCKS) {
				pieceXCoordinate -= Constants.PIXELS_PER_BLOCK;
			}
		}
		
		if(getLeftExtremity()/Constants.PIXELS_PER_BLOCK < 0) {
			while(getLeftExtremity()/Constants.PIXELS_PER_BLOCK < 0) {
				pieceXCoordinate += Constants.PIXELS_PER_BLOCK;
			}
		}
		
		if(getShapeBottom()/Constants.PIXELS_PER_BLOCK > Constants.NUM_VBLOCKS) {
			while(getShapeBottom()/Constants.PIXELS_PER_BLOCK != Constants.NUM_VBLOCKS)
				pieceYCoordinate -= Constants.PIXELS_PER_BLOCK;
		}
    }
    
    
    //Moves tetrominoe left by 1 block
    public void moveLeft() {
    	pieceXCoordinate -= Constants.PIXELS_PER_BLOCK;
    }
    
    //Moves tetrominoe by 1 block
    public void moveRight() {
    	pieceXCoordinate += Constants.PIXELS_PER_BLOCK;
    }
    
    //Drops tetrominoe by a block
    public void dropShape() {
    	pieceYCoordinate += Constants.PIXELS_PER_BLOCK;
    }
    
    //Draw the final shape on board
    public void drawShape(Graphics2D myGraphics) {
    	Rectangle2D r;
    	for(int i = 0; i < 4; i++) {
    		int x = (pieceBaseCoordinates[i][0] * Constants.PIXELS_PER_BLOCK) + pieceXCoordinate;
    		int y = (pieceBaseCoordinates[i][1] * Constants.PIXELS_PER_BLOCK) + pieceYCoordinate;
			r = new Rectangle2D.Float((pieceBaseCoordinates[i][0] * Constants.PIXELS_PER_BLOCK) + pieceXCoordinate, (pieceBaseCoordinates[i][1] * Constants.PIXELS_PER_BLOCK) + pieceYCoordinate, Constants.PIXELS_PER_BLOCK , Constants.PIXELS_PER_BLOCK);
			
			GradientPaint gradient = new GradientPaint(x, y, piece.getColor(), x + 50, y + 50, Color.BLACK);
			myGraphics.setPaint(gradient);
			myGraphics.fill(r);
			myGraphics.setPaint(Color.BLACK);
			myGraphics.draw(r);
		}
    }
}
