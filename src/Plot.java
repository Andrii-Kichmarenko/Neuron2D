import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;

public class Plot extends Pane {
	Path normalVector;
	double startX;
	double startY;

	public Plot(Function<Double, Double> f, double xMin, double xMax, double xInc, Axes axes, double coeffB) {
		Path mainPath = new Path();
		mainPath.setStroke(Color.ORANGE);
		mainPath.setStrokeWidth(2);

		mainPath.setClip(new Rectangle(0, 0, axes.getPrefWidth(), axes.getPrefHeight()));

		if (coeffB != 0) {
			double x = xMin;
			double y = f.apply(x);

			mainPath.getElements().add(new MoveTo(mapX(x, axes), mapY(y, axes)));

			x += xInc;
			while (x < xMax) {
				y = f.apply(x);

				mainPath.getElements().add(new LineTo(mapX(x, axes), mapY(y, axes)));

				x += xInc;
			}
		} else {
			// work for square plot!!! xMin and yMin is the same
			double y = xMin;
			double x = f.apply(y);

			mainPath.getElements().add(new MoveTo(mapX(x, axes), mapY(y, axes)));

			y += xInc;
			while (y < xMax) {
				x = f.apply(y);

				mainPath.getElements().add(new LineTo(mapX(x, axes), mapY(y, axes)));

				y += xInc;
			}
		}
		// creating normal vector
		normalVector = new Path();
		normalVector.setStroke(Color.GREEN);
		PathElement startNormalVector = mainPath.getElements().get(mainPath.getElements().size() / 2);
		Scanner sc = new Scanner(startNormalVector.toString().replaceAll("[^\\d.]", " "));
		startX = sc.nextDouble();
		startY = sc.nextDouble();
		sc.close();
//		System.out.println(startX + " " + startY);
		normalVector.getElements().add(new MoveTo(startX, startY));

		setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
		setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
		setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

		getChildren().setAll(axes, mainPath);

	}

	public void addNormalVector(double a, double b) {
		normalVector.getElements().add(new LineTo(startX + a * 25, startY - b * 25));
		getChildren().add(normalVector);
	}

	public double mapX(double x, Axes axes) {
		double tx = axes.getPrefWidth() / 2;
		double sx = axes.getPrefWidth() / (axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound());

		return x * sx + tx;
	}

	public double mapY(double y, Axes axes) {
		double ty = axes.getPrefHeight() / 2;
		double sy = axes.getPrefHeight() / (axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound());

		return -y * sy + ty;
	}

}
