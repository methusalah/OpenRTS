/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.battlefield;

import controller.Controller;
import controller.GUIController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import java.util.ArrayList;
import model.Commander;
import model.Reporter;
import model.army.data.Unit;
import model.army.Unity;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class BattlefieldGUI extends GUIController {

    Commander commander;
    Reporter reporter;
    ArrayList<Unity> unities = new ArrayList<>();
    
    public BattlefieldGUI(Nifty nifty, Controller controller, Commander commander, Reporter reporter) {
        super(controller, nifty);
        this.reporter = reporter;
        this.commander = commander;
    }
    
    @Override
    public void activate(){
        nifty.gotoScreen("hud");
    }
    
    public void selectAll(){
        commander.selectAll();
    }

    @Override
    public void update() {
        if(!nifty.isActive("interface/screen.xml", "hud"))
            return;
        String n = System.getProperty("line.separator");

        // update unities
        unities = commander.getUnitiesInContext();
        // Unity selectors
        for(int i=0; i<5; i++){
            if(i > unities.size()-1){
                if(getElement("psel"+i).isVisible())
                    getElement("psel"+i).hide();
            } else {
                if(!getElement("psel"+i).isVisible())
                    getElement("psel"+i).show();
                if(getElement("sel"+i) != null && getElement("sel"+i).getRenderer(TextRenderer.class)!=null){
                    Unity u = unities.get(i);
                    getElement("sel"+i).getRenderer(TextRenderer.class).setText(u.get(0).UIName+n+u.size());
                }
            }
        }
        
        // update info
        if(commander.selection.size() == 1){
            Unit u = commander.selection.get(0);
            getElement("unitName").getRenderer(TextRenderer.class).setText(Reporter.getName(u));
            getElement("unitHealth").getRenderer(TextRenderer.class).setText(Reporter.getHealth(u));
            getElement("unitState").getRenderer(TextRenderer.class).setText(Reporter.getState(u));
            getElement("unitOrder").getRenderer(TextRenderer.class).setText(Reporter.getOrder(u));
            getElement("unitHolding").getRenderer(TextRenderer.class).setText(Reporter.getHolding(u));
            getElement("info").show();
        } else
            getElement("info").hide();

            
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }
    
    public void select(String s){
        int index = Integer.parseInt(s);
        commander.selectUnityInContext(unities.get(index));
    }
}
