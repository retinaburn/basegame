package com.moynes;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class GameState {
    float speed = 0.1f; //m/s2
    V2 velocity = new V2();
    V2 playerPos = new V2(0, 0);
    V2 acceleration = new V2();
    float drag = -0.08f;
    double adjustmentOffsetWhenCollides = 0.01;


    V2 circle1;
    V2 circle2;
    V2 circle3;
    V2 circle4;
    int radius = 5;
    int width;
    int height;

    double circle2Degrees = 0;
    double circle3Degrees = 0;

    public GameState(int width, int height) {
        this.width = width;
        this.height = height;
        circle1 = new V2(width/2.0 - radius, height/2.0 - radius);
    }

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
        }
        newAcceleration = newAcceleration.normalize().multiply(speed);
        newAcceleration.add(new V2(velocity).multiply(drag));

        V2 newPlayerPos = new V2(playerPos);
        //p = 1/2*a*sq(t) + v't + p
        newPlayerPos = (new V2(newAcceleration))
                .multiply(dt * dt)
                .multiply(0.5)
                .add(new V2(velocity).multiply(dt))
                .add(new V2(newPlayerPos));
        velocity = (new V2(newAcceleration).multiply(dt).add(velocity));
        acceleration = newAcceleration;

        if (velocity.getLength() < 0.01)
            velocity = new V2();
        if (acceleration.getLength() < 0.01)
            acceleration = new V2();


        circle2Degrees += dt / 10.0;
        circle2Degrees %= 360;
        double radians = Math.toRadians(circle2Degrees);
        double c2X = (width/2.0 - radius + Math.cos(radians) * 100);
        double c2Y = (height/2.0 - radius + Math.sin(radians) * 100);
        circle2 = new V2(c2X, c2Y);

        radians = Math.toRadians(-circle2Degrees);
        double c4X = (width/2.0 - radius + Math.cos(radians) * 50);
        double c4Y = (height/2.0 - radius + Math.sin(radians) * 50);
        circle4 = new V2(c4X, c4Y);

        circle3Degrees += dt / 2.0;
        radians = Math.toRadians(circle3Degrees);
        double c3X = c2X + Math.sin(radians)*30;
        double c3Y = c2Y + Math.cos(radians)*30;
        circle3 = new V2(c3X, c3Y);
        return true;
    }
}
