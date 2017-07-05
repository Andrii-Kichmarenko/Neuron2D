
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {
	private Scene scene;
	private AnchorPane anchorPane;
	private Plot plot;
	private Axes axes;
	private double w1;
	private double w2;
	private double threshold;
	private ArrayList<Circle> pointsList;

	@FXML
	private Pane plotPane;

	@FXML
	private Pane listenerPane;

	@FXML
	private Button drawButton;

	@FXML
	private TextField w1TextField;

	@FXML
	private TextField w2TextField;

	@FXML
	private TextField thresholdTextField;

	@FXML
	private ComboBox activFuncChoiseBox;

	private ToggleGroup radioButtonGroup;
	@FXML
	private RadioButton topRadioButton;

	@FXML
	private RadioButton buttomRadioButton;

	@Override
	public void start(Stage stage) {
		try {
			anchorPane = (AnchorPane) FXMLLoader.load(Main.class.getResource("./resources/file.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		scene = new Scene(anchorPane);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	public void initialize() {
		// making group of RadioButton
		topRadioButton.setContentDisplay(ContentDisplay.TOP);
		radioButtonGroup = new ToggleGroup();
		topRadioButton.setToggleGroup(radioButtonGroup);
		buttomRadioButton.setToggleGroup(radioButtonGroup);
		topRadioButton.setSelected(true);
		// fill text fields with random values
		w1TextField.setText(String.valueOf(((int) (Math.random() * 20) - 10)));
		w2TextField.setText(String.valueOf(((int) (Math.random() * 20) - 10)));
		thresholdTextField.setText(String.valueOf(((int) (Math.random() * 20) - 10)));

		activFuncChoiseBox
				.setItems(FXCollections.observableArrayList("Unipolar binary function", "Bipolar binary function"));
		activFuncChoiseBox.getSelectionModel().selectFirst();
		activFuncChoiseBox.setTooltip(new Tooltip("Select the activation function"));

		pointsList = new ArrayList<Circle>();
	}

	@FXML
	public void drawButtonAction() {
		try {
			w1 = Double.valueOf(w1TextField.getText());
			w2 = Double.valueOf(w2TextField.getText());
			threshold = Double.valueOf(thresholdTextField.getText());
		} catch (NumberFormatException e) {
			showErrorDialog("Error", "Invalid input", "Check input fields");
			return;
		}
		if (w1 == 0 && w2 == 0) {
			showErrorDialog("Error", "Invalid data", "Weights can't be zero at one time");
		} else {
			drawPlot(w1, w2, threshold);
			repaintPoints();
		}
	}

	@FXML
	public void comboBoxAction() {
		System.out.println("comboBoxAction");
		if (activFuncChoiseBox.getSelectionModel().isSelected(0)) {
			buttomRadioButton.setText("0");
		} else {
			buttomRadioButton.setText("-1");
		}
	}

	@FXML
	void onMouseClicked(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY) && plot != null) {
			double horizontalPos = (event.getX() - axes.getPrefWidth() / 2) / 25;
			double verticalPos = (axes.getPrefHeight() / 2 - event.getY()) / 25;
			Circle circle = new Circle(event.getX(), event.getY(), 4);
			paintPoint(circle);

			plotPane.getChildren().add(circle);
			pointsList.add(circle);
		}
	}

	@FXML
	public void removePoints() {
		plotPane.getChildren().removeAll(pointsList);
	}

	public void drawPlot(double a, double b, double c) {
		axes = new Axes((int) plotPane.getWidth(), (int) plotPane.getHeight(), -9, 9, 1, -9, 9, 1);

		// remove recent plot if present
		plotPane.getChildren().remove(plot);

		if (b != 0) {
			plot = new Plot(x -> (a * x - c) / (-b), -9, 9, 0.1, axes, b);
		} else {
			plot = new Plot(y -> c / a, -9, 9, 0.1, axes, b);
		}
		plot.addNormalVector(a, b);

		plotPane.getChildren().add(plot);
	}

	public double countNet(double horizontalPos, double verticalPos) {
		return horizontalPos * w1 + verticalPos * w2 - threshold;
	}

	public void showErrorDialog(String titleText, String headerText, String contentText) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(titleText);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	public void paintPoint(Circle circle) {
		double horizontalPos = (circle.getCenterX() - axes.getPrefWidth() / 2) / 25;
		double verticalPos = (axes.getPrefHeight() / 2 - circle.getCenterY()) / 25;
		double net = countNet(horizontalPos, verticalPos);
		// System.out.println(horizontalPos+ " " + verticalPos);
		if ((radioButtonGroup.getSelectedToggle() == topRadioButton && net == 0) || net > 0) {
			circle.setFill(Color.GREEN);
		} else {
			circle.setFill(Color.BLUE);
		}
	}

	public void repaintPoints() {
		for (Circle circle : pointsList) {
			paintPoint(circle);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}