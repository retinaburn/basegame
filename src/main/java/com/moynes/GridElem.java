package com.moynes;

import lombok.Data;

import java.awt.*;

@Data
public class GridElem {
    Rectangle pos;
    int index;
    boolean isOccupied;
    boolean goal = false;
    boolean frontier = false;
    boolean reached = false;
    int distance = 0;
    boolean wall = false;

    public int x(){
        return pos.x;
    }
    public int y(){
        return pos.y;
    }
    public int height(){
        return pos.height;
    }
    public int width(){
        return pos.width;
    }
    public GridElem(Rectangle r){
        pos = r;
        isOccupied = false;
    }
}
