package model.battlefield;

import geometry.tools.LogUtil;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;
import model.builders.MapStyleBuilder;
import model.builders.definitions.BuilderManager;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.fasterxml.jackson.databind.ObjectMapper;

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
				m.getTiles().add(new Tile(x, y, m));
			}
		}
		LogUtil.logger.info("   map builders");

		LogUtil.logger.info("   map's tiles' links");
		linkTiles(m);

		Battlefield res = new Battlefield(m);
		ModelManager.setBattlefield(res);

		LogUtil.logger.info("Loading done.");
		return res;
	}

	public Battlefield load() {
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
		Battlefield bField = null;


		try {
			LogUtil.logger.info("Loading battlefield " + file.getCanonicalPath() + "...");
			// this is the new JSON importer
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
			bField = mapper.readValue(file, Battlefield.class);

			// FIXME: remove the old parser
			// Serializer serializer = new Persister();
			// bField = serializer.read(Battlefield.class, file);
			bField.setFileName(file.getCanonicalPath());
			ModelManager.setBattlefield(bField);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (bField == null) {
			LogUtil.logger.info("Load failed");
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
				c.connect();
			}
		}

		int i = 0;
		LogUtil.logger.info("   cliffs' shapes");
		for (Tile t : bField.getMap().getTiles()) {
			for (Cliff c : t.getCliffs()) {
				BuilderManager.getCliffShapeBuilder(t.cliffShapeID).build(c);
				i++;
			}
		}
		LogUtil.logger.info("   cliffs' shapes " + i);


		ModelManager.getBattlefield().buildParcels();

		bField.getMap().resetTrinkets();
		bField.getEngagement().reset();

		LogUtil.logger.info("   texture atlas");
		bField.getMap().atlas.loadFromFile(bField.getFileName());
		LogUtil.logger.info("Loading done.");
		return bField;
	}

	public void save(Battlefield battlefield) {
		battlefield.getEngagement().save();
		battlefield.getMap().saveTrinkets();
		Serializer serializer = new Persister();
		try {
			if (battlefield.getFileName() != null) {
				LogUtil.logger.info("Saving battlefield overwriting " + battlefield.getFileName() + "...");
				serializer.write(battlefield, new File(battlefield.getFileName()));
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
					serializer.write(battlefield, new File(battlefield.getFileName()));
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(BattlefieldFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		LogUtil.logger.info("Saving texture atlas...");
		battlefield.getMap().atlas.saveToFile(battlefield.getFileName());
		LogUtil.logger.info("Done.");
	}

	private void linkTiles(Map map) {
		for (int x = 0; x < map.width; x++) {
			for (int y = 0; y < map.height; y++) {
				Tile t = map.getTile(x, y);
				t.map = map;
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
