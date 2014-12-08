/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ressources.definitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import tools.LogUtil;


/**
 *
 * @author Benoît
 */
public class DefParser {
    private static final String ID = "id";
    HashMap<File, Long> files = new HashMap<>();
    ArrayList<File> filesToRead = new ArrayList<>();
    
    BuilderLibrary lib;
    
    public DefParser(BuilderLibrary lib){
        this.lib = lib;
    }
    
    public void addFile(File f){
        files.put(f, 0l);
    }
    
    public void readFile() {
        filesToRead.clear();
        for(File f : files.keySet())
            if(f.lastModified() != files.get(f)){
                files.put(f, f.lastModified());
                filesToRead.add(f);
            }
        
        for(File f : filesToRead)
            try {
                String fileName = f.getName();
                LogUtil.logger.info("Updating "+fileName);
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                InputStream in = new FileInputStream(f);
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

                Definition def = null;
                // read the XML document
                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();
                    if(event.isStartElement()) {
                        def = parseEvent(event, def);
                    } else if(event.isEndElement()){
                        String elementName = event.asEndElement().getName().getLocalPart();
                        if(def != null && elementName.equals(def.type)){
                            lib.submit(def);
                            def = null;
                        }
//                        else
//                            throw new RuntimeException("("+fileName+") At line "+event.getLocation().getLineNumber()+", find a closing element that is not closing a definition"+elementName);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
//        if(!filesToRead.isEmpty())
//            lib.linkPrototypes();
    }
    
    private Definition parseEvent(XMLEvent event, Definition def){
        StartElement se = event.asStartElement();
        String elementName = se.getName().getLocalPart();
        if(elementName.equals("catalog"))
            return null;
        
        Iterator<Attribute> attributes = se.getAttributes();
        
        

        if(def == null){
            Attribute id = attributes.next();
            if(id.getName().toString() != ID)
                throw new RuntimeException("At line "+event.getLocation().getLineNumber()+", problem with definition '"+elementName+"'. The first attribute of a definition must be called '"+ID+"'.");
            def = new Definition(elementName, id.getValue());
//                LogUtil.logger.info("def cree "+def.type+" - "+def.id);
        } else {
            DefElement de = new DefElement(elementName);
            while(attributes.hasNext()) {
                Attribute a = attributes.next();
                de.addVal(a.getName().toString(), a.getValue());
            }
            def.elements.add(de);
//            LogUtil.logger.info("    element ajouté : "+de.name+" - "+de.getVal());
        }
        return def;
    }
}
