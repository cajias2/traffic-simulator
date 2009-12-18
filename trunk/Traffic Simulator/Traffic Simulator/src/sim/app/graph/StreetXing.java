package sim.app.graph;

/*
 * @author Raul Cajias
 */
import java.awt.Font;
import java.util.Queue;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

@SuppressWarnings("serial")
public class StreetXing implements Steppable
{

	private final String ID;
	private final int _steps = 0;
	private Queue carsWaiting;

	public Font nodeFont = new Font("SansSerif", Font.PLAIN, 12);

	/**
	 * Constructor for class StreetXingNode
	 * 
	 * @author biggie
	 */
	public StreetXing(String _id)
	{
		ID = _id;
	}

	/**
	 * 
	 * @author biggie
	 */
	public String toString()
	{
		return ID;
	}


	public void step(SimState state) {
		// TODO Auto-generated method stub
		
	}

}
