/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.acting;

import model.battlefield.actors.Actor;
import model.battlefield.actors.AnimationActor;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.scene.Spatial;

/**
 *
 * @author Benoît
 */
public class AnimationPerformer extends Performer{
	public AnimationPerformer(ActorDrawer bs) {
		super(bs);
	}

	@Override
	public void perform(Actor a) {
		AnimationActor actor = (AnimationActor)a;
		if(actor.launched) {
			return;
		}
		actor.launched = true;
		Spatial s = actor.getParentModelActor().getViewElements().spatial;
		
		// Try modern JME3 AnimComposer
		com.jme3.anim.AnimComposer composer = s.getControl(com.jme3.anim.AnimComposer.class);
		if (composer != null) {
			boolean loop = (actor.cycle != AnimationActor.Cycle.Once);
			composer.setCurrentAction(actor.animName, com.jme3.anim.AnimComposer.DEFAULT_LAYER, loop);
			composer.setGlobalSpeed((float)actor.speed);
			return;
		}

		// Fallback to legacy AnimControl
		AnimControl animCtrl = s.getControl(AnimControl.class);
		if (animCtrl != null) {
			AnimChannel channel = animCtrl.getChannel(0);
			if (channel == null) {
				channel = animCtrl.createChannel();
			}
			channel.setAnim(actor.animName);
			switch (actor.cycle){
				case Once : channel.setLoopMode(LoopMode.DontLoop); break;
				case Loop : channel.setLoopMode(LoopMode.Loop); break;
				case Cycle : channel.setLoopMode(LoopMode.Cycle); break;
			}
			channel.setSpeed((float)actor.speed);
		}
	}
}
