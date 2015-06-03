package model.battlefield.map.parcel;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;

/**
 * Divides the tile based grid into parcels for performance purpose. the objectives : - group tiles for the graphic card to manage less objects, - divide map to
 * prevent the graphic car to draw it entirely at each frame. Other resolutions may offer better results. Resolution may become dynamic. The challenge here is
 * to smooth texture at parcels' frontiers (see ParcelMesh)
 */
public class ParcelManager {

	private static final int RESOLUTION = 10;

	private static List<ParcelMesh> meshes = new ArrayList<>();
	private static int widthJump;

	private ParcelManager() {
	}

	public static void createParcelMeshes(Map map) {
		widthJump = (int) (Math.ceil((double) map.width / RESOLUTION));
		int nbParcel = widthJump * (int) Math.ceil((double) map.height / RESOLUTION);
		for (int i = 0; i < nbParcel; i++) {
			getMeshes().add(new ParcelMesh());
		}

		for (int i = 0; i < map.width; i++) {
			for (int j = 0; j < map.height; j++) {
				int index = (int) (Math.floor(j / RESOLUTION) * widthJump + Math.floor(i / RESOLUTION));
				getMeshes().get(index).add(map.getTile(i, j));
			}
		}

		for (ParcelMesh mesh : getMeshes()) {
			mesh.compute();
		}
	}

	public static List<ParcelMesh> getParcelsFor(List<Tile> tiles) {
		List<ParcelMesh> res = new ArrayList<>();
		for (Tile t : tiles) {
			int index = (int) (Math.floor((t.y) / RESOLUTION) * widthJump + Math.floor((t.x) / RESOLUTION));
			if (!res.contains(getMeshes().get(index))) {
				res.add(getMeshes().get(index));
			}
		}
		return res;
	}

	public static List<ParcelMesh> updateParcelsFor(List<Tile> tiles) {
		List<ParcelMesh> meshes = getParcelsFor(tiles);
		for (ParcelMesh mesh : meshes) {
			mesh.reset();
		}
		for (ParcelMesh mesh : meshes) {
			mesh.compute();
		}
		return meshes;
	}

	public static List<ParcelMesh> getNeighbors(ParcelMesh parcelMesh) {
		List<ParcelMesh> res = new ArrayList<>();
		int index = getMeshes().indexOf(parcelMesh);
		if (index + 1 < getMeshes().size()) {
			res.add(getMeshes().get(index + 1));
		}

		if (index + widthJump - 1 < getMeshes().size()) {
			res.add(getMeshes().get(index + widthJump - 1));
		}
		if (index + widthJump < getMeshes().size()) {
			res.add(getMeshes().get(index + widthJump));
		}
		if (index + widthJump + 1 < getMeshes().size()) {
			res.add(getMeshes().get(index + widthJump + 1));
		}

		if (index - 1 >= 0) {
			res.add(getMeshes().get(index - 1));
		}

		if (index - widthJump - 1 >= 0) {
			res.add(getMeshes().get(index - widthJump - 1));
		}
		if (index - widthJump >= 0) {
			res.add(getMeshes().get(index - widthJump));
		}
		if (index - widthJump + 1 >= 0) {
			res.add(getMeshes().get(index - widthJump + 1));
		}

		return res;
	}

	public static List<ParcelMesh> getMeshes() {
		return meshes;
	}

	public static void setMeshes(List<ParcelMesh> meshes) {
		ParcelManager.meshes = meshes;
	}

}
