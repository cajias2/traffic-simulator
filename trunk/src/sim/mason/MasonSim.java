package sim.mason;

import static java.lang.Math.floor;
import static java.lang.Math.sin;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.app.agents.display.vehicle.Car;
import sim.app.agents.display.vehicle.Vehicle;
import sim.app.road.Road;
import sim.app.road.StreetXing;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.xml.XmlParseService;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

@SuppressWarnings ( "serial" )
public class MasonSim extends CitySimState
{
    private static Logger _log;
    private static String clazz = MasonSim.class.getSimpleName();
    private static String _cityXml;
    private static Random	  _rand = new Random(System.currentTimeMillis());
    private final List<StreetXing> _sourceXings;
    private final List<StreetXing> _destXings;
    
    public static final double XMIN = 0;
    public static final double XMAX = 800;
    public static final double YMIN = 0;
    public static final double YMAX = 600;
    public static int MAX_CAR_COUNT;
    

    /**
     * Creates a MasonSim simulation with the given random number seed.
     */
    public MasonSim(long seed, String cityXmlFileName_, Logger log_)
    {
        super( seed );
        XmlParseService parsedGraph = new XmlParseService( cityXmlFileName_, log_ );
        setCity( parsedGraph.getGraph() );
        MAX_CAR_COUNT = parsedGraph.getMaxCars();
        _sourceXings = parsedGraph.getSourceXings();
        _destXings = parsedGraph.getDestXings();
        _log = log_;
    }
    
    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     */
    public MasonSim(long seed)
    {
        super( seed );
        XmlParseService parsedGraph = new XmlParseService( _cityXml, _log );
        setCity( parsedGraph.getGraph() );
        MAX_CAR_COUNT = parsedGraph.getMaxCars();
        _sourceXings = parsedGraph.getSourceXings();
        _destXings = parsedGraph.getDestXings();
    }
    
    /**
     * Start the Simulation.
     */
    @Override
    public void start ()
    {
        super.start();
        schedule.reset(); // clear out the schedule
        
        scheduleTrafficLights();
        
        Steppable carGenerator = new Steppable()
        {
	    DijkstraShortestPath<StreetXing, Road> routeMap = new DijkstraShortestPath<StreetXing, Road>(getCity());
            
            public void step ( SimState state )
            {
		double vhclThisStep = carFlow();
		for (int i = 0; Vehicle.getTotVhlCount() < MAX_CAR_COUNT && i < vhclThisStep; i++)
                {
                    StreetXing source = getSource();
		    StreetXing target = getDest();
                    // Make sure start and end are different. Otherwise...
                    // what's the point?
                    while ( source.getId().equals( target.getId() ) )
                    {
			target = getDest();
                    }
		    List<Road> trayectory = routeMap.getPath(source, target);
		    Vehicle vhcl = new Car(trayectory, getCity(), _log);
		    vhcl.toDiePointer = schedule.scheduleRepeating(schedule.getTime(), Vehicle.getTotVhlCount(), vhcl);
                }
            }
            
        };
        // Schedule the car Generator
        schedule.scheduleRepeating( Schedule.EPOCH, 1, carGenerator, 1 );
    }
    
    /**
     * Cicle through each street crossing and schedule traffic lights if found
     * O(# of street xings)
     */
    private void scheduleTrafficLights ()
    {
        Iterator<StreetXing> iter = getCity().getVertices().iterator();
        while ( iter.hasNext() )
        {
            StreetXing xing = iter.next();
            if ( xing.hasTrafficLight() )
            {
                schedule.scheduleRepeating( xing.getTrafficLight() );
            }
        }
        
    }

    /**
     * Return a random car begining based on the {@code startingOdds} attribute
     * in the city xml.
     * <p/>
     * See {@link TraffiSimulation.xsd} for more info
     * 
     * @return
     */
    protected StreetXing getDest() {
	StreetXing pickedXing = null;
	int targetIndex = _rand.nextInt(100);
	int currentIndex = 0;

	for (StreetXing xing : _destXings) {
	    currentIndex += xing.getEndOdds();
	    if (currentIndex >= targetIndex) {
		pickedXing = xing;
		break;
	    }
	}

	return pickedXing;
    }

    
    /**
     * Return a random car begining based on the {@code startingOdds} attribute
     * in the city xml.
     * <p/>
     * See {@link TraffiSimulation.xsd} for more info
     * 
     * @return
     */
    private StreetXing getSource() {
	StreetXing pickedXing = null;
	int targetIndex = _rand.nextInt(100);
	int currentIndex = 0;

	for (StreetXing xing : _sourceXings) {
	    currentIndex += xing.getStartOdds();
	    if (currentIndex >= targetIndex) {
		pickedXing = xing;
		break;
	    }
	}
	return pickedXing;
    }
    
    /**
     * Return number of cars to generate per step... Should follow a sinoidal
     * function to mimic traffic waves.     
     * 
     * @return cars to generate per step
     */
    private double carFlow ()
    {
        double carPerStep= floor(sin((schedule.getTime()+473)/100) *( MAX_CAR_COUNT/2) +  MAX_CAR_COUNT/2);
        _log.log( Level.FINE, "Will try to create: "+carPerStep+"cars" );
        return carPerStep;
        
    }

    /**
     * Main
     * 
     * @param args
     */
    public static void main ( String[] args )
    {
        _log = Logger.getLogger( "SimLogger" );
        _log.setLevel( Level.SEVERE );

        
        if ( args.length < 2 || "city".equals( args[0] ) )
        {
            System.err.println( "Usage: java -jar " + clazz + ".jar -city [xml file]\n"
                    + "See TrafficSimulation.xsd for details" );
            System.exit( 1 );
            
        }
        for ( int i = 0; i < args.length; i++ )
        {
            if ( "-city".equals( args[i] ) )
            {
                _cityXml = args[++i];
            } 
            else if( "-verbose".equals( args[i] ) || "-v".equals( args[i] ))
            {
                _log.setLevel( Level.INFO );
            } else if ( "-debug".equals( args[i] ) )
            {
                _log.setLevel( Level.FINE );
            }
        }
        if ( null == _cityXml || "".equals( _cityXml ) )
        {
            System.err.println( "Usage: java -jar " + clazz + ".jar -city [xml file]\n"
                    + "See TrafficSimulation.xsd for details" );
            System.exit( 1 );
        }
        
        doLoop( MasonSim.class, args );
    }
}
