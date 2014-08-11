/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.battlefield;

import controller.GUI;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import java.util.ArrayList;
import model.Commander;
import model.Reporter;
import model.army.data.Unit;
import model.army.Unity;

/**
 *
 * @author Benoît
 */
public class BattleFieldGUI extends GUI {

    Commander c;
    Reporter r;
    ArrayList<Unity> unities = new ArrayList<>();
    
    public BattleFieldGUI(Nifty nifty, Commander commander, Reporter reporter) {
        this.nifty = nifty;
        nifty.setIgnoreKeyboardEvents(true);
        nifty.fromXml("interface/screen.xml", "hud", this);
        r = reporter;
        c = commander;
    }
    
    public void selectAll(){
        c.selectAll();
    }

    @Override
    public void update() {
        String n = System.getProperty("line.separator");

        // update unities
        unities = c.getUnitiesInContext();
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
        if(c.selection.size() == 1){
            Unit u = c.selection.get(0);
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
    
    private Element getElement(String s){
        return nifty.getCurrentScreen().findElementByName(s);
    }
    
    public void select(String s){
        int index = Integer.parseInt(s);
        c.selectUnityInContext(unities.get(index));
    }
}
