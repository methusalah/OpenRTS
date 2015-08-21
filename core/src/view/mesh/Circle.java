package view.mesh;

import java.nio.FloatBuffer;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class Circle extends Mesh {
	private Vector3f center;
	private float radius;
	private int samples;

	public Circle(float radius) {
		this(Vector3f.ZERO, radius, 16);
	}

	public Circle(float radius, int samples) {
		this(Vector3f.ZERO, radius, samples);
	}

	public Circle(Vector3f center, float radius, int samples) {
		super();
		this.center = center;
		this.radius = radius;
		this.samples = samples;

		setMode(Mode.Lines);
		updateGeometry();
	}

	protected void updateGeometry() {
		FloatBuffer positions = BufferUtils.createFloatBuffer(samples * 3);
		FloatBuffer normals = BufferUtils.createFloatBuffer(samples * 3);
		short[] indices = new short[samples * 2];

		float rate = FastMath.TWO_PI / samples;
		float angle = 0;
		for (int i = 0; i < samples; i++) {
			float x = FastMath.cos(angle) + center.x;
			float y = FastMath.sin(angle) + center.y;
			positions.put(x * radius).put(y * radius).put(center.z);
			normals.put(new float[] { 0, 1, 0 });
			indices[i * 2] = (short) i;
			indices[i * 2 + 1] = (short) ((i + 1) % samples);
			angle += rate;
		}

		setBuffer(Type.Position, 3, positions);
		setBuffer(Type.Normal, 3, normals);
		setBuffer(Type.Index, 2, indices);

		setBuffer(Type.TexCoord, 2, new float[] { 0, 0, 1, 1 });

		updateBound();
	}
}