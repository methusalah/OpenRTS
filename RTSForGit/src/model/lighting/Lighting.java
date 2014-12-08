/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.lighting;

import java.awt.Color;

/**
 *
 * @author Benoît
 */
public abstract class Lighting {
    public Color color;
    public double intensity = 1;

    public Lighting(Color color) {
        this.color = color;
    }
}
