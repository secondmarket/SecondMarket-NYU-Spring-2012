package com.secondmarket.properties;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/***
 * @author Danjuan Ye
 * 
 */
public class PropertyGroup implements PropertyLoader {
	public static final String PATH_DELIMITER = ".";

	private static final PropertyLoader DEFAULT_LOADER = new ClasspathPropertyLoader();
	private static final String ROOT_LOADER_CLASS_PROP = "property.loader.class";
	public static final String SUBSTITUTE_DELIMITER = "$";
	public static final String OPEN_SUBSTITUTE = "{";
	public static final String CLOSE_SUBSTITUTE = "}";
	public static final String VALUE = "VALUE";
	private static final int MINIMUM_TOKEN_COUNT = 4;

	private static PropertyGroup root;
	private String name;
	private String value;
	private Properties properties;
	private Map<String, PropertyGroup> groups;
	private Map lists;
	private PropertyLoader loader;

	/**
	 * Private constructor used to create new PropertyGroups
	 */
	private PropertyGroup(PropertyLoader loader) {

		this.name = null;
		this.value = null;
		this.properties = new Properties();
		this.groups = new HashMap();
		this.lists = new HashMap();
		this.loader = loader;
	}

	private PropertyGroup() {
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/**
	 * Determines if this group was actually defined somewhere, or if it is
	 * merely a placeholder
	 * 
	 */
	public synchronized boolean exists() {
		boolean exists = (properties.size() > 0 || listsExist() || ((value != null)));
		Iterator i;
		if (!exists) {
			i = groups.values().iterator();
			while (!exists && i.hasNext()) {
				exists = ((PropertyGroup) i.next()).exists();
			}
		}
		return exists;
	}

	private synchronized boolean listsExist() {
		boolean flag = false;

		if (lists.size() > 0) {
			Iterator iter = lists.values().iterator();

			while (iter.hasNext()) {
				List list = (List) iter.next();

				if (list.size() > 0) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * Gets the value of this group.
	 */
	public String getValue() {
		// return substitute(value);
		return value;
	}

	/**
	 * return all of the values in the list
	 * 
	 */
	public List getList(String name) {
		PropertyGroup group;
		String[] path = parsePath(name);
		if (path.length == 0) {
			return null;
		}
		group = getGroup(path, 0, path.length - 2);

		return group.getListValue(path[path.length - 1]);
	}

	/**
	 * Gets specified property's value.
	 */
	public String getProperty(String name) {
		PropertyGroup group;
		String[] path = parsePath(name);
		if (path.length == 0) {
			return getValue();
		}
		group = getGroup(path, 0, path.length - 2);
		return group.getPropertyValue(path[path.length - 1]);
	}

	/**
	 * Gets a property value from relative path and name.
	 */
	public String getProperty(String path, String name) {
		return getGroup(path).getProperty(name);
	}

	/**
	 * Gets specified property value converted to a Boolean.
	 */
	public Boolean getBooleanProperty(String name) {
		Boolean ret = null;
		String prop = getProperty(name);
		if (prop != null) {
			ret = prop.equalsIgnoreCase("true") || prop.equalsIgnoreCase("y") ? Boolean.TRUE
					: Boolean.FALSE;
		}
		return ret;
	}

	/**
	 * Gets a property value converted to a Boolean from relative path and name.
	 */
	public Boolean getBooleanProperty(String path, String name) {
		return getGroup(path).getBooleanProperty(name);
	}

	/**
	 * Gets specified property value converted to a Integer.
	 */
	public Integer getIntegerProperty(String name) {
		Integer ret = null;
		String prop = getProperty(name);
		if (prop != null) {
			ret = new Integer(prop);
		}
		return ret;
	}

	/**
	 * Gets a property value converted to a Integer from relative path and name.
	 * If property is substituted, then the substituted value is utilized to get
	 * Integer.
	 * 
	 */
	public Integer getIntegerProperty(String path, String name) {
		return getGroup(path).getIntegerProperty(name);
	}

	/**
	 * Gets a group value from relative path. If the value is substituted, gets
	 * the substituted value
	 */
	public String getValue(String path) {
		return getGroup(path).getValue();
	}

	/**
	 * Gets a PropertyGroup using relative path.
	 * 
	 */
	public PropertyGroup getGroup(String path) {
		String[] parsedPath = parsePath(path);
		return getGroup(parsedPath, 0, parsedPath.length - 1);
	}

	/**
	 * Substitutes a string surrounded by ${} with the appropriate value from
	 * the proper PropertyGroup.
	 * 
	 * 
	 * Example: "${Entities.ORDER.alias}" should return the alias property in
	 * the Entities.ORDER group
	 * 
	 */
	private String substitute(String value) {
		if (value == null) {
			return null;
		}

		String path = null;
		String name = null;

		StringTokenizer tokenizer = new StringTokenizer(value,
				SUBSTITUTE_DELIMITER + OPEN_SUBSTITUTE + CLOSE_SUBSTITUTE, true);
		StringBuffer buffer = null;
		if (tokenizer.countTokens() >= MINIMUM_TOKEN_COUNT) {
			boolean substitute = false;

			buffer = new StringBuffer();

			while (tokenizer.hasMoreElements()) {
				String token = tokenizer.nextToken();
				if (token.equals(SUBSTITUTE_DELIMITER)) {
					substitute = true;
				} else if (token.equals(OPEN_SUBSTITUTE)) {
				} else if (token.equals(CLOSE_SUBSTITUTE)) {
					substitute = false;
				} else {
					if (substitute) {
						int lastIndex = token.lastIndexOf(PATH_DELIMITER);

						if (lastIndex > 0) {
							path = token.substring(0, lastIndex);
							name = token.substring(lastIndex + 1,
									token.length());
						} else {
							name = token;
						}

						if (name.equals(VALUE)) {
							if (root.getValue(path) != null) {
								buffer.append(substitute(root.getValue(path)));
							} else {
								return value;
							}
						} else {
							if (root.getProperty(path, name) != null) {
								buffer.append(substitute(root.getProperty(path,
										name)));
							} else {
								return value;
							}
						}

					} else {
						buffer.append(substitute(token));
					}
				}
			}
		} else {
			return value;
		}
		return buffer.toString();
	}

	/**
	 * Gets the value of the given property in this PropertyGroup
	 * 
	 */
	private String getPropertyValue(String name) {
		return substitute(properties.getProperty(name));
	}

	/**
	 * Gets a List from this PropertyGroup
	 * 
	 */
	private synchronized List getListValue(String name) {
		List list = (List) lists.get(name);
		if (list == null) {
			list = new PropertyGroupList(name);
			lists.put(name, list);
		}
		return list;
	}

	/**
	 * Gets a PropertyGroup from the given path, index, and target. Used
	 * internally when getting relative groups
	 */
	private PropertyGroup getGroup(String[] pathArray, int index,
			int targetIndex) {
		if (targetIndex < index) {
			return this;
		}

		PropertyGroup group = getChild(pathArray[index]);
		if (index == targetIndex) {
			return group;
		}
		return group.getGroup(pathArray, index + 1, targetIndex);
	}

	public synchronized PropertyGroup addChild(String childName) {
		PropertyGroup group = null;// (PropertyGroup) groups.get(childName);
		if (group == null) {
			group = new PropertyGroup(this);
			group.setName(childName);

			groups.put(childName, group);
		}
		return group;
	}

	/**
	 * Gets the child group from this group. If the child does not exist, one is
	 * created and its initial attributes are set
	 * 
	 */
	private synchronized PropertyGroup getChild(String childName) {
		PropertyGroup group = (PropertyGroup) groups.get(childName);
		if (group == null) {
			group = new PropertyGroup(this);
			group.setName(childName);

			groups.put(childName, group);
		}
		return group;
	}

	/**
	 * Parses path into String array.
	 */
	private static String[] parsePath(String s) {
		StringTokenizer st = new StringTokenizer(s, PATH_DELIMITER);

		int tokens = st.countTokens();
		String[] path = new String[tokens];

		for (int i = 0; i < tokens; i++) {
			path[i] = st.nextToken();
		}
		return path;
	}

	/**
	 * Gets a Properties object of all properties for this group.
	 */
	public synchronized Properties getProperties() {
		Properties substitutedProps = new Properties();
		Iterator enumer = getPropertyNames().iterator();

		while (enumer.hasNext()) {
			String name = (String) enumer.next();
			substitutedProps.put(name,
					substitute((String) properties.get(name)));
		} // end while
		return substitutedProps;
	}

	/**
	 * Gets names of each list for this group
	 */
	public synchronized Collection getLists() {
		Collection retval = new ArrayList(lists.size());
		Iterator i = lists.values().iterator();
		List list;
		while (i.hasNext()) {
			list = (List) i.next();
			if (!list.isEmpty()) {
				retval.add(list);
			}
		}
		return Collections.unmodifiableCollection(retval);
	}

	/**
	 * Gets PropertyGroup objects of each child group for this group
	 */
	public synchronized Collection getGroups() {
		Collection retval = new ArrayList(groups.size());
		Iterator i = groups.values().iterator();
		PropertyGroup group;
		while (i.hasNext()) {
			group = (PropertyGroup) i.next();
			if (group.exists()) {
				retval.add(group);
			}
		}
		return Collections.unmodifiableCollection(retval);
	}

	/**
	 * Gets PropertyGroup objects of each child group for this group
	 */
	public synchronized Map<String, PropertyGroup> getGroupsMap() {

		HashMap<String, PropertyGroup> resultMap = new HashMap<String, PropertyGroup>();
		PropertyGroup pg;

		for (String key : groups.keySet()) {
			pg = groups.get(key);
			if (pg.exists())
				resultMap.put(key, pg);
		}
		return Collections.unmodifiableMap(resultMap);
	}

	/**
	 * Gets list of property names
	 * 
	 */
	public Collection getPropertyNames() {
		return Collections.unmodifiableSet(properties.keySet());
	}

	/**
	 * Gets list of child group names for this group
	 * 
	 */
	public synchronized Collection getGroupNames() {
		Collection retval = new ArrayList(groups.size());
		Iterator i = groups.entrySet().iterator();
		Map.Entry entry;
		PropertyGroup group;
		while (i.hasNext()) {
			entry = (Map.Entry) i.next();
			group = (PropertyGroup) entry.getValue();
			if (group.exists()) {
				retval.add(entry.getKey());
			}
		}
		return Collections.unmodifiableCollection(retval);
	}

	/**
	 * Gets list of list names
	 */
	public synchronized Collection getListNames() {
		Collection retval = new ArrayList(lists.size());
		Iterator i = lists.entrySet().iterator();
		Map.Entry entry;
		List list;
		while (i.hasNext()) {
			entry = (Map.Entry) i.next();
			list = (List) entry.getValue();
			if (!list.isEmpty()) {
				retval.add(entry.getKey());
			}
		}
		return Collections.unmodifiableCollection(retval);
	}

	/******* GET ORIGINAL VALUES -- *******/
	/**
	 * Gets orignal Properties
	 */
	public Properties getOriginalProperties() {
		return properties;
	}

	/**
	 * Gets orignal value
	 */
	public String getOriginalValue() {
		return value;
	}

	/**
	 * Gets orignal list values for a list in this PropertyGroup, values
	 * 
	 */
	public List getOriginalList(String name) {
		return ((PropertyGroupList) getList(name)).getOriginalList();
	}

	/*************************************************
	 * ROOT METHODS
	 *************************************************/
	/**
	 * Gets the root property group. The name of the loader to use is defined by
	 * the "property.loader.class" system property.
	 * 
	 */
	public static PropertyGroup getRoot() throws Exception {

		if (root == null)
			initRoot();
		return root;
	}

	/**
	 * Initializes the root <code>PropertyGroup</code>
	 */
	private synchronized static void initRoot() throws Exception {

		if (PropertyGroup.root == null) {
			PropertyLoader root_loader = getRootLoader();
			PropertyGroup.root = getGroup(root_loader);
		}
	}

	/**
	 * Gets the root's PropertyLoader. If property.loader.class is specified in
	 * the system properties, then that is used, otherwise, the DEFAULT_LOADER
	 * is used
	 * 
	 */
	private static PropertyLoader getRootLoader() throws Exception {

		PropertyLoader loader;
		String loaderClass;

		try {
			loaderClass = System.getProperty(ROOT_LOADER_CLASS_PROP);
			if (loaderClass == null)
				loader = DEFAULT_LOADER;
			else
				loader = (PropertyLoader) (Class.forName(loaderClass)
						.newInstance());
		} catch (ClassCastException e) {
			throw new Exception(
					"Exception getting root PropertyGroup, Class cast exception");
		} catch (InstantiationException e) {
			throw new Exception(
					"Exception getting root PropertyGroup, InstantiationException");
		} catch (IllegalAccessException e) {
			throw new Exception(
					"Exception getting root PropertyGroup, IllegalAccessException");
		} catch (ClassNotFoundException e) {
			throw new Exception(
					"Exception getting root PropertyGroup, ClassNotFoundException");
		}
		return loader;
	}

	/**
	 * Obtains a property group using the specified loader
	 */
	public static PropertyGroup getGroup(PropertyLoader loader)
			throws Exception {

		PropertyGroup group = new PropertyGroup(loader);
		group.reload();
		return group;
	}

	/**
	 * Sets the value of a property in this PropertyGroup
	 * 
	 */
	public String setProperty(String prop, String value) {
		PropertyGroup group;
		String[] path = parsePath(prop);
		if (path.length == 0) {
			return getValue();
		}
		group = getGroup(path, 0, path.length - 2);
		return group.setPropertyValue(path[path.length - 1], value);
	}

	/**
	 * Sets value of a property in this PropertyGroup
	 */
	private String setPropertyValue(String name, String value) {
		String retval = (String) properties.setProperty(name, value);
		return retval;
	}

	/**
	 * Sets the value of a list in this PropertyGroup
	 * 
	 */
	public void setList(String listName, List list) {
		PropertyGroupList pgList = new PropertyGroupList(listName, list);
		lists.put(listName, pgList);
	}

	/**
	 * Sets the value of this PropertyGroup
	 * 
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Clears this PropertyGroup; empties the Properties object, resets value to
	 * null, recurses through all child groups clearing them as well
	 */
	public synchronized void clear() {
		// Loop through children, recursively calling clear()
		Iterator i = groups.values().iterator();
		while (i.hasNext()) {
			((PropertyGroup) i.next()).clear();
		}

		// Clear all lists
		i = lists.values().iterator();
		while (i.hasNext()) {
			List list = (List) i.next();
			if (list.size() > 0) {
				list.clear();
			}
		}

		// Clear Properties object and value for this group
		this.value = null;
		this.properties.clear();
	}

	/******************************************************
	 * Load, store, and reload methods
	 ******************************************************/
	/**
	 * Clears and reloads parameters from all sources.
	 */
	public void reload() throws Exception {

		clear();
		loader.load(this);
	}

	/**
	 * Loads groups using loader. Since a group's parent is its loader (except
	 * for the root), this method will be called recursively until the root is
	 * reached. Then, the root will be reloaded from its PropertyLoader.
	 */
	public void load(PropertyGroup group) throws Exception {
		load();
	}

	/**
	 * Convenience method to call {@link #load(PropertyGroup)}
	 */
	public void load() throws Exception {
		loader.load(this);
	}

	/**
	 * Stores groups using loader. Since a group's parent is its loader (except
	 * for the root), this method will be called recursively until the root is
	 * reached. Then, the root will be restored from its PropertyLoader.
	 * 
	 */
	public void store(PropertyGroup group) throws Exception {
		store();
	}

	/**
	 * Convenience method to call {@link #store(PropertyGroup)}
	 */
	public void store() throws Exception {
		loader.store(this);
	}

	/**
	 * Gets a string representation of this PropertyGroup;
	 */
	public String toString() {
		StringBuffer toString = new StringBuffer(128);

		toString.append("\n" + getClass());
		toString.append("\nNAME=" + getName());
		toString.append("\nVALUE=" + getValue());
		toString.append("\nATTRIBUTES:");

		Enumeration enumer = Collections.enumeration(getPropertyNames());

		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			toString.append("\n    " + key + "=" + properties.get(key));
		}

		toString.append("\nLISTS:");
		Iterator it = getListNames().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			toString.append("\n    " + key);

			Iterator it2 = getList(key).iterator();

			while (it2.hasNext()) {
				toString.append("\n       " + it2.next());
			}
		}

		toString.append("\nCHILD GROUPS:");
		Iterator it3 = getGroupNames().iterator();
		while (it3.hasNext()) {
			PropertyGroup group = (PropertyGroup) groups.get(it3.next());
			toString.append("\n...." + group);
		}

		return toString.toString();
	}

	/**
	 * Test method for properties. Displays the property, group, and list
	 * returned by the path entered on command line. If no parameter is
	 * enetered, the root group is printed.
	 * 
	 * @param argv
	 *            array of command line parameters, test method expects only 1
	 *            string, a "." delimited path to a group or property
	 */
	public static void main(String[] argv) throws Exception,
			java.io.IOException {
		if (argv.length == 1) {
			System.out.println(PropertyGroup.getRoot().getProperty(argv[0]));
			System.out.println(PropertyGroup.getRoot().getGroup(argv[0])
					.getValue());
			System.out.println(PropertyGroup.getRoot().getList(argv[0]));
		} else {
			System.out.println(PropertyGroup.getRoot());
		}
	}

	@SuppressWarnings("rawtypes")
	private class PropertyGroupList extends AbstractList {
		private List list;
		@SuppressWarnings("unused")
		private String name;

		public PropertyGroupList(String name) {
			this.name = name;
			list = new Vector();
		}

		public PropertyGroupList(String name, List localList) {
			this.name = name;

			if (localList == null) {
				this.list = new Vector();
			} else {
				this.list = new Vector(localList);
			}
		}

		@SuppressWarnings("unchecked")
		public void add(int index, Object o) {
			list.add(index, getString(o));
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public boolean addAll(int index, Collection col) {
			return list.addAll(index, col);
		}

		public void clear() {
			list.clear();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public List getOriginalList() {
			return Collections.unmodifiableList(list);
		}

		public boolean contains(Object o) {
			return list.contains(getString(o));
		}

		public Object get(int i) {
			return substitute((String) list.get(i));
		}

		public int indexOf(Object o) {
			return list.indexOf(getString(o));
		}

		public int lastIndexOf(Object o) {
			return list.lastIndexOf(getString(o));
		}

		public Object remove(int index) {
			Object retval = list.remove(index);
			return retval;
		}

		public Object set(int index, Object o) {
			@SuppressWarnings("unchecked")
			Object retval = list.set(index, getString(o));
			return retval;
		}

		public int size() {
			return list.size();
		}

		private String getString(Object o) {
			return o == null ? "" : o.toString();
		}
	}
}
