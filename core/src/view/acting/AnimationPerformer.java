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
		AnimChannel channel = s.getControl(AnimControl.class).getChannel(0);
		channel.setAnim(actor.animName);
		switch (actor.cycle){
			case Once : channel.setLoopMode(LoopMode.DontLoop); break;
			case Loop : channel.setLoopMode(LoopMode.Loop); break;
			case Cycle : channel.setLoopMode(LoopMode.Cycle); break;
		}
		channel.setSpeed((float)actor.speed);
	}
}
