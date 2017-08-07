import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Display extends JFrame{
	
	private final int mineScale;
	private final int tankScale; 
//	private final int windowWidth;
//	private final int windowHeight;
	
	private ArrayList<Point2D.Double> tankVerts;
	private ArrayList<Point2D.Double> mineVerts;
	
	JFrame backround;
	Graphics g; 
	
	Display(int windowWidth, int windowHeight, int mineScale, int tankScale){

		super("Tank training game");
		


		
		this.mineScale = mineScale;
		this.tankScale = tankScale;
//		this.windowWidth = windowWidth;
//		this.windowHeight = windowHeight;

	
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(windowWidth, windowHeight);
		setVisible(true);
		g = getGraphics();
		
	}
	
	private void transform_world(ArrayList<Point2D.Double> buffer, Point2D.Double minePosition){
		
		Matrix2D worldMatrix = new Matrix2D();
		
		// scale
		worldMatrix.scale(mineScale, mineScale);
		
		// translate
		worldMatrix.translate(minePosition.x, minePosition.y);
		
		// transform 
		worldMatrix.transform(mineVerts);
		
	}
	
	public void update(ArrayList<Tank> tanks, ArrayList<Point2D.Double> mines, int epoch, boolean training, String trainedTime){

		// redraw the screen
		this.paint(g);

		int index = 0;
		int topScore = 0;
		int topScorer = 0;
		for(Tank tank: tanks){
			if(tank.score > topScore){
				topScore = tank.score;
				topScorer = index;
			}
			++index;
		}
		
		// mine color
		g.setColor(Color.GRAY);
		
		for(Point2D.Double mine: mines){
			
			// create mine draw matrix
			init_mine_verts();
			transform_world(mineVerts,mine);
		
			// draw mine
			g.drawLine((int)mineVerts.get(0).x, (int)mineVerts.get(0).y, (int)mineVerts.get(1).x, (int)mineVerts.get(1).y);
			g.drawLine((int)mineVerts.get(1).x, (int)mineVerts.get(1).y, (int)mineVerts.get(2).x, (int)mineVerts.get(2).y);
			g.drawLine((int)mineVerts.get(2).x, (int)mineVerts.get(2).y, (int)mineVerts.get(3).x, (int)mineVerts.get(3).y);
			g.drawLine((int)mineVerts.get(3).x, (int)mineVerts.get(3).y, (int)mineVerts.get(0).x, (int)mineVerts.get(0).y);

		}
		
		index = 0;
		for(Tank tank: tanks){
			
			// best performing tank is red!
			if (topScorer == index){
				g.setColor(Color.RED);
				if(training){
					setTitle("Tank Trainer ( top scorer: " + topScore + " epoch: " + epoch + " ) ... TRAINING");
				}
				else{
					setTitle("Tank Trainer ( top scorer: " + topScore + " level: " + epoch + " trained for: " + trainedTime + " ) ... TRAINED");
				}
			}
			else{
				g.setColor(Color.BLACK);
			}

			++index;
			
			// create tank draw matrix
			init_tank_verts();
			tank.transform_world(tankVerts);

			// draw left track
			g.drawLine((int)tankVerts.get(0).x, (int)tankVerts.get(0).y, (int)tankVerts.get(1).x, (int)tankVerts.get(1).y);
			g.drawLine((int)tankVerts.get(1).x, (int)tankVerts.get(1).y, (int)tankVerts.get(2).x, (int)tankVerts.get(2).y);
			g.drawLine((int)tankVerts.get(2).x, (int)tankVerts.get(2).y, (int)tankVerts.get(3).x, (int)tankVerts.get(3).y);
			g.drawLine((int)tankVerts.get(3).x, (int)tankVerts.get(3).y, (int)tankVerts.get(0).x, (int)tankVerts.get(0).y);
			
			// draw right track
			g.drawLine((int)tankVerts.get(4).x, (int)tankVerts.get(4).y, (int)tankVerts.get(5).x, (int)tankVerts.get(5).y);
			g.drawLine((int)tankVerts.get(5).x, (int)tankVerts.get(5).y, (int)tankVerts.get(6).x, (int)tankVerts.get(6).y);
			g.drawLine((int)tankVerts.get(6).x, (int)tankVerts.get(6).y, (int)tankVerts.get(7).x, (int)tankVerts.get(7).y);
			g.drawLine((int)tankVerts.get(7).x, (int)tankVerts.get(7).y, (int)tankVerts.get(4).x, (int)tankVerts.get(4).y);
			
			g.drawLine((int)tankVerts.get(8).x, (int)tankVerts.get(8).y, (int)tankVerts.get(9).x, (int)tankVerts.get(9).y);
			
			g.drawLine((int)tankVerts.get(10).x, (int)tankVerts.get(10).y, (int)tankVerts.get(11).x, (int)tankVerts.get(11).y);
			g.drawLine((int)tankVerts.get(11).x, (int)tankVerts.get(11).y, (int)tankVerts.get(12).x, (int)tankVerts.get(12).y);
			g.drawLine((int)tankVerts.get(12).x, (int)tankVerts.get(12).y, (int)tankVerts.get(13).x, (int)tankVerts.get(13).y);
			g.drawLine((int)tankVerts.get(13).x, (int)tankVerts.get(13).y, (int)tankVerts.get(14).x, (int)tankVerts.get(14).y);
			g.drawLine((int)tankVerts.get(14).x, (int)tankVerts.get(14).y, (int)tankVerts.get(15).x, (int)tankVerts.get(15).y);

		}

	}
/*	
	private void init_tank_verts(){

		tankVerts = new ArrayList<>();
	
		// left track
		Point2D.Double vertex = new Point2D.Double(-1.0, -1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-1.0, 1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-0.5, 1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-0.5, -1.0);
		tankVerts.add(vertex);

		// right track
		vertex = new Point2D.Double(0.5, -1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.0, -1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.0, 1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(0.5, 1.0);
		tankVerts.add(vertex);
		
		vertex = new Point2D.Double(-0.5, -0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(0.5, -0.5);
		tankVerts.add(vertex);
		
		vertex = new Point2D.Double(-0.5, 0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-0.25, 0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-0.25, 1.75);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(0.25, 1.75);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(0.25, 0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(0.5, 0.5);
//		vertex.setLocation(0.5, 0.5);
		tankVerts.add(vertex);

	}
*/


	private void init_tank_verts(){

		tankVerts = new ArrayList<>();
	
		// left track
		Point2D.Double vertex = new Point2D.Double(-1.0, 1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.0, 1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.0, 0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-1.0, 0.5);
		tankVerts.add(vertex);

		// right track
		vertex = new Point2D.Double(-1.0, -0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-1.0, -1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.0, -1.0);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.0, -0.5);
		tankVerts.add(vertex);
		
		vertex = new Point2D.Double(-0.5, 0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-0.5, -0.5);
		tankVerts.add(vertex);
		
		vertex = new Point2D.Double(0.5, 0.5);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(0.5, 0.25);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.75, 0.25);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(1.75, -0.25);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(0.5, -0.25);
		tankVerts.add(vertex);
		vertex = new Point2D.Double(-0.5, -0.5);
//		vertex.setLocation(0.5, 0.5);
		tankVerts.add(vertex);

	}
	
	private void init_mine_verts(){
		// mine vertices
		mineVerts = new ArrayList<>();
		Point2D.Double vertex = new Point2D.Double(-1.0, -1.0);
		mineVerts.add(vertex);
		vertex = new Point2D.Double(-1.0, 1.0);
		mineVerts.add(vertex);
		vertex = new Point2D.Double(1.0, 1.0);
		mineVerts.add(vertex);
		vertex = new Point2D.Double(1.0, -1.0);
		mineVerts.add(vertex);
		
	}

}
