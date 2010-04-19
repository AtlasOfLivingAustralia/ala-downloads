package ala.infosource;

import java.io.InputStream;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelReader implements Runnable {
	
	public static void main(String[] args) throws Exception{
		InputStream is = WebUtils.getUrlContent("http://ala-bie.googlecode.com/svn/trunk/bie-datasharing/docs/PaDILExample.xls");
		
		POIFSFileSystem fileSystem = new POIFSFileSystem(is);
		
		ExcelExtractor excelExtractor = new ExcelExtractor(fileSystem);
		
		//System.out.println(excelExtractor.getText());
		
		String excelContentStr = excelExtractor.getText();
		
		String[] excelContentStrs = excelContentStr.split("\n");
		
		for (String str : excelContentStrs) {
			//System.out.println("!!!!!!!!" + str);
			String[] tmpRowComponents = str.split("[\\s&&[^ ]]{1,}");
			
			if (tmpRowComponents.length > 1) {
				System.out.println(tmpRowComponents[0] + "!!!!!!!" + tmpRowComponents[1]);
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
