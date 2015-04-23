package model.battlefield.lighting;

import java.awt.Color;

/**
 *
 */
public abstract class Lighting {
    public Color color;
    public double intensity = 1;

    public Lighting(Color color) {
        this.color = color;
    }
}
