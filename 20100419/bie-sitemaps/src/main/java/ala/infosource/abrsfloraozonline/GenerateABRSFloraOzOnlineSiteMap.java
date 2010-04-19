package ala.infosource.abrsfloraozonline;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Generates a sitemap file for ABRS Flora of Australia Online.
 * http://www.anbg.gov.au/abrs/online-resources/flora/nameslist.xsql?pnid=1
 *
 * Sitemap file will contain the unique URL for each data item listed
 * in the database.
 *
 * @author Hon Hwang (hon.hwang@csiro.au)
 *
 * @since 2009-11-13
 */
@SiteMapGenerator(longName="ABRS Flora of Australia Online", shortName="abrsfloraozonline")
public class GenerateABRSFloraOzOnlineSiteMap {

	/**
	 * Flag to determine whether to show debug messages or not.
	 */
	private boolean verbose = false;

	/**
	 * JRE's logger class.  Will be initialised when the <code>verbose</code>
	 * instance variable is set.
	 */
	private java.util.logging.Logger classLogger = Logger.getLogger(GenerateABRSFloraOzOnlineSiteMap.class.getCanonicalName());

	/**
	 * The starting index for the ABRS Flora Online data.
	 */
	private int currentIndex = 1;
	// private int currentIndex = 3299;
	// private int currentIndex = 7053;

	/**
	 * The final index to parse.
	 * Obtained by manually requesting HTTP connections till no data
	 * message is received.
	 */
	private int maxIndex = 10499;
	// private int maxIndex = 200;

	/**
	 * Number of index before showing a timing message.
	 */
	private int countIncrement = 100;

	/**
	 * The base URL (for taxon information) which the starting index will be appended to.
	 */
	private final String infoBaseUrl =
			"http://www.anbg.gov.au/abrs/online-resources/flora/stddisplay.xsql?pnid=";

	/**
	 * The base URL (for taxon ranking) which the starting index will be appended to.
	 */
	private final String rankBaseUrl =
			"http://www.anbg.gov.au/abrs/online-resources/flora/nameslist.xsql?pnid=";

	/*
	 * Separator between data in the file.
	 */
	private final String dataSeparatorChar = "\t";
	// private final String dataSeparatorChar = ", ";

	/**
	 * Current URL (for taxon information) to parse.
	 * Sets to the first data set on instantiation.
	 */
	private String currentInfoUrl = this.infoBaseUrl + Integer.toString(this.currentIndex);

	/**
	 * Content from `currentUrl`.  Expected to be valid HTML.
	 */
	private String currentInfoContent = null;

	/**
	 * Current rank of the taxon.
	 */
	private String currentTaxonRank = null;

	/**
	 * Writes to sitemap text file.
	 */
	private Writer writer = null;

	/**
	 * Regular expression to extract the name part from web pages.
	 * The content of this extraction also includes HTML formatting that
	 * will need to be removed later.
	 */
	private Pattern getNameRegex = Pattern.compile(
	 		".*<p.*class=\"name\">.*</b>",
	 		Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	/**
	 * Default constructor.  Sets up the {@link java.io.Writer }
	 * for writing to sitemap text file.  Also sets up the headings inside
	 * the text file.
	 *
	 * @throws Exception If there are initialisation errors.
	 *
	 * @since 2009-11-16
	 */
	public GenerateABRSFloraOzOnlineSiteMap() throws Exception {

		// Sets up java.io.Writer for writing.
		this.writer = ExtractUtils.getSiteMapWriter("abrsfloraozonline");
		// Writes the column headings.
		ExtractUtils.writeColumnHeaders(writer, new String[]{
					ColumnHeaders.URI,
					"Rank",
					ColumnHeaders.SCIENTIFIC_NAME
				});

	} // End of default constructor.

	/**
	 * Entry point to execute/run this sitemap extractor.
	 *
	 * @param args Parameters to receive.  The argument `verbose` will
	 * show debug messages to standard output.
	 *
	 * @since 2009-11-13
	 */
	public static void main(String[] args) throws Exception {

		boolean verbose = false;
		
		// Parses command line arguments.
		if (args.length > 0) {
			if ("verbose".equals(args[0])) {
				verbose = true;
			}
		}

		System.out.println("=== START OF SITE MAP GENERATION ===");
		System.out.println();

		// Instantiates a new site map extractor class (this class)
		GenerateABRSFloraOzOnlineSiteMap extractor =
				new GenerateABRSFloraOzOnlineSiteMap();

		System.out.println(">> Verbose mode is: " + verbose);
		extractor.setVerbose(verbose);

		System.out.println(">> Generating site map.");
		extractor.generateSiteMap();
		
		System.out.println("=== END OF SITE MAP GENERATION ===");
		System.out.println();


	} // End of `main` method.

	/**
	 * Returns the verbose flage.
	 *
	 * @return Flag to indicate whether to log debug messages or not.
	 *
	 * @since 2009-11-16
	 */
	public boolean getVerbose() {
		return this.verbose;
	} // End of `getVerbose` method.

	/**
	 * Sets the verbose flage.
	 *
	 * @param debug Flag to indicate whether to log debug messages or not.
	 *
	 * @since 2009-11-16
	 */
	public void setVerbose(boolean debug) {
		this.verbose = debug;
		if ( (this.verbose == true) && (this.classLogger == null)) {
			this.classLogger = java.util.logging.Logger.getLogger(
					GenerateABRSFloraOzOnlineSiteMap.class.getName());
		}
	} // End of `setVerbose` method.

	/**
	 * Returns the current index.
	 *
	 * @return The current index.
	 *
	 * @since 2009-11-16
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	} // End of `getCurrentIndex` method.

	/**
	 * Test executor.  Actual test is run from this method.
	 *
	 * @throws Exception On error.  There mau be an inner Exception and
	 * error messages.
	 *
	 * @since 2009-11-16
	 */
	public void generateSiteMap() throws Exception {

		long totalTime = 0L;
		long startTime = 0L;
		long endTime = 0L;

		while (getCurrentIndex() <= maxIndex) {

			startTime = System.currentTimeMillis();

			// Current URL to harvest.
			this.currentInfoUrl = infoBaseUrl + Integer.toString(getCurrentIndex());

			// Gets the first page.
			this.currentInfoContent = this.getHtmlContent(this.currentInfoUrl);

			if (isErrorPage(this.currentInfoContent) == true) {
				this.classLogger.warning(
						"`" + this.currentInfoUrl + "`" + " " +
						"is an error page.  Skipping.");
				this.currentIndex++;
				continue;
			}

			/*
			if (getVerbose() == true) {
				this.classLogger.log(Level.INFO,
						"HTML Content of " + "`" +
						this.currentUrl + "`" + "\n" +
						this.currentContent + "\n");
			}
			*/

			// Determines whether the current URL is marked as
			// `doubtful`.
			// The HTML page to grab this kind of data can be an error page,
			// so check it here.
			boolean taxonDoubtful = false;
			try {
				taxonDoubtful = isTaxonMarkedDoubtful();
			} catch (Exception err) {
				if (err.getMessage().equals("Error page encountered.")) {
					this.classLogger.warning(
							"`" + this.rankBaseUrl + "`" + " " +
							"is an error page.");
					this.currentIndex++;
					continue;
				} else {
					throw new Exception(err);
				}
			}

			// Determines whether the current URL is marked as
			// `doubtful`.  If so, skip.
			if (isTaxonMarkedDoubtful() == true) {
				this.classLogger.info(
						"Taxon with PNID " + getCurrentIndex() + " " +
						"is marked as doubtful.");
				this.currentIndex++;
				continue;
			}

			writer.write(this.currentInfoUrl);
			writer.write(this.dataSeparatorChar);

			// Obtains the rank.
			this.currentTaxonRank = extractRankAbbr();
			if (this.currentTaxonRank != null) {
				writer.write(this.currentTaxonRank);
				writer.write(this.dataSeparatorChar);
			} else {
				writer.write(this.dataSeparatorChar);
			}

			// Obtains the name from current web page.
			String name = extractName();
			if (name != null) {
				// Writes the current URL to the sitemap file.
				writer.write(name);
				writer.write(this.dataSeparatorChar);
			} else {
				// Attempt to extrac the name from title.
				String titleName = extractNameFromTitle();
				if (titleName != null) {
					writer.write(titleName);
					writer.write(this.dataSeparatorChar);
				}
				writer.write(this.dataSeparatorChar);
			}

			writer.write("\n");
			writer.flush();

			endTime = System.currentTimeMillis();
			long diffTime = endTime - startTime;
			totalTime += diffTime;

			// Shows timing and progress message.
			if (getCurrentIndex() % this.countIncrement == 0) {
				System.out.println(
						getCurrentIndex() + " pages processed.  " +
						Long.toString(diffTime) + "ms " +
						" for last batch of " +
						Integer.toString(this.countIncrement) + ".  " +
						"Total time: " + Long.toString(totalTime) + "ms");
				writer.flush();
			}

			this.classLogger.info(">> NEXT RECORD\n");

			this.currentIndex++;

		} // End of while

		writer.flush();
		writer.close();

	} // End of `generateSiteMap` method.

	/**
	 * Obtains and returns the HTML specified in the <code>url</code>
	 * parameter as a String.
	 * 
	 * Utilises {@link ala.infosource.WebUtils#getUrlContentAsString(java.lang.String) }
	 * method.
	 * 
	 * @param url URL to fetch.
	 * @return HTML content as a String.  <code>null</code> on error.
	 *
	 * @throws Exception On error.  There may be more detailed mesage in
	 * exception object.
	 * 
	 * @since 2009-11-16
	 */
	private String getHtmlContent(String url) throws Exception {

		if (url == null) {
			throw new NullPointerException(
					"Supplied URL to fetch has null reference.");
		}

		if (url.isEmpty()) {
			throw new IllegalArgumentException(
					"Supplied URL to fetch is empty.");
		}

		String targetUrl = url;
		if (getVerbose() == true) {
			this.classLogger.log(Level.INFO,
					"URL to fetch is: " + "`" +
					targetUrl + "`");
		}

		// Obtains content of URL
		// String urlContent = WebUtils.getUrlContentAsString(targetUrl);
		String urlContent = inputStreamToString(
				WebUtils.getUrlContent(targetUrl));
		
		return urlContent;
		
	} // End of `getContent` method.
	
	/**
	 * Extracts the Taxon Name from the extracted HTML.
	 * 
	 * @return Taxon Name from the extracted HTML.  <code>null</code> if there
	 * are not any.
	 * 
	 * @since 2009-11-16
	 */
	private String extractName() {

		// Based on the taxon's rank, determine whether the taxon has a
		// bionomial or trinomial name.
		// At this time (Nov. 2009), taxa with rank subspecies (subsp.) and
		// variety (var.) have trinomial names.
		boolean isTrinomialName = false;
		if ( (this.currentTaxonRank.equals("subsp.")) ||
			 (this.currentTaxonRank.equals("var."))) {
			isTrinomialName = true;
			this.classLogger.info(
					"Current taxon's ranks is " +
					"`" + this.currentTaxonRank + "`" + "  " +
					"Expecting a trinomial name.");
		} else {
			isTrinomialName = false;
		}

		Matcher nameMatcher = this.getNameRegex.matcher(
				this.currentInfoContent);

		if (nameMatcher.find() == false) {
			if (getVerbose() == true) {
				this.classLogger.log(Level.WARNING,
						"Pattern to extract name not found in current content.");
			}
			return null;
		}

		// Narrows down to the name part
		// Example result:
		// <p class="name"><b>*namePart1</b>&nbsp;<b>namePart2</b>&nbsp;
		String nameWithFormatting = nameMatcher.group(0);
		if (getVerbose() == true) {
			this.classLogger.log(Level.INFO,
					"Name with formatting is: " + "`" +
					nameWithFormatting + "`");
		}

		// Remove HTML tags from name.
		// Assume the name is contained inside bold HTML tags.
		// Example output
		// *namePart1 namePart2
		String nameWithNoHtml = new String();
		int namePartCount = 0;
		Pattern extractNamesFromFormattingRegex =
				Pattern.compile("<b[^>]*>(.*?)</b>",
				Pattern.CASE_INSENSITIVE);
		Matcher namesFromFormattingMatcher = extractNamesFromFormattingRegex.
				matcher(nameWithFormatting);

		while (namesFromFormattingMatcher.find()) {

			if (getVerbose()) {
				this.classLogger.info("Found String part");
			}

			String currentNamePart = namesFromFormattingMatcher.group(1);

			// Special case where the names have a star in front.
			// Removes it.
			if (currentNamePart.startsWith("*")) {
				String tempNamePart = currentNamePart.substring(
						1, currentNamePart.length());
				currentNamePart = tempNamePart;
			}

			// Deal with taxon names for subspecies (subsp.) and
			// variations (var.) which have trinomial name scheme of
			// <genus> <specific epithet> <rank> <infraspecific epithet>
			if ( (namePartCount == 1) && (isTrinomialName == true)) {
				nameWithNoHtml += this.currentTaxonRank + " ";
				if (getVerbose()) {
					this.classLogger.log(Level.INFO,
							"Added taxon concept " +
							"`" + this.currentTaxonRank + "`" + " " +
							"for trinomial name.");
				}
			}

			// Deal with empty names String.  Don't write it.
			if (currentNamePart.isEmpty()) {
				namePartCount++;
				continue;
			}

			nameWithNoHtml += currentNamePart + " ";
			if (getVerbose()) {
				this.classLogger.log(Level.INFO,
						"Current name sequence: " + "`" +
						nameWithNoHtml + "`");
			}
			namePartCount++;
		}

		return nameWithNoHtml;
		
	} // End of `extractName` method.

	/**
	 * Extracts the name from the title of a HTML page.
	 *
	 * This is the fallback method of extracting names for those HTML pages
	 * from ABRS Flora Australia Online web site that don't have a name
	 * in the description.  In these cases, the names are obtained from
	 * the title.
	 * E.g, {@link http://www.anbg.gov.au/abrs/online-resources/flora/stddisplay.xsql?pnid=2 MAGNOLIOPHYTA}
	 *
	 * @since 2009-11-17
	 *
	 * @return Name from extracted HTML.
	 */
	private String extractNameFromTitle() {

		// Regular expression to extract the title portion of HTML.
		Pattern getTitleHtmlPortionRegex = Pattern.compile(
				".*<h3.*class=\"title\">.*</h3>");

		Matcher matcher = getTitleHtmlPortionRegex.matcher(this.currentInfoContent);
		if (matcher.find() == false) {
			if (getVerbose() == true) {
				this.classLogger.log(Level.WARNING,
						"Pattern to extract title not found in current content.");
			}
			return null;
		}

		// Extracted HTML should look something like:
		// <h3 class="title">name</h3>
		String titleWithHtml = matcher.group();

		// Regular expression to grab the title from HTML heading tag.
		// From: http://www.regular-expressions.info/examples.html
		Pattern getTitleRegex = Pattern.compile(
				"<h3\\b[^>]*>(.*?)</h3>");

		matcher = getTitleRegex.matcher(titleWithHtml);
		if (matcher.find() == false) {
			if (getVerbose() == true) {
				this.classLogger.log(Level.WARNING,
						"Pattern to extract title from HTML not found in current content.");
			}
			return null;
		}

		return matcher.group(1);
	} // End of `extractNameFromTitle` method.

	/**
	 * Extracts the abbreviation of the rank of the current taxon.
	 * 
	 * @return The rank abbreviation of current taxon.
	 *
	 * @throws Exception On error.
	 *
	 * @since 2009-11-17
	 */
	private String extractRankAbbr() throws Exception {

		String content = getHtmlContent(this.rankBaseUrl +
				getCurrentIndex());

		// Regular expression to grab the HTML portion where rank abbreviation
		// can be obtained.
		Pattern getRankHtmlPortionRegex = Pattern.compile(
				".*<a.*href=\".*pnid=" + getCurrentIndex() + "\".*><img.*>");

		Matcher matcher = getRankHtmlPortionRegex.matcher(content);
		if (matcher.find() == false) {
			if (getVerbose() == true) {
				this.classLogger.log(Level.WARNING,
						"Pattern to extract title not found in current content.");
			}
			return null;
		}

		// Extracted HTML portion should look like:
		// <li><a href="nameslist.xsql?pnid=1046"><img src="icons/sp.png" alt="sp.">
		String titleHtmlPortion = matcher.group();

		if (getVerbose() == true) {
			this.classLogger.info("Title portion HTML is: " + "`" +
					titleHtmlPortion + "`");
		}

		// Regular expression pattern to grab the attribute `alt`
		// and its value from `img` HTML tag.
		// From:
		// http://stackoverflow.com/questions/317053/regular-expression-for-extracting-tag-attributes
		Pattern getRankAbbrRegex = Pattern.compile(
				"(\\S+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?");

		matcher = getRankAbbrRegex.matcher(titleHtmlPortion);
		if (matcher.find() == false) {
			if (getVerbose() == true) {
				this.classLogger.log(Level.WARNING,
						"Pattern to extract rank abbreviation not found in current content.");
			}
			return null;
		}

		while (matcher.find()) {
			String attributeName = matcher.group(1);
			if ("alt".equals(attributeName)) {
				if (getVerbose() == true) {
					this.classLogger.log(Level.WARNING,
							"Extracted rank abbreviation is: " + "`" +
							matcher.group(2) + "`");
				}
				return matcher.group(2);
			}
		}

		return null;

	} // End of `extractRank` method.

	/**
	 * Converts {@link java.io.InputStream } to a String.
	 * 
	 * @param inStream
	 * @return String representation of the bytes from <code>inStream</code>
	 * parameter.
	 *
	 * @throws Exception On error.  See message for details.
	 */
	private String inputStreamToString(InputStream inStream) throws Exception {

		// Reads bytes from `inStream` as characters, using UTF-8 encoding.
		InputStreamReader isReader = new InputStreamReader(inStream, "UTF-8");
		BufferedReader bufReader = new BufferedReader(isReader);
		StringBuilder builder = new StringBuilder();

		String currentString = new String();
		while ( (currentString = bufReader.readLine()) != null) {
			builder.append(currentString);
			builder.append("\n");
		}

		return builder.toString();

	} // End of `inputStreamToString` method.

	/**
	 * Determines whether the current Taxon is marked by ABRS as `doubtful`
	 *
	 * This is determined by the icon image file used in the hierarchical
	 * listing.
	 *
	 * @return <code>true</code> if current Taxon is marked as `doubtful`
	 * else <code>false</code>
	 *
	 * @throws Exception Thrown when there is an error.  In particular, if
	 * there is an error HTML page.  The Exception will have a message of
	 * <code>Error page encountered</code>
	 *
	 * @since 2009-11-24
	 */
	private boolean isTaxonMarkedDoubtful() throws Exception {
		
		String content = getHtmlContent(this.rankBaseUrl +
				getCurrentIndex());

		// Check whether the extracted HTML page is an error page or not.
		if (isErrorPage(content) == true) {
			this.classLogger.warning(
					"`" + this.rankBaseUrl + getCurrentIndex() + "`" + " " +
					"is an error page.  Throwing new Exception.");
			throw new Exception("Error page encountered.");
		}

		// Obtain the hyperlink element of current Taxon, including the
		// embedded image icon for the hyperlink.
		// Returned format will be:
		// <li><a href="nameslist.xsql?pnid=166"><img src="icons/doubtful.png" alt="var.">
		Pattern getTaxonUrlRegex = Pattern.compile(
				".*<a.*href=\".*pnid=" + getCurrentIndex() + "\"" + "><img.*src=\".*\".*>");

		Matcher matcher = getTaxonUrlRegex.matcher(content);
		String hyperlinkLine = null;
		if (matcher.find() == true) {
			hyperlinkLine = matcher.group(0);
		} else {
			this.classLogger.warning(
					"The hyperlink string has null reference or is empty.");
		}

		this.classLogger.info("Current hyperlink string is: " +
				"`" + hyperlinkLine + "`");

		if ( (hyperlinkLine == null) || (hyperlinkLine.isEmpty())) {
			this.classLogger.warning(
					"The hyperlink string has null reference or is empty.");
		}

		// Regular expression pattern to grab the attribute `alt`
		// and its value from `img` HTML tag.
		// From:
		// http://stackoverflow.com/questions/317053/regular-expression-for-extracting-tag-attributes
		Pattern getImageIconRegex = Pattern.compile(
				"(\\S+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?");

		matcher = getImageIconRegex.matcher(hyperlinkLine);
		String imageIconLoc = null;
		int findIndex = 0;
		while (matcher.find()) {
			if (findIndex == 1) {
				imageIconLoc = matcher.group(2);
				break;
			}
			findIndex++;
		}

		if ("icons/doubtful.png".equals(imageIconLoc)) {
			return true;
		} else {
			return false;
		}
		
	} // End of `currentTaxonMarkedDoubtful` method.

	/**
	 * Determines whether a supplied String contains the ABRS Flora of
	 * Austrlia Online's error message.
	 *
	 * @param htmlPageContent Content of a taxon's HTML page in String.
	 *
	 * @return <code>true</code> If current taxon's HTML page is an error
	 * page, <code>false</code> otherwise.
	 *
	 * @since 2009-11-24
	 */
	private boolean isErrorPage(String htmlPageContent) {
		
		Pattern errorHeadingRegex = Pattern.compile(
				"<h1>Database Error</h1>");

		Pattern noDataRegex = Pattern.compile(
				".*returned no data.*");

		Matcher matcher = errorHeadingRegex.matcher(htmlPageContent);
		boolean errorHeadingMatchStatus = matcher.find();
		if (errorHeadingMatchStatus) {
			this.classLogger.info(
					"Found error heading.");
		}

		matcher = noDataRegex.matcher(htmlPageContent);
		boolean nodataMatchStatus = matcher.find();
		if (nodataMatchStatus) {
			this.classLogger.info(
					"Found error body.");
		}

		if ( (errorHeadingMatchStatus) ||
			 (nodataMatchStatus)) {
			return true;
		} else {
			return false;
		}

	} // End of `isErrorPage` method.

} // End of `GenerateABRSFloraOzOnlineSiteMap` class.
