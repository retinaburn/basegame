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

    @Override
    public void paintComponent(Graphics g) {
        int width = this.getMaximumSize().width;
        int height = this.getMaximumSize().height;
        g.clearRect(0, 0, width, height);

        g.setColor(Color.RED);
        int circleRadius = 5;
        int circleCenterX = width/2 - circleRadius;
        int circleCentery = height/2 - circleRadius;
        g.fillOval(circleCenterX, circleCentery, circleRadius*2, circleRadius*2);
    }

    BufferedImage loadImage(String filename) throws IOException {
        String imagePath = "images/";
        ClassLoader loader = this.getClass().getClassLoader();
        return ImageIO.read(Objects.requireNonNull(loader.getResourceAsStream(imagePath + filename)));
    }
}



