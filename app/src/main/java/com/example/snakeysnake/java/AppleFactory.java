package com.example.snakeysnake.java;

import java.util.Random;

public class AppleFactory {
    public AppleType createApple() {
        Random random = new Random();
        int type = random.nextInt(2);  // Assume 0 for NormalApple, 1 for SpeedApple
        if (type == 0) {
            return new NormalApple();
        } else {
            return new SpeedApple();
        }
    }
}