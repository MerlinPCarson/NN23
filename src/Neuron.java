import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Neuron implements Serializable{

	private boolean DEBUG = false;
	
	private final int numInputs;
	private final double learningRate;
	private ArrayList<Double> weights = new ArrayList<>();
	private double bias;
	
	private ArrayList<Double> inputs  = new ArrayList<>();
	
	private double output = 0.0;
	private double preSigmoidOutput = 0.0;
	
	
	Neuron(){
		numInputs = 0;
		learningRate = 0.0;
	}

	Neuron(int numInputs, double learningRate){
		
		Random random = new Random();
		this.numInputs = numInputs;
		this.learningRate = learningRate;
		
		double low = -1.0;
		double high = 1.0;
		double randomDbl;  
		
		// random init values for input weights
		for(int cnt = 0; cnt < numInputs; ++cnt){
//			double weight = Math.tanh(Math.toRadians(random.nextDouble()-random.nextDouble())); // tanh creates a num [-1,1] 
			randomDbl = ThreadLocalRandom.current().nextDouble(low,high);
			double weight = Math.round(randomDbl * 100.0)/100.0;
			//			double weight = Sigmoid(random.nextDouble());
			weights.add(weight);
		}
		
		bias = Math.tanh(Math.toRadians(random.nextDouble()-random.nextDouble())); // tanh creates a num [-1,1] 
	}
	
	public ArrayList<Double> get_weights(){
		
		ArrayList<Double> weights = new ArrayList<>();
		for(Double weight: this.weights){
			weights.add(weight);
		}
		
		return weights;
	}
	
	public void update_weights(ArrayList<Double> newWeights){
		
		// takes weight off front of newWeights array, then removes it from the list
		for(Double weight: this.weights){
			weight = newWeights.get(0);
			newWeights.remove(0);
		}
		
	}
	
	public int num_weights(){
		return numInputs;	// add one for the bias at end of weights array list
	}

	public void display_weights(){
		double totalWeights = 0.0;
		
		for(Double weight: weights){
			totalWeights += weight;
			System.out.println(weight);
		}
		
		// debug
		System.out.println("weight total: " + totalWeights);
	}

	public Double get_outputs(ArrayList<Double> inputs) {

		this.inputs = inputs;
		
		// add up the product of each input and it's corresponding weight
		for(int cnt = 0; cnt < numInputs; ++cnt){
			output += weights.get(cnt) * inputs.get(cnt);
		}
		
		preSigmoidOutput = output;
		output = Sigmoid(output + bias);
		// Sigmoid function returns a value between 0.0 and 1.0
		return output;	// subtract bias, last element of weights array list (i.e. numInputs)
	}
	
	protected Double[] back_propagate_output(double deltaError){
		
		Double[] connections = new Double[numInputs];
		Double newWeight;
		
		int index = 0;
		for(Double weight: weights){
			newWeight = weight + (learningRate * deltaError * inputs.get(index));
			weights.set(index, newWeight);
			connections[index] = weight * deltaError;
			++index;
		}
		
		bias -= learningRate * deltaError;
		
		return connections; 
	}
	
	protected Double[] back_prop_hidden(double connectionError){
		
		Double[] connections = new Double[numInputs];
		int index = 0;
		double deltaError = delta_error_hidden(output, connectionError);
		double newWeight;
		
		for(Double weight: weights){
//			System.out.println("new weight = " + weight + " + " + learningRate + " * " + deltaError + " * " + inputs.get(index) );
			newWeight = weight + (learningRate * deltaError * inputs.get(index));
//			System.out.println("New weight: " + newWeight);
			weights.set(index, newWeight);
			connections[index] = weight * deltaError;
			++index;
		}
		
		// debug
		if(DEBUG){
			System.out.println("Error: " + connectionError);
			display_weights();
		}
		
		return connections;
	}
	
	private double Sigmoid(double input){
		return ( 1 / ( 1 + Math.exp(-input)));
	}
	
	
	private double delta_error_hidden(Double predicted, double connectionError){

		return predicted * (1 - predicted) * connectionError;
	}

	public double getPreSigmoidOutput(){
		return preSigmoidOutput;
	}
}

