import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

public class NeuralNet implements Serializable{

	private boolean DEBUG = false;
	private boolean DEBUG2 = false;
	
	private final double PI = 3.14159265358979;
	private final double TWOPI = 2 * PI;
	private final double PI_HALVES = PI/2;
	private final double THREE_PI_HALVES = 3*PI_HALVES;
	private final int numInputs;
	private final int numOutputs;
	private final int numNeuronsPerLayer = 6;
	private final int numHiddenLayers = 2;
	
	// consts for layers
	private final int numOfNNOutputs = 2;
	private final double minGroundTruth = 0.0;
	private final double maxGroundTruth = 1.0;
	
	// consts for neurons
	private final double learningRate = .75;
	
	ArrayList<Layer> layers = new ArrayList<>();
	
	NeuralNet(int numOfInputs, int numOfOutputs){
		
		numInputs = numOfInputs;
		numOutputs = numOfOutputs;
		
		// creates first hidden layer
		Layer firstLayer = new Layer(numNeuronsPerLayer, numInputs, learningRate, numOfNNOutputs, minGroundTruth, maxGroundTruth);	// takes actual input
		layers.add(firstLayer);
		
		// creates any additional layers, if they exist
		for(int cnt = 1; cnt < numHiddenLayers; ++cnt){
			Layer newLayer = new Layer(numNeuronsPerLayer, numNeuronsPerLayer, learningRate, numOfNNOutputs, minGroundTruth, maxGroundTruth);	// takes input from previous hidden layer
			layers.add(newLayer);
		}

		// creates output layer
		Layer outputLayer = new Layer(numOutputs, numNeuronsPerLayer, learningRate, numOfNNOutputs, minGroundTruth, maxGroundTruth);
		layers.add(outputLayer);
		
// debug: display Neural Net!!!
		if(DEBUG){
			int layerNum = 1;
			for(Layer layer:layers){
				if(layerNum != layers.size()){
					System.out.println("Hidden Layer:  " + layerNum++);
				}
				else{	// last layer is the output layer
					System.out.println("Output Layer: ");
				}
				layer.display();
			}
		}

	}
	
	public ArrayList<Double> get_weights(){
		
		ArrayList<Double> allWeights = new ArrayList<>();
		
		for(Layer layer: layers){
			allWeights.addAll(layer.get_weights());
		}
		
		return allWeights;
	}
	
	public int total_weights(){
		int totalWeights = 0;
		
		for(Layer layer: layers){
			totalWeights += layer.num_weights();
		}
		
		return totalWeights;
	}
	
	public void update_weights(ArrayList<Double> newWeights){
		
		for(Layer layer: layers){
			layer.update_weights(newWeights);
		}
		
	}
	
	public ArrayList<Double> update(ArrayList<Double> inputs){
		
		// resultant outputs for each layer
		ArrayList<Double> outputs = new ArrayList<Double>();
		
		// check inputs array is the correct size
		if(inputs.size() != numInputs){
			return outputs;	// return empty list
		}
		
		// get the outputs from each layer
		for(Layer layer: layers){
			outputs = layer.get_outputs(inputs);
			inputs = outputs;	// use the outputs of previous layer for input of next layer
		}

		
		return outputs;
	}
	
	public void back_propagate(ArrayList<Double> inputs, ArrayList<Double> outputs, double errorRate){

		double groundTruth = ground_truth(inputs);

		
//		System.out.println("current rotation: " + currRotation + " desired rotation: " + groundTruth);
		ArrayList<Double[]> prevLayerConnections = new ArrayList<>();
				
// BACKPRAPOGATION: iterator through layers in reverse order to update weights
		int layerNum = numHiddenLayers;	// # of hidden layers + output layer
		ListIterator<Layer> listIterator = layers.listIterator(layers.size());
		
		if(DEBUG){
			System.out.println("updating OUTPUT layer");
		}
		
		prevLayerConnections = listIterator.previous().back_propagate_output(outputs, errorRate, groundTruth);
		while(listIterator.hasPrevious()){
			
			if(DEBUG){
				System.out.println("updating HIDDEN layer: " + layerNum--);
			}
			
			prevLayerConnections = listIterator.previous().back_propagate_hidden(outputs, prevLayerConnections);
			
		}
	}
	
	private double ground_truth(ArrayList<Double> inputs){
		
		
		double closestMineX = inputs.get(0);
		double closestMineY = inputs.get(1);
		double tankX = inputs.get(2);
		double tankY = inputs.get(3);
		double tankAngle = inputs.get(4);
//		double lookingX = inputs.get(4);
//		double lookingY = inputs.get(5);
		double desiredRotation = 0.0;
		Point2D.Double rotation = new Point2D.Double();
		double angle;
		
		rotation.x = closestMineX - tankX;
		rotation.y = closestMineY - tankY;
		
		angle = Math.atan2(rotation.y, rotation.x);
		
		desiredRotation = angle - tankAngle;
		
		
		// keep in range [-PI,PI]
		if(desiredRotation > PI) {
			desiredRotation -= TWOPI;
		}
		else if(desiredRotation < -PI) {
			desiredRotation += TWOPI;
		}
		
		if(Math.abs(desiredRotation) > Math.abs(TWOPI - desiredRotation)) {
			desiredRotation = TWOPI - desiredRotation;
		}
		
		
		
	/*	
		
		desiredRotation = Math.atan2(lookingY, lookingX ) - Math.atan2(closestMineY, closestMineX);
	*/	
		if(DEBUG2){
	//		System.out.println("Position: " + lookingX + "," + lookingY);
		System.out.println("tank angle: " + tankAngle + " difference angle: " + angle);
		System.out.println("diffX: " + rotation.x + " diffY: " + rotation.y );
		System.out.println("desired rotation: " + desiredRotation);
		}
	
		return desiredRotation;
		
		// difference between current rotation and desired rotation
//		return currRotation - desiredRotation;	// this will be used to determine ground truth
			
	}
	
}
