package com.project.view;

import java.util.ArrayList;
import java.util.List;

import com.project.model.bubble.BubbleColor;
import com.project.util.ImageUtil;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class CirclePainter {

	private Circle circle;

	private List<BubbleColor> colors;

	public CirclePainter(Circle circle, List<BubbleColor> colors) {
		if (colors.size() == 0)
			throw new IllegalArgumentException("Not enough colors");
		this.circle = circle;
		this.colors = new ArrayList<BubbleColor>(colors);
		if (colors.size() == 1)
			paintGhost();
		else if (colors.size() == 2)
			paintTwoColors();
		else
			paintThreeColours();
	}

	private void paintGhost() {
		Image image = ImageUtil.GHOST_IMAGE;
		PixelReader pixelReader = image.getPixelReader();
		WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		for (int i = 0; i < writableImage.getWidth(); i++)
			for (int j = 0; j < writableImage.getHeight(); j++) {
				if (!pixelReader.getColor(i, j).equals(Color.TRANSPARENT))
					pixelWriter.setColor(i, j, colors.get(0).getColor());
			}
		circle.setFill(new ImagePattern(writableImage));
	}

	private void paintTwoColors() {
		Image image = ImageUtil.TWO_COLOURED_CIRCLE_IMAGE;
		PixelReader pixelReader = image.getPixelReader();
		WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		for (int i = 0; i < writableImage.getWidth(); i++)
			for (int j = 0; j < writableImage.getHeight(); j++) {
				Color color = pixelReader.getColor(i, j);
				if (color.getBlue() < 0.95 && color.getRed() < 0.95 && color.getGreen() < 0.95)
					pixelWriter.setColor(i, j, colors.get(0).getColor());
				else
					pixelWriter.setColor(i, j, colors.get(1).getColor());
			}
		circle.setFill(new ImagePattern(writableImage));
	}

	private void paintThreeColours() {
		Image image = ImageUtil.THREE_COLOURED_CIRCLE_IMAGE;
		PixelReader pixelReader = image.getPixelReader();
		WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		for (int i = 0; i < writableImage.getWidth(); i++)
			for (int j = 0; j < writableImage.getHeight(); j++) {
				Color color = pixelReader.getColor(i, j);
				if (color.getBlue() < 0.4 && color.getRed() < 0.4 && color.getGreen() < 0.4)
					pixelWriter.setColor(i, j, colors.get(0).getColor());
				else if (color.getBlue() > 0.6 && color.getRed() > 0.6 && color.getGreen() > 0.6)
					pixelWriter.setColor(i, j, colors.get(1).getColor());
				else
					pixelWriter.setColor(i, j, colors.get(2).getColor());
			}
		circle.setFill(new ImagePattern(writableImage));
	}

	public void updatePaint(List<BubbleColor> colors) {
		if (colors.size() != this.colors.size())
			throw new IllegalArgumentException("Wrong list size");
		for (int i = 0; i < this.colors.size(); i++)
			if (!this.colors.get(i).equals(colors.get(i))) {
				this.colors.clear();
				this.colors.addAll(colors);
				i = this.colors.size();
				if (this.colors.size() == 1)
					paintGhost();
				else if (this.colors.size() == 2)
					paintTwoColors();

				else
					paintThreeColours();
			}
	}

	public Circle getCircle() {
		return circle;
	}

}
