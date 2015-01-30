/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.cliff.faces;

import collections.Ring;
import geometry3D.Point3D;

import java.util.ArrayList;

import model.battlefield.map.Trinket;
import model.battlefield.map.cliff.Cliff;

/**
 *
 * @author Benoît
 */
public abstract class Face {
    final protected Cliff cliff;
	
    public Face(Cliff cliff) {
    	this.cliff = cliff;
	}
    public abstract Ring<Point3D> getLowerGround();
    public abstract Ring<Point3D> getUpperGround();
    
    public Ring<Point3D> getRotation(Ring<Point3D> ground){
    	Ring<Point3D> res = new Ring<>();
    	for(Point3D p : ground)
    		res.add(p.getRotationAroundZ(cliff.angle));
    	return res;
    	
    }
    public abstract String getType();
}
