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
	private void paintAsset(Graphics g, Asset a,int minDimension,int horOff,int verOff) {
		int n=model.getLevelMap().size;
		int radius=(int)Math.floor(a.getDiameter()*minDimension);
		int x=(int)Math.floor((a.getX())*minDimension)+horOff/2;
		int y=(int)Math.floor((a.getY())*minDimension)+verOff/2;
		 
		Color c;
		
		switch (a.getOwner()) {
		case 0: c=Color.GRAY;
				break;
        case 1: c=Color.RED;
				break;
        case 2: c=Color.BLUE;
				break;
        case 3: c=Color.GREEN;
        		break;
        case 4: c=Color.MAGENTA;
		default:c=Color.YELLOW;
		}
		g.setColor(c);
		
		if (a instanceof Unit) {
			if(a instanceof Archer){
				g.fillPolygon(new int[]{x,x+radius,x+radius/2}, new int[]{y,y,y+radius}, 3);
				g.setColor(Color.BLACK);
				g.drawPolygon(new int[]{x,x+radius,x+radius/2}, new int[]{y,y,y+radius}, 3);
			}else if(a instanceof Cavalry){
				g.fillPolygon(new int[]{x+radius/2,x+radius,x+radius/2,x}, new int[]{y,y+radius/2,y+radius,y+radius/2}, 4);
				g.setColor(Color.BLACK);
				g.drawPolygon(new int[]{x+radius/2,x+radius,x+radius/2,x}, new int[]{y,y+radius/2,y+radius,y+radius/2}, 4);
			}else{
				g.fillOval(x, y, radius, radius);
				g.setColor(Color.BLACK);
				g.drawOval(x, y, radius, radius);
			}
		} else {
			g.fillRect(x, y, radius, radius);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, radius, radius);
		}
		
		
		
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
				tile = map.getTile(x, y);

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
		for (Player p: model.getPlayerList()) {
			for (Asset a : p.getAssets()) {
				this.paintAsset(g, a,absoluteSquareSize*map.size,horizontalOffset,verticalOffset);
			}
		}

	}

}
