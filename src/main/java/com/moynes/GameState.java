package com.moynes;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameState {
    static float PIXELS_PER_METER = 100;
    double launchDegrees = 85.0;
    float launchSpeed = 800.0f / PIXELS_PER_METER; //m/s2
    float moveSpeed = 3.0f / PIXELS_PER_METER; //m/s2
    V2 velocity = new V2();
    V2 playerPos = new V2(0, 0);
    V2 projectilePos = null;
    V2 launcherEnd = setLauncherEnd(launchDegrees);

    private V2 setLauncherEnd(double launchDegrees) {
        V2 result = new V2((int)(Math.cos(Math.toRadians(launchDegrees))*100),
                (int)(Math.sin(Math.toRadians(launchDegrees))*100));
        return result;
    }

    V2 acceleration = new V2();
    V2 gravity = new V2(0, -9.8 / PIXELS_PER_METER);
    float airDrag = -0.8f / PIXELS_PER_METER;
    float groundFriction = -4.0f / PIXELS_PER_METER;
    double adjustmentOffsetWhenCollides = 0.01;

    int radius = 5;
    int width;
    int height;


    public GameState(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean update(KeyState keyState, long dt) {
        V2 newAcceleration = new V2();

        if (keyState.UP_KEY_DOWN) {
        }
        if (keyState.DOWN_KEY_DOWN) {
        }
        if (keyState.LEFT_KEY_DOWN) {
            launchDegrees+=0.1;
            launcherEnd = setLauncherEnd(launchDegrees);
        }
        if (keyState.RIGHT_KEY_DOWN) {
            launchDegrees-=0.1;
            launcherEnd = setLauncherEnd(launchDegrees);
        }

        if (keyState.SPACE_KEY_DOWN && projectilePos == null) {
            newAcceleration.x = Math.cos(Math.toRadians(launchDegrees));
            newAcceleration.y = Math.sin(Math.toRadians(launchDegrees));
            projectilePos = new V2(launcherEnd);
        }
        newAcceleration = newAcceleration.normalize().multiply(launchSpeed);
        if (projectilePos != null && projectilePos.y > 0)
            newAcceleration.add(new V2(velocity.x * airDrag, 0));
        else {
            newAcceleration.add(new V2(velocity.x * groundFriction, 0));
        }

        newAcceleration.add(new V2(gravity));

        if (projectilePos != null) {
            V2 testProjectilePos = new V2(projectilePos);
            //p = 1/2*a*sq(t) + v't + p
            testProjectilePos = (new V2(newAcceleration))
                    .multiply(dt * dt)
                    .multiply(0.5)
                    .add(new V2(velocity).multiply(dt))
                    .add(new V2(testProjectilePos));
            velocity = (new V2(newAcceleration).multiply(dt).add(velocity));
            acceleration = newAcceleration;

            projectilePos = testProjectilePos;

            if (projectilePos.y <= 0.0) {
                projectilePos.y = 0;
                velocity = new V2(velocity.x, 0);
            }
            if (projectilePos.x > 800) {
                projectilePos.x = 0;
            } else if (projectilePos.x < 0) {
                projectilePos.y = 0;
            }
            if (acceleration.x < 0) {
                acceleration.x = 0;
            }
            if (velocity.x < 0.0001) {
                velocity.x = 0;
                projectilePos = null;
            }
        }

//        if (velocity.getLength() < 0.01)
//            velocity = new V2();
//        if (acceleration.getLength() < 0.01 )
//            acceleration = new V2();

        //log.debug("Acceleration: {}, Velocity: {}, ProjectilePos: {}", acceleration, velocity, projectilePos);

        return true;
    }
}

