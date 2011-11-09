/**
 * 
 */
package sim.app.social.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

/**
 * @author biggie
 */
public class DBManager {
    private static final String HOST = "localhost:3306";
    private static final String SCHEMA = "socSimDB";
    private static final String USR = "root";
    private static final String PWD = "";
    private static final String DB_URL = "jdbc:mysql://" + HOST + "/" + SCHEMA + "?user=" + USR + "&password=" + PWD;
    private PreparedStatement _pstmtNode;
    private PreparedStatement _pstmtEdge;

    private Connection _conn;

    /**
     * Constructor
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
	}
	return newSimRowID;
    }

    /**
     * Add individual node insert statments for batch insert.
     * 
     * @param simID_
     * @param node_
     */
    public void addNode(Integer simID_, Integer node_) {

	try {
	    if (_pstmtNode == null) {
		_pstmtNode = _conn.prepareStatement("insert into nodes (sim_id, node) values (?, ?)");
	    }
	    _pstmtNode.setLong(1, simID_);
	    _pstmtNode.setLong(2, node_);
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
	    int[] update = _pstmtNode.executeBatch();
	    System.out.println("Added " + _pstmtNode.getUpdateCount());
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
    public void addEdge(Integer simID_, Integer step_, Integer from_, Integer to_) {
	try {
	    if (_pstmtEdge == null) {
		_pstmtEdge = _conn.prepareStatement("insert into graphs (sim_id, step, from_node, to_node) "
			+ "values (?,?,(SELECT id FROM nodes where node = ? and sim_id = ? ),"
			+ "(SELECT id FROM nodes where node = ? and sim_id = ? ))");
	    }
	    _pstmtEdge.setInt(1, simID_);
	    _pstmtEdge.setInt(2, step_);
	    _pstmtEdge.setInt(3, from_);
	    _pstmtEdge.setInt(1, simID_);
	    _pstmtEdge.setInt(4, to_);
	    _pstmtEdge.setInt(1, simID_);
	    _pstmtEdge.addBatch();
	} catch (SQLException e) {
	    displaySQLErrors(e);
	}
    }

    /**
     * 
     */
    public void insertEdges() {
	try {
	    _pstmtEdge.executeBatch();
	    _pstmtEdge = null;
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

}
