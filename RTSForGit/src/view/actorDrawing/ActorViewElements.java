/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.actorDrawing;

import com.jme3.animation.Bone;
import com.jme3.effect.Particle;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import view.jme.MyParticleEmitter;

/**
 *
 * @author Benoît
 */
public class ActorViewElements {
    public Spatial spatial;
    public ParticleEmitter particleEmitter;
    public Node selectionCircle;
    public ArrayList<Particle> lastParticles;
}
