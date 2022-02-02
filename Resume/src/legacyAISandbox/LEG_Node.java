package legacyAISandbox;

public class LEG_Node {
	// Parameters:
	double value;
	double[] weights;
	double bias;
	
	// New initialization
	LEG_Node(int weightCount) {
		weights = new double[weightCount];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}
		bias = 0;
	}
	
	// Duplication initialization
	LEG_Node(double value, double[] weights, double bias) {
		this.value = value;
		this.weights = weights.clone();
		this.bias = bias;
	}
	
	// Calculating the value of the Node
	public void calculateValue(double[] inputs) {
		// Adds the value of each input value with the respective weight value
		double sum = 0;
		for (int i = 0; i < inputs.length; i++) {
			sum += inputs[i] * weights[i];
		}
		
		// Adds the bias to the sum, takes the sigmoid of that, and saves it to value
		value = sigmoid(sum + bias);
	}
	
	// Calculating the sigmoid of a double
	public static double sigmoid(double input) {
		return (1 / (1 + Math.exp(-input)));
	}
	
	// Returns a clone of the Node
	public Object clone() {
		return new LEG_Node(value, weights, bias);
	}
}