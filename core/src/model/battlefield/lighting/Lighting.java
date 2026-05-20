package model.battlefield.lighting;

import util.compat.Color;

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
