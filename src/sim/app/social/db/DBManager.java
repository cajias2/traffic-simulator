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
import java.util.Set;

import sim.graph.utils.Edge;

import com.mysql.jdbc.Statement;

/**
 * @author biggie
 */
public class DBManager {
    /**
     * 
     */
    private static final String SELECT_STEP_CNT = "select max(step) cnt  from graphs where sim_id = ?";
    private static final String NEW_SIMULATION_STMT = "insert into simulations (time_start, agent_count) values (?, ?)";
    private static final String INSERT_NODE_STMT = "insert into nodes (id) values(?)";
    private static final String INSERT_EDGES_PER_STEP_STMT = "insert into graph_edges (sim_id, graph_id, from_node, to_node, is_create_edge) "
	    + "values (?,?,?,?,?)";
    private static final String SIM_AGENT_COUNT_STMT = "select agent_count cnt from simulations where id = ?";
    private static final String SELECT_EDGES_PER_STEP_STMT = "select from_node, to_node, is_create_edge from graph_edges where sim_id = ? and graph_id = ?";
    private static final String INSERT_SIM_STEP_STMT = "insert into graphs (sim_id, step, created) values (?, ?, ?)";
    private static final String NEW_COMM_STMT = "insert into communities (sim_id, graph_id) values (?,?)";
    private static final String INSERT_COMM_MEMBERS_STMT = "insert into community_members (comm_id, node_id) values (?,?)";

    private static final String NODE_CNT = "select count(*) cnt from nodes";
    private static final String HOST = "localhost:3306";
    private static final String SCHEMA = "socSimDB";
    private static final String USR = "root";
    private static final String PWD = "miramar";
    private static final String DB_URL = "jdbc:mysql://" + HOST + "/" + SCHEMA + "?user=" + USR + "&password=" + PWD;
    private PreparedStatement _pstmtNode;
    private PreparedStatement _pstmtEdge;
    private PreparedStatement _pstmtComm;
    private Connection _conn;

    /**
     * Constructor Instantiates a new DB Connection.
     */
    public DBManager() {
	try {
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    _conn = DriverManager.getConnection(DB_URL);
	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
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
    public int newSimulation(int agentCount_) {
	PreparedStatement pstmt = null;
	int newSimRowID = -1;
	try {
	    pstmt = _conn.prepareStatement(NEW_SIMULATION_STMT, Statement.RETURN_GENERATED_KEYS);
	    pstmt.setLong(1, System.currentTimeMillis());
	    pstmt.setInt(2, agentCount_);
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
     * Add individual node insert statements for batch insert.
     * 
     * @param simID_
     * @param node_
     */
    public void addNodeToBatch(int nodeID_) {

	try {
	    if (_pstmtNode == null) {
		_pstmtNode = _conn.prepareStatement(INSERT_NODE_STMT);
	    }
	    _pstmtNode.setLong(1, nodeID_);
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
     * Adds an edge to the insert batch
     * 
     * @param simID_
     * @param step_
     * @param from_
     * @param to_
     */
    public void addEdgeToBatch(int simID_, long graphID_, int fromID_, int toID_, boolean isNewEdge_) {
	try {
	    if (_pstmtEdge == null) {
		_pstmtEdge = _conn.prepareStatement(INSERT_EDGES_PER_STEP_STMT);
	    }
	    _pstmtEdge.setInt(1, simID_);
	    _pstmtEdge.setLong(2, graphID_);
	    _pstmtEdge.setInt(3, fromID_);
	    _pstmtEdge.setInt(4, toID_);
	    _pstmtEdge.setBoolean(5, isNewEdge_);
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
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    public void insertNewSimStep(Integer simID_, long step_) {
	PreparedStatement simStepSTMT;
	try {
	    simStepSTMT = _conn.prepareStatement(INSERT_SIM_STEP_STMT);
	    simStepSTMT.setInt(1, simID_);
	    simStepSTMT.setLong(2, step_);
	    simStepSTMT.setLong(3, System.currentTimeMillis());
	    simStepSTMT.addBatch();
	    simStepSTMT.executeBatch();
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
	e_.printStackTrace();
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
    public int getAgentCount(int simID_) {
	return getCount(simID_, SIM_AGENT_COUNT_STMT);

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

    /**
     * Returns a list of edges created in a given step
     * 
     * @param simID_
     * @param simStep_
     * @return
     */
    public Integer getDBNodeCnt() {
	PreparedStatement stmt = null;
	ResultSet rs = null;
	int nodeCnt = 0;

	try {
	    stmt = _conn.prepareStatement(NODE_CNT);
	    stmt.addBatch();
	    rs = stmt.executeQuery();
	    rs.next();
	    nodeCnt = rs.getInt("cnt");

	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	} finally {
	    close(rs, stmt, null);
	}
	return nodeCnt;
    }

    /**
     * @param simID_
     * @return
     */
    public int getSimStepCount(int simID_) {
	return getCount(simID_, SELECT_STEP_CNT);
    }

    /**
     * @param simID_
     * @param qry_
     * @return
     */
    private int getCount(int simID_, String qry_) {
	PreparedStatement stmt = null;
	ResultSet rs = null;
	int cnt = 0;
	try {
	    stmt = _conn.prepareStatement(qry_);
	    stmt.setInt(1, simID_);
	    stmt.addBatch();
	    rs = stmt.executeQuery();
	    rs.next();
	    cnt = rs.getInt("cnt");

	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	} finally {
	    close(rs, stmt, null);
	}
	return cnt;
    }

    /**
     * Adds communities to the insert batch
     * 
     * @param simID_
     * @param graphID_
     * @param commMembers_
     */
    public void addCommunityToBatch(int simID_, long graphID_, Set<Integer> commMembers_) {
	int commID = insertCommunity(simID_, graphID_);
	try {
	    if (_pstmtComm == null) {
		_pstmtComm = _conn.prepareStatement(INSERT_COMM_MEMBERS_STMT);
	    }
	    for (Integer mbr : commMembers_) {
		_pstmtComm.setInt(1, commID);
		_pstmtComm.setLong(2, mbr);
		_pstmtComm.addBatch();
	    }
	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	}
    }

    /**
     * Inserts a community to the db.
     */
    public void insertCommMembers() {
	try {
	    if (null != _pstmtComm) {
	    _pstmtComm.executeBatch();
	    }
	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	}
    }

    /**
     * @param simID_
     * @param graphID_
     * @return newly created community ID
     */
    private int insertCommunity(int simID_, long graphID_) {
	PreparedStatement pstmt = null;
	int commId = -1;
	try {
	    pstmt = _conn.prepareStatement(NEW_COMM_STMT, Statement.RETURN_GENERATED_KEYS);
	    pstmt.setInt(1, simID_);
	    pstmt.setLong(2, graphID_);
	    pstmt.addBatch();
	    pstmt.executeBatch();
	    ResultSet resultSet = pstmt.getGeneratedKeys();
	    if (resultSet != null && resultSet.next()) {
		commId = resultSet.getInt(1);
	    }
	} catch (SQLException e) {
	    displaySQLErrors(e);
	    System.exit(-1);
	}
	return commId;
    }



}
