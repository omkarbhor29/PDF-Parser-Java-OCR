import java.io.File;
import java.sql.SQLException;

import net.sourceforge.tess4j.TesseractException;

public class Main {

	public static void main(String args[]) throws TesseractException, ClassNotFoundException, SQLException
	{
		
		String pdfFilePath = "C:\\Users\\omkar_bhor\\Downloads\\file-example_PDF_1MB.pdf";
		String getpdfname = getPdfNameFromFilePath(pdfFilePath).toLowerCase();
		String pdfName = getpdfname.replace("-", "_");
		
		//Other File
//		String pdfFilePath = "D:\\CoreJava.pdf";
//		String pdfName ="corejava";
	
		//create a dynamic database with PDF Name
		DatabaseManager databaseManager = new DatabaseManager();
		databaseManager.createDatabase(pdfName);
//		
		//Text Parser
		PdfTextParser textParser = new PdfTextParser();
		textParser.parsePDF(pdfFilePath,pdfName);
		
		//Image Parser
		PdfImageParser textimageExtract = new PdfImageParser(pdfFilePath);
		textimageExtract.extractImgAndSave(pdfName);
		
	}

	private static String getPdfNameFromFilePath(String pdfFilePath) {

		File file = new File(pdfFilePath);
		String fileName = file.getName();
		
		//Remove File Extension
		int extentsionIndex = fileName.lastIndexOf(".");
		return (extentsionIndex == -1)? fileName :fileName.substring(0,extentsionIndex);
	}
}

