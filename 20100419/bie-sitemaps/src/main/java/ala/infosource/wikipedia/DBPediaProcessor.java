package ala.infosource.wikipedia;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;

/**
 * Strips out triples associated with species pages. Uses
 * the presence of certain triples to provide an indication
 * if the page is a species page.
 * 
 * @author Dave Martin
 */
public class DBPediaProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		long start = System.currentTimeMillis();
		
		String inputFile = args[0];
		String outputFile = args[1];
		
		FileReader fr = new FileReader(new File(inputFile));
		FileWriter fw = new FileWriter(new File(outputFile));
		
		BufferedReader br = new BufferedReader(fr);
		
		Pattern p = Pattern.compile("\t");
		
		String line = "";
		int read = 0;
		int writesToOutput =0;
		
		StringBuffer sb = new StringBuffer();;
		
		String currentURI = "";
		boolean isBufferOfInterest = false;
		
		while((line=br.readLine())!=null){
			read++;
//			System.out.print("\rLines read: "+read+", writes to output:"+writesToOutput);
			
			String[] parts = p.split(line);
			
			if(parts.length>3){

				String uri = parts[0];
				
				if(!currentURI.equals(uri)){
					if(isBufferOfInterest){
						//write out to file
						fw.write(sb.toString());
						fw.flush();
						writesToOutput++;
					}
					//reset state
					currentURI = uri;
					isBufferOfInterest = false;
					sb = new StringBuffer();
				}

				String predicate = parts[1];
				
				//is current line indicating species page ?
				if("binomial".equals(predicate) 
						|| "species".equals(predicate)
						|| "genus".equals(predicate) 
						|| "familia".equals(predicate)
						|| "superfamilia".equals(predicate)
						|| "subfamilia".equals(predicate)
						|| "ordo".equals(predicate)
						|| "classis".equals(predicate)){
					isBufferOfInterest = true;
				}

				//append line to current buffer
				sb.append(line);
				sb.append('\n');
			}
		}
		
		long finish = System.currentTimeMillis();
		System.out.println(read+" triples processed in :"+((finish-start)/60000)+" minutes.");
		System.out.println(writesToOutput+" separate subjects written");
	}
}
