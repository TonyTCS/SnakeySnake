package com.example.snakeysnake.java;

public class SpeedApple implements AppleType {
    @Override
    public void applyEffect(Snake snake) {
        // Example effect for a speed apple: increase snake speed
        snake.increaseSpeed();
    }
}