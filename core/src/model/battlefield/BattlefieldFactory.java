package model.battlefield;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import tools.LogUtil;
import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;
import model.battlefield.map.parcel.ParcelManager;
import model.builders.MapStyleBuilder;
import model.builders.definitions.BuilderManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

/**
 * this class serializes and deserializes a battlefield into and from files everything is translated in and from XML format, except for the texture atlas of the
 * ground (buffer array)
 */
public class BattlefieldFactory {
	private static final String BATTLEFIELD_FILE_EXTENSION = "btf";

	public BattlefieldFactory() {
	}

	public Battlefield getNew(int width, int height) {
		LogUtil.logger.info("Creating new battlefield...");
		MapStyleBuilder styleBuilder = BuilderManager.getMapStyleBuilder("StdMapStyle");
		Map m = new Map(styleBuilder.width, styleBuilder.height);
		styleBuilder.build(m);

		for (int y = 0; y < m.height; y++) {
			for (int x = 0; x < m.width; x++) {
				m.getTiles().add(new Tile(x, y));
			}
		}
		LogUtil.logger.info("   map builders");

		LogUtil.logger.info("   map's tiles' links");
		linkTiles(m);

		Battlefield res = new Battlefield();
		res.setMap(m);

		LogUtil.logger.info("Loading done.");
		return res;
	}

	public Battlefield loadWithFileChooser() {
		final JFileChooser fc = new JFileChooser(ModelManager.DEFAULT_MAP_PATH);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("RTS Battlefield file (*." + BATTLEFIELD_FILE_EXTENSION + ")", BATTLEFIELD_FILE_EXTENSION);
		fc.addChoosableFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			return load(f);
		}
		return null;
	}

	public Battlefield load(String fname) {
		return load(new File(fname));
	}

	public Battlefield load(File file) {
		ModelManager.setBattlefieldUnavailable();
		Battlefield bField = null;

		try {
			LogUtil.logger.info("Loading battlefield " + file.getCanonicalPath() + "...");
			// this is the new JSON importer
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

			bField = mapper.readValue(file, Battlefield.class);

			bField.setFileName(file.getCanonicalPath());
			bField.getMap().atlas.finalize();
			bField.getMap().cover.finalize();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (bField == null) {
			LogUtil.logger.info("Load failed");
			ModelManager.setBattlefieldReady();
			return null;
		}

		LogUtil.logger.info("   builders");

		BuilderManager.getMapStyleBuilder(bField.getMap().mapStyleID).build(bField.getMap());

		LogUtil.logger.info("   tiles' links");
		linkTiles(bField.getMap());

		LogUtil.logger.info("   ramps");
		for (Ramp r : bField.getMap().ramps) {
			r.connect(bField.getMap());
		}

		for (Tile t : bField.getMap().getTiles()) {
			int minLevel = t.level;
			int maxLevel = t.level;
			for (Tile n : bField.getMap().get8Around(t)) {
				maxLevel = Math.max(maxLevel, n.level);
			}
			if (minLevel != maxLevel) {
				t.setCliff(minLevel, maxLevel);
			}
		}

		LogUtil.logger.info("   cliffs' connexions");
		for (Tile t : bField.getMap().getTiles()) {
			for (Cliff c : t.getCliffs()) {
				c.connect(bField.getMap());
			}
		}

		int i = 0;
		for (Tile t : bField.getMap().getTiles()) {
			for (Cliff c : t.getCliffs()) {
				BuilderManager.getCliffShapeBuilder(t.getCliffShapeID()).build(c);
				i++;
			}
		}
		LogUtil.logger.info("   cliffs' shapes (" + i+")");

		ParcelManager.createParcelMeshes(bField.getMap());

		LogUtil.logger.info("   texture atlas");
		bField.getMap().atlas.loadFromFile(bField.getFileName(), "atlas");
		bField.getMap().cover.loadFromFile(bField.getFileName(), "cover");
		LogUtil.logger.info("Loading done.");
		return bField;
	}

	public void save(Battlefield battlefield) {
		battlefield.getEngagement().save();
		battlefield.getMap().saveTrinkets();

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			if (battlefield.getFileName() != null) {
				LogUtil.logger.info("Saving battlefield overwriting " + battlefield.getFileName() + "...");
				mapper.writeValue(new File(battlefield.getFileName()), battlefield);
			} else {
				final JFileChooser fc = new JFileChooser(ModelManager.DEFAULT_MAP_PATH);
				fc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("RTS Battlefield file (*." + BATTLEFIELD_FILE_EXTENSION + ")",
						BATTLEFIELD_FILE_EXTENSION);
				fc.addChoosableFileFilter(filter);
				int returnVal = fc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					int i = f.getName().lastIndexOf('.');
					if (i == 0 || !f.getName().substring(i + 1).equals(BATTLEFIELD_FILE_EXTENSION)) {
						f = new File(f.toString() + "." + BATTLEFIELD_FILE_EXTENSION);
					}

					battlefield.setFileName(f.getCanonicalPath());
					LogUtil.logger.info("Saving map as " + battlefield.getFileName() + "...");
					mapper.writeValue(new File(battlefield.getFileName()), battlefield);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(BattlefieldFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		LogUtil.logger.info("Saving texture atlas...");
		battlefield.getMap().atlas.saveToFile(battlefield.getFileName(), "atlas");
		battlefield.getMap().cover.saveToFile(battlefield.getFileName(), "cover");
		LogUtil.logger.info("Done.");
	}

	private void linkTiles(Map map) {
		for (int x = 0; x < map.width; x++) {
			for (int y = 0; y < map.height; y++) {
				Tile t = map.getTile(x, y);
				if (x > 0) {
					t.w = map.getTile(x - 1, y);
				}
				if (x < map.width - 1) {
					t.e = map.getTile(x + 1, y);
				}
				if (y > 0) {
					t.s = map.getTile(x, y - 1);
				}
				if (y < map.height - 1) {
					t.n = map.getTile(x, y + 1);
				}
			}
		}

	}
}
