package legacyAISandbox;

public class LEG_Network {
	// Parameters:
	LEG_Layer inputLayer;
	LEG_Layer[] hiddenLayers;
	LEG_Layer outputLayer;
	double fitness;
	
	// Initialization:
	LEG_Network(LEG_Layer inputLayer, LEG_Layer[] hiddenLayers, LEG_Layer outputLayer) {
		// Doing some weird cloning stuff to make sure there are no references to pre-existing variables
		this.inputLayer = (LEG_Layer) inputLayer.clone();
		
		LEG_Layer[] newHiddenLayers = new LEG_Layer[hiddenLayers.length];
		for (int i = 0; i < hiddenLayers.length; i++)
			newHiddenLayers[i] = (LEG_Layer) hiddenLayers[i].clone();
		this.hiddenLayers = newHiddenLayers;
		
		this.outputLayer = (LEG_Layer) outputLayer.clone();
		
		this.fitness = 0;
	}
	
	public int evaluate(double[] dataInput) {
		// Sets the input layer's nodes' values to be equal to dataInput
		for (int i = 0; i < inputLayer.nodes.length; i ++) {
			inputLayer.nodes[i].value = dataInput[i];
		}
		
		// if there is anything in hiddenLayers
		if (hiddenLayers.length != 0) {
			// Calculate the values of the first hidden layer using inputLayer
			hiddenLayers[0].calculateValues(inputLayer);
						
			// Calculate each other layer's values using the previous layer
			// Will not occur if there is only one hidden layer
			for (int i = 1; i < hiddenLayers.length; i++) {
				hiddenLayers[i].calculateValues(hiddenLayers[i - 1]);
			}
			
			// Calculate the values of outputLayer using the last hidden layer
			outputLayer.calculateValues(hiddenLayers[hiddenLayers.length - 1]);
		} else {
			// Otherwise, calculate the values of outputLayer using inputLayer
			outputLayer.calculateValues(inputLayer);
		}
		
		// Finding the decision:
		
		int maxIndex = 0;
		double maxValue = 0;
		double previousMaxValue = 0;
		
		// For every node in outputLayer
		for (int i = 0; i < outputLayer.nodes.length; i++) {
			// Find the max between the max and the node currently being analized
			maxValue = Math.max(maxValue, outputLayer.nodes[i].value);
			// If the value is different from previousMax then set the index to that of the node being analized
			if (maxValue != previousMaxValue) {
				maxIndex = i;
			}
			
			// Update previousMaxValue before moving onto the next
			previousMaxValue = maxValue;
		}
		return maxIndex;
	}
	
	// Returns a clone of the Network
	public Object clone() {
		return new LEG_Network(inputLayer, hiddenLayers, outputLayer);
	}
}