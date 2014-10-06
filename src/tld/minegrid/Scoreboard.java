/**
 * A class to handle interactions with the scoreboard database.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/*
 SQL Table Structure:
 
 CREATE table "HIGHSCORES" (
    "NAME"       VARCHAR2(20) NOT NULL,
    "SCORE"      NUMBER(10) NOT NULL,
    "TIME"       NUMBER(16) NOT NULL,
    "WIDTH"      NUMBER(3) NOT NULL,
    "HEIGHT"     NUMBER(3) NOT NULL,
    "MINES"      NUMBER(4) NOT NULL,
    "LIVES"      NUMBER(2) NOT NULL,
    "ID"         NUMBER(7),
    constraint  "HIGHSCORES_PK" primary key ("ID")
  )
*/

public class Scoreboard {
	// FIXME: Potential security risk.
	// Highscores database limited-user login details.
	private final String USERNAME = "minegrid";
	private final String PASSWORD = "";
	
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	// The next ID value to use when inserting new score records.
	private int nextId;
	// Whether the scoreboard database is enabled.
	private boolean enabled = true;
	
	private PreparedStatement pstGetScores;
	private PreparedStatement pstInsertScore;
	
	/**
	 * Constructs a new Scoreboard instance by connecting to the database.
	 */
	public Scoreboard() {
		String message;
		try {
			connect();
		}
		catch (SQLException e) {
			message = "Scoreboard database error:\n" + e.getMessage() + "\n\nThe scoreboard will be disabled.";
			JOptionPane.showMessageDialog(null, message,
					"MineGrid Scoreboard Error", JOptionPane.ERROR_MESSAGE);
			enabled = false;
		}
	}
	
	/**
	 * Returns whether the scoreboard database is enabled.
	 */
	public boolean enabled() {
		return enabled;
	}

	/**
	 * Connects to the scoreboard database and prepares queries.
	 */
	private void connect() throws SQLException {

		try {
			con = DriverManager.getConnection("jdbc:oracle:thin:@dbserver.tld:1000:DBSERV",
					USERNAME, PASSWORD);
		}
		catch (java.sql.SQLRecoverableException e) {
			con = DriverManager.getConnection("jdbc:oracle:thin:@dbserver:1000:DBSERV",
					USERNAME, PASSWORD);
		}
		
		stmt = con.createStatement();
		
		fetchNextId();
		createPreparedStatements();
	}
	
	/**
	 * Requests and stores the next ID value to use when inserting new score records. 
	 */
	private void fetchNextId() throws SQLException {
		rs = stmt.executeQuery("SELECT id FROM highscores ORDER BY id DESC");
		if (rs.next()) {
			nextId = rs.getInt(1) + 1;
		}
	}

	/**
	 * Creates the SQL prepared statements for the queries that will be made.
	 */
	private void createPreparedStatements() throws SQLException {
		
		pstGetScores = con.prepareStatement(
			"SELECT name, score, time, width, height, mines, lives FROM highscores ORDER BY score DESC");
		
		pstInsertScore = con.prepareStatement(
			"INSERT INTO highscores (id, name, score, time, width, height, mines, lives) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
	}
	
	/**
	 * Requests all the scores from the database and returns them ready for display.
	 * 
	 * @return	a list of rows of objects containing the value of each cell
	 */
	public ArrayList<Object[]> getScores() {
		
		ArrayList<Object[]> scores = new ArrayList<Object[]>();
		Object[] score;
		String grid;
		int rank = 1;
		
		try {
			rs = pstGetScores.executeQuery();

			while (rs.next()) {
				
				grid = String.format("%dx%d",
					rs.getLong("width"), rs.getLong("height"));
				
				score = new Object[] {
					rank++,
					rs.getString("name"),
					grid,
					rs.getLong("mines"),
					rs.getLong("lives"),
					rs.getLong("time"),
					rs.getLong("score"),
				};
				scores.add(score);
			}
		}

		catch (SQLException e) {
			scores.add(new Object[] {e.getMessage()});
		}

		return scores;
	}
	
	/**
	 * Inserts a new score record into the database.
	 */
	public boolean insertScore(String name, int score, long time, int width, int height, int mines, int lives) throws SQLException {
		
		int param = 1;
		int rows;
		
		pstInsertScore.setInt(param++, nextId++);
		pstInsertScore.setString(param++, name);
		pstInsertScore.setInt(param++, score);
		pstInsertScore.setLong(param++, time);
		pstInsertScore.setInt(param++, width);
		pstInsertScore.setInt(param++, height);
		pstInsertScore.setInt(param++, mines);
		pstInsertScore.setInt(param++, lives);
		rows = pstInsertScore.executeUpdate();
		
		return (rows == 1);
	}

}
