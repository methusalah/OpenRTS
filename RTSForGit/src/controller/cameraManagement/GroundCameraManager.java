package controller.cameraManagement;


import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import geometry3D.Point3D;
import model.Model;
import view.math.Translator;

public class GroundCameraManager extends CameraManager {
    protected final static String ROTATE_LEFT = "rotateleft";
    protected final static String ROTATE_RIGHT = "rotateright";
    protected final static String ROTATE_UP = "rotateup";
    protected final static String ROTATE_DOWN = "rotatedown";
    protected final static String STRAFE_LEFT = "strafeleft";
    protected final static String STRAFE_RIGHT = "straferight";
    protected final static String MOVE_FOREWARD = "moveforeward";
    protected final static String MOVE_BACKWARD = "movebackward";

    private double maxSpeed = 10;
    private double maxRotSpeed = 100;
    private Point3D pos;
    private final Model model;
	
    public GroundCameraManager(Camera cam, Model model){
        super(cam);
        this.model = model;
        pos = Translator.toPoint3D(cam.getLocation());
        placeCam();
        setMappaings();
    }
    
    private void setMappaings(){
        mappings = new String[]{
            ROTATE_UP,
            ROTATE_DOWN,
            ROTATE_LEFT,
            ROTATE_RIGHT,
            
            STRAFE_LEFT,
            STRAFE_RIGHT,
            MOVE_FOREWARD,
            MOVE_BACKWARD,
        };
    }
    
    private void placeCam(){
        if(model.battlefield.map.isInBounds(pos.get2D()))
            pos.z = model.battlefield.map.getGroundAltitude(pos.get2D())+0.9;
        else
            pos.z = 0;
        cam.setLocation(Translator.toVector3f(pos));
    }

    @Override
    public void unregisterInputs(InputManager inputManager){
        for(String s : mappings)
            if(inputManager.hasMapping(s))
                inputManager.deleteMapping(s);
        inputManager.removeListener(this);
    }

    @Override
    public void registerInputs(InputManager inputManager){
        inputManager.addMapping(ROTATE_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(ROTATE_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(ROTATE_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(ROTATE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        
        inputManager.addMapping(STRAFE_LEFT, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(STRAFE_RIGHT, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(MOVE_FOREWARD, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addListener(this, mappings);
    }
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
        double velocity = tpf*maxSpeed*1;
        switch(name){
            case ROTATE_UP : rotateCamera(value, getLeft()); break;
            case ROTATE_DOWN : rotateCamera(-value, getLeft()); break;
            case ROTATE_LEFT : rotateCamera(value, Point3D.UNIT_Z); break;
            case ROTATE_RIGHT : rotateCamera(-value, Point3D.UNIT_Z); break;
                
            case STRAFE_LEFT : move(getLeft(), velocity); break;
            case STRAFE_RIGHT : move(getRight(), velocity); break;
            case MOVE_FOREWARD : move(getDir(), velocity); break;
            case MOVE_BACKWARD : move(getRear(), velocity); break;
        }
    }

    private void move(Point3D vec, double distance){
        pos = pos.getAddition(vec.getScaled(distance));
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        
    }
    
    protected void rotateCamera(float value, Point3D axis){
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis((float)maxRotSpeed * value, Translator.toVector3f(axis));

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
    }
    
    private Point3D getLeft(){
        return Translator.toPoint3D(cam.getLeft());
    }
    private Point3D getRight(){
        return Translator.toPoint3D(cam.getLeft()).getNegation();
    }
    private Point3D getDir(){
        return Translator.toPoint3D(cam.getDirection());
    }
    private Point3D getRear(){
        return Translator.toPoint3D(cam.getDirection()).getNegation();
    }
}
