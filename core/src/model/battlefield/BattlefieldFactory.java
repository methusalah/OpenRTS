package model.battlefield;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ModelManager;
import model.builders.MapArtisanUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * this class serializes and deserializes a battlefield into and from files everything is translated in and from XML format, except for the texture atlas of the
 * ground (buffer array)
 */
public class BattlefieldFactory {

	private static final Logger logger = Logger.getLogger(BattlefieldFactory.class.getName());
	private static final String BATTLEFIELD_FILE_EXTENSION = "btf";

	public BattlefieldFactory() {
	}

	public Battlefield getNew(int width, int height) {
		logger.info("Creating new battlefield...");

		Battlefield res = new Battlefield();
		MapArtisanUtil.buildMap(res);

		logger.info("Loading done.");
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
			logger.info("Loading battlefield " + file.getCanonicalPath() + "...");
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
			bField = mapper.readValue(file, Battlefield.class);
			bField.setFileName(file.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (bField == null) {
			logger.info("Load failed");
			ModelManager.setBattlefieldReady();
			return null;
		}

		MapArtisanUtil.buildMap(bField);

		logger.info("   texture atlas");
		bField.getMap().getAtlas().loadFromFile(bField.getFileName(), "atlas");
		bField.getMap().getCover().loadFromFile(bField.getFileName(), "cover");
		logger.info("Loading done.");
		return bField;
	}

	public void save(Battlefield battlefield) {
		battlefield.getEngagement().save();
		battlefield.getMap().saveTrinkets();

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			if (battlefield.getFileName() != null) {
				logger.info("Saving battlefield overwriting " + battlefield.getFileName() + "...");
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
					logger.info("Saving map as " + battlefield.getFileName() + "...");
					mapper.writeValue(new File(battlefield.getFileName()), battlefield);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(BattlefieldFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		logger.info("Saving texture atlas...");
		battlefield.getMap().getAtlas().saveToFile(battlefield.getFileName(), "atlas");
		battlefield.getMap().getCover().saveToFile(battlefield.getFileName(), "cover");
		logger.info("Done.");
	}
}
