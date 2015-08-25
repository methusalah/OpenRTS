package util

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import exception.TechnicalException;

class FileUtil {

	static List<File> getFilesInDirectory(String folderPath, String allowedExtension = null) {
		List<File> res = new ArrayList<File>();
		File folder = new File(folderPath);
		if (!folder.exists()) {
			throw new TechnicalException("the folder " + folderPath +  " was not found.");
		}
		for (File f : folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory() || !allowedExtension || (allowedExtension && file.getPath().endsWith(allowedExtension));
			}
		})) {
			res.add(f);
		}
		return res;
	}
}
