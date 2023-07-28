import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.itextpdf.text.pdf.PdfName;

public class DatabaseManager {

	String dbUrl = "jdbc:postgresql://localhost:5432/";
	String DbUsername = "postgres";
	String DbPassword = "1234";
	
	public Connection connect(String pdfName) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection(dbUrl+ pdfName ,DbUsername,DbPassword);
	}
	
	public void createDatabase(String pdfName) {
		
		String createDb = "CREATE DATABASE " + pdfName;
		try(Connection conn = DriverManager.getConnection(dbUrl,DbUsername,DbPassword);
				Statement stmt = conn.createStatement())
		{
			stmt.executeUpdate(createDb);
			System.out.println("DB Created");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}

	public void createTableForPDF(String pdfName) throws ClassNotFoundException
	{
		
		String createTextTableSql = "CREATE TABLE IF NOT EXISTS text_data (" + "SNo SERIAL PRIMARY KEY,"+
				"Page_number INTEGER UNIQUE," + "Text TEXT" + ");";
		
		String createImgTableSql = "CREATE TABLE IF NOT EXISTS image_data (" + "SNo SERIAL PRIMARY KEY,"+
				"Images_Page_number INTEGER REFERENCES text_data(Page_number)," 
				+ "Image_Data BYTEA" + ");";
		
		try (
				Connection conn = connect(pdfName);
				Statement stmt = conn.createStatement())
			{
				stmt.executeUpdate(createTextTableSql);
				stmt.executeUpdate(createImgTableSql);
			}catch(SQLException e)
		{
				e.printStackTrace();
		}
		
	}

}
