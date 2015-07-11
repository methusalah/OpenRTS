package view.acting;

import view.math.TranslateUtil;

import com.jme3.audio.AudioNode;

import model.battlefield.actors.Actor;
import model.battlefield.actors.SoundActor;

public class SoundPerformer extends Performer{

	public SoundPerformer(ActorDrawer bs) {
		super(bs);
	}

	@Override
	public void perform(Actor a) {
		SoundActor actor = (SoundActor)a;
		AudioNode audio = actorDrawer.getAudioNode(actor.soundPath);
		audio.setPositional(actor.positional);
		if(actor.positional)
			audio.setLocalTranslation(TranslateUtil.toVector3f(actor.getParentModelActor().getPos()));
		
		audio.setLooping(actor.looping);
		audio.setVolume((float)(actor.volume));
		
		audio.setRefDistance(4);
		audio.setReverbEnabled(false);
		
		audio.playInstance();
		a.stopActing();
	}

}
