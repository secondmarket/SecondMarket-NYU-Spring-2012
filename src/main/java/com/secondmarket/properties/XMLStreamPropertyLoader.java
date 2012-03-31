package com.secondmarket.properties;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/***
 * 
 * @author Danjuan
 *
 */
public class XMLStreamPropertyLoader {

	public static final XMLStreamPropertyLoader INSTANCE = new XMLStreamPropertyLoader();
	// constants used for reading and writing the XML
	private static final String CLOSE_TAG = ">";
	private static final String END_TAG = "/>";
	private static final String EQUALS = "=";
	private static final String NEW_LINE = "\n";
	private static final String QUOTE = "\"";
	private static final String FORWARD_SLASH = "/";
	private static final String OPEN_TAG = "<";
	private static final String INDENT = "	";
	private static final String SPACE = " ";
	private static final String LIST = "list";
	private static final String LIST_ITEM_TAG = "li";
	private static final String TEXT_NODE = "#text";
	private static final String NAME = "name";
	private static final String CLIENT_DEFAULTS = "client_defaults";
	private static final String ROOT = "root";
	private static final String ESCAPE_OPEN_TAG = "&gt;";
	private static final String ESCAPE_CLOSE_TAG = "&lt;";
	private static final String ESCAPE_QUOTE = "&quot";

	private static List INDENTS = new ArrayList();
	static {
		INDENTS.add("");
	}

	public static XMLStreamPropertyLoader getInstance() {
		return INSTANCE;
	}

	private Document getDocument(Reader in) {
		DOMParser parser = new DOMParser();
		InputSource source = new InputSource(in);
		try {
			parser.parse(source);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (Document) parser.getDocument();
	}

	public void load(PropertyGroup group, Reader in) throws Exception {
		Document document = getDocument(in);
		load(group, null, document.getDocumentElement());
	}

	public void load(PropertyGroup group, PropertyGroup parent, Node node)
			throws Exception {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			loadAttributes(group, node);
			loadChildren(group, parent, node);
		} else if (node.getNodeType() == Node.COMMENT_NODE) {
			;
		} else {
			throw new Exception("Unrecognized node type encountered for node"
					+ node.getNodeName());
		}
	}

	private void loadAttributes(PropertyGroup group, Node node) {
		NamedNodeMap attributes;
		attributes = node.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Attr attr = (Attr) attributes.item(i);
				addProperty(group, attr.getName(), attr.getValue());
			}
		}
	}

	private void loadChildren(PropertyGroup group, PropertyGroup parent,
			Node node) throws Exception {
		NodeList children;
		Node child;
		int numChildren;
		String nodeName;
		String value;
		children = node.getChildNodes();
		if (children != null) {
			numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				child = children.item(i);
				nodeName = child.getNodeName();
				if (nodeName.equals(LIST)) {
					addList(group, child);
				} else if (nodeName.equals(TEXT_NODE)) {
					if (parent != null) {
						setValue(group, child);
					}
				} else {
					addGroup(group, child);
				}
			}
		}
	}

	private void setValue(PropertyGroup group, Node node) {
	//	String value = node.getNodeValue().trim();
		String value = node.getNodeValue();
		if (!value.equals("")) {
			group.setValue(value);
		}
	}

	private void addGroup(PropertyGroup group, Node node) throws Exception {
		if (node.getNodeType() != Node.TEXT_NODE) {
			String name = getName(node);
			PropertyGroup child = group.getGroup(name);
			load(child, group, node);
		}
	}

	private void addProperty(PropertyGroup group, String name, String value) {
		group.setProperty(name, value);
	}

	private void addList(PropertyGroup group, Node node) throws Exception {
		Collection list;
		Collection propList;
		String name = getName(node);
		list = getList(node);
		propList = group.getList(name);
		propList.clear();
		propList.addAll(list);
	}

	private String getName(Node node) {
		String name = node.getNodeName();
		NamedNodeMap attributes = node.getAttributes();
		Attr attr;
		if (attributes != null) {
			attr = (Attr) attributes.getNamedItem(NAME);
			if (attr != null) {
				name = attr.getValue();
			}
		}
		return name;
	}

	private Collection getList(Node node) throws Exception {
		Collection list = new LinkedList();
		NodeList items = node.getChildNodes();
		int itemLength = items.getLength();
		for (int i = 0; i < itemLength; i++) {
			Node item = items.item(i);
			String tag = item.getNodeName();
			if (tag.equals(LIST_ITEM_TAG)) {
				NodeList texts = item.getChildNodes();
				if (texts.getLength() == 1) {
					Node text = texts.item(0);
					String value = text.getNodeValue();
					list.add(value);
				} else {
					throw new Exception("List tags may not contain child nodes");
				}
			} else if (!tag.equals(TEXT_NODE)) {
				throw new Exception("Unexcepted tag <" + tag
						+ "> encountered in list. Only " + LIST_ITEM_TAG
						+ " tags are allowed in a list");
			}
		}
		return list;
	}

	public void store(PropertyGroup group, Writer out) throws Exception {
		store(group, out, "", true);
	}

	public void store(PropertyGroup group, Writer out, String path, boolean root)
			throws Exception {
		if (!group.exists() && !root) {
			throw new Exception("Cannot write a group which does not exist");
		}
		List parsedPath = new ArrayList();
		int pathLen = 0;
		int indent = 1;
		StringTokenizer tokens = new StringTokenizer(path,
				PropertyGroup.PATH_DELIMITER);
		while (tokens.hasMoreTokens()) {
			parsedPath.add(tokens.nextToken());
		}
		pathLen = parsedPath.size();
		if (root) {
			writeOpen(ROOT, false, out, 0);
		}
		for (int i = 0; i < pathLen; i++) {
			writeOpen((String) parsedPath.get(i), false, out, indent++);
		}
		writeGroup(group, out, indent);
		for (int i = pathLen - 1; i >= 0; i--) {
			writeClose((String) parsedPath.get(i), out, --indent);
		}
		if (root) {
			writeClose(ROOT, out, 0);
		}
	}

	private void writeGroup(PropertyGroup group, Writer out, int indent)
			throws IOException {
		boolean empty = false;
		if (group.getName() != null) {
			if (group.getLists().size() < 1 && group.getGroups().size() < 1
					&& group.getOriginalValue() == null) {
				empty = true;
			}

			// initial tag
			out.write(indent(indent));
			out.write(OPEN_TAG);
			out.write(group.getName());
			indent++;
			// attributes
			if (group.getOriginalProperties().size() > 0) {
				out.write(writeAttributes(group.getOriginalProperties()));
			}
			// if tag has attributes only, then close with "/>"
			if (empty) {
				out.write(END_TAG);
			} else {
				out.write(CLOSE_TAG);
			}
			out.write(NEW_LINE);
			// write the value of the group to the out
			if (group.getOriginalValue() != null) {
				out.write(indent(indent));
				out.write(eccape(group.getOriginalValue()));
				out.write(NEW_LINE);
			}
			if (group.getLists().size() > 0) {
				Iterator iter = group.getListNames().iterator();
				while (iter.hasNext()) {
					String listName = (String) iter.next();
					out.write(writeList(listName,
							group.getOriginalList(listName), indent));
				}
			}
			if (group.getGroups().size() > 0) {
				Collection children = (Collection) group.getGroups();
				Iterator groupIterator = children.iterator();
				while (groupIterator.hasNext()) {
					PropertyGroup child = (PropertyGroup) groupIterator.next();
					if (child.exists()) {
						writeGroup(child, out, indent);
					}
				}
			}
			indent--;
			if (!empty) {
				out.write(indent(indent));
				out.write(OPEN_TAG);
				out.write(FORWARD_SLASH);
				out.write(group.getName());
				out.write(CLOSE_TAG);
				out.write(NEW_LINE);
			}
		}
	}

	private String writeAttributes(Properties props) {
		StringBuffer attributeBuffer = new StringBuffer(128);
		attributeBuffer.append(SPACE);
		Iterator attribIterator = props.keySet().iterator();
		while (attribIterator.hasNext()) {
			String key = (String) attribIterator.next();
			attributeBuffer.append(key).append(EQUALS).append(QUOTE)
					.append(eccape((String) props.get(key))).append(QUOTE)
					.append(SPACE);
		}
		String attributeString = attributeBuffer.deleteCharAt(
				attributeBuffer.length() - 1).toString();
		return attributeString;
	}

	private String writeList(String listName, List list, int indent) {
		StringBuffer listBuffer = new StringBuffer(128);
		listBuffer.append(indent(indent)).append(OPEN_TAG).append(QUOTE)
				.append(listName).append(QUOTE).append(CLOSE_TAG);
		listBuffer.append(writeListElements(list, indent));
		listBuffer.append(indent(indent)).append(OPEN_TAG)
				.append(FORWARD_SLASH).append(LIST).append(CLOSE_TAG)
				.append(NEW_LINE);
		return listBuffer.toString();
	}

	private String writeListElements(List list, int indent) {
		StringBuffer elements = new StringBuffer(128);
		indent++;
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			String value = (String) iter.next();
			elements.append(NEW_LINE).append(indent(indent)).append(OPEN_TAG)
					.append(LIST_ITEM_TAG).append(CLOSE_TAG)
					.append(eccape(value)).append(OPEN_TAG)
					.append(FORWARD_SLASH).append(LIST_ITEM_TAG)
					.append(CLOSE_TAG);
		}
		indent--;
		elements.append(NEW_LINE);
		String elementString = elements.toString();
		return elementString;
	}

	private String eccape(String string) {
		StringTokenizer tokenizer = new StringTokenizer(string, OPEN_TAG
				+ CLOSE_TAG + QUOTE, true);
		StringBuffer buffer = new StringBuffer(128);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.equals(OPEN_TAG)) {
				token = ESCAPE_OPEN_TAG;
			} else if (token.equals(CLOSE_TAG)) {
				token = ESCAPE_CLOSE_TAG;
			} else if (token.equals(QUOTE)) {
				token = ESCAPE_QUOTE;
			}
			buffer.append(token);
		}
		return buffer.toString();
	}

	private String indent(int indent) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			buffer.append(INDENT);
		}
		return buffer.toString();
	}

	private void writeOpen(String tag, boolean end, Writer out, int indent)
			throws IOException {
		writeOpen(tag, null, end, out, indent);
	}

	private void writeOpen(String tag, Properties properties, boolean end,
			Writer out, int indent) throws IOException {
		out.write(indent(indent));
		out.write(OPEN_TAG);
		out.write(tag);
		writeProperties(properties, out, indent);
		out.write(end ? END_TAG : CLOSE_TAG);
		out.write(NEW_LINE);
	}

	private void writeClose(String tag, Writer out, int indent)
			throws IOException {
		out.write(indent(indent));
		out.write(OPEN_TAG);
		out.write(FORWARD_SLASH);
		out.write(tag);
		out.write(CLOSE_TAG);
		out.write(FORWARD_SLASH);
	}

	private void writeProperties(Properties attributes, Writer out, int indent)
			throws IOException {
		String name, value;
		Enumeration names;
		if (attributes != null) {
			names = attributes.propertyNames();
			while (names.hasMoreElements()) {
				name = (String) names.nextElement();
				value = attributes.getProperty(name);
				out.write(SPACE);
				out.write(name);
				out.write(EQUALS);
				out.write(QUOTE);
				out.write(value);
				out.write(QUOTE);
			}
		}
	}
}
