package sim.app;

import java.io.IOException;
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
    
    final static String NODE_SIM = "simulation";
    final static String NODE_XINGS = "crossing";
    final static String NODE_CONS = "connection";
    
    final static String ATTR_NAME = "name";
    final static String ATTR_XING_HASTF = "hasTrafficLight";
    final static String ATTR_XING_START_ODDS = "startingOdds";
    final static String ATTR_XING_END_ODDS = "endingOdds";
    final static String ATTR_CONN_FROM = "from";
    final static String ATTR_CONN_TO = "to";
    final static String ATTR_CONN_OR = "orientation";
    final static String ATTR_CONN_LEN = "length";
    
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
    
    /**
     * Create a city based on the xml file passed in
     */
    private void createCity ()
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        
        try
        {
            db = dbf.newDocumentBuilder();
            
            Document doc = db.parse( _cityXml );
            doc.getDocumentElement().normalize();
            Map<String, StreetXing> xingMap = new HashMap<String, StreetXing>();
            
            /*
             * Create crossing instances from the xml.
             */
            NodeList xingNodes = doc.getElementsByTagName( NODE_XINGS );
            for ( int i = 0; i < xingNodes.getLength(); i++ )
            {
                Node xingNode = xingNodes.item( i );
                String xingName = xingNode.getAttributes().getNamedItem( ATTR_NAME ).getNodeValue();
                StreetXing xing = new StreetXing( xingName );
                
                Node tfNode = xingNode.getAttributes().getNamedItem( ATTR_XING_HASTF );
                if ( null != tfNode &&
                        (( null != tfNode.getNodeValue() || !"".equals( tfNode.getNodeValue() ) ) 
                                && true == Boolean.parseBoolean( tfNode.getNodeValue() ) ))
                {
                    xing.setTrafficLight( new TrafficLight() );
                }
                
                Node startNode = xingNode.getAttributes().getNamedItem( ATTR_XING_START_ODDS );
                if ( null != startNode && ( null != startNode.getNodeValue() || !"".equals( startNode.getNodeValue() ) ) )
                {
                    // TODO use startOdds
                    double startOdds = Double.parseDouble( startNode.getNodeValue() );
                }
                Node endNode = xingNode.getAttributes().getNamedItem( ATTR_XING_END_ODDS );
                if ( null != endNode && ( null != endNode.getNodeValue() || !"".equals( endNode.getNodeValue() ) ) )
                {
                    // TODO use endOdds
                    double endOdds = Double.parseDouble( endNode.getNodeValue() );
                }
                xingMap.put( xingName, xing );
                // Finally, add the vertex.
                getCity().addVertex( xing );
            }
            
            /*
             * Create all the connections
             */
            NodeList connNodes = doc.getElementsByTagName( NODE_CONS );
            for ( int i = 0; i < connNodes.getLength(); i++ )
            {
                Node connNode = connNodes.item( i );
                String fromXing = connNode.getAttributes().getNamedItem( ATTR_CONN_FROM ).getNodeValue();
                String toXing = connNode.getAttributes().getNamedItem( ATTR_CONN_TO ).getNodeValue();
                Orientation or = translateOr( connNode.getAttributes().getNamedItem( ATTR_CONN_OR ).getNodeValue() );
                double length = Double.parseDouble( connNode.getAttributes().getNamedItem( ATTR_CONN_LEN )
                        .getNodeValue() );
                
                Pair<StreetXing> edge = new Pair<StreetXing>( xingMap.get( fromXing ), xingMap.get( toXing ) );
                Street street = new Street( or, length );
                // Finally, add the edge
                getCity().addEdge( street, edge );
            }
        } catch ( SAXException e )
        {
            System.err.println( clazz + ".jar: File: " + _cityXml + " Does not conform to xsd."
                    + "See TrafficSimulation.xsd for details" );
            e.printStackTrace();
            System.exit( 1 );
        } catch ( IOException e )
        {
            System.err.println( clazz + ".jar: File: " + _cityXml + " Could not be opened." );
            System.exit( 1 );
        } catch ( Exception e )
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Translates orientation ENUM from {@link TrafficSimulation.xsd}
     * 
     * @param orString
     *            'NS' || 'EW'
     * @return
     */
    private Orientation translateOr ( String orString )
    {
        Orientation or;
        if ( "NS".equals( orString ) )
        {
            or = Orientation.NORTH_SOUTH;
        } else
        {
            or = Orientation.EAST_WEST;
        }
        return or;
    }
    
    /**
     * Start the Simulation.
     */
    @Override
    public void start ()
    {
        super.start(); // clear out the schedule
        
        schedule.reset();
        createCity();
        scheduleTrafficLights();
        // List<StreetXing> xings = new ArrayList<StreetXing>( CITY_SIZE );
        // for ( int i = 0; i < CITY_SIZE; i++ )
        // {
        // xings.add( new StreetXing() );
        // getCity().addVertex( xings.get( i ) );
        // }
        //        
        // Pair edge1 = new Pair( xings.get( 0 ), xings.get( 1 ) );
        // Pair edge2 = new Pair( xings.get( 0 ), xings.get( 2 ) );
        // Pair edge3 = new Pair( xings.get( 0 ), xings.get( 3 ) );
        // Pair edge4 = new Pair( xings.get( 0 ), xings.get( 4 ) );
        //        
        // Pair edge5 = new Pair( xings.get( 1 ), xings.get( 0 ) );
        // Pair edge6 = new Pair( xings.get( 2 ), xings.get( 0 ) );
        // Pair edge7 = new Pair( xings.get( 3 ), xings.get( 0 ) );
        // Pair edge8 = new Pair( xings.get( 4 ), xings.get( 0 ) );
        //        
        // // Edges going into 0
        // getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10 ), edge1
        // );
        // getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10 ), edge2
        // );
        // getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge3 );
        // getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge4 );
        // // Edges going out of 0
        // getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10 ), edge5
        // );
        // getCity().addEdge( new Street( Orientation.NORTH_SOUTH, 10 ), edge6
        // );
        // getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge7 );
        // getCity().addEdge( new Street( Orientation.EAST_WEST, 10 ), edge8 );
        // // Set traffic Light in middle intersection
        // TrafficLight tf = new TrafficLight();
        // ( (StreetXing) xings.get( 0 ) ).setTrafficLight( tf );
        // schedule.scheduleRepeating( tf );
        
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
