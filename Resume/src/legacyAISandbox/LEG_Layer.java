package legacyAISandbox;

public class LEG_Layer {
	// Parameters:
	LEG_Node[] nodes;

	// Initialization
	LEG_Layer(LEG_Node[] nodes) {
		// Doing some weird cloning stuff to make sure there are no references to
		// pre-existing variables
		LEG_Node[] newNodes = new LEG_Node[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			newNodes[i] = (LEG_Node) nodes[i].clone();
		}
		this.nodes = newNodes;
	}

	// Calculating the value of each node within nodes
	public void calculateValues(LEG_Layer previousLayer) {
		// A variable that gets passed through to the node who's value is being
		// calculated
		double[] passThrough = new double[previousLayer.nodes.length];

		// Formats passThrough with the values of the previous layer's node values
		for (int i = 0; i < previousLayer.nodes.length; i++) {
			passThrough[i] = previousLayer.nodes[i].value;
		}

		// For every node in nodes, calculate its value using passThrough
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].calculateValue(passThrough);
		}
	}

	// Returns a clone of the Layer
	public Object clone() {
		return new LEG_Layer(nodes);
	}
}