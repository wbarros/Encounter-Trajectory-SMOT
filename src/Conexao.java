import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.util.PGobject;

public class Conexao {
	private Statement stmt;
	private String databaseURL;
	private String usuario;
	private String senha;
	private String driverName;
	private Statement stmtroll;
	private  java.sql.Connection connection;

	public Conexao() {
		conecta();
	}
	
	@SuppressWarnings("unchecked")
	final void conecta() {
		setDatabase("jdbc:postgresql://localhost/tccII");
	    setUser("postgres");
	    setSenha("#Arthur#");
	    setDriver("org.postgresql.Driver");
	    try {
	      Class.forName(getDriver()).newInstance();
	      connection = DriverManager.getConnection(getDatabase(), getUser(), getSenha());
	      ((org.postgresql.PGConnection)connection).addDataType("geometry",(Class<? extends PGobject>) Class.forName("org.postgis.PGgeometry"));
	      ((org.postgresql.PGConnection)connection).addDataType("box3d",(Class<? extends PGobject>) Class.forName("org.postgis.PGbox3d"));
	      stmt = connection.createStatement();
	      stmtroll = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	      System.out.println("Conexão obtida com sucesso.");
	    }
	    catch (SQLException ex) {
	      System.out.println("SQLException: " + ex.getMessage());
	      System.out.println("SQLState: " + ex.getSQLState());
	      System.out.println("VendorError: " + ex.getErrorCode());
	    }
	    catch (Exception e) {
	      System.out.println("Problemas ao tentar conectar com o banco de dados: " + e);
	    }      
	}
	public void disconecta() {
		try {
			if(!getStatment().isClosed())
				getStatment().close();
			if(!getStatmentRoll().isClosed())
				getStatmentRoll().close();
			getConn().close();
			System.out.println("Conexão finalizada");
		}catch (SQLException ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}
	}
	public Connection getConn() {
		return connection;
	}
	public Statement getStatmentRoll() {
		return stmtroll;
	}
	public Statement getStatment() {
		return stmt;
	}
	public  String getUser() {
		return usuario;
	}
	public  void setUser(String usuario) {
		this.usuario = usuario;
	}
	public  String getSenha() {
		return senha;
	}
	public  void setSenha(String senha) {
		this.senha = senha;
	}
	public  void setDatabase(String databaseURL) {
		this.databaseURL = databaseURL;
	}
	public  String getDatabase() {
		return databaseURL;
	}
	public  void setDriver(String driverName) {
		this.driverName = driverName;
	}
	public  String getDriver() {
		return driverName;
	}
	
}