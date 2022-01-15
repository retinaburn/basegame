package com.moynes;

import lombok.extern.slf4j.Slf4j;

import java.awt.event.*;

@Slf4j
public class Listener implements KeyListener, MouseListener, MouseMotionListener {

    KeyState keyState;
    public Listener(KeyState keyState){
        this.keyState = keyState;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    static final int KEY_UP_ARROW = 38;
    static final int KEY_DOWN_ARROW = 40;
    static final int KEY_LEFT_ARROW = 37;
    static final int KEY_RIGHT_ARROW = 39;
    static final int KEY_SPACE = 32;
    static final int KEY_C = 67;

    @Override
    public void keyPressed(KeyEvent e) {
        log.debug("Pressed: {}", e);
        switch(e.getKeyCode()){
            case KEY_UP_ARROW -> keyState.UP_KEY_DOWN = true;
            case KEY_DOWN_ARROW -> keyState.DOWN_KEY_DOWN = true;
            case KEY_LEFT_ARROW -> keyState.LEFT_KEY_DOWN = true;
            case KEY_RIGHT_ARROW -> keyState.RIGHT_KEY_DOWN = true;
            case KEY_SPACE -> keyState.SPACE_KEY_DOWN = true;
            case KEY_C -> keyState.C_KEY_DOWN = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.debug("Released: {}", e);
        switch(e.getKeyCode()){
            case KEY_UP_ARROW -> keyState.UP_KEY_DOWN = false;
            case KEY_DOWN_ARROW -> keyState.DOWN_KEY_DOWN = false;
            case KEY_LEFT_ARROW -> keyState.LEFT_KEY_DOWN = false;
            case KEY_RIGHT_ARROW -> keyState.RIGHT_KEY_DOWN = false;
            case KEY_SPACE -> keyState.SPACE_KEY_DOWN = false;
            case KEY_C -> keyState.C_KEY_DOWN = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        log.debug("{},{}", e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / 10;
        int y = (600 - e.getY())/10;
        keyState.LEFT_MOUSE_DOWN = true;
        keyState.MOUSE_LOCATION = e.getPoint();
        log.info("Mouse: (x,y) = ({},{}) -> ({}, {})", e.getX(), e.getY(), x, y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        log.debug("{},{}", e.getX(), e.getY());
        keyState.LEFT_MOUSE_DOWN = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        log.debug("{},{}", e.getX(), e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        log.debug("{},{}", e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //log.info("Dragged: {}", e.getPoint());
        keyState.LEFT_MOUSE_DOWN = true;
        keyState.MOUSE_LOCATION = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
