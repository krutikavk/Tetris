package tetrisGame;

import java.awt.Color;

public class GridElement {
	
	protected Boolean occupied;
	protected Color color;
	
	public GridElement () {
		color = Color.LIGHT_GRAY;
		occupied = false;
	}
}
