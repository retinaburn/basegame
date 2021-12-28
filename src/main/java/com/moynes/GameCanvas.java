package com.moynes;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class GameCanvas extends JPanel {

    GameState gameState;

    BufferedImage[] bufferedImage = new BufferedImage[10];

    public GameCanvas(int width, int height, GameState gameState, KeyListener keyListener) {
        this.gameState = gameState;
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.addKeyListener(keyListener);
    }

    long timeSinceLastAnimatedFrame = 0;

    public void render(long dT) {
        timeSinceLastAnimatedFrame += dT;
        if (timeSinceLastAnimatedFrame >= 30)
            timeSinceLastAnimatedFrame = 0;
        this.repaint();
    }

    double degrees = 0;

    @Override
    public void paintComponent(Graphics g) {
        int width = this.getMaximumSize().width;
        int height = this.getMaximumSize().height;
        g.clearRect(0, 0, width, height);

        int circleRadius = gameState.radius;
        int circleCenterX = gameState.circle1.getIntX();
        int circleCenterY = gameState.circle1.getIntY();
        drawFilledCenteredCircle(g, circleCenterX, circleCenterY, circleRadius, Color.red);

        drawFilledCenteredCircle(g, gameState.circle2.getIntX(), gameState.circle2.getIntY(), circleRadius, Color.blue);
        drawFilledCenteredCircle(g, gameState.circle3.getIntX(), gameState.circle3.getIntY(), circleRadius, Color.black);
        drawFilledCenteredCircle(g, gameState.circle4.getIntX(), gameState.circle4.getIntY(), circleRadius, Color.ORANGE);

        degrees+=1;
    }

    void drawFilledCenteredCircle(Graphics g, int x, int y, int radius, Color color){
        Color originalColor = g.getColor();
        g.setColor(color);
        g.fillOval(x - radius, y - radius, radius*2, radius*2);
        g.setColor(originalColor);

    }

    BufferedImage loadImage(String filename) throws IOException {
        String imagePath = "images/";
        ClassLoader loader = this.getClass().getClassLoader();
        return ImageIO.read(Objects.requireNonNull(loader.getResourceAsStream(imagePath + filename)));
    }
}



