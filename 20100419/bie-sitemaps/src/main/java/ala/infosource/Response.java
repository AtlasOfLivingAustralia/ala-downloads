package ala.infosource;
/**
 * Simple bean wrapper for a response.
 * 
 * @author Dave Martin
 */
public class Response {

	protected String responseUrl;
	protected String contentType;
	protected byte[] responseAsBytes;
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	/**
	 * @return the responseAsBytes
	 */
	public byte[] getResponseAsBytes() {
		return responseAsBytes;
	}
	/**
	 * @param responseAsBytes the responseAsBytes to set
	 */
	public void setResponseAsBytes(byte[] responseAsBytes) {
		this.responseAsBytes = responseAsBytes;
	}
	/**
	 * @return the responseUrl
	 */
	public String getResponseUrl() {
		return responseUrl;
	}
	/**
	 * @param responseUrl the responseUrl to set
	 */
	public void setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
	}
}
