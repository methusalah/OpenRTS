/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity.definitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import exception.TechnicalException;

/**
 * @author Benoît
 */
public class DefParser {
	private static final String ID = "id";
	Map<File, Long> filesAndTimers = new HashMap<>();
	List<File> filesToRead = new ArrayList<>();

	public DefParser(String path) {
		ArrayList<File> filesAndDir = getFiles(path);
		while (!filesAndDir.isEmpty()) {
			ArrayList<File> toAdd = new ArrayList<>();
			for (File f : filesAndDir) {
				if (f.isFile()) {
					addFile(f);
				} else if (f.isDirectory()) {
					toAdd.addAll(getFiles(f.getAbsolutePath()));
				}
			}
			filesAndDir.clear();
			filesAndDir.addAll(toAdd);
		}
		readFile();
	}

	private ArrayList<File> getFiles(String folderPath) {
		ArrayList<File> res = new ArrayList<>();
		File folder = new File(folderPath);
		if (!folder.exists()) {
			throw new TechnicalException("the folder " + folderPath +  " was not found.");
		}
		for (File f : folder.listFiles()) {
			res.add(f);
		}
		return res;
	}

	public void addFile(File f) {
		filesAndTimers.put(f, 0l);
	}

	public void readFile() {
		filesToRead.clear();
		for (File f : filesAndTimers.keySet()) {
			if (f.lastModified() != filesAndTimers.get(f)) {
				filesAndTimers.put(f, f.lastModified());
				filesToRead.add(f);
			}
		}
		String log = "updated : ";
		for (File f : filesToRead) {
			try {
				String fileName = f.getName();
				log = log.concat(fileName+", ");
				XMLInputFactory inputFactory = XMLInputFactory.newInstance();
				InputStream in = new FileInputStream(f);
				XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

				Definition def = null;
				// read the XML document
				while (eventReader.hasNext()) {
					XMLEvent event = eventReader.nextEvent();
					if (event.isStartElement()) {
						def = parseEvent(event, def);
					} else if (event.isEndElement()) {
						String elementName = event.asEndElement().getName().getLocalPart();
						if (def != null && elementName.equals(def.getType())) {
							BuilderManager.submit(def);
							def = null;
						}
						// else
						// throw new
						// RuntimeException("("+fileName+") At line "+event.getLocation().getLineNumber()+", find a closing element that is not closing a definition"+elementName);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		if (!filesToRead.isEmpty()) {
			BuilderManager.buildLinks();
		}

	}

	private Definition parseEvent(XMLEvent event, Definition def) {
		StartElement se = event.asStartElement();
		String elementName = se.getName().getLocalPart();
		if (elementName.equals("catalog")) {
			return null;
		}

		Iterator<Attribute> attributes = se.getAttributes();

		if (def == null) {
			Attribute id = attributes.next();
			if (id.getName().toString() != ID) {
				throw new RuntimeException("At line " + event.getLocation().getLineNumber() + ", problem with definition '" + elementName
						+ "'. The first attribute of a definition must be called '" + ID + "'.");
			}
			def = new Definition(elementName, id.getValue());
			// LogUtil.logger.info("def cree "+def.type+" - "+def.id);
		} else {
			DefElement de = new DefElement(elementName);
			while (attributes.hasNext()) {
				Attribute a = attributes.next();
				de.addVal(a.getName().toString(), a.getValue());
			}
			def.getElements().add(de);
			// LogUtil.logger.info("    element ajouté : "+de.name+" - "+de.getVal());
		}
		return def;
	}
}
