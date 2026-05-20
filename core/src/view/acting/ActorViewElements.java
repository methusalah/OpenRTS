/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.acting;

import java.util.ArrayList;

import com.jme3.effect.Particle;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Benoît
 */
public class ActorViewElements {
    public Spatial spatial;
    public ParticleEmitter particleEmitter;
    public Node selectionCircle;
    public Node healthBar;
    public ArrayList<Particle> lastParticles;
}
