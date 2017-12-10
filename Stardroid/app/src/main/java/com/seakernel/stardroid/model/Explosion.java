package com.seakernel.stardroid.model;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 12/9/2017.
 */

public class Explosion {
    private static final float EXPLOSION_ANIMATION_MILLISECONDS = 500;
    private static final int PARTICLES_COUNT = 50;

    private float mElapsedTime;
    private List<Particle> mParticles;

    public Explosion (SpaceShip ship) {
        super();

        mParticles = new ArrayList<>();

        createParticles(ship);
    }

    private void createParticles(SpaceShip ship) {
        for (int i = 0; i < PARTICLES_COUNT; i++) {
            Particle particle = new Particle(ship);
            mParticles.add(particle);
        }
    }

    public void draw(float[] mvpMatrix, float dt) {
        mElapsedTime += dt;

        for (Particle particle : mParticles) {
            particle.doDraw(mvpMatrix, dt);
        }
    }

    public boolean isFinished() {
        return mElapsedTime > EXPLOSION_ANIMATION_MILLISECONDS;
    }

    private class Particle extends StardroidShape {
        private float mSpeed;
        private float mMoveToX;
        private float mMoveToY;
        private double mMaxAngleRad = Math.PI * 2; // +- max angle
        private double mMovementDistance;

        public Particle (SpaceShip ship) {
            super();

            mPositionX = ship.getPositionX();
            mPositionY = ship.getPositionY();

            setCoordinates(ship);
        }

        private void setCoordinates(SpaceShip ship) {
            mSpeed = (float)(ship.getRawSpeed() - ship.getRawSpeed() * Math.random() * 0.6);

            mMovementDistance = mSpeed * EXPLOSION_ANIMATION_MILLISECONDS;

            double rand = Math.random();
            mMoveToX = ship.getPositionX() + (float) (mMovementDistance * (Math.cos(rand * mMaxAngleRad)));
            mMoveToY = ship.getPositionY() + (float) (mMovementDistance * (Math.sin(rand * mMaxAngleRad)));
        }

        @Override
        protected void initialize() {

        }

        @Override
        protected float[] getCoordinates() {
            final float size = 0.01f;
            return new float[] {
                    -size, -size, 0.0f, // bottom left
                    size, -size, 0.0f,  // bottom right
                    -size,  size, 0.0f, // top left
                    size,  size, 0.0f,  // top right
            };
        }

        @Override
        protected void draw(float[] mvpMatrix, float dt) {
            final float dx = mMoveToX - mPositionX;
            final float dy = mMoveToY - mPositionY;
            final float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            final float step = Math.min(mSpeed * dt, distance);

            if (mMoveToX != mPositionX) {
                mPositionX += step * (dx / distance);
            }

            if (mMoveToY != mPositionY) {
                mPositionY += step * (dy / distance);
            }
            Matrix.translateM(mvpMatrix, 0, mPositionX, mPositionY, 0.0f);
        }
    }
}
