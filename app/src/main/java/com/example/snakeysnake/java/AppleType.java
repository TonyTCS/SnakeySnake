package com.example.snakeysnake.java;

/**
 * An interface representing different types of apples that can have various effects on a Snake object.
 */
public interface AppleType {
    /**
     * Apply the effect of this apple type to the given snake.
     * Each implementing class should provide the specific effect logic.
     *
     * @param snake The Snake object that will receive the effect.
     */
    void applyEffect(Snake snake);
}