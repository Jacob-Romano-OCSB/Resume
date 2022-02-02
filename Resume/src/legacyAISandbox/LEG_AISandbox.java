package legacyAISandbox;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Author: Jacob Romano Date: 2021-10-29 Course: ICS3U AISandbox.java Purpose:
 * To create a tool to experiment with creating and growing artificial
 * intelligences
 */

public class LEG_AISandbox extends Application {
	// JavaFX utility variables:
	GameTimer gameTimer;
	GridPane root;
	Scene scene;

	// JavaFX UI variables:
	// Text fields:
	TextField txtLayerCount;
	TextField txtNodeCount;

	// Buttons:
	Button btnCompileBrain;
	Button btnTrainOne;
	Button btnTrainTen;
	Button btnTrainHundred;
	Button btnSelectMOne;
	Button btnSelectMTwo;

	// Labels:
	Label lblSetup;
	Label lblTrainInfo;
	Label lblTraining;
	Label lblLayerCount;
	Label lblNodeCount;
	Label lblGeneration;
	Label lblFitness;
	Label lblCompileError;
	Label lblTutorial;
	Label lblTutorialLink;

	// The graph and it's other... things:
	WritableImage graph;
	ImageView imageView;
	PixelWriter pixelWriter;
	Group imageGroup;
	Circle[] trainingDataMarkers = new Circle[0];

	// Misc variables:
	LEG_Brain brain;
	boolean graphNeedsUpdating = true;
	int selectedMarker = MARKER_ONE_VALUE;
	int trainingRoundsQueued = 0;
	double[][] trainingData = new double[0][];

	// UI and screen setup constants:
	static final int SCREEN_WIDTH = 900;
	static final int SCREEN_HEIGHT = 600;
	static final int GRAPH_WIDTH = 400;
	static final int GRAPH_HEIGHT = 400;
	static final int GRID_WIDTH = 100;
	static final int GRID_HEIGHT = 20;
	static final int GRID_COLUMN_COUNT = 7;
	static final int GRID_ROW_COUNT = 27;

	// Marker constants:
	static final Color MARKER_ONE_COLOUR = Color.BLUE;
	static final Color MARKER_ONE_GRAPH_COLOUR = Color.valueOf("#6058d6");
	static final int MARKER_ONE_VALUE = 0;
	static final Color MARKER_TWO_COLOUR = Color.RED;
	static final Color MARKER_TWO_GRAPH_COLOUR = Color.valueOf("#db4242");
	static final int MARKER_TWO_VALUE = 1;
	static final double MARKER_RADIUS = 2;

	// Whatever the heck JavFX magic this does:
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {
		// Initializing the graph
		graph = new WritableImage(GRAPH_WIDTH, GRAPH_HEIGHT);
		pixelWriter = graph.getPixelWriter();
		imageView = new ImageView(graph);

		// Initializing the root GridPane
		root = new GridPane();
		root.setAlignment(Pos.CENTER);
		for (int i = 0; i < GRID_COLUMN_COUNT; i++) {
			root.getColumnConstraints().add(new ColumnConstraints(GRID_WIDTH));
		}
		for (int i = 0; i < GRID_ROW_COUNT; i++) {
			root.getRowConstraints().add(new RowConstraints(GRID_HEIGHT));
		}
		root.setBackground(
				new Background(new BackgroundFill(Paint.valueOf("888888"), CornerRadii.EMPTY, Insets.EMPTY)));

		// Adding the UI elements to the root
		// Lables:
		lblSetup = new Label("Setup:");
		lblSetup.setFont(Font.font(40));
		root.add(lblSetup, 0, 20, 2, 2); // Column 0, row 20

		lblLayerCount = new Label("How many hidden layers the\nnetwork will have");
		lblLayerCount.setFont(Font.font(15));
		root.add(lblLayerCount, 0, 25, 2, 2); // Column 0, row 25

		lblNodeCount = new Label("How many nodes each\nlayer will have");
		lblNodeCount.setFont(Font.font(15));
		root.add(lblNodeCount, 3, 25, 2, 2); // Column 3, row 25

		lblTraining = new Label("Training:");
		lblTraining.setFont(Font.font(40));
		root.add(lblTraining, 5, 0, 2, 2); // Column 5, row 0

		lblTrainInfo = new Label("Select a marker and place it\non the chart");
		lblTrainInfo.setFont(Font.font(15));
		root.add(lblTrainInfo, 5, 5, 2, 2); // Column 5, row 5

		lblGeneration = new Label("Gen: 0");
		lblGeneration.setFont(Font.font(15));
		root.add(lblGeneration, 5, 8); // Column 5, row 8

		lblFitness = new Label("Fitness: 0%");
		lblFitness.setFont(Font.font(15));
		root.add(lblFitness, 5, 9); // Column 5, row 9

		lblCompileError = new Label("Compile Error");
		lblCompileError.setFont(Font.font(15));
		lblCompileError.setTextFill(Paint.valueOf("FF0000"));
		lblCompileError.setVisible(false);
		root.add(lblCompileError, 6, 25); // Column 6, row 25

		lblTutorial = new Label("Tutorial:");
		lblTutorial.setFont(Font.font(40));
		root.add(lblTutorial, 0, 0, 2, 2); // Column 0, row 0

		lblTutorialLink = new Label("https://youtu.be/TEzur-8APiw");
		lblTutorialLink.setFont(Font.font(15));
		root.add(lblTutorialLink, 0, 2, 2, 1); // Column 0, row 2

		// Text fields:
		txtLayerCount = new TextField();
		root.add(txtLayerCount, 0, 23, 2, 1); // Column 0, row 23

		txtNodeCount = new TextField();
		root.add(txtNodeCount, 3, 23, 2, 1); // Column 3, row 23

		// Buttons:
		btnCompileBrain = new Button("Compile brain");
		btnCompileBrain.setMinWidth(GRID_WIDTH);
		btnCompileBrain.setOnAction(event -> compileBrain());
		root.add(btnCompileBrain, 6, 23); // Column 6, row 23

		btnSelectMOne = new Button("Marker 1");
		btnSelectMOne.setMinWidth(GRID_WIDTH);
		btnSelectMOne.setOnAction(event -> selectMarkerOne());
		btnSelectMOne.setTextFill(MARKER_ONE_COLOUR);
		root.add(btnSelectMOne, 5, 3); // Column 5, row 3

		btnSelectMTwo = new Button("Marker 2");
		btnSelectMTwo.setMinWidth(GRID_WIDTH);
		btnSelectMTwo.setOnAction(event -> selectMarkerTwo());
		btnSelectMTwo.setTextFill(MARKER_TWO_COLOUR);
		root.add(btnSelectMTwo, 6, 3); // Column 6, row 3

		btnTrainOne = new Button("Train AI 1x");
		btnTrainOne.setMinWidth(GRID_WIDTH);
		btnTrainOne.setOnAction(event -> queueTrain(1));
		root.add(btnTrainOne, 6, 8); // Column 6, row 8

		btnTrainTen = new Button("Train AI 10x");
		btnTrainTen.setMinWidth(GRID_WIDTH);
		btnTrainTen.setOnAction(event -> queueTrain(10));
		root.add(btnTrainTen, 6, 10); // Column 6, row 10

		btnTrainHundred = new Button("Train AI 100x");
		btnTrainHundred.setMinWidth(GRID_WIDTH);
		btnTrainHundred.setOnAction(event -> queueTrain(100));
		root.add(btnTrainHundred, 6, 12); // Column 6, row 12

		// Graph:
		imageView.setOnMousePressed(event -> placeMarker(event));
		imageGroup = new Group(imageView);
		root.add(imageGroup, 0, 0, 4, 20); // Top left corner of the screen

		// Setting up the scene
		scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

		// Initializing the stage
		stage.setTitle("AISandbox (LEGACY)");
		stage.setScene(scene);
		stage.show();

		gameTimer = new GameTimer();
		gameTimer.start();
	}

	// The main loop:
	class GameTimer extends AnimationTimer {
		@Override
		public void handle(long now) {
			// If any of this fails, just don't scream at me, ok?
			try {
				// Training the brain only once each frame to make it look better
				if (trainingRoundsQueued > 0) {
					train();
					trainingRoundsQueued -= 1;
				}
				// Updates the graph if it needs to be updated
				if (graphNeedsUpdating) {
					updateGraph();
					graphNeedsUpdating = false;
				}
			} catch (Exception e) {
				// Hush my console child, no need to cry exception
			}
		}
	}

	// Creates the graph using brain's decisions on each pixel location
	public void updateGraph() {
		int decision = 0; // holds what the AI has decided the pixel should be

		// for every single pixel at location (i, j):
		for (int i = 0; i < GRAPH_WIDTH; i++) {
			for (int j = 0; j < GRAPH_HEIGHT; j++) {

				// Decides what the pixel should be from its coordinates
				decision = brain.evaluate(new double[] { ((double) i / GRAPH_WIDTH) * 2 - 1, // Formats from pixel space
				                                                                             // to between -1 and 1
						((double) j / GRAPH_HEIGHT) * 2 - 1 });

				// If decision is equal to MARKER_ONE_VALUE, set the pixel to MARKER_ONE_COLOUR
				// Continue pattern for marker two
				if (decision == MARKER_ONE_VALUE) {
					pixelWriter.setColor(i, j, MARKER_ONE_GRAPH_COLOUR);
				} else if (decision == MARKER_TWO_VALUE) {
					pixelWriter.setColor(i, j, MARKER_TWO_GRAPH_COLOUR);
				} else { // If it does not fit a marker value, set it to black
					pixelWriter.setColor(i, j, Color.BLACK);
				}
			}
		}
	}

	// Called when btnCompileBrain is pushed
	private void compileBrain() {
		// Tries to compile, if it gets an error, it throws a compile error to the user
		try {
			// Input layer size is always 2 because there are only 2 axies on the graph
			int inputLayerSize = 2;

			// Sets the layer count to the value of txtLayerCount
			int layerCount = Integer.parseInt(txtLayerCount.getText());

			// Sets the layer count to be the value of txtNodeCount if the layerCount is not
			// zero
			int nodeCount = Integer.parseInt(txtNodeCount.getText());
			;
			// If layerCount is not zero
			if (layerCount != 0) {
				// if nodeCount is zero or less, set nodeCount to be one and update the text
				// field
				if (nodeCount <= 0) {
					nodeCount = 1;
					txtNodeCount.setText("1");
				}
			} else { // Otherwise set nodeCount to zero since there are no hidden layers
				nodeCount = 0;
			}
			// Output layer size is equal to how many marker types have been programmed in,
			// 2 for now
			int outputLayerSize = 2;

			// Make brain to be a new Brain with the desired parameters
			brain = new LEG_Brain(inputLayerSize, layerCount, nodeCount, outputLayerSize);

			// Requests the graph to be updated:
			graphNeedsUpdating = true;

			// Updates the info texts:
			lblGeneration.setText("Gen: 0");
			lblFitness.setText("Fitness: 0%");

			// Sets the compile error to be invisible in case the last compile attempt ended
			// in an error
			lblCompileError.setVisible(false);
		} catch (Exception e) {
			lblCompileError.setVisible(true);
		}
	}

	// Called when btnSelectMOne is pushed
	private void selectMarkerOne() {
		selectedMarker = MARKER_ONE_VALUE;
	}

	// Called when btnSelectMTwo is pushed
	private void selectMarkerTwo() {
		selectedMarker = MARKER_TWO_VALUE;
	}

	// Called when the graph is clicked
	private void placeMarker(MouseEvent clickEvent) {
		// Finding the coordinate of the click relative to the graph:
		double xOnGraph = clickEvent.getSceneX() - imageGroup.getLayoutX();
		double yOnGraph = clickEvent.getSceneY() - imageGroup.getLayoutY();

		// Making one more spot in trainingData:
		double[][] tmpData = trainingData.clone();
		trainingData = new double[trainingData.length + 1][];
		for (int i = 0; i < tmpData.length; i++) {
			trainingData[i] = tmpData[i].clone();
		}

		// Appending the new training data:
		trainingData[trainingData.length - 1] = new double[] { xOnGraph, yOnGraph, (double) selectedMarker };

		// Making one more spot in trainingDataMarkers:
		Circle[] tmpDataMarker = trainingDataMarkers.clone();
		trainingDataMarkers = new Circle[trainingDataMarkers.length + 1];
		for (int i = 0; i < tmpDataMarker.length; i++) {
			trainingDataMarkers[i] = tmpDataMarker[i];
		}

		// Appending the new training data marker:
		double[] circleData = trainingData[trainingData.length - 1];
		trainingDataMarkers[trainingDataMarkers.length - 1] = new Circle(circleData[0], circleData[1], MARKER_RADIUS);

		// Finding the colour of the marker:
		if ((int) (circleData[2] + 0.1) == MARKER_ONE_VALUE)
			trainingDataMarkers[trainingDataMarkers.length - 1].setFill(MARKER_ONE_COLOUR);
		else if ((int) (circleData[2] + 0.1) == MARKER_TWO_VALUE)
			trainingDataMarkers[trainingDataMarkers.length - 1].setFill(MARKER_TWO_COLOUR);
		else
			trainingDataMarkers[trainingDataMarkers.length - 1].setFill(Color.BLACK);

		// Putting it in the imageGroup
		imageGroup.getChildren().add(trainingDataMarkers[trainingDataMarkers.length - 1]);
	}

	// Called during a GameTimer cycle if trainingRoundsQueued is more than one
	private void train() {
		// Formating trainingData to be in the correct format
		double[][] passThrough = new double[trainingData.length][];
		for (int i = 0; i < trainingData.length; i++) {
			passThrough[i] = trainingData[i].clone();
		}

		// Converting from pixel space to be between -1 and 1
		for (int i = 0; i < passThrough.length; i++) {
			passThrough[i][0] = passThrough[i][0] / GRAPH_WIDTH * 2 - 1;
			passThrough[i][1] = passThrough[i][1] / GRAPH_HEIGHT * 2 - 1;
		}
		// The bigboy method:
		brain.train(passThrough);

		// Updating the info texts:
		lblGeneration.setText("Gen: " + brain.roundsDone);
		lblFitness.setText("Fitness: " + ((int) (brain.master.fitness * 100)) + "%");

		// Sets a flag for the graph to be updated in GameTimer
		graphNeedsUpdating = true;
	}

	// Called when a btnTrain is pushed
	private void queueTrain(int numberOfTimes) {
		trainingRoundsQueued += numberOfTimes;
	}
}