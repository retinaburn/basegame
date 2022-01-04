package com.moynes;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class GameCanvas extends JPanel {

    GameState gameState;

    BufferedImage[] bufferedImage = new BufferedImage[10];
    int width;
    int height;

    public GameCanvas(int width, int height, GameState gameState, Listener listener) {
        this.gameState = gameState;
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.addKeyListener(listener);
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);

        this.width = this.getMaximumSize().width;
        this.height = this.getMaximumSize().height;
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
        g.clearRect(0, 0, width, height);

        drawGrid(g, gameState.posGrid);
        drawStart(g, gameState.posStart, gameState.dimStart);
        drawGoal(g, gameState.posGoal, gameState.dimGoal.getIntX(), gameState.dimGoal.getIntY());
        drawMob(g, gameState.posMob, gameState.dimMob);
        drawMob(g, gameState.posMob2, gameState.dimMob);
        for (int i = 0; i < gameState.posMobList.size(); i++) {
            drawMob(g, gameState.posMobList.get(i), gameState.dimMob);
        }

    }

    void drawMob(Graphics g, V2 pos, V2 dim){
        if (pos == null)
            return;
        V2 mappedStart = new V2(pos.x, height-pos.y+dim.getIntY());
        Color originalColor = g.getColor();
        g.setColor(Color.BLUE);
        g.fillOval(mappedStart.getIntX(), mappedStart.getIntY(), dim.getIntX(), dim.getIntY());
        //g.drawRect(mappedStart.getIntX(), mappedStart.getIntY(), dim.getIntX()+5, dim.getIntY()+5);
        drawRightAlignedText(g, "P: "+mappedStart, 30);
        //log.debug("{}", mappedStart);
        g.setColor(originalColor);
    }

    void drawStart(Graphics g, V2 start, V2 dim){
        V2 mappedStart = new V2(start.x, height-start.y);
        Color originalColor = g.getColor();
        g.setColor(Color.GREEN);
        g.fillRect(mappedStart.getIntX(), mappedStart.getIntY(), dim.getIntX(), dim.getIntY());

        g.setColor(originalColor);
    }

    void drawGrid(Graphics g, List<GridElem> posGrid){
        Color originalColor = g.getColor();

        for(GridElem posGridElem : posGrid) {
            if (posGridElem.isOccupied)
                g.setColor(Color.BLACK);
            else
                g.setColor(Color.LIGHT_GRAY);

            Rectangle mappedElem = new Rectangle(posGridElem.x(), height - posGridElem.y(),
                    posGridElem.width(), posGridElem.height());
            if (posGridElem.isWall()){
                g.fillRect(mappedElem.x, mappedElem.y, mappedElem.width, mappedElem.height);
            } else {
                g.drawRect(mappedElem.x, mappedElem.y, mappedElem.width, mappedElem.height);
            }
        }
        g.setColor(originalColor);
    }

    void drawGoal(Graphics g, V2 goal, int width, int height){
        V2 mappedGoal = new V2(goal.x, this.height-goal.y);
        Color originalColor = g.getColor();
        g.setColor(Color.BLACK);
        g.drawRect(mappedGoal.getIntX(), mappedGoal.getIntY(), width, height);
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



