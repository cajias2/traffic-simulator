/**
 * 
 */
package sim.app.social.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import sim.agents.Agent;
import sim.graph.utils.Edge;

import com.mysql.jdbc.Statement;

/**
 * @author biggie
 */
public class DBManager {
    /**
     * 
     */
    private static final String INSERT_EDGES_PER_STEP_STMT = "insert into graphs (sim_id, step, from_node, to_node, is_create_edge) "
	    + "values (?,?,(SELECT id FROM nodes where node = ? and sim_id = ? ),"
	    + "(SELECT id FROM nodes where node = ? and sim_id = ? ), ?)";
    /**
     * 
     */
    private static final String SELECT_NODES_PER_STEP_STMT = "select node from nodes where sim_id = ? and step_created = ?";
    /**
     * 
     */
    private static final String SELECT_EDGES_PER_STEP_STMT = "select from_node, to_node, is_create_edge from graphs where sim_id = ? and step = ?";
    private static final String HOST = "localhost:3306";
    private static final String SCHEMA = "socSimDB";
    private static final String USR = "root";
    private static final String PWD = "miramar";
    private static final String DB_URL = "jdbc:mysql://" + HOST + "/" + SCHEMA + "?user=" + USR + "&password=" + PWD;
    private PreparedStatement _pstmtNode;
    private PreparedStatement _pstmtEdge;

    private Connection _conn;

    /**
     * Constructor
     * Instantiates a new DB Connection.
     */
    public DBManager() {
	try {
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    _conn = DriverManager.getConnection(DB_URL);
	} catch (SQLException e) {
	    displaySQLErrors(e);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }

    /**
     * Creates a new simulation record in the DB
     * 
     * @return new simulation row id.
     */
    public Integer newSimulation() {
	PreparedStatement pstmt = null;
	Integer newSimRowID = null;
	try {
	    pstmt = _conn.prepareStatement("insert into simulations (time_start) values (?)",
		    Statement.RETURN_GENERATED_KEYS);
	    pstmt.setLong(1, System.currentTimeMillis());
	    pstmt.addBatch();
	    pstmt.executeBatch();
	    ResultSet resultSet = pstmt.getGeneratedKeys();
	    if (resultSet != null && resultSet.next()) {
		newSimRowID = resultSet.getInt(1);
	    }
	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	}
	return newSimRowID;
    }

    /**
     * Add individual node insert statments for batch insert.
     * 
     * @param simID_
     * @param node_
     */
    public void addNode(Integer simID_, Integer node_, long stepCreated_) {

	try {
	    if (_pstmtNode == null) {
		_pstmtNode = _conn.prepareStatement("insert into nodes (sim_id, node, step_created) values (?, ?, ?)");
	    }
	    _pstmtNode.setLong(1, simID_);
	    _pstmtNode.setLong(2, node_);
	    _pstmtNode.setLong(3, stepCreated_);
	    _pstmtNode.addBatch();
	} catch (SQLException e) {
	    displaySQLErrors(e);
	}
    }

    /**
     * Execute a batch node insert
     */
    public void insertNodes() {
	try {
	    _pstmtNode.executeBatch();
	    _pstmtNode.close();
	    _pstmtNode = null;
	} catch (SQLException e) {
	    displaySQLErrors(e);
	}
    }

    /**
     * Creates a new graph edge
     * 
     * @param simID_
     * @param step_
     * @param from_
     * @param to_
     */
    public void addEdge(Integer simID_, long step_, Agent from_, Agent to_, boolean isNewEdge_) {
	try {
	    if (_pstmtEdge == null) {
		_pstmtEdge = _conn.prepareStatement(INSERT_EDGES_PER_STEP_STMT);
	    }
	    _pstmtEdge.setInt(1, simID_);
	    _pstmtEdge.setLong(2, step_);
	    _pstmtEdge.setInt(3, from_.getID());
	    _pstmtEdge.setInt(4, simID_);
	    _pstmtEdge.setInt(5, to_.getID());
	    _pstmtEdge.setInt(6, simID_);
	    _pstmtEdge.setBoolean(7, isNewEdge_);
	    _pstmtEdge.addBatch();
	} catch (SQLException e) {
	    displaySQLErrors(e);
	}
    }

    /**
     * Batch insert of all edges added so far.
     * <p>
     * see {@link sim.app.social.db.DBManager.addEdge}
     */
    public void insertEdges() {
	try {
	    _pstmtEdge.executeBatch();
	} catch (SQLException e) {
	    displaySQLErrors(e);
	}
    }

    /**
     * @param e_
     */
    private static void displaySQLErrors(SQLException e_) {
	System.out.println("SQLException:\t" + e_.getMessage());
	System.out.println("SQLState:\t" + e_.getSQLState());
	System.out.println("VendorError:\t" + e_.getErrorCode());
    }

    /**
     * @param rs
     * @param stmt_
     * @param conn
     */
    public static void close(ResultSet rs, PreparedStatement stmt_, Connection conn) {
	if (rs != null) {
	    try {
		rs.close();
	    } catch (SQLException e) {
		displaySQLErrors(e);
	    }
	}
	if (stmt_ != null) {
	    try {
		stmt_.close();
	    } catch (SQLException e) {
		displaySQLErrors(e);
	    }
	}
	if (conn != null) {
	    try {
		conn.close();
	    } catch (SQLException e) {
		displaySQLErrors(e);
	    }
	}
    }

    /**
     * @param simID_
     * @param simStep_
     * @return
     */
    public List<Integer> getNodes(int simID_, int simStep_) {
	List<Integer> nodeIds = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;

	try {
	    stmt = _conn.prepareStatement(SELECT_NODES_PER_STEP_STMT);
	    stmt.setLong(1, simID_);
	    stmt.setLong(2, simStep_);
	    stmt.addBatch();
	    rs = stmt.executeQuery();
	    nodeIds = new LinkedList<Integer>();
	    while (rs.next()) {
		nodeIds.add(rs.getInt("node"));
	    }
	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	} finally {
	    close(rs, stmt, null);
	}
	return nodeIds;

    }

    /**
     * Returns a list of edges created in a given step
     * 
     * @param simID_
     * @param simStep_
     * @return
     */
    public List<Edge<Integer>> getEdges(int simID_, int simStep_) {
	List<Edge<Integer>> edgeList = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;

	try {
	    stmt = _conn.prepareStatement(SELECT_EDGES_PER_STEP_STMT);
	    stmt.setLong(1, simID_);
	    stmt.setLong(2, simStep_);
	    stmt.addBatch();
	    rs = stmt.executeQuery();
	    edgeList = new LinkedList<Edge<Integer>>();
	    while (rs.next()) {
		Edge<Integer> edge = new Edge<Integer>(rs.getBoolean("is_create_edge"));
		edge.v1 = rs.getInt("from_node");
		edge.v2 = rs.getInt("to_node");
		edgeList.add(edge);
	    }
	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	} finally {
	    close(rs, stmt, null);
	}
	return edgeList;
    }
}
