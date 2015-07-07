/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package view.mapDrawing;

import geometry.geom3d.MyMesh;
import geometry.geom3d.Point3D;
import model.battlefield.map.Tile;
import model.battlefield.map.parcelling.Parcel;

/**
 * @author Benoît
 */
public class GridMesh extends MyMesh {

	static final double Z_OFFSET = 0.1;
	Parcel parcel;

	public GridMesh(Parcel parcel) {
		this.parcel = parcel;
		for (Tile t : parcel.getTiles()) {
			if (t.n() == null || t.e() == null) {
				continue;
			}

			int index = vertices.size();
			vertices.add(t.getPos().getAddition(0, 0, Z_OFFSET));
			vertices.add(t.n().getPos().getAddition(0, 0, Z_OFFSET));
			vertices.add(t.n().e().getPos().getAddition(0, 0, Z_OFFSET));
			vertices.add(t.e().getPos().getAddition(0, 0, Z_OFFSET));

			normals.add(Point3D.UNIT_Z);
			normals.add(Point3D.UNIT_Z);
			normals.add(Point3D.UNIT_Z);
			normals.add(Point3D.UNIT_Z);

			textCoord.add(t.getCoord());
			textCoord.add(t.n().getCoord());
			textCoord.add(t.n().e().getCoord());
			textCoord.add(t.e().getCoord());

			indices.add(index);
			indices.add(index + 2);
			indices.add(index + 1);
			indices.add(index);
			indices.add(index + 3);
			indices.add(index + 2);
		}
	}

	public void update() {
		vertices.clear();
		for (Tile t : parcel.getTiles()) {
			if (t.n() == null || t.e() == null) {
				continue;
			}
			vertices.add(t.getPos().getAddition(0, 0, Z_OFFSET));
			vertices.add(t.n().getPos().getAddition(0, 0, Z_OFFSET));
			vertices.add(t.n().e().getPos().getAddition(0, 0, Z_OFFSET));
			vertices.add(t.e().getPos().getAddition(0, 0, Z_OFFSET));
		}

	}
}
