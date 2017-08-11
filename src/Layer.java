import java.io.Serializable;
import java.util.ArrayList;

public class Layer implements Serializable{

	private boolean DEBUG = true;
	
	private final double PI = 3.14159265358979;
	private final int numNeurons;
	private final int numOfNNOutputs;
	private final double minGroundTruth;
	private final double maxGroundTruth;
	
	private ArrayList<Neuron> neurons = new ArrayList<>();
//	private ArrayList<Double> inputs = new ArrayList<>();
	
	Layer(){
		numNeurons = 0;
		numOfNNOutputs = 0;
		minGroundTruth = 0.0;
		maxGroundTruth = 0.0;
	}
	
	Layer(int numNeurons, int numInputs, double learningRate, int numOfNNOutputs, double minGroundTruth, double maxGroundTruth){
		
		this.numOfNNOutputs = numOfNNOutputs;
		this.minGroundTruth = minGroundTruth;
		this.maxGroundTruth = maxGroundTruth;
		
		this.numNeurons = numNeurons;
				
		for(int cnt = 0; cnt < numNeurons; ++cnt){
			Neuron newNeuron = new Neuron(numInputs, learningRate);
			neurons.add(newNeuron);
			
		}
		
	}
	
	public ArrayList<Double> get_weights(){
		
		ArrayList<Double> weights = new ArrayList<>();
		
		for(Neuron neuron: neurons){
			weights.addAll(neuron.get_weights());
		}
		
		return weights;
	}
	
	public void update_weights(ArrayList<Double> newWeights){
		
		for(Neuron neuron:neurons){
			neuron.update_weights(newWeights);
		}
		
	}
	
	public int num_weights(){
		
		int numWeights = 0;
		
		for(Neuron neuron:neurons){
			numWeights += neuron.num_weights();
		}
		
		return numWeights;
	}
	
	public void display(){
		int index = 1;
		
		for(Neuron neuron:neurons){
			System.out.println("Neuron: " + index++);
			neuron.display_weights();
		}
		
	}

	public ArrayList<Double> get_outputs(ArrayList<Double> inputs) {
		
		ArrayList<Double> outputs = new ArrayList<>();
//		this.inputs = inputs;	// save inputs to layer for back propagation
		
		for(Neuron neuron: neurons){
			outputs.add(neuron.get_outputs(inputs));
		}
		
		return outputs;
	}
	
	protected ArrayList<Double[]> back_propagate_output(ArrayList<Double> outputs, double errorRate, double groundTruth){
		
		ArrayList<Double[]> connections = new ArrayList<>();
		
		double[] delta_error = new double[numOfNNOutputs];
		
		// loss from distance
/*		if(outputs.get(0) > outputs.get(1)){
			if(errorRate > 0){
				delta_error[0] = -errorRate;
				delta_error[1] = errorRate;
			}
			else{
				delta_error[0] = errorRate;
				delta_error[1] = -errorRate;
			}
		}
		else{
			if(errorRate > 0){
				delta_error[0] = errorRate;
				delta_error[1] = -errorRate;
			}
			else{
				delta_error[0] = -errorRate;
				delta_error[1] = errorRate;
			}
		}
*/	
		// loss from direction
		// normalize ground truth
		groundTruth /= PI;
		if(groundTruth < 0){	// rotate clockwise
			delta_error[0] = delta_error_output(outputs.get(0), minGroundTruth);
			delta_error[1] = delta_error_output(outputs.get(1), groundTruth);
		}
		else{					// rotate counterclockwise
			delta_error[0] = delta_error_output(outputs.get(0), groundTruth);
			delta_error[1] = delta_error_output(outputs.get(1), minGroundTruth);
		}
	
			connections.add(neurons.get(0).back_propagate_output(delta_error[0]));	// update output 1, gets weights * delta error for neuron
			connections.add(neurons.get(1).back_propagate_output(delta_error[1]));	// update output 2, gets weights * delta error for neuron
				
				// debug - display error and weights change!
				if(DEBUG){
					//System.out.println("Left track: " + outputs.get(0) + " Right track: " + outputs.get(1) );
					System.out.println("error left track " + delta_error[0] + " error right track " + delta_error[1]);
					display();
				}

		return connections;
	}
	
	protected ArrayList<Double[]> back_propagate_hidden(ArrayList<Double> outputs, ArrayList<Double[]> outConnections){
		
		ArrayList<Double[]> connections = new ArrayList<>();
		
		double connectionError = 0.0;
		for(int cnt = 0; cnt < numNeurons; ++cnt){
				
			for(int cnt2 = 0; cnt2 < outConnections.size(); ++cnt2){
				connectionError += outConnections.get(cnt2)[cnt];
			}

			if(DEBUG){
//				System.out.println("Neuron: " + (cnt + 1) + " ");
			}
			
			connections.add(neurons.get(cnt).back_prop_hidden(connectionError));

		}
			
		return connections;
	}
	
	private double delta_error_output(double predicted, double actual){
		
		if(DEBUG){
			System.out.println("predicted: " + predicted + " actual: " + actual);
		}
		
//		return  actual - predicted;
				return predicted * (1 - predicted) * (actual - predicted);
//		return derivativeOfSigmoidAt(preSigmoidOut) * (actual-predicted);
	}
 
	//function S'(x) derivative of sigmoid at value x is the return
    public double derivativeOfSigmoidAt(double x){
        double derivative = (Math.pow(Math.E,x))/Math.pow((Math.pow(Math.E,x)+1),2);
        return derivative;
    }

}
