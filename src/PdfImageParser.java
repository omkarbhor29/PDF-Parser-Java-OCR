import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import java.io.InputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PdfImageParser {
	
	private String pdfFilePath;
	DatabaseManager databaseManager = new DatabaseManager();

	public PdfImageParser(String pdfFilePath) {
		this.pdfFilePath = pdfFilePath;
	}
	
	//Extract pages
	public void extractImgAndSave(String pdfName) throws ClassNotFoundException, SQLException {
		
		try (PDDocument document = PDDocument.load(new File(pdfFilePath)))
		{
			
			int NoOfPages = document.getNumberOfPages();
			for(int pageNumber=1; pageNumber<= NoOfPages;pageNumber++)
			{
				PDPage page = document.getPage(pageNumber-1);
				extractImages(pageNumber, page,pdfName);
			}
		}
			
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	void extractImages(int pageNumber,PDPage page,String pdfName) throws ClassNotFoundException, SQLException
	{
		try 
		{
			//get resources - image
			PDResources resources = page.getResources();
			
			//loop through all images object
			for(COSName xObjectName :resources.getXObjectNames())
			{
				//check resources is image or not
				if(resources.isImageXObject(xObjectName))
				{
					//get images object
					PDImageXObject imageXObject = (PDImageXObject) resources.getXObject(xObjectName);
					BufferedImage bufferedImage = imageXObject.getImage();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage, "png", baos);
					
					byte[] imageData = baos.toByteArray();
//					System.out.println(imageData);
					storeImageInDB(pageNumber, imageData,pdfName);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	void storeImageInDB (int pageNumber,byte[] imageData,String pdfName) throws ClassNotFoundException, SQLException
	{
		String insertImageSql = "INSERT INTO image_data (Images_Page_number,image_Data) VALUES (?,?);";
		
		try (Connection conn = databaseManager.connect(pdfName);
				PreparedStatement pstmt = conn.prepareStatement(insertImageSql))	
				{
					pstmt.setInt(1, pageNumber);
					pstmt.setBytes(2, imageData);
					pstmt.executeUpdate();
				}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}


