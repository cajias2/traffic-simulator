package sim.app;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sim.app.agents.Car;
import sim.app.agents.TrafficLight;
import sim.app.graph.CitySimState;
import sim.app.graph.Street;
import sim.app.graph.StreetXing;
import sim.app.utils.Orientation;
import sim.app.xml.XmlParseService;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

@SuppressWarnings ( "serial" )
public class TrafficSim extends CitySimState
{
    
    private static String clazz = TrafficSim.class.getSimpleName();
    private static String _cityXml;
    private List<StreetXing> _sourceXings;
    private List<StreetXing> _destXings;
 
    public static final double XMIN = 0;
    public static final double XMAX = 800;
    public static final double YMIN = 0;
    public static final double YMAX = 600;
    public static int MAX_CAR_COUNT;
    
    /**
     * Creates a TrafficSim simulation with the given random number seed.
     */
    public TrafficSim(long seed, String cityXmlFileName_)
    {
        super( seed );
        XmlParseService parsedGraph = new XmlParseService( cityXmlFileName_ );
        setCity( parsedGraph.getGraph() );
        MAX_CAR_COUNT = parsedGraph.getMaxCars();
        _sourceXings = parsedGraph.getSourceXings();
        _destXings = parsedGraph.getDestXings();
    }
    
    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     */
    public TrafficSim(long seed)
    {
        super( seed );
        XmlParseService parsedGraph = new XmlParseService( _cityXml );
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
        schedule.reset();   // clear out the schedule  
        
        scheduleTrafficLights();
        
        Steppable carGenerator = new Steppable()
        {
            DijkstraShortestPath<StreetXing, Street> routeMap = new DijkstraShortestPath<StreetXing, Street>( getCity() );
            
            public void step ( SimState state )
            {
                for ( int i = 0; i < carFlow() && Car.getNumberOfCars() < MAX_CAR_COUNT; i++ )
                {
                    StreetXing source = getSource();
                    StreetXing target = getTarget();
                    // Make sure start and end are different. Otherwise... what's the point?
                    while( source.getId().equals( target.getId() ))
                    {
                      target = getTarget();  
                    }
                    List<Street> trayectory = routeMap.getPath( source, target );
                    Car car = new Car( trayectory );
                    car.toDiePointer = schedule.scheduleRepeating( schedule.getTime(), Car.getNumberOfCars(), car );
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
        while ( iter.hasNext())
        {            
            StreetXing xing = iter.next();
            if(xing.hasTrafficLight())
            {
                schedule.scheduleRepeating( xing.getTrafficLight() );
            }
        }
        
    }
    
    /**
     * Return a random car destination based on the {@code endOdds} attribute in the city xml.
     * <p/>
     * See {@link TraffiSimulation.xsd} for more info
     * @return Destination
     */
    private StreetXing getTarget ()
    {
        StreetXing pickedXing = null;
        Random rand = new Random( System.currentTimeMillis() );
        int targetIndex = rand.nextInt( 100 );
        int currentIndex = 0;
        
        for(StreetXing xing: _sourceXings)
        {
            currentIndex += xing.getStartOdds();
            if(currentIndex >= targetIndex)
            {
                pickedXing = xing;
                break;
            }
        }
        
        return pickedXing;
    }
    
    /**
     * Return a random car begining based on the {@code startingOdds} attribute in the city xml.
     * <p/>
     * See {@link TraffiSimulation.xsd} for more info
     * 
     * @return
     */
    private StreetXing getSource ()
    {
        StreetXing pickedXing = null;
        Random rand = new Random( System.currentTimeMillis() );
        int targetIndex = rand.nextInt( 100 );
        int currentIndex = 0;
        
        for(StreetXing xing: _destXings)
        {
            currentIndex += xing.getEndOdds();
            if(currentIndex >= targetIndex)
            {
                pickedXing = xing;
                break;
            }
        }
        
        return pickedXing;
    }
    
    /**
     * Return number of cars to generate per step... Should follow a sinoidal
     * function to mimic traffic waves. TODO hardcoded to 2.. should change as
     * per comment.
     * 
     * @return cars to generate per step
     */
    private int carFlow ()
    {
        schedule.getTime();
        return 1;
    }
    
    /**
     * Main
     * @param args
     */
    public static void main ( String[] args )
    {
        if ( args.length != 2 || "city".equals( args[0] ) )
        {
            System.err.println( "Usage: java " + clazz + ".jar -city [xml file]\n"
                    + "See TrafficSimulation.xsd for details" );
            System.exit( 1 );
            
        }
        _cityXml = args[1];        
        doLoop( TrafficSim.class, args );
    }
}
