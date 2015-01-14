package app;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Spatial;

public class MyDebugger {

	private BitmapText bitmap;
	private StringBuilder text = new StringBuilder();
	
	public MyDebugger(int x, int y, BitmapFont font) {
		bitmap = new BitmapText(font, false);
		bitmap.setLocalTranslation(x, y, 0);
		bitmap.setText("");
	}

	public Spatial getNode() {
		bitmap.setText(text.toString());
		return bitmap;
	}

	public void reset() {
		text = new StringBuilder();
	}

	public void add(Object... stuff) {
		for (Object o : stuff)
			text.append(o.toString());
		text.append("\n");
	}

}
