import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Tank {

	private final boolean DEBUG = false;
	
	private final double PI = 3.14159265358979;
	private final double TWOPI = 2*PI;
	private final double maxTurnRate = 0.2;
	private final double maxSpeed = 2;
	private final int numOfOutputs;
	private final double MAXSCREENDISTANCE;

//	int trainingFreq = 2;		// frequency of back propagation to outputs
	
	int windowWidth, windowHeight;
	int scale;
	
	Point2D.Double position  = new Point2D.Double();
	Point2D.Double direction = new Point2D.Double();
	
	double rotation;
	double speed = maxSpeed;

	// variables for the neural net
	NeuralNet brain;
	double leftTrack, rightTrack;	// NN outputs
	
	int closestMine;				// NN inputs(position and vector to closest mine)
	Point2D.Double closestMineLocation = new Point2D.Double();
//	double distanceToMine = 0.0;
	
	int score;						// number of items collected
	
	Tank(int numOfInputs, int numOfOutputs, int windowWidth, int windowHeight, int scale){
		
		Random random = new Random();
		
		this.numOfOutputs = numOfOutputs;
		this.windowWidth  = windowWidth;
		this.windowHeight = windowHeight;
		this.MAXSCREENDISTANCE = Math.sqrt(windowWidth*windowWidth + windowHeight*windowHeight);
		this.scale = scale;
		
		brain = new NeuralNet(numOfInputs, numOfOutputs);
		
		this.leftTrack   = 0.16;
		this.rightTrack  = 0.16;
		this.closestMine = 0;
		this.score 		 = 0;
		
		// init tank's position
		position.x = random.nextFloat()*windowWidth;
		position.y = random.nextFloat()*windowHeight;
		
		// init tanks direction
		rotation = 0;//random.nextFloat()-.5*PI*2;
		// update direction vector of tank
		direction.x = -Math.sin(rotation);
		direction.y = Math.cos(rotation);
	}
		
	public void reset(){
		
		Random random = new Random();
		
		// ordered pair
		position.x = random.nextFloat()*windowWidth;
		position.y = random.nextFloat()*windowHeight;
		
//		score = 0;
		
		this.rotation = random.nextFloat()*PI*2;
		
	}
	
	public boolean update(ArrayList<Point2D.Double> mines, boolean training){
		
		double tankRotation;
		
		// inputs and outputs for the neural net
		ArrayList<Double> inputs  = new ArrayList<>();
		ArrayList<Double> outputs = new ArrayList<>();
		
		// vector to closest mine
		Point2D.Double vClosestMine = closest_mine(mines);
		// distance before move
		double distanceX = vClosestMine.x - position.x;
		double distanceY = vClosestMine.y - position.y;
		double distanceToMine = Math.sqrt(distanceX*distanceX + distanceY*distanceY);
		
		// normalize vector to closest mine 	???optimize???
		double vectorLength = Math.sqrt(vClosestMine.x*vClosestMine.x + vClosestMine.y*vClosestMine.y);
		vClosestMine.x /= vectorLength;
		vClosestMine.y /= vectorLength;
		
		// create inputs for neural net
		// vector to closest mine
		inputs.add(vClosestMine.x);
		inputs.add(vClosestMine.y);
		// direction tank is looking
//		inputs.add(direction.x);
//		inputs.add(direction.y);	
		
		Point2D.Double vPosition = new Point2D.Double(position.x, position.y);

		
		vectorLength = Math.sqrt(vPosition.x*vPosition.x + vPosition.y*vPosition.y);
		vPosition.x /= vectorLength;
		vPosition.y /= vectorLength;
/*		
		// position of tank w/r direction
		if(direction.x > 0 && direction.y > 0){
			vPosition.x /= vectorLength;
			vPosition.y /= vectorLength;
		}
		else if(direction.x > 0 && direction.y < 0){
			vPosition.x /=  vectorLength;
			vPosition.y /= -vectorLength;
		}
		else if(direction.x < 0 && direction.y > 0){
			vPosition.x /= -vectorLength;
			vPosition.y /=  vectorLength;
		}
		else{
			vPosition.x /= -vectorLength;
			vPosition.y /= -vectorLength;
		}
	
*/
		inputs.add(vPosition.x);
		inputs.add(vPosition.y);
//		inputs.add(direction.x);
//		inputs.add(direction.y);
		inputs.add(rotation);

		// send inputs to neural net and get it's outputs
		outputs = brain.update(inputs);
		// check size of outputs is correct
		if(outputs.size() < numOfOutputs){
			return false;		// incorrect number of outputs
		}
		
		leftTrack  = outputs.get(0);
		rightTrack = outputs.get(1);
		
		// determine magnitude of tank direction
		tankRotation = leftTrack - rightTrack;
		
		// clamp rotation magnitude
		tankRotation =  Math.max(-maxTurnRate, Math.min(maxTurnRate, tankRotation));
		
		// add rotation to tanks current angle
		rotation += tankRotation;
		
		// keep in range [-PI,PI]
		if(rotation > PI) {
			double diff = rotation - PI;
			rotation = -PI + diff;
		}
		else if(rotation < -PI) {
			double diff = rotation + PI;
			rotation = PI - diff;
		}
		
		// keep in range [0,2PI]
/*
		if (rotation < 0) {
			rotation = TWOPI - rotation;
		}
		else if(rotation > TWOPI) {
			rotation %= TWOPI;
		}
*/		
		// update direction vector of tank
//		direction.x = -Math.sin(rotation);
//		direction.y = Math.cos(rotation);
		// update direction vector of tank
		direction.x = Math.cos(rotation);
		direction.y = Math.sin(rotation);
//		System.out.println("looking X: " + direction.x + " looking Y: " + direction.y);
		
		// update tanks position
		position.x += (direction.x * speed);
		position.y += (direction.y * speed);
		
		// wrap around the window, vertically and horizontally
		if(position.x > windowWidth)   position.x = 0;
		if(position.x < 0)  			position.x = windowWidth;
		if(position.y > windowHeight)  position.y = 0;
		if(position.y < 0) 			position.y = windowHeight;
		
		// distance after move
		distanceX = vClosestMine.x - position.x;
		distanceY = vClosestMine.y - position.y;
		double newDistanceToMine = Math.sqrt(distanceX*distanceX + distanceY*distanceY);
		
		double errorRate;
		
		if(newDistanceToMine < distanceToMine){
			if(newDistanceToMine > 10) {
				errorRate = -newDistanceToMine;		// getting warmer, decrease error rate
			}
			else {
				errorRate = 0;
			}
		}
		else{
			errorRate = newDistanceToMine;		// getting colder, increase error rate
		}
		// normalize
		errorRate /= MAXSCREENDISTANCE;
		
		if(training){
		
//			if(trainingFreq == 0){
				brain.back_propagate(inputs, outputs, errorRate);
//				trainingFreq = 2;
//			}
//		--trainingFreq;
		
		}
		
		return true;
	}
	
	public void transform_world(ArrayList<Point2D.Double> sweeper){
		
		Matrix2D transformMatrix = new Matrix2D();
		
		//scale
		transformMatrix.scale(scale, scale);
		
		//rotate
		transformMatrix.rotate(rotation);
		
		//translate
		transformMatrix.translate(position.x, position.y);
		
		// transform vertices
		transformMatrix.transform(sweeper);
		
	}
	
	public Point2D.Double closest_mine(ArrayList<Point2D.Double> mines){
		
		double closestDistance = 99999;
		
		double distanceFromTank;
		double distanceX;
		double distanceY;
		
		for(Point2D.Double mine: mines){
		
			distanceX = position.x-mine.x;
			distanceY = position.y-mine.y;
			distanceFromTank = Math.sqrt(distanceX*distanceX+distanceY*distanceY); // distance formula
			
			// check if this mine is closer than previously ones
			if(distanceFromTank < closestDistance){
				closestDistance = distanceFromTank;
				// save vector to closest mine
				closestMineLocation.x = mine.x;
				closestMineLocation.y = mine.y;
				closestMine = mines.indexOf(mine);
			}
			
			
		}
		
		if(DEBUG){
			System.out.println("Closest Mine: " + closestMine);
		}
		
		return closestMineLocation;
			
	}
	
	public int mine_collision(ArrayList<Point2D.Double> mines, int mineSize){
		
		Point2D.Double mineLocation = closest_mine(mines);
		
		double distanceX = position.x - mineLocation.x;
		double distanceY = position.y - mineLocation.y;
		double distanceFromTank = Math.sqrt(distanceX*distanceX+distanceY*distanceY); // distance formula
		
		// check collision
		if( distanceFromTank < (mineSize)){
			mine_found();
			return closestMine;		// return index of object collided with
		}
		
		return -1;	// no collision
		
	}
	
	public void tank_collision(ArrayList<Tank> tanks, int tankSize) {

		double distanceX;
		double distanceY;
		double distanceFromTank;
		
		for(Tank tank: tanks){
			if(tank != this){
				distanceX = position.x - tank.position.x;
				distanceY = position.y - tank.position.y;
				distanceFromTank = Math.sqrt(distanceX*distanceX+distanceY*distanceY); // distance formula
				
				if(distanceFromTank < tankSize){
					Random random = new Random();
					// reset tanks direction due to collision
					rotation = random.nextFloat()*PI*2;
					// update direction vector of tank
					direction.x = -Math.sin(rotation);
					direction.y = Math.cos(rotation);
					
				}
			}
		}
	}
	
	private Point2D.Double curr_position(){
		return position;
	}
	
	private void mine_found(){
		++score;
	}
	
	private int score(){
		return score;
	}
	
	private void put_weights(ArrayList<Double> weights){
		brain.update_weights(weights);
	}
	
	private int num_of_weights(){
		return brain.total_weights();
	}

	
}
