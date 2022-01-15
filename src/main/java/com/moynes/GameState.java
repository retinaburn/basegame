package com.moynes;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

@Data
@Slf4j
public class GameState {
    static float PIXELS_PER_METER = 10;
    float speed = 0.1f / PIXELS_PER_METER; //m/s2
    V2 velocity = new V2();
    V2 playerPos = new V2(0, 0);
    V2 acceleration = new V2();
    float drag = -0.8f / PIXELS_PER_METER;
    double adjustmentOffsetWhenCollides = 0.01;

    V2 posGoal = new V2(500, 500);
    V2 dimGoal = new V2(10, 10);

    V2 posStart = new V2(0, 10);
    V2 dimStart = new V2(10, 10);

    List<GridElem> posGrid = new ArrayList<>();
    V2 dimGrid = new V2(10, 10);

    V2 posMob = new V2(10, 10);
    V2 dimMob = new V2(5, 5);

    V2 posMob2 = new V2(0, 10);
    int NUM_OF_MOBS = 100;
    List<V2> posMobList = new ArrayList<>(NUM_OF_MOBS);
    int DT_PER_MOVE = 30;
    int dtCount = 0;

    int radius = 5;
    int width;
    int height;

    Queue<GridElem> frontier;
    Map<Integer, Boolean> dictionary;

    public GameState(int width, int height) {
        this.width = width;
        this.height = height;

        for (int yIter = 0; yIter < height; yIter += dimGrid.getIntY()) {
            for (int xIter = 0; xIter < width; xIter += dimGrid.getIntX()) {
                GridElem elem = new GridElem(new Rectangle(xIter, yIter, dimGrid.getIntX(), dimGrid.getIntY()));
                posGrid.add(elem);
                elem.setIndex(yIter / 10 * (width / 10) + xIter / 10);
                if (xIter == 500 && yIter == 500) {
                    elem.setGoal(true);
                }
                if (xIter >= 20 && xIter <= 700 && yIter == 300)
                    elem.setWall(true);
                else if (xIter <= 100 && yIter == 200)
                    elem.setWall(true);
            }
        }
        buildBreadthFirstSearch(posGrid, posGoal);

        for (int i = 0; i < NUM_OF_MOBS; i++) {
            posMobList.add(new V2((int)(Math.random() * 700), 20));
        }


//        int index = posGoal.getIntY() / 10 * 80 + posGoal.getIntX() / 10;
//        GridElem goalElem = posGrid.get(index);
//        log.debug("Index: {}, Goal: {}", index, goalElem);
//
//        frontier = new ArrayDeque<>();
//        frontier.add(goalElem);
//        goalElem.setFrontier(true);
//
//        dictionary = new HashMap<>();
//        dictionary.put(goalElem.index, Boolean.TRUE);
//        goalElem.setReached(true);


//        String logLine = "";
//        for(int y = 0; y < 60; y++){
//            for(int x = 0; x < 80; x++) {
//                int index = y * 80 + x;
//                logLine = logLine + index+":"+posGrid.get(index).distance + " ";
//                //log.debug("{} ", posGrid.get(index).distance);
//            }
//            log.debug("{}", logLine);
//            logLine = "";
//        }
    }

    int mouseMode = 0;
    public boolean update(KeyState keyState, long dt) {
        V2 newAcceleration = new V2();

        if (keyState.UP_KEY_DOWN) {
            newAcceleration.y += 1;
        }
        if (keyState.DOWN_KEY_DOWN) {
            newAcceleration.y -= 1;
        }
        if (keyState.LEFT_KEY_DOWN) {
            newAcceleration.x -= 1;
        }
        if (keyState.RIGHT_KEY_DOWN) {
            newAcceleration.x += 1;
        }
        if (keyState.SPACE_KEY_DOWN) {
            //buildBreadthFirstSearchIter(posGrid);
            //posMob2 = new V2(10,10);
            //dtCount += DT_PER_MOVE;
        }
        if (keyState.LEFT_MOUSE_DOWN) {
            int x = keyState.MOUSE_LOCATION.x / 10;
            int y = (600 - keyState.MOUSE_LOCATION.y)/10;
            y+=1; //why??
            GridElem elem = posGrid.get(y*80+x);
            if (mouseMode == 0){
                if (!elem.isWall()) {
                    mouseMode = 1;
                } else {
                    mouseMode = 2;
                }
            }
            if (mouseMode == 1)
                elem.setWall(true);
            else if (mouseMode == 2)
                elem.setWall(false);

            buildBreadthFirstSearch(posGrid, posGoal);
        } else {
            mouseMode = 0;
        }
        newAcceleration = newAcceleration.normalize().multiply(speed);
        newAcceleration.add(new V2(velocity).multiply(drag));

        V2 newMobPos = new V2(posMob);
        //p = 1/2*a*sq(t) + v't + p
        newMobPos = (new V2(newAcceleration))
                .multiply(dt * dt)
                .multiply(0.5)
                .add(new V2(velocity).multiply(dt))
                .add(new V2(newMobPos));
        velocity = (new V2(newAcceleration).multiply(dt).add(velocity));
        acceleration = newAcceleration;
        posMob = newMobPos;

        if (dtCount >= DT_PER_MOVE && posMob2 != null) {
            int index = posMob2.getIntY() / 10 * 80 + posMob2.getIntX() / 10;
            GridElem currentLocation = posGrid.get(index);
            //log.debug("Pos: {},{} Index: {}, Current Location: {}", posMob2.getIntX(), posMob2.getIntY(), index, currentLocation.distance);
            List<GridElem> lowestDistanceNeighbours = getLowestDistanceNeighbours(currentLocation);
            //log.debug("Lowest Distance Neighbours: {}", lowestDistanceNeighbours);
            int randomDirection = (int) (Math.random() * lowestDistanceNeighbours.size());
            if (lowestDistanceNeighbours.size() > 0) {
                posMob2.x = lowestDistanceNeighbours.get(randomDirection).x();
                posMob2.y = lowestDistanceNeighbours.get(randomDirection).y();
            }
            if (posMob2.x == posGoal.x && posMob2.y == posGoal.y) {
                //log.debug("Arrived: Mob: {} Goal: {}", posMob2, posGoal);
                posMob2 = new V2(10, 10);
            }
        }
        if (dtCount >= DT_PER_MOVE) {
            for (int iter=0; iter < posMobList.size(); iter++){
                V2 posMobIter = posMobList.get(iter);
                int index = posMobIter.getIntY() / 10 * 80 + posMobIter.getIntX() / 10;
                GridElem currentLocation = posGrid.get(index);
                //log.debug("Pos: {},{} Index: {}, Current Location: {}", posMob2.getIntX(), posMob2.getIntY(), index, currentLocation.distance);
                List<GridElem> lowestDistanceNeighbours = getLowestDistanceNeighbours(currentLocation);
                //log.debug("Lowest Distance Neighbours: {}", lowestDistanceNeighbours);
                int randomDirection = (int) (Math.random() * lowestDistanceNeighbours.size());
                if (lowestDistanceNeighbours.size() > 0) {
                    posMobIter.x = lowestDistanceNeighbours.get(randomDirection).x();
                    posMobIter.y = lowestDistanceNeighbours.get(randomDirection).y();
                }
                if (posMobIter.x == posGoal.x && posMobIter.y == posGoal.y) {
                    //log.debug("Arrived: Mob: {} Goal: {}", posMobIter, posGoal);
                    posMobList.remove(posMobIter);
                    posMobList.add(new V2((int)(Math.random() * 700), 20));
                }
            }
        }
        if (dtCount >= DT_PER_MOVE)
            dtCount = 0;
        dtCount += dt;

        Rectangle rMob = new Rectangle(posMob.getIntX(), posMob.getIntY(),
                dimMob.getIntX(), dimMob.getIntY());
        for (GridElem elem : posGrid) {
            if (elem.getPos().intersects(rMob)) {
                elem.isOccupied = true;
            } else {
                elem.isOccupied = false;
            }
        }

        if (velocity.getLength() < 0.01)
            velocity = new V2();
        if (acceleration.getLength() < 0.01)
            acceleration = new V2();

        return true;
    }

    List<GridElem> getLowestDistanceNeighbours(GridElem curr) {
        List<GridElem> result = new ArrayList<>(2);
        int currDistance = curr.distance;
        int currIndex = curr.index;

        GridElem testElem;

        if ((currIndex + 1) % 80 != 0) {
            testElem = getElem(posGrid, currIndex + 1);
            if (testElem != null && !testElem.isWall() && testElem.distance < currDistance)
                result.add(testElem);
        }
        if (currIndex % 80 != 0) {
            testElem = getElem(posGrid, currIndex - 1);
            if (testElem != null && !testElem.isWall() && testElem.distance < currDistance)
                result.add(testElem);
        }
        testElem = getElem(posGrid, currIndex + 80);
        if (testElem != null && !testElem.isWall() && testElem.distance < currDistance)
            result.add(testElem);
        testElem = getElem(posGrid, currIndex - 80);
        if (testElem != null && !testElem.isWall() && testElem.distance < currDistance)
            result.add(testElem);

        return result;
    }

    void buildBreadthFirstSearchIter(List<GridElem> gridElems) {

        if (!frontier.isEmpty()) {
            GridElem elem = frontier.remove();
            elem.setFrontier(false);

            if (elem.index % 80 != 0) {
                GridElem leftElem = getElem(gridElems, elem.getIndex() - 1);
                addElem(frontier, dictionary, leftElem, elem.distance);
            }
            if ((elem.index + 1) % 80 != 0) {
                GridElem rightElem = getElem(gridElems, elem.getIndex() + 1);
                addElem(frontier, dictionary, rightElem, elem.distance);
            }
            GridElem topElem = getElem(gridElems, elem.getIndex() + 80);
            addElem(frontier, dictionary, topElem, elem.distance);

            GridElem bottomElem = getElem(gridElems, elem.getIndex() - 80);
            addElem(frontier, dictionary, bottomElem, elem.distance);
        }
    }

    void buildBreadthFirstSearch(List<GridElem> gridElems, V2 goal) {
        int index = goal.getIntY() / 10 * 80 + goal.getIntX() / 10;
        GridElem goalElem = gridElems.get(index);
        //log.debug("Index: {}, Goal: {}", index, goalElem);

        Queue<GridElem> frontier = new ArrayDeque<>();
        frontier.add(goalElem);
        goalElem.setFrontier(true);

        Map<Integer, Boolean> dictionary = new HashMap<>();
        dictionary.put(goalElem.index, Boolean.TRUE);
        goalElem.setReached(true);

        while (!frontier.isEmpty()) {
            GridElem elem = frontier.remove();
            elem.setFrontier(false);

            if (elem.index % 80 != 0) {
                GridElem leftElem = getElem(gridElems, elem.getIndex() - 1);
                addElem(frontier, dictionary, leftElem, elem.distance);
            }
            if ((elem.index + 1) % 80 != 0) {
                GridElem rightElem = getElem(gridElems, elem.getIndex() + 1);
                addElem(frontier, dictionary, rightElem, elem.distance);
            }
            GridElem topElem = getElem(gridElems, elem.getIndex() + 80);
            addElem(frontier, dictionary, topElem, elem.distance);

            GridElem bottomElem = getElem(gridElems, elem.getIndex() - 80);
            addElem(frontier, dictionary, bottomElem, elem.distance);
        }
    }

    private void addElem(Queue<GridElem> frontier, Map<Integer, Boolean> dictionary, GridElem elem, int existingDistance) {
        if (elem != null && !elem.isWall() && dictionary.get(elem.getIndex()) == null) {
            elem.setFrontier(true);
            frontier.add(elem);
            dictionary.put(elem.index, true);
            elem.setReached(true);
            elem.setDistance(existingDistance + 1);
        }
    }

    GridElem getElem(List<GridElem> gridElems, int index) {
        try {
            return gridElems.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
