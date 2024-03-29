package com.piotrek.model.gameplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.piotrek.model.bubble.BombBubble;
import com.piotrek.model.bubble.Bubble;
import com.piotrek.model.bubble.BubbleColor;
import com.piotrek.model.bubble.ColoredBubble;
import com.piotrek.model.bubble.DestroyingBubble;
import com.piotrek.model.bubble.TransparentBubble;
import com.piotrek.model.mode.GameMode;

public class BubblesTab {

    public static final double HEIGHT_COEFFICIENT;

    public static final int ROWS;

    public static final int COLUMNS;

    public static final double ROW_HEIGHT;

    public static final double HEIGHT;

    public static final double WIDTH;

    public static final double BUBBLES_HEIGHT;

    private int throwsCounter;

    private int rowOffset;

    private final int[] numberOfColors;

    private final boolean[] isWaiting;

    private final List<Supplier<BubbleColor>> suppliers;

    private final Bubble[][] bubbles;

    private Bubble bubbleToThrow;

    private Bubble nextBubble;

    private Bubble replaceBubble;

    private final Gameplay gameplay;

    private final Random random;

    private final Object locker;

    private GoDown goDown;

    static {
        HEIGHT_COEFFICIENT = Math.pow(3, 0.5) / 2;
        ROWS = 26;
        COLUMNS = 25;
        ROW_HEIGHT = Bubble.DIAMETER * HEIGHT_COEFFICIENT;
        BUBBLES_HEIGHT = 24 * ROW_HEIGHT + Bubble.DIAMETER;
        HEIGHT = BUBBLES_HEIGHT + 3 * Bubble.DIAMETER;
        WIDTH = (COLUMNS + 0.5) * Bubble.DIAMETER;
    }

    {
        isWaiting = new boolean[]{false};
        suppliers = new ArrayList<>();
        suppliers.add(this::getRandomBubbleColor);
        suppliers.add(this::getRandomBubbleColorIfColorExists);
        random = new Random();
        locker = new Object();
    }

    public BubblesTab(Gameplay gameplay) {
        bubbles = new Bubble[ROWS][COLUMNS];
        this.gameplay = gameplay;
        numberOfColors = new int[]{gameplay.getGameMode().getDifficultyLevel().getNumberOfColors()};
        initBubbles();
        nextBubble = getRandomBubbleType(WIDTH / 2, BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2, 0.2,
                suppliers.get(1));
        setNextBubbleAsBubbleToThrow();
        setReplaceBubble();
        createNewTask();
        if (gameplay.getGameMode().equals(GameMode.SURVIVAL_MODE))
            scheduleColorsIncrementRunnable();
    }

    private class GoDown implements Runnable {

        private int consumerMethodNumber;

        {
            if (gameplay.getGameMode().equals(GameMode.ARCADE_MODE))
                consumerMethodNumber = 1;
        }

        @Override
        public void run() {
            Runnable runnable = () -> {
                synchronized (locker) {
                    while (gameplay.isMoving()) {
                        try {
                            isWaiting[0] = true;
                            locker.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    isWaiting[0] = false;
                    if (!gameplay.getFinishedProperty().get())
                        moveBubblesDown();
                }
            };
            Thread thread = new Thread(runnable, "Go Down Thread");
            thread.setDaemon(true);
            thread.start();
        }

        private void moveBubblesDown() {
            switchRowOffset();
            for (int i = ROWS - 1; i > 0; i--) {
                boolean finished = false;
                for (int j = 0; j < COLUMNS; j++) {
                    if (bubbles[i - 1][j] != null) {
                        bubbles[i][j] = bubbles[i - 1][j];
                        bubbles[i - 1][j] = null;
                        bubbles[i][j].setCenterY(getCenterY(i));
                        if (i == ROWS - 1)
                            finished = true;
                    }
                }
                if (finished)
                    gameplay.finishGame();
            }
            initRow();
            gameplay.sendMoveNotifications();
        }

        private void initRow() {
            double yCoordinate = getCenterY(0);
            for (int j = 0; j < COLUMNS; j++) {
                double xCoordinate = getCenterX(0, j);
                Supplier<BubbleColor> supplier = suppliers.get(consumerMethodNumber);
                bubbles[0][j] = getRandomNotThrowableTypeBubble(xCoordinate, yCoordinate, supplier);
                if (bubbles[0][j] instanceof ColoredBubble)
                    for (BubbleColor color : ((ColoredBubble) bubbles[0][j]).getColors())
                        gameplay.getColorsCounter().increment(color);
            }
        }

        private void switchRowOffset() {
            if (rowOffset == 0)
                rowOffset = 1;
            else
                rowOffset = 0;
        }

    }

    private void initBubbles() {
        for (int i = 0; i < ROWS / 2; i++) {
            double yCoordinate = getCenterY(i);
            for (int j = 0; j < COLUMNS; j++) {
                double xCoordinate = getCenterX(i, j);
                bubbles[i][j] = getRandomNotThrowableTypeBubble(xCoordinate, yCoordinate, suppliers.get(0));
                if (bubbles[i][j] instanceof ColoredBubble)
                    for (BubbleColor color : ((ColoredBubble) bubbles[i][j]).getColors())
                        gameplay.getColorsCounter().increment(color);
                gameplay.sendBubbleAddedNotifications(bubbles[i][j]);
            }
        }
    }

    private void createNewTask() {
        goDown = new GoDown();
        gameplay.getTimer().schedule(goDown, 20_000);
    }

    private void scheduleColorsIncrementRunnable() {
        Runnable[] runnable = {null};
        runnable[0] = () -> {
            numberOfColors[0]++;
            if (gameplay.getPointsCounter().getCombo() < gameplay.getPointsCounter().getMinimumCombo())
                gameplay.getPointsCounter().resetCombo();
            if (numberOfColors[0] == BubbleColor.values().length)
                gameplay.getTimer().cancelTask(runnable[0]);
        };
        gameplay.getTimer().schedule(runnable[0], 4 * 60_000);
    }

    private void setNextBubbleAsBubbleToThrow() {
        bubbleToThrow = nextBubble;
        bubbleToThrow.setCenterX(WIDTH / 2);
        gameplay.sendBubbleChangedNotifications(bubbleToThrow);
        double centerX = WIDTH / 3;
        double centerY = BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2;
        nextBubble = getRandomBubbleType(centerX, centerY, 0.2, suppliers.get(1));
        gameplay.sendBubbleAddedNotifications(nextBubble);
    }

    private void setReplaceBubble() {
        if (throwsCounter == 0) {
            throwsCounter = 10;
            double centerX = WIDTH * 2 / 3;
            double centerY = BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2;
            if (replaceBubble != null)
                gameplay.sendBubbleRemovedNotifications(replaceBubble);
            replaceBubble = getRandomBubbleType(centerX, centerY, 1, suppliers.get(1));
            gameplay.sendBubbleAddedNotifications(replaceBubble);
        }
        throwsCounter--;
    }

    private Bubble getRandomBubbleType(double centerX, double centerY, double probability,
                                       Supplier<BubbleColor> supplier) {
        double randomNumber = random.nextDouble();
        if (randomNumber > 0.1)
            return getColoredBubble(centerX, centerY, probability, supplier);
        randomNumber = random.nextDouble();
        if (randomNumber <= 0.49)
            return new BombBubble(centerX, centerY);
        if (randomNumber <= 0.98)
            return new TransparentBubble(centerX, centerY, getRandomBubbleColorIfColorExists());
        return new DestroyingBubble(centerX, centerY);
    }

    private Bubble getRandomNotThrowableTypeBubble(double centerX, double centerY,
                                                   Supplier<BubbleColor> supplier) {
        double randomNumber = random.nextDouble();
        if (randomNumber <= 0.03)
            return new BombBubble(centerX, centerY);
        else
            return getColoredBubble(centerX, centerY, 0.05, supplier);
    }

    private Bubble getColoredBubble(double centerX, double centerY, double probability,
                                    Supplier<BubbleColor> supplier) {
        double randomNumber;
        Bubble result;
        BubbleColor color = supplier.get();
        randomNumber = random.nextDouble();
        if (randomNumber <= probability && gameplay.getColorsCounter().getActiveBubblesNumber() >= 2) {
            BubbleColor secondColor;
            do
                secondColor = supplier.get();
            while (secondColor.equals(color));
            randomNumber = random.nextDouble();
            if (randomNumber <= 0.33 && gameplay.getColorsCounter().getActiveBubblesNumber() >= 3) {
                BubbleColor thirdColor;
                do {
                    thirdColor = supplier.get();
                } while (thirdColor.equals(color) || thirdColor.equals(secondColor));
                result = new ColoredBubble(centerX, centerY, new BubbleColor[]{color, secondColor, thirdColor});
            } else
                result = new ColoredBubble(centerX, centerY, new BubbleColor[]{color, secondColor});
        } else
            result = new ColoredBubble(centerX, centerY, new BubbleColor[]{color});
        return result;
    }

    public void setBubbleToThrow() {
        setReplaceBubble();
        if (gameplay.getGameMode().equals(GameMode.ARCADE_MODE)) {
            nextBubble = changeBubbleIfNeed(nextBubble);
            replaceBubble = changeBubbleIfNeed(replaceBubble);
        } else
            addColorsAndBubblesIfNeed();
        setNextBubbleAsBubbleToThrow();
    }

    public void replaceBubble() {
        if (!gameplay.getFinishedProperty().get() && !gameplay.getTimer().isPaused() && !gameplay.isMoving()
                && replaceBubble != null) {
            replaceBubble.setCenterX(bubbleToThrow.getCenterX());
            replaceBubble.setCenterY(bubbleToThrow.getCenterY());
            gameplay.sendBubbleRemovedNotifications(bubbleToThrow);
            bubbleToThrow = replaceBubble;
            gameplay.sendBubbleChangedNotifications(bubbleToThrow);
            replaceBubble = null;
            throwsCounter = 9;
        }
    }

    private void addColorsAndBubblesIfNeed() {
        if (gameplay.getColorsCounter().getColorsSum() < 50)
            for (int i = 0; i < 3; i++)
                goDown.run();
    }

    private Bubble changeBubbleIfNeed(Bubble bubbleToChange) {
        if (bubbleToChange instanceof ColoredBubble) {
            ColoredBubble bubble = (ColoredBubble) bubbleToChange;
            int activeColorsNumber = gameplay.getColorsCounter().getActiveBubblesNumber();
            if (bubble.getColorsQuantity() > activeColorsNumber)
                return setNewBubble(bubble, activeColorsNumber);
            else
                return changeBubble(bubble);
        } else if (bubbleToChange instanceof TransparentBubble)
            return changeTransparentBubble(bubbleToChange);
        return bubbleToChange;
    }

    private Bubble changeTransparentBubble(Bubble bubbleToChange) {
        TransparentBubble transparentBubble = (TransparentBubble) bubbleToChange;
        if (gameplay.getColorsCounter().getQuantity(transparentBubble.getColor()) == 0) {
            transparentBubble.setColor(getRandomBubbleColorIfColorExists());
            gameplay.sendBubbleChangedNotifications(transparentBubble);
        }
        return transparentBubble;
    }

    private Bubble changeBubble(ColoredBubble bubble) {
        boolean changed = false;
        for (int i = 0; i < bubble.getColorsQuantity(); i++) {
            if (gameplay.getColorsCounter().getQuantity(bubble.getColors().get(i)) == 0) {
                changed = true;
                BubbleColor color;
                boolean colorRepeat;
                do {
                    colorRepeat = false;
                    color = getRandomBubbleColorIfColorExists();
                    for (int j = 0; j < bubble.getColorsQuantity(); j++)
                        if (i != j && color.equals(bubble.getColors().get(j)))
                            colorRepeat = true;
                } while (colorRepeat);
                bubble.getColors().set(i, color);
            }
        }
        if (changed)
            gameplay.sendBubbleChangedNotifications(bubble);
        return bubble;
    }

    private Bubble setNewBubble(ColoredBubble bubble, int colorsNumber) {
        BubbleColor[] activeColors = new BubbleColor[colorsNumber];
        int counter = 0;
        for (BubbleColor color : bubble.getColors())
            if (gameplay.getColorsCounter().getQuantity(color) != 0)
                activeColors[counter++] = color;
        gameplay.sendBubbleRemovedNotifications(bubble);
        bubble = new ColoredBubble(bubble.getCenterX(), bubble.getCenterY(), activeColors);
        gameplay.sendBubbleAddedNotifications(bubble);
        return bubble;
    }

    private BubbleColor getRandomBubbleColorIfColorExists() {
        BubbleColor color;
        do
            color = getRandomBubbleColor();
        while (gameplay.getColorsCounter().getQuantity(color) == 0);
        return color;
    }

    public BubbleColor getRandomBubbleColor() {
        BubbleColor[] colors = BubbleColor.values();
        return colors[random.nextInt(numberOfColors[0])];
    }

    public double getCenterY(int row) {
        return row * ROW_HEIGHT + 0.5 * Bubble.DIAMETER;
    }

    public double getCenterX(int row, int column) {
        double result = column * Bubble.DIAMETER + 0.5 * Bubble.DIAMETER;
        int rowToCheck = row + rowOffset;
        if (rowToCheck % 2 == 1)
            result += 0.5 * Bubble.DIAMETER;
        return result;
    }

    public Bubble[][] getBubbles() {
        return bubbles;
    }

    public Bubble getBubbleToThrow() {
        return bubbleToThrow;
    }

    public Bubble getNextBubble() {
        return nextBubble;
    }

    public Bubble getReplaceBubble() {
        return replaceBubble;
    }

    public Object getLocker() {
        return locker;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public boolean isWaiting() {
        return isWaiting[0];
    }

    public int getNumberOfColors() {
        return numberOfColors[0];
    }

}