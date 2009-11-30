package stupidModel15;

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
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Portrayal;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

public class StupidModelWithUI extends GUIState {
	private final int MAX_INTENSITY = 100;
	private final double FOOD_FOR_MAX_INTENSITY = .2; 
	public Display2D display;
	public JFrame displayFrame;
	public ObjectGridPortrayal2D bugPortrayal;
	public ObjectGridPortrayal2D habitatPortrayal;
	private Controller theController;
	
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
    	theController = c;
    	setupDisplay();
    }
    
    private void setupDisplay() {
    	StupidModel theModel = (StupidModel) state;
  	  DoubleFileGridReader theReader = new DoubleFileGridReader(theModel.getDataFile());
	  if (!theReader.nextData()) {
			System.err.println("Empty data file");
			System.exit(0);
			}
	  int x = theReader.getX();		
	  int y = theReader.getY();
	  theReader.close();
	  int xSpaceSize = x+1;
	  int ySpaceSize = y+1;
    	display = new Display2D(4 * xSpaceSize, 4 * ySpaceSize, this, 1); // at 400x400, we've got 4x4 per array position
    	displayFrame = display.createFrame();
    	theController.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
    	displayFrame.setVisible(true);

    	// attach the portrayals
    	bugPortrayal = new ObjectGridPortrayal2D() {
    		public Portrayal getDefaultPortrayal() {
    			return new OvalPortrayal2D(Color.red,1.0) {
    				   public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
    					   if (object instanceof StupidBug) {
    						   StupidBug theBug = (StupidBug) object;
    						   double sizePct = theBug.getSize() / ((StupidModel)state).getBugReproductionSize();
    						   int gbIntensity = Math.max(0,255 - (int)(sizePct*255));
    						   paint = new Color(255,gbIntensity,gbIntensity);
							   super.draw(object, graphics, info);
    					   }
    				   }
    			};
    		}
    	};
    	habitatPortrayal = new ObjectGridPortrayal2D() {
    		public Portrayal getDefaultPortrayal() {
    			return new RectanglePortrayal2D(Color.green,1.0) {
    				   public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
    					   if (object instanceof HabitatCell) {
    						   HabitatCell theCell = (HabitatCell) object;
    						   double food = Math.min(1.0, theCell.getFoodAvailable() / FOOD_FOR_MAX_INTENSITY);
    						   int gIntensity = (int) (food * MAX_INTENSITY);
    						   paint = new Color(0,gIntensity,0);
							   super.draw(object, graphics, info);
    					   }
    				   }
    			};
    		}
    	};
        display.attach(habitatPortrayal, "Stupid habitat");
    	display.attach(bugPortrayal,"Stupid portrayal");
    	// specify the backdrop color  -- what gets painted behind the displays
    	display.setBackdrop(Color.black);
    }

	public void start() {
		super.start();
		// this is the logical place to call this,
		// because then the model knows the dimensions
		// of the habitat.  But when I wrote it this way,
		// I could only get the program to work in the 
		// debugger.
		//setupDisplay();
    	bugPortrayal.setField(((StupidModel) state).getBugSpace());
    	habitatPortrayal.setField(((StupidModel) state).getHabitatSpace());
    	display.reset();
    	display.repaint();
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
