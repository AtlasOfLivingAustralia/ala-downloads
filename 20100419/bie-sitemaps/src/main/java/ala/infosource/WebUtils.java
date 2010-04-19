package ala.infosource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 * Simple HTTP methods that wrap Commons HTTPClient API
 * 
 * @author Dave Martin
 */
public class WebUtils {

	public static InputStream getUrlContent(String url) throws Exception {
    	HttpClient httpClient  = new HttpClient();
    	GetMethod gm = new GetMethod(url);
    	httpClient.executeMethod(gm);
    	return gm.getResponseBodyAsStream();
	}
	
	public static String getUrlContentAsString(String url) throws Exception {
    	HttpClient httpClient  = new HttpClient();
    	GetMethod gm = new GetMethod(url);
    	gm.setFollowRedirects(true);
    	httpClient.executeMethod(gm);
    	
//    	String requestCharset = gm.getRequestCharSet();
    	String content = gm.getResponseBodyAsString();
//    	content = new String(content.getBytes(requestCharset), "UTF-8");
    	return content;
	}
	
	public static Response getUrlContentAsBytes(String url) throws Exception {
		return getUrlContentAsBytes(url, true); 
	}
	
	public static Response getUrlContentAsBytes(String url, boolean followRedirect) throws Exception {
		Response response = new Response();
		
		//allow circular redirects - for Larval Fishes
    	HttpClientParams httpParams = new HttpClientParams();
//    	httpParams.setBooleanParameter("ALLOW_CIRCULAR_REDIRECTS",true);
    	HttpClient httpClient  = new HttpClient(httpParams);
    	GetMethod gm = new GetMethod(url);
    	gm.setFollowRedirects(followRedirect);
    	
    	httpClient.getParams().setParameter("http.protocol.allow-circular-redirects",true);
    	httpClient.executeMethod(gm);
    	response.setResponseUrl(gm.getURI().toString());
    	
    	InputStream input =  gm.getResponseBodyAsStream();
    	ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    	byte[] buff = new byte[1000];
    	int read=0;
    	while((read=input.read(buff))>0){
    		bOut.write(buff, 0, read);
    	}
    	
    	Header hdr = gm.getResponseHeader("Content-Type");
    	if(hdr!=null){
    		response.setContentType(hdr.getValue());
    	}
    	response.setResponseAsBytes(bOut.toByteArray());
    	return response;
	}
}
