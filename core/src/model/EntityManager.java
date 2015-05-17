/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


/**
 * @author mario
 */
public final class EntityManager {

	public static long NOT_VALID_ID = -1;
	private static long idx = 0;

	// no instancing from outside
	private EntityManager() {

	}

	public static long getNewEntityId() {
		return idx++;
	}

	public static boolean isValidId(long id) {
		return id > NOT_VALID_ID;
	}
}
