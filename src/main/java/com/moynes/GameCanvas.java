package com.moynes;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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

        int circleRadius = gameState.radius;

        if (gameState.projectilePos != null) {
            V2 mappedPlayer = new V2(10 + gameState.projectilePos.x, 400 - gameState.projectilePos.y);
            drawFilledCenteredCircle(g, mappedPlayer.getIntX(), mappedPlayer.getIntY(), circleRadius, Color.black);
            drawRightAlignedText(g, "P: "+gameState.projectilePos, 25);
        }
        drawRightAlignedText(g, "A: "+gameState.acceleration, 10);
        drawRightAlignedText(g, "Launch Angle: "+gameState.launchDegrees, 45);

        drawLauncher(g, gameState.playerPos, gameState.launchDegrees);

    }

//    private void drawLauncher(Graphics g, V2 playerPos, V2 launcherEnd) {
//        Color originalColor = g.getColor();
//        g.setColor(Color.RED);
//        V2 mappedPlayer = new V2(playerPos.getIntX(), 400 - playerPos.getIntY());
//        V2 mappedLauncherEnd = new V2(launcherEnd.x, 400 - launcherEnd.y);
//
//        g.drawLine(mappedPlayer.getIntX(), mappedPlayer.getIntY(), mappedLauncherEnd.getIntX(), mappedLauncherEnd.getIntY());
//
//        g.setColor(originalColor);
//    }

//
    private void drawLauncher(Graphics g1, V2 playerPos, double launchDegrees) {
        Graphics2D g = (Graphics2D)g1;
        AffineTransform originalTransform = g.getTransform();
        Color originalColor = g.getColor();
        g.setColor(Color.RED);

        V2 mappedPlayer = new V2(playerPos.getIntX(), 400 - playerPos.getIntY());

        g.rotate(Math.toRadians(-launchDegrees-90), mappedPlayer.getIntX(), mappedPlayer.getIntY());
        Rectangle r = new Rectangle(mappedPlayer.getIntX(), mappedPlayer.getIntY(), 10, 100 );
        g.draw(r);
        g.fill(r);

        g.setTransform(originalTransform);
        g.setColor(originalColor);
    }

    void drawFilledCenteredCircle(Graphics g, int x, int y, int radius, Color color){
        Color originalColor = g.getColor();
        g.setColor(color);
        g.fillOval(x - radius, y - radius, radius*2, radius*2);
        g.setColor(originalColor);

    }
    private void drawRightAlignedText(Graphics g, String string, int y) {
        Rectangle2D fontBound = g.getFontMetrics().getStringBounds(string, g);
        int posX = this.getMaximumSize().width - fontBound.getBounds().width;

        g.setColor(Color.BLACK);
        g.fillRect(
                posX,
                y + (int) fontBound.getCenterY() - (int) fontBound.getHeight() / 2,
                fontBound.getBounds().width,
                (int) fontBound.getHeight());

        g.setColor(Color.GREEN);
        g.drawString(string,
                posX,
                y);
    }

    BufferedImage loadImage(String filename) throws IOException {
        String imagePath = "images/";
        ClassLoader loader = this.getClass().getClassLoader();
        return ImageIO.read(Objects.requireNonNull(loader.getResourceAsStream(imagePath + filename)));
    }
}



