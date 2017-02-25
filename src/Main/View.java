package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import Assets.*;
/**
 * This Class draws the view of the game, it assumes the input map is square.
 * Diameter of units is taken as a factor of the size of the map tiles.
 * So an asset with a diameter of 0.9 becomes a circle with a diameter
 * which equals 90% * tileWidth
 * @author voldelord
 *
 */
public class View extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7251401100069949488L;
	Model model;

	public View(Model m) {
		super();
		model = m;
		setBackground(Color.BLACK);
	}

	@Override
	public void update(Observable o, Object arg) {
		this.repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		paintView(g);
	}
	
	/**
	 * This function draws a single Asset
	 * @param g
	 * @param a
	 * @param squareSize
	 * @param horOff
	 * @param verOff
	 */
	private void paintAsset(Graphics g, Asset a,int squareSize,int horOff,int verOff) {
		int n=model.getLevelMap().size;
		int radius=(int)Math.floor(a.getDiameter()*squareSize);
		int x=(int)Math.floor((a.getX()-a.getDiameter()/2/n-0.5/n)*n*squareSize);
		int y=(int)Math.floor((a.getY()-a.getDiameter()/2/n-0.5/n)*n*squareSize);
		 
		Color c;
		
		if (a instanceof Unit) {
			if(a instanceof Archer){
				c=Color.GREEN;
			}else if(a instanceof Cavalry){
				c=Color.YELLOW;
			}else{
				c=Color.RED;
			}
		} else {
			c=Color.blue;
		}
		
		g.setColor(c);
		g.fillOval(x+horOff/2, y+verOff/2, radius, radius);
		
	}

	/**xc 
	 * This function draws the entire model in the panel
	 * @param g
	 */
	private void paintView(Graphics g) {
		Map map = model.getLevelMap();
		int minDimension = Math.min(getWidth(), getHeight());
		double relativeSize = 1.0 / map.size;
		int absoluteSquareSize = (int) Math.floor(relativeSize * minDimension);
		int horizontalOffset = getWidth() - map.size * absoluteSquareSize;
		int verticalOffset = getHeight() - map.size * absoluteSquareSize;
		char tile;
		Color c;
		for (int x = 0; x < map.size; x++) {
			for (int y = 0; y < map.size; y++) {
				tile = map.getTile(y, x);

				switch (tile) {
				case '#':
					c = Color.BLACK;
					break;
				default:
					c = Color.WHITE;
				}
				g.setColor(c);
				g.fillRect(x * absoluteSquareSize + horizontalOffset / 2, y * absoluteSquareSize + verticalOffset / 2,
						absoluteSquareSize, absoluteSquareSize);
				
			}
		}

		for (Asset a : model.getPlayer1()) {
			this.paintAsset(g, a,absoluteSquareSize,horizontalOffset,verticalOffset);
		}

		for (Asset a : model.getPlayer2()) {
			this.paintAsset(g, a,absoluteSquareSize,horizontalOffset,verticalOffset);
		}

	}

}