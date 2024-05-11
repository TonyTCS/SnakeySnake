package com.example.snakeysnake.java;

public class NormalApple implements AppleType {
    @Override
    public void applyEffect(Snake snake) {
        // Increase snake size
        snake.increaseSize();
    }
}