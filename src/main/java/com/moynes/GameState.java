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
    double adjustmentOffsetWhenCollides = 0.01;

    public GameState() {
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
        newAcceleration.add(new V2(velocity).multiply(-0.08));

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

        return true;
    }
}
