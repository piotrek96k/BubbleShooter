package com.piotrek.view;

import java.util.ArrayList;
import java.util.List;

import com.piotrek.image.GameImage;
import com.piotrek.model.bubble.BubbleColor;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public class Painter {

    private final Circle circle;

    private final List<BubbleColor> colors;

    public Painter(Circle circle, List<BubbleColor> colors) {
        if (colors.size() == 0)
            throw new IllegalArgumentException("Not enough colors");
        this.circle = circle;
        this.colors = new ArrayList<>(colors);
        if (colors.size() == 1)
            paintGhost();
        else if (colors.size() == 2)
            paintTwoColors();
        else
            paintThreeColors();
    }

    public static Paint getLinearGradientPaint(Color color) {
        Color firstColor = color.brighter();
        Color secondColor = color.darker();
        return getLinearGradientPaint(firstColor, secondColor);
    }

    public static Paint getLinearGradientPaint(Color firstColor, Color secondColor) {
        Stop[] stops = {new Stop(0.5, firstColor), new Stop(0.8, secondColor)};
        return new LinearGradient(0.0, 0.0, 1.0, 1.0, true, CycleMethod.NO_CYCLE, stops);
    }

    private void paintGhost() {
        Image image = GameImage.GHOST.getImage();
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < writableImage.getWidth(); i++)
            for (int j = 0; j < writableImage.getHeight(); j++) {
                if (!pixelReader.getColor(i, j).equals(Color.TRANSPARENT)) {
                    Color basicColor = colors.get(0).getColor();
                    Color color = new Color(basicColor.getRed(), basicColor.getGreen(), basicColor.getBlue(),
                            pixelReader.getColor(i, j).getOpacity());
                    pixelWriter.setColor(i, j, color);
                }
            }
        circle.setFill(new ImagePattern(writableImage));
    }

    private void paintTwoColors() {
        Image image = GameImage.TWO_COLORED_CIRCLE.getImage();
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        Color first = colors.get(0).getColor();
        Color second = colors.get(1).getColor();
        for (int i = 0; i < writableImage.getWidth(); i++)
            for (int j = 0; j < writableImage.getHeight(); j++) {
                Color color = pixelReader.getColor(i, j);
                if (!color.equals(Color.TRANSPARENT)) {
                    double red = color.getRed() * first.getRed() + (1 - color.getRed()) * second.getRed();
                    double green = color.getGreen() * first.getGreen() + (1 - color.getGreen()) * second.getGreen();
                    double blue = color.getBlue() * first.getBlue() + (1 - color.getBlue()) * second.getBlue();
                    Color newColor = new Color(red, green, blue, 1.0);
                    pixelWriter.setColor(i, j, newColor);
                }

            }
        circle.setFill(new ImagePattern(writableImage));
    }

    private void paintThreeColors() {
        Image image = GameImage.THREE_COLORED_CIRCLE.getImage();
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        Color first = colors.get(0).getColor();
        Color second = colors.get(1).getColor();
        Color third = colors.get(2).getColor();
        for (int i = 0; i < writableImage.getWidth(); i++)
            for (int j = 0; j < writableImage.getHeight(); j++) {
                Color color = pixelReader.getColor(i, j);
                if (!color.equals(Color.TRANSPARENT)) {
                    double sum = color.getRed() + color.getGreen() + color.getBlue();
                    double red = color.getRed() / sum * first.getRed() + color.getGreen() / sum * second.getRed()
                            + color.getBlue() / sum * third.getRed();
                    double green = color.getRed() / sum * first.getGreen() + color.getGreen() / sum * second.getGreen()
                            + color.getBlue() / sum * third.getGreen();
                    double blue = color.getRed() / sum * first.getBlue() + color.getGreen() / sum * second.getBlue()
                            + color.getBlue() / sum * third.getBlue();
                    Color newColor = new Color(red, green, blue, 1.0);
                    pixelWriter.setColor(i, j, newColor);
                }
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
                    paintThreeColors();
            }
    }

    public Circle getCircle() {
        return circle;
    }

}