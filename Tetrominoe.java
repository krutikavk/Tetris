package tetrisGame;

import java.awt.*;
import java.util.Random;



public enum Tetrominoe {
	
	NoShape{
		public Color getColor() {
			return Color.LIGHT_GRAY;
		}
		
		public String getName() {
			return "N";
		}
	}, 

	SShape{
		public Color getColor() {
			return Color.CYAN;		
		}
		public String getName() {
			return "S";
		}
	}, 
	ZShape{
		public Color getColor() {
			return Color.BLUE;
		}
		public String getName() {
			return "Z";
		}
	},
	LineShape{
		public Color getColor() {
			return Color.GREEN;
		}
		public String getName() {
			return "|";
		}
	}, 
    TShape{
		public Color getColor() {
			return Color.MAGENTA;
		}
		public String getName() {
			return "T";
		}
	}, 
    SquareShape{
		public Color getColor() {
			return Color.YELLOW;
		}
		public String getName() {
			return "O";
		}
	}, 
    LShape{ 
		@Override
		public Color getColor() {
    		return Color.ORANGE;
    	}
		public String getName() {
			return "L";
		}
	},
    JShape{
    	public Color getColor() {
    		return Color.RED;
    	}
    	public String getName() {
			return "J";
		}
    	
    };
	
	public abstract Color getColor();
	public abstract String getName();
	
	public static Tetrominoe getRandomTetrominoe() {
		Random random = new Random();
    	return values()[random.nextInt(7) + 1];
	}
	
}

