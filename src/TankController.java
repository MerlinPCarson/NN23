import java.util.ArrayList;
import java.util.Random;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;

public class TankController {

	private final boolean MANUAL = false;
	private int trainingTimes = 920000;	// number of times we back propagate
	private final long trainingStart = System.currentTimeMillis();
	private String trainedTime;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	// Neural Net constants
	private final int numOfInputsPerNeuron = 5;
	private final int numOfOutputsInNN = 2;
	
	// controller constants
	private final int windowHeight = 600;
	private final int windowWidth  = 600;
	private final int tankScale    = 5;
	private final int tankSize     = 5 + 2 * tankScale;
	private final int mineScale    = 2;
	private final int mineSize     = 5 + 2 * mineScale;
	private final int numOfMines   =  50;
	private final int resetMines   = 5;
	private final int numOfTanks   = 3;
	private final int refreshRate   = 30;
	
	// tanks and mines on the screen
	private ArrayList<Tank> tanks = new ArrayList<>();
	private ArrayList<Point2D.Double> mines;
	
	// game state variables
	private int remainingMines;
	private int epoch = 1;	// level
	private boolean training = true;	// true if net is training, else false
	
	private Display display;

	
	TankController(){
		
		// initialize tanks
		add_tanks();

		
		// initialize mine locations
		init_mines();
		
		// for testing
//		mines.add(new Point2D.Double(160, 160));
//		mines.add(new Point2D.Double(230, 260));
//		mines.add(new Point2D.Double(270, 260));
//		remainingMines = 3;
		
		// create display window
		display = new Display(windowWidth, windowHeight, mineScale, tankScale);
	}

	private void add_tanks() {
		Tank newTank;	
		
		// create the tanks!
		for(int cnt = 0; cnt < numOfTanks; ++cnt){
			if(new File("savNN\\NN" + cnt + ".ser").isFile()) {
				try {
			         FileInputStream fileIn = new FileInputStream("savNN\\NN" + cnt + ".ser");
			         ObjectInputStream in = new ObjectInputStream(fileIn);
			         newTank = (Tank) in.readObject();
			         in.close();
			         fileIn.close();
			      }catch(IOException i) {
			         i.printStackTrace();
			         return;
			      }catch(ClassNotFoundException c) {
			         System.out.println("Tank class not found");
			         c.printStackTrace();
			         return;
			      }
			}
			else {
				newTank = new Tank(numOfInputsPerNeuron, numOfOutputsInNN, windowWidth, windowHeight, tankScale);
			}
			tanks.add(newTank);
		}
		
	}

	private void init_mines(){
		Random random = new Random();
		
		mines = new ArrayList<>();
		remainingMines = numOfMines;
		
		// randomly place mines
		for(int cnt = 0; cnt < numOfMines; ++cnt){
			mines.add(new Point2D.Double(random.nextDouble() * windowWidth, random.nextDouble() * windowHeight));
		}
	}

	public boolean run(){
		
		long refreshTimer;
		
		while(true){
			
			refreshTimer = System.currentTimeMillis();
			while(System.currentTimeMillis() - refreshTimer < refreshRate){

			}
			// refresh screen and NN!!!
			
			update();
		}
		
	}
	
	public boolean update(){
		
		int collision;
		
		if(training){
			if(trainingTimes > 0){
				--trainingTimes;
			}
			else{
				trainedTime = sdf.format(System.currentTimeMillis()-trainingStart);
				training = false;
				// save the neural networks for each tank using serializable
				save_nets();
				
			}
		}
		
		if(remainingMines > resetMines){

			// update the neural net
			for(Tank tank: tanks){

				if(!MANUAL) {
					if(!tank.update(mines, training)){
						System.err.println("Wrong number of NN inputs!");
						return false;
					}
				}
				else {
					tank.update_position();
				}

				// check for tank collision
				tank.tank_collision(tanks, tankSize);
				
				// check for collision
				collision = tank.mine_collision(mines,  mineSize); 
				
				if(collision >= 0){
					mines.remove(collision);	// remove mine from field
					--remainingMines;
				}
				
				display.update(tanks, mines, epoch, training, trainedTime);
				
			}
			
		}
		else{
			System.out.println("Epoch " + epoch + " complete!");
			++epoch;
			
			// reset level
			init_mines();
			for(Tank tank: tanks){
				tank.reset();
			}
			
		}
	
		return true;
			
	}

	private void save_nets() {
		
		int index = 0;
		
		for(Tank tank: tanks) {
			try {
				FileOutputStream fileOut =
						new FileOutputStream("savNN\\NN" + index + ".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(tank);
				out.close();
				fileOut.close();
				System.out.printf("Serialized data is saved in savNN\\NN" + index + ".ser");
			}catch(IOException i) {
				i.printStackTrace();
			}
		
			++index;
		}
	}
	

	
}
