package com.project.view;

import java.util.*;

import com.project.controller.GameplayController;
import com.project.image.GameImage;
import com.project.model.bubble.Bubble;
import com.project.model.bubble.BubbleColor;
import com.project.model.bubble.ColoredBubble;
import com.project.model.bubble.DestroyingBubble;
import com.project.model.bubble.TransparentBubble;
import com.project.model.gameplay.BubblesTab;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class GameplayView {

    public static final double BUBBLE_RADIUS_REDUCE;

    private GameplayController controller;

    private final Pane pane;

    private Circle replaceCircle;

    private final List<Line> lines;

    private final Map<Integer, Circle> circlesMap;

    private final Map<Integer, Painter> paintersMap;

    private final Timer timer;

    private int offset;

    static {
        BUBBLE_RADIUS_REDUCE = 1.0;
    }

    {
        lines = new ArrayList<>();
        circlesMap = new HashMap<>();
        paintersMap = new HashMap<>();
        pane = new Pane();
        timer = new Timer(true);
    }

    private class Rotator extends TimerTask {

        private double rotation;

        @Override
        public void run() {
            if (rotation == 0)
                rotation += 360;
            rotation -= 1;
            Platform.runLater(() -> replaceCircle.setRotate(rotation));
        }

    }

    public void setController(GameplayController gameplayController) {
        this.controller = gameplayController;
        setPaneSize();
        pane.setBackground(new Background(
                new BackgroundFill(Painter.getLinearGradientPaint(Color.rgb(40, 41, 42), Color.rgb(0, 1, 2)),
                        CornerRadii.EMPTY, new Insets(0.0))));
        addRectangle();
        pane.setOnMouseEntered(this::handleMouseEntering);
        pane.setOnMouseExited(this::handleMouseExiting);
        pane.setOnMouseMoved(this::handleMouseMoving);
        pane.setOnMouseDragged(this::handleMouseMoving);
        pane.setOnMouseClicked(this::handleMouseClicking);
        updateBubblesTab();
        addBubble(controller.getGameplay().getBubblesTab().getBubbleToThrow());
        addBubble(controller.getGameplay().getBubblesTab().getNextBubble());
        addBubble(controller.getGameplay().getBubblesTab().getReplaceBubble());
        initReplaceCircle();
        startLineAnimation();
    }

    private void initReplaceCircle() {
        Bubble replaceBubble = controller.getGameplay().getBubblesTab().getReplaceBubble();
        replaceCircle = new Circle(replaceBubble.getCenterX(), replaceBubble.getCenterY(), Bubble.DIAMETER);
        pane.getChildren().add(replaceCircle);
        replaceCircle.setOpacity(0.75);
        replaceCircle.setFill(new ImagePattern(GameImage.REPLACE.getImage()));
        timer.schedule(new Rotator(), 10, 10);
    }

    private void addRectangle() {
        double height = BubblesTab.HEIGHT - BubblesTab.BUBBLES_HEIGHT;
        double startY = pane.getPrefHeight() - height;
        Rectangle rectangle = new Rectangle(0, startY, pane.getPrefWidth(), height);
        rectangle.setFill(Painter.getLinearGradientPaint(Color.rgb(36, 36, 36)));
        pane.getChildren().add(0, rectangle);
    }

    private void setPaneSize() {
        pane.setPrefHeight(BubblesTab.HEIGHT);
        pane.setPrefWidth(BubblesTab.WIDTH);
        pane.setMinHeight(Pane.USE_PREF_SIZE);
        pane.setMinWidth(Pane.USE_PREF_SIZE);
    }

    public void updateBubblesTab() {
        Bubble[][] bubbles = controller.getGameplay().getBubblesTab().getBubbles();
        for (Bubble[] bubblesRow : bubbles)
            for (Bubble bubble : bubblesRow)
                if (bubble != null && !updateBubble(bubble))
                    addBubble(bubble);
    }

    public void addBubble(Bubble bubble) {
        double centerX = bubble.getCenterX();
        double centerY = bubble.getCenterY();
        double radius = Bubble.DIAMETER / 2 - BUBBLE_RADIUS_REDUCE;
        if (bubble instanceof ColoredBubble)
            addMulticoloredCircle(bubble, centerX, centerY, radius);
        else if (bubble instanceof TransparentBubble)
            addTransparentBubble(bubble, centerX, centerY, radius);
        else if (bubble instanceof DestroyingBubble)
            addDestroyingBubble(bubble, centerX, centerY, radius);
        else
            addBombBubble(bubble, centerX, centerY, radius);
    }

    private void addDestroyingBubble(Bubble bubble, double centerX, double centerY, double radius) {
        Circle circle = new Circle(centerX, centerY, radius, Color.WHITE);
        GaussianBlur gaussianBlur = new GaussianBlur(10.0);
        circle.setEffect(gaussianBlur);
        circlesMap.put(bubble.BUBBLE_NUMBER, circle);
        pane.getChildren().add(circle);
    }

    private void addBombBubble(Bubble bubble, double centerX, double centerY, double radius) {
        Circle circle = new Circle(centerX, centerY, radius + 2);
        circle.setFill(new ImagePattern(GameImage.BOMB.getImage()));
        circlesMap.put(bubble.BUBBLE_NUMBER, circle);
        pane.getChildren().add(circle);
    }

    private void addTransparentBubble(Bubble bubble, double centerX, double centerY, double radius) {
        TransparentBubble transparentBubble = (TransparentBubble) bubble;
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setOpacity(0.8);
        Painter painter = new Painter(circle, Collections.singletonList(transparentBubble.getColor()));
        paintersMap.put(bubble.BUBBLE_NUMBER, painter);
        pane.getChildren().add(circle);
    }

    private void addMulticoloredCircle(Bubble bubble, double centerX, double centerY, double radius) {
        ColoredBubble coloredBubble = (ColoredBubble) bubble;
        List<BubbleColor> colors = coloredBubble.getColors();
        if (coloredBubble.getColorsQuantity() == 1) {
            Paint paint = Painter.getLinearGradientPaint(colors.get(0).getColor());
            Circle circle = new Circle(centerX, centerY, radius, paint);
            circlesMap.put(bubble.BUBBLE_NUMBER, circle);
            pane.getChildren().add(circle);
        } else {
            Circle circle = new Circle(centerX, centerY, radius);
            Painter painter = new Painter(circle, coloredBubble.getColors());
            pane.getChildren().add(circle);
            paintersMap.put(bubble.BUBBLE_NUMBER, painter);
        }
    }

    public void removeBubble(Bubble bubble) {
        Circle circle = circlesMap.get(bubble.BUBBLE_NUMBER);
        if (circle != null) {
            circlesMap.remove(bubble.BUBBLE_NUMBER);
            pane.getChildren().remove(circle);
            return;
        }
        Painter painter = paintersMap.get(bubble.BUBBLE_NUMBER);
        if (painter == null)
            return;
        paintersMap.remove(bubble.BUBBLE_NUMBER);
        pane.getChildren().remove(painter.getCircle());
    }

    public boolean updateBubble(Bubble bubble) {
        Circle circle = circlesMap.get(bubble.BUBBLE_NUMBER);
        if (circle != null) {
            if (bubble instanceof ColoredBubble)
                circle.setFill(Painter.getLinearGradientPaint(((ColoredBubble) bubble).getColors().get(0).getColor()));
            circle.setCenterX(bubble.getCenterX());
            circle.setCenterY(bubble.getCenterY());
            return true;
        }
        Painter painter = paintersMap.get(bubble.BUBBLE_NUMBER);
        if (painter != null) {
            circle = painter.getCircle();
            circle.setCenterX(bubble.getCenterX());
            circle.setCenterY(bubble.getCenterY());
            if (bubble instanceof ColoredBubble)
                painter.updatePaint(((ColoredBubble) bubble).getColors());
            else if (bubble instanceof TransparentBubble)
                painter.updatePaint(Collections.singletonList(((TransparentBubble) bubble).getColor()));
            return true;
        }
        return false;
    }

    private void startLineAnimation() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (--offset < 0)
                        offset = 25;
                    lines.forEach(line -> line.setStrokeDashOffset(offset));
                });
            }
        };
        timer.schedule(timerTask, 25, 25);
    }

    private void setLineStyle(Line line) {
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(3.0);
        line.setOpacity(0.75);
        line.getStrokeDashArray().addAll(10.0, 15.0);
        line.setStrokeDashOffset(offset);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeLineJoin(StrokeLineJoin.BEVEL);
    }

    private void handleMouseEntering(MouseEvent event) {
        pane.getScene().setCursor(Cursor.NONE);
        List<Point2D> points = controller.getLinePoints(event.getX(), event.getY());
        for (int i = 0; i < points.size() - 1; i++)
            addLine(points, i);
    }

    private void handleMouseExiting(MouseEvent event) {
        pane.getScene().setCursor(Cursor.DEFAULT);
        for (Line line : lines)
            pane.getChildren().remove(line);
        lines.clear();
    }

    private void handleMouseMoving(MouseEvent event) {
        List<Point2D> points = controller.getLinePoints(event.getX(), event.getY());
        if (points.isEmpty())
            handleMouseExiting(event);
        else {
            if (!pane.getScene().getCursor().equals(Cursor.NONE))
                pane.getScene().setCursor(Cursor.NONE);
            manageLines(points);
        }
    }

    public void manageLines(List<Point2D> points) {
        int difference = points.size() - 1 - lines.size();
        if (difference >= 0) {
            for (int i = 0; i < lines.size(); i++)
                setLine(points, i);
            for (int i = lines.size(); i < points.size() - 1; i++)
                addLine(points, i);
        } else {
            for (int i = 0; i < points.size() - 1; i++)
                setLine(points, i);
            if (points.size() > 0) {
                int i = points.size() - 1;
                while (i < lines.size())
                    removeLine(i);
            }
        }
    }

    private void handleMouseClicking(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY)
            controller.replaceBubble();
        else if (event.getButton() == MouseButton.PRIMARY) {
            controller.throwBubble(event.getX(), event.getY());
            Point2D point = new Point2D(event.getX(), event.getSceneY());
            if (replaceCircle.contains(point))
                controller.replaceBubble();
        }
    }

    private void removeLine(int i) {
        Line line = lines.get(i);
        pane.getChildren().remove(line);
        lines.remove(i);
    }

    private void setLine(List<Point2D> points, int i) {
        Line line = lines.get(i);
        line.setStartX(points.get(i).getX());
        line.setStartY(points.get(i).getY());
        line.setEndX(points.get(i + 1).getX());
        line.setEndY(points.get(i + 1).getY());
    }

    private void addLine(List<Point2D> points, int i) {
        Point2D previous = points.get(i);
        Point2D next = points.get(i + 1);
        Line line = new Line(previous.getX(), previous.getY(), next.getX(), next.getY());
        setLineStyle(line);
        lines.add(line);
        pane.getChildren().add(1, line);
    }

    public void cancelTimer() {
        timer.cancel();
    }

    public Pane getPane() {
        return pane;
    }

}