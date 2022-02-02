package legacyAISandbox;

public class LEG_Brain {
	// Parameters:
	LEG_Network master;
	LEG_Network[] students;
	int roundsDone;

	private static final int STUDENT_COUNT = 1000;

	// Random initialization:
	LEG_Brain(int inputLayerSize, int layerCount, int nodeCount, int outputLayerSize) {
		// This will hold a temporary array of unique nodes generated with the
		// appropriate size
		LEG_Node[] tmpNodeArray;

		// Making the input layer:
		LEG_Layer tmpInputLayer;
		tmpNodeArray = new LEG_Node[inputLayerSize];
		// For as many nodes as there should be in the input layer, make a new node with
		// zero inputs and add it to tmpNodeArray
		for (int i = 0; i < inputLayerSize; i++) {
			tmpNodeArray[i] = new LEG_Node(0);
		}
		// Make tmpInputLayer a Layer based on tmpNodeArray
		tmpInputLayer = new LEG_Layer(tmpNodeArray);

		// Making the hidden layers:
		LEG_Layer[] tmpHiddenLayers = new LEG_Layer[layerCount];

		// For every single layer that needs to be generated:
		for (int i = 0; i < layerCount; i++) {
			tmpNodeArray = new LEG_Node[nodeCount];

			// For as many nodes as there should be in each hidden layer, make a new node
			for (int j = 0; j < nodeCount; j++) {
				// If it's the first node, make a node with inputLayerSize
				if (i == 0)
					tmpNodeArray[j] = new LEG_Node(inputLayerSize);
				// Otherwise, use nodeCount
				else
					tmpNodeArray[j] = new LEG_Node(nodeCount);
			}
			// Add this to the tmpHiddenLayers array as a new Layer
			tmpHiddenLayers[i] = new LEG_Layer(tmpNodeArray);
		}

		// Making the output layer
		LEG_Layer tmpOutputLayer;
		tmpNodeArray = new LEG_Node[outputLayerSize];

		// For as many nodes there should be in the output layer
		for (int i = 0; i < outputLayerSize; i++) {
			// If there were no hidden layers, make nodes using inputLayerSize
			if (layerCount == 0)
				tmpNodeArray[i] = new LEG_Node(inputLayerSize);
			// Otherwise, make new nodes using nodeCount
			else
				tmpNodeArray[i] = new LEG_Node(nodeCount);
		}
		// Make the output layer using tmpNodeArray
		tmpOutputLayer = new LEG_Layer(tmpNodeArray);

		// Setting the master to the new network
		master = new LEG_Network(tmpInputLayer, tmpHiddenLayers, tmpOutputLayer);
	}

	// Clone initialization:
	LEG_Brain(LEG_Network master) {
		this.master = (LEG_Network) master.clone();
	}

	// Training the brain
	public void train(double[][] trainingData) {
		int answer; // For what the student thinks is the right answer
		double correctTests; // For how many tests the student got right

		// Clears the previous students
		students = new LEG_Network[STUDENT_COUNT];
		// For every single student
		for (int i = 0; i < students.length; i++) {
			// Make a new Network based on the master and mutate it
			students[i] = (LEG_Network) master.clone();
			mutate(students[i], i + 1);

			// Go through every single test in training data
			correctTests = 0;
			for (int j = 0; j < trainingData.length; j++) {
				// Feed each data point into the Network using evaluate
				answer = students[i].evaluate(trainingData[j]);

				// If answer is equal to the correct answer stored in the training data add one
				// to correctTests
				if (answer == (int) ((trainingData[j][trainingData[j].length - 1]) + 0.1)) {
					correctTests += 1;
				}
			}
			// Calculate the average and store it in the fitness of the student
			students[i].fitness = correctTests / trainingData.length;
		}
		int maxIndex = -1; // The index of the highest scoring student
		double maxValue = -1; // The score of the highest scoring student

		// For every single student
		for (int i = 0; i < students.length; i++) {
			// If maxValue is less than or equal to the given student's fitness, make it the
			// new max
			// This is done so that if most have the same score, the one with the most
			// extreme changes will be chosen
			if (maxValue <= students[i].fitness) {
				maxValue = students[i].fitness;
				maxIndex = i;
			}
		}
		// Making the new master
		master = (LEG_Network) students[maxIndex].clone();
		master.fitness = students[maxIndex].fitness;
		roundsDone++;
	}

	// Evaluating using master
	public int evaluate(double[] dataInput) {
		return master.evaluate(dataInput);
	}

	// Takes the subject Network passed through and randomly mutates some parameters
	private void mutate(LEG_Network subject, int mutationFactor) {
		// how many layers the subject has - the amount of hidden layers plus one for
		// the output layer
		int numberOfLayers = subject.hiddenLayers.length + 1;

		int layerDecider; // For deciding what layer to mutate

		LEG_Layer selectedLayer; // A pointer variable that holds what layer is being modified

		int nodeDecider; // For deciding what node to mutate

		int parameterDecider; // For deciding what weight/bias to mutate

		for (int i = 0; i < (int) (mutationFactor / 100 + 1); i++) {
			// Deciding what layer to modify (not including the input layer because that has
			// no weights/bias)
			layerDecider = (int) (Math.random() * numberOfLayers);

			// Assigning selectedLayer
			if (layerDecider != numberOfLayers - 1)
				selectedLayer = subject.hiddenLayers[layerDecider]; // A hidden layer
			else
				selectedLayer = subject.outputLayer; // Output layer

			// Deciding what node to change
			nodeDecider = (int) (Math.random() * selectedLayer.nodes.length);

			// Deciding what parameter to change:
			parameterDecider = (int) (Math.random() * (selectedLayer.nodes[nodeDecider].weights.length + 1));

			// If nodeDecider chose the last possible value, it selected the bias
			if (parameterDecider == selectedLayer.nodes[nodeDecider].weights.length)
				selectedLayer.nodes[nodeDecider].bias += (Math.random() * 2 - 1) / 10;
			// Otherwise, it chose a weight
			else
				selectedLayer.nodes[nodeDecider].weights[parameterDecider] += (Math.random() * 2 - 1) / 10;
		}
	}
}