package com.secondmarket.filter;

import info.bliki.wiki.model.WikiModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.mongodb.BasicDBObject;
import com.secondmarket.model.Company;
import com.secondmarket.properties.SMProperties;
import com.secondmarket.utility.Utils;
import com.secondmarket.utility.WikipediaUtils;

/**
 * 
 * @author Ming Li & Danjuan Ye
 * 
 */
public class WikipediaFilter {

	private SMProperties p;

	public WikipediaFilter(SMProperties wikiProperty) {
		this.p = wikiProperty;
	}

	private String getStringFromNestedJson(String jsonBody, String beginTag,
			int beginIndexVar, String endTag, int endIndexVar) {
		int beginIndex = 0;
		int endIndex = 0;
		char pointerChar;

		try {
			StringBuffer tempString = new StringBuffer();
			while (!Pattern
					.compile(Pattern.quote(beginTag), Pattern.CASE_INSENSITIVE)
					.matcher(tempString.toString()).find()) {
				pointerChar = jsonBody.charAt(beginIndex);
				tempString.append(pointerChar);
				beginIndex++;
				endIndex++;
			}

			StringBuffer targetString = new StringBuffer();
			while (!targetString.toString().contains(endTag)) {
				pointerChar = jsonBody.charAt(endIndex);
				targetString.append(pointerChar);
				endIndex++;
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out
					.println("This company does not have infobox, or wrong data stored in DB");
			return "";
		}
		return jsonBody.substring(beginIndex - beginIndexVar, endIndex
				- endIndexVar);
	}

	/**
	 * Implemented algorithm to extract the text body for each topic which is
	 * tagged with mark-up syntax, e.g "==TopicName=="
	 * 
	 * @param basicDBObject
	 * @return
	 */
	public Map<String, String> extractText(BasicDBObject basicDBObject,
			Company company) {
		String jsonBody = basicDBObject.toString().trim();
		// System.out.println(jsonBody);
		String oneWhiteSpaceBody = jsonBody.replaceAll("\\s+", " ");

		String companyName = company.getCompanyName();
	//    String companyName = "Lotame";
		String name = "";
		int beginIndex = 0;
		int secondIndex = 0;
		boolean flag = false;
		int backCount = 0;
		//First Method to find the beginning of the paragraph
		do {
			backCount = 0;
			beginIndex = oneWhiteSpaceBody.indexOf("'''", secondIndex);
			if(beginIndex == -1){
				flag = true;
				break;
			}
			beginIndex +=3;
			backCount +=3;
			while(oneWhiteSpaceBody.charAt(beginIndex)=='\''){
				beginIndex++;
				backCount++;
			}
			secondIndex = oneWhiteSpaceBody.indexOf("'''", beginIndex);
			if(secondIndex == -1){
				flag = true;
				break;
			}
			name = oneWhiteSpaceBody.substring(beginIndex, secondIndex);
			secondIndex += 3;
			while(oneWhiteSpaceBody.charAt(secondIndex)=='\''){
				secondIndex++;
			}

		} while (!Utils.compareTwoStrings(companyName, name));
		
		//Second Method to find the beginnig of the paragraph
		int infoboxIndex = 0;
		int endInfoboxIndex = 0;
		if(flag){
			if(oneWhiteSpaceBody.toLowerCase().contains("infobox")){
				infoboxIndex = oneWhiteSpaceBody.toLowerCase().indexOf("infobox");
				endInfoboxIndex = oneWhiteSpaceBody.indexOf("}}", infoboxIndex);
				oneWhiteSpaceBody = oneWhiteSpaceBody.substring(endInfoboxIndex+2);
				oneWhiteSpaceBody = oneWhiteSpaceBody.replaceFirst("(\\\\n)+", "");
			}
		}else{
			oneWhiteSpaceBody = oneWhiteSpaceBody.substring(beginIndex-backCount);
		}
//		System.out.println("0000000"+oneWhiteSpaceBody);
		// Minus 2 to get rid of the ending quote
		int endIndex = oneWhiteSpaceBody.indexOf("}]") /*- 2*/;
		
		Map<String, String> contentMap = new LinkedHashMap<String, String>();
		if(endIndex < 0)
			return contentMap;
	
		String allTopicsBody = oneWhiteSpaceBody
				.substring(0, endIndex);

		// Map<String, List<String>> contentMap = new LinkedHashMap<String,
		// List<String>>();
		
		int indexMarker = 0;
		int nextEndIndex = allTopicsBody.indexOf("\\n==");
		String topicBody = "";
		String tempBody = "";
		// int i = 0;
		if(nextEndIndex == -1){
			topicBody = allTopicsBody;
//			System.out.println("11111111"+topicBody);
			String topicName = this.getTopicName(topicBody);

			String cleanedString = cleanTextBody(topicBody);
//			System.out.println("22222222"+cleanedString);
			if (cleanedString.length() != 0) {
				if (topicName.contains(".")) {
					topicName = topicName.replaceAll("\\.", "&#46;");
				}
				contentMap.put(topicName, cleanedString);
			}
			return contentMap;
		}
		while (nextEndIndex > 0) {
			
			topicBody = allTopicsBody.substring(indexMarker, indexMarker
					+ nextEndIndex);
			String topicName = this.getTopicName(topicBody);
			// System.out.println(topicName);

			String cleanedString = cleanTextBody(topicBody);
			/*
			 * List<String> sentenceList = this.extractEventSentences(
			 * cleanedString, company); String[] detectedSentences =
			 * this.sentenceAnalysis(cleanedString); List<String> sentenceList =
			 * new ArrayList<String>( Arrays.asList(detectedSentences));
			 * 
			 * if (sentenceList.size() != 0) { contentMap.put(topicName,
			 * sentenceList); }
			 */
			if (cleanedString.length() != 0) {
				if (topicName.contains(".")) {
					topicName = topicName.replaceAll("\\.", "&#46;");
				}
				contentMap.put(topicName, cleanedString);
			}

			tempBody = allTopicsBody.substring(indexMarker + nextEndIndex + 1);
			// System.out.println("LASTLATST:" +
			// tempBody.substring(tempBody.length()-5));
			indexMarker = indexMarker + nextEndIndex;
			nextEndIndex = tempBody.indexOf("\\n==") + 1;
			// i++;
		}
		return contentMap;
	}

	public String getTopicName(String topicBody) {
		String topicName = "";
		if (topicBody.startsWith("'''")||topicBody.startsWith("[")) {
			topicName = "Summary";
		} else {
			Pattern pattern = Pattern.compile("={2,}.+={2,}");
			Matcher matcher = pattern.matcher(topicBody);
			if (matcher.find()) {
				topicName = matcher.group().replace("=", "").trim();
				// Get rid of topic name quotes symbols, e.g. "\\\"
				topicName = topicName.replaceAll("\\\\", "");
			} else {
				topicName = "unknown";
			}
		}

		return topicName;
	}

	public String cleanTextBody(String topicBody) {
		// First get rid of section name part

		topicBody = topicBody.replaceAll("n\\s*={2,}.+={2,}", "n");

		// Wikipedia has its own mark-up syntax "As of",
		// http://en.wikipedia.org/wiki/Wikipedia:As_of, which can not be parsed
		// by bliki WikiModel
		topicBody = topicBody.replaceAll("(?i)(\\{{2})(As of\\|)(.*?)(\\}{2})",
				"As of $3");
		// remove image.
		// topicBody = topicBody.replaceAll("\\[\\[Image:.*?\\]\\]", "");
		// topicBody = topicBody.replaceAll("[[File:]]","");
		// //remove comments <!-- -->
		// topicBody = topicBody.replaceAll("<!--.*?-->","");
		// //remove wikinews
		// topicBody = topicBody.replaceAll("\\{\\{wikinews.*?\\}\\}", "");
		// // remove {{Main|news}}
		// topicBody = topicBody.replaceAll("\\{\\{[Mm]ain\\|.*?\\}\\}", "");
		// System.out.println("0000000" + topicBody + "\n");
		topicBody = topicBody.replaceFirst("(\\\\n)+", "");
		topicBody = topicBody.replaceAll("(\\\\t)+", "")
				.replaceAll("(\\\\n)+", "\\\\n")
				.replaceAll("( *\\\\n)+", "\\\\n");

		WikiModel wikiModel = new WikiModel(
				"http://en.wikipedia.org/wiki/${image}",
				"http://en.wikipedia.org/wiki/${title}");
		String htmlStr = wikiModel.render(topicBody);

		// System.out.println("1111111" + htmlStr + "\n");

		// Whitelist whiteList = Whitelist.basic();
		// String cleanedStr = Jsoup.clean(htmlStr, whiteList);
		// remove image
		String cleanedStr = htmlStr;
		while (cleanedStr.contains("</div")) {
			int backIndex = cleanedStr.indexOf("</div>");
			String tmp = cleanedStr.substring(0, backIndex);
			int index = tmp.lastIndexOf("<div");
			cleanedStr = cleanedStr.substring(0, index)
					+ cleanedStr.substring(backIndex + 6);
		}
		// System.out.println("3333333" + cleanedStr + "\n");
		// Remove "{{any content}}"
		cleanedStr = cleanedStr.replaceAll("\\{{2}.*?\\}{2}", "");
		// Remove special table
		cleanedStr = cleanedStr.replaceAll("\\{\\| *class.*?\\|\\}",
				"Table is removed!");
		// System.out.println("3333333" + cleanedStr + "\n");

		// Remove tags like "[1]"
		cleanedStr = cleanedStr.replaceAll(
				"<sup[^>]*><a[^>]*>\\[\\d*\\]</a></sup>", "");
		cleanedStr = cleanedStr.replaceAll("\\\\\\&#34;", "&#34;");
		// clear \n
		cleanedStr = cleanedStr.replaceAll("\\n", "");

		cleanedStr = cleanedStr.replaceAll("(\\\\n)+", "\\\\n");
		cleanedStr = cleanedStr.replaceFirst("<p>(\\\\n)+", "<p>");
		// while (cleanedStr.contains("<p>\n") || cleanedStr.contains("<p>\\n"))
		// {
		// cleanedStr = cleanedStr.replaceAll("^(\\n)*<p>(\\n)*(\\\\n)*",
		// "<p>");
		// }
		// System.out.println("5555555" + cleanedStr + "\n");

		cleanedStr = cleanedStr.replaceAll("(\\\\n)+", "<br/>");
		cleanedStr = cleanedStr.replaceAll("(<br/>)+", "<br/>");
		cleanedStr = cleanedStr.replaceAll("(<br/>)+", "<p>");
		// cleanedStr = cleanedStr.replaceAll("\\[\\d*\\]", "");
		// System.out.println("2222222" + cleanedStr + "\n");

		if (cleanedStr.equals("<p></p>")) {
			cleanedStr = "";
		}

		// // cleanedStr = cleanedStr.replaceAll("\\[\\d*\\]", "")
		// // .replaceAll("\\\\n", "").replace("{{", "").replace("}}", "");
		//
		// // Remove tags like "[1]"
		// // Remove tags like "\\n"
		// // Remove all slashes
		// cleanedStr = cleanedStr.replaceAll("\\[\\d*\\]", "")
		// .replaceAll("\\\\n", "").replace("\\", "");
		//
		// // Remove "{{any content}}"
		// cleanedStr = cleanedStr.replaceAll("\\{{2}.*?\\}{2}", "");
		//
		// System.out.println("33333333" + cleanedStr + "\n");
		//
		// // Clean tags like "\\\" and "\\"
		// cleanedStr = cleanedStr.replace("\\\\\\", "").replace("\\\\", " ");
		//
		// // Format the text with exactly one white space for multiple spaces
		// cleanedStr = cleanedStr.replaceAll("\\s+", " ");
		//
		// System.out.println("4444444" + cleanedStr + "\n");

		return cleanedStr;
	}

	/**
	 * Extracts the sentences that contain both year and company name
	 * information
	 * 
	 * @param cleanedString
	 * @param company
	 * @return
	 */
	public List<String> extractEventSentences(String cleanedString,
			Company company) {
		List<String> eventSentencesList = new ArrayList<String>();
		String[] detectedSentences = this.sentenceAnalysis(cleanedString);

		// Matching year information
		Pattern yearPattern = Pattern.compile("(18|19|20|21)\\d{2}");

		// Matching company name information, "(?i) indicates case insensitive",
		// "\b" indicates word boundary
		String companyNamePatternString = "(?i)\\b(" + company.getCompanyName()
				+ ")\\b";
		Pattern companyNamePattern = Pattern.compile(companyNamePatternString);

		/*
		 * List<String> tokens = new ArrayList<String>();
		 * tokens.add(companyName); tokens.add("million"); String
		 * companyNamePatternString = "\\b(" + StringUtils.join(tokens, "|") +
		 * ")\\b"; Pattern companyNamePattern =
		 * Pattern.compile(companyNamePatternString);
		 */

		for (String sentence : detectedSentences) {
			Matcher yearMatcher = yearPattern.matcher(sentence);
			Matcher companyNameMatcher = companyNamePattern.matcher(sentence);
			if (yearMatcher.find() && companyNameMatcher.find()) {
				eventSentencesList.add(sentence);
			} else {
				// System.out.println(sentence);
				continue;
			}
		}

		return eventSentencesList;
	}

	/**
	 * Using OpenNLP to execute the sentence detection, ModelSentence file in
	 * package "file"
	 * 
	 * @param cleanedString
	 * @return
	 */
	private String[] sentenceAnalysis(String cleanedString) {
		SentenceDetector sentenceDetector = null;
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getResourceAsStream(
					"/com/secondmarket/traineddata/en-sent.bin");
			SentenceModel sentenceModel = new SentenceModel(inputStream);
			inputStream.close();
			sentenceDetector = new SentenceDetectorME(sentenceModel);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sentenceDetector.sentDetect(cleanedString);
	}

	/***
	 * Add by danjuan March 31, 2012
	 * 
	 * @param basicDBObject
	 * @param company
	 * @return
	 */
	public Map<String, String> getFilteredWikipediaDoc(
			BasicDBObject basicDBObject, Company company) {
		Map<String, String> map;
		try {
			map = extractText(basicDBObject, company);
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("Unable to extract the plain text for "
					+ company.getCompanyName()
					+ ". Leaving wikipedia content as null");
			return null;
		}

		List<Pattern> patternList = p.getValues("CLEAN", "OPTIONS");
		Iterator<String> iter = map.keySet().iterator();
		List<String> removedList = new ArrayList<String>();
		while (iter.hasNext()) {
			String key = iter.next();
			// System.out.println("Key in the MAP: " + key);
			for (Pattern pattern : patternList) {
				if (WikipediaUtils.checkPatternMatch(pattern, key)) {
					removedList.add(key);
					// System.out.println("MATTCH: " + pattern.toString());
					break;
				}
			}
		}
		for (String key : removedList) {
			map.remove(key);
		}

		return map;

	}

	/*
	 * public static void main(String[] args) { WikipediaFilter test = new
	 * WikipediaFilter(); String topicBody =
	 * "[[Facebook]]<ref name=\\\"Growth\\\">{{Cite news | =December 19, 2008/ |titleEldon, Eric}}</ref> {{As of|February 2012}}, Facebook has more {{I don't know}} yes it is"
	 * ; topicBody = topicBody.replaceAll("(?i)(\\{{2})(As of\\|)(.*?)(\\}{2})",
	 * "As of $3"); System.out.println(topicBody); }
	 */

}
