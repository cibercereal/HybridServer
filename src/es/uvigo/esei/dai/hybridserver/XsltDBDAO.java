package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class XsltDBDAO implements DocDAO{
	private String url, user, pass;

	public XsltDBDAO(String url, String user, String pass) {
		this.url = url;
		this.user = user;
		this.pass = pass;
	}

	public String getContentPage(String uuid) {
		String content = "";
		try (Connection conection = DriverManager.getConnection(this.url, this.user, this.pass)) {
			try (PreparedStatement statement = conection.prepareStatement("SELECT * FROM XSLT " + "WHERE uuid=?")) {
				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next())
						content = result.getString("content");
					else
						content = null;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		return content;
	}

	public List<String> getList() {
		List<String> list = new LinkedList<>();
		try (Connection conection = DriverManager.getConnection(this.url, this.user, this.pass)) {
			try (PreparedStatement statement = conection.prepareStatement("SELECT uuid FROM XSLT")) {
				try (ResultSet result = statement.executeQuery()) {
					while (result.next())
						list.add(result.getString("uuid"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		return list;
	}

	public void putPages(String uuid, String content) {
		try (Connection conection = DriverManager.getConnection(this.url, this.user, this.pass)) {
			try (PreparedStatement statement = conection
					.prepareStatement("INSERT INTO XSLT(uuid, content) VALUES (?,?)")) {
				statement.setString(1, uuid);
				statement.setString(2, content);

				int result = statement.executeUpdate();

				if (result == 1) {
					System.out.println("Inserción realizada correctamente");
				} else {
					throw new SQLException("Error en la inserción " + result);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public void removePages(String uuid) {
		try (Connection conection = DriverManager.getConnection(this.url, this.user, this.pass)) {
			try (PreparedStatement statement = conection.prepareStatement("DELETE FROM XSLT WHERE uuid=?")) {
				statement.setString(1, uuid);

				int result = statement.executeUpdate();

				if (result == 1) {
					System.out.println("Borrado realizado correctamente");
				} else {
					throw new SQLException("Error en el borrado " + result);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	public boolean containsPage(String uuid) {
		String list = "";
		try (Connection conection = DriverManager.getConnection(this.url, this.user, this.pass)) {
			try (PreparedStatement statement = conection.prepareStatement("SELECT uuid FROM XSLT")) {
				try (ResultSet result = statement.executeQuery()) {
					if (result.next())
						list = result.getString("uuid");
					if (list.length() > 0)
						return true;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		return false;
	}
	
	public String getXsd(String uuid) {
		String content = "";
		try (Connection conection = DriverManager.getConnection(this.url, this.user, this.pass)) {
			try (PreparedStatement statement = conection.prepareStatement("SELECT * FROM XSLT " + "WHERE uuid=?")) {
				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next())
						content = result.getString("xsd");
					else
						content = null;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		return content;
	}
}
