import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import net.sourceforge.tess4j.TesseractException;

public class PdfTextParser {
	
	DatabaseManager databaseManager = new DatabaseManager();

	public void parsePDF(String pdfFilePath, String pdfName) throws TesseractException, ClassNotFoundException, SQLException {
		
		databaseManager.createTableForPDF(pdfName);
		try {
			PDDocument document = PDDocument.load(new File(pdfFilePath));
			System.out.println("No of Pages:" + document.getNumberOfPages());
			
			//Utility class of PDFBox that extract text from pdf
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			
			for(int page=1;page<=document.getNumberOfPages();page++)
			{
				pdfTextStripper.setStartPage(page);
				pdfTextStripper.setEndPage(page);
//				PDPage pageNo = document.getPage(page);
//				System.out.println(page);
				String pageText = pdfTextStripper.getText(document);
				storeTextInDB(page,pageText,pdfName);
				
			}
		
			document.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

	private void storeTextInDB(int pageNo, String text,String pdfName) throws SQLException, ClassNotFoundException {
		
		String insertTextSql = "INSERT INTO text_data(page_number, Text) VALUES(?,?);";
		
		try (Connection conn = databaseManager.connect(pdfName);
			PreparedStatement pstmt = conn.prepareStatement(insertTextSql))	
			{
				pstmt.setInt(1, pageNo);
				pstmt.setString(2, text);
				pstmt.executeUpdate();
			}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}

}
