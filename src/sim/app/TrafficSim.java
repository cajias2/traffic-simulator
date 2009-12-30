package sim.app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sim.app.agents.Car;
import sim.app.agents.TrafficLight;
import sim.app.graph.CitySimState;
import sim.app.graph.Street;
import sim.app.graph.StreetXing;
import sim.app.utils.Orientation;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

@SuppressWarnings ( "serial" )
public class TrafficSim extends CitySimState
{
    
    private static Map<Street, List<Car>> _carLocations;
    
    private static final int CITY_SIZE = 5; // Number of intersections in the
    // city.
    public static final double XMIN = 0;
    public static final double XMAX = 800;
    public static final double YMIN = 0;
    public static final double YMAX = 600;
    public static final int MAX_CAR_COUNT = 2;
    
    /**
     * Creates a NetworkTest simulation with the given random number seed.
     */
    public TrafficSim(long seed)
    {
        super( seed );
        setCity( new DirectedSparseGraph<StreetXing, Street>() );
    }
    
    public static Map<Street, List<Car>> getCarLocations ()
    {
        return _carLocations;
    }
    
    /**
     * Start the Simulation.
     */
    @Override
    public void start ()
    {
        super.start(); // clear out the schedule
        
        schedule.reset();
        List<StreetXing> xings = new ArrayList<StreetXing>( CITY_SIZE );
        for ( int i = 0; i < CITY_SIZE; i++ )
        {
            xings.add( new StreetXing() );
            getCity().addVertex( xings.get( i ) );
        }
        
        Pair edge1 = new Pair( xings.get( 0 ), xings.get( 1 ) );
        Pair edge2 = new Pair( xings.get( 0 ), xings.get( 2 ) );
        Pair edge3 = new Pair( xings.get( 0 ), xings.get( 3 ) );
        Pair edge4 = new Pair( xings.get( 0 ), xings.get( 4 ) );
        
        Pair edge5 = new Pair( xings.get( 1 ), xings.get( 0 ) );
        Pair edge6 = new Pair( xings.get( 2 ), xings.get( 0 ) );
        Pair edge7 = new Pair( xings.get( 3 ), xings.get( 0 ) );
        Pair edge8 = new Pair( xings.get( 4 ), xings.get( 0 ) );
        
        // Edges going into 0
        getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10 ), edge1 );
        getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10 ), edge2 );
        getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge3 );
        getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge4 );
        // Edges going out of 0
        getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10  ), edge5 );
        getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10  ), edge6 );
        getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge7 );
        getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge8 );
        // Set traffic Light in middle intersection
        TrafficLight tf = new TrafficLight();
        ((StreetXing)xings.get( 0 )).setTrafficLight( tf );
        schedule.scheduleRepeating( tf);
        
        Steppable carGenerator = new Steppable()
        {
            DijkstraShortestPath<StreetXing, Street> routeMap = new DijkstraShortestPath<StreetXing, Street>( getCity() );
            
            public void step ( SimState state )
            {
                for ( int i = 0; i < carFlow() && Car.getNumberOfCars() < MAX_CAR_COUNT; i++ )
                {
                    StreetXing source = getSource();
                    StreetXing target = getTarget();
                    List<Street> trayectory = routeMap.getPath( source, target );
                    Car car = new Car( trayectory );
                    car.toDiePointer = schedule.scheduleRepeating (schedule.getTime(), Car.getNumberOfCars(),  car);
                }
            }
            
        };
        // Schedule the car Generator
        schedule.scheduleRepeating( Schedule.EPOCH, 1, carGenerator, 1 );      
    }
    
    /**
     * @return Target
     */
    private StreetXing getTarget ()
    {
        // TODO set random seed;
        Random rand = new Random( System.currentTimeMillis() );
        int targetIndex = 1 + rand.nextInt( getCity().getVertexCount() - 1 );
        
        return (StreetXing) ( getCity().getVertices().toArray() )[targetIndex];
    }
    
    /**
     * TODO: hardcoded to 0... need to be configurable = xml
     * 
     * @return
     */
    private StreetXing getSource ()
    {
        return (StreetXing) ( getCity().getVertices().toArray() )[0];
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
     * @param args
     */
    public static void main ( String[] args )
    {
        doLoop( TrafficSim.class, args );
    }
}
