/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import model.battlefield.abstractComps.FieldComp;


/**
 * @author mario
 */
public final class EntityManager {

	private static final Logger logger = Logger.getLogger(EntityManager.class.getName());
	public static int NOT_VALID_ID = -1;
	private static int idx = 0;
	private static Map<Integer, FieldComp> entities = new HashMap<Integer, FieldComp>();

	// no instancing from outside
	private EntityManager() {

	}

	public static Integer registerEntity(FieldComp entity) {
		int id = idx++;
		entities.put(id, entity);
		logger.info("register new Entity:" + id);
		return id;
	}

	public static boolean isValidId(long id) {
		return id > NOT_VALID_ID;
	}

	public static FieldComp getEntity(int entityId) {
		return entities.get(entityId);
	}

}
