// should have a histogram
package stupidModel06;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JLabel;

import ec.util.MersenneTwisterFast;

import sim.display.Controller;
import sim.display.GUIState;
import sim.display.Console;
import sim.display.Display2D;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Portrayal;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.MiniHistogram;

public class StupidModelWithUI extends GUIState {
	public Display2D display;
	public JFrame displayFrame;
	public JFrame inspectorFrame;
	public ObjectGridPortrayal2D portrayal;
	private StupidInspector inspector;

	private double[] bugSizeValues;
    
    public static void main(String[] args) {
		StupidModelWithUI sim = new StupidModelWithUI();
		Console c = new Console(sim);
		c.setVisible(true);
    }

	public StupidModelWithUI() { 
		super(new StupidModel(System.currentTimeMillis()));
	}

    public void init(Controller c) {
    	super.init(c);
        
    	display = new Display2D(400,400,this,1); // at 400x400, we've got 4x4 per array position
    	displayFrame = display.createFrame();
    	c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
    	displayFrame.setVisible(true);

    	// attach the portrayals
    	portrayal = new ObjectGridPortrayal2D() {
    		public Portrayal getDefaultPortrayal() {
    			return new OvalPortrayal2D(Color.red,1.0) {
    				   public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
    					   if (object instanceof StupidBug) {
    						   int gbIntensity = Math.max(0,255 - (int)((StupidBug)object).getSize());
    						   paint = new Color(255,gbIntensity,gbIntensity);
							   super.draw(object, graphics, info);
    					   }
    				   }
    			};
    		}
    	};
    	display.attach(portrayal,"Stupid portrayal");
    
    	// specify the backdrop color  -- what gets painted behind the displays
    	display.setBackdrop(Color.black);
    	
        /* this doesn't work
       	histogramFrame = new JFrame();
    	histogramFrame.getContentPane().add(bugSizeHistogram);
    	c.registerFrame(histogramFrame);   // register the frame so it appears in the "Display" list
    	histogramFrame.setVisible(true); */
     }

	public void start() {
		super.start();
    	portrayal.setField(((StupidModel) state).getBugSpace());
    	inspector = new StupidInspector(((StupidModel)state).getBugs());
//        	display.attach( inspector, "Inspector" );
    	display.reset();
    	display.repaint();
        inspectorFrame = new JFrame("Stupid inspector");
//        inspectorFrame.getContentPane().add(inspector, "Center");
        inspectorFrame.setContentPane(inspector);
        inspectorFrame.pack();
        inspectorFrame.setVisible(true);
        controller.registerFrame(inspectorFrame);
        inspectorFrame.repaint();
        scheduleImmediateRepeat(true, new Steppable() {
        	public void step(SimState state) {
        		inspector.updateInspector();
        		inspectorFrame.repaint();
        	}
        });

/*	just had this in here to see when start method is
    called
    	JFrame f = new JFrame();
		f.getContentPane().add(new JLabel("start"));
		f.pack();
		f.show(); */
    }
            
	public void quit() {
		super.quit();
        
		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;  // let gc
		display = null;       // let gc
    }

	public Object getSimulationInspectedObject() {
		return state;
	}

}
