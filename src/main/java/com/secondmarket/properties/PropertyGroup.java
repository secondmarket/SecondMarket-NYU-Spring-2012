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
 * @author Danjuan
 * PropertyGroup can be used to represent any grouping of properties for a
 * variety of purposes. Each PropertyGroup contains a name, a value, a
 * Properties object containing name/value pairs, a Map of lists, a Map of child
 * PropertyGroup objects, a {@link PropertyLoader}. Each group is intended to be 
 * a unit of information which is logically grouped together. PropertyGroups are 
 * loaded and stored by an implementation of PropertyLoader. Each PropertyGroup 
 * must have a PropertyLoader, and all PropertyGroups with the exception of the 
 * root group, have its parent PropertyGroup as its PropertyLoader.
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

	/**
	 * Local fields
	 */
	private String name; // Either the tag name, or the "name"
	// attribute of this group
	private String value; // Value between open and close tags
	private Properties properties; // attributes of THIS group
	private Map<String, PropertyGroup> groups; // sub-groups belonging to THIS
												// group
	private Map lists; // property lists belonging to THIS group
	private PropertyLoader loader; // The loader for THIS group

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
	 * @return true if this group has a non-null value, attributes, lists, or
	 *         children who have non-null values, attributes or lists. false
	 *         otherwise
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
	 * Gets the value of this group. If the value is substituted, gets the
	 * substituted value.
	 * 
	 * 
	 * @return a String containing the value of this group.
	 */
	public String getValue() {
	//	return substitute(value);
		return value;
	}

	/**
	 * return all of the values in the list
	 * 
	 * @param name
	 *            a String representing the name of the list
	 * 
	 * @return an Iterator containing the values of the list, null if no list is
	 *         found
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
	 * Gets specified property's value, if the value is substituted, return the
	 * substituted value
	 * 
	 * @param String
	 *            name of the attribute to get
	 * 
	 * @return property's value, null if property is not found
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
	 * Gets a property value from relative path and name. If value is
	 * substituted, gets the substituted value
	 * 
	 * @param path
	 *            relative path
	 * @param name
	 *            name of the property to get
	 * 
	 * @return attribute's value, null if property not found
	 */
	public String getProperty(String path, String name) {
		return getGroup(path).getProperty(name);
	}

	/**
	 * Gets specified property value converted to a <code>Boolean</code>. The
	 * method considers the strings <code>"true"</code> and <code>"y"</code> as
	 * true (case ignored). All other strings will return false. If property is
	 * substituted, then the substituted value is utilized to get
	 * <code>Boolean</code>.
	 * 
	 * @param name
	 *            of the property to get
	 * 
	 * @return Property's value converted to a <code>Boolean</code>, null if
	 *         property is not found
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
	 * Gets a property value converted to a <code>Boolean</code> from relative
	 * path and name. If property is substituted, then the substituted value is
	 * utilized to get <code>Boolean</code>.
	 * 
	 * @param path
	 *            relative path
	 * @param name
	 *            name of the property to get
	 * 
	 * @return Property's value converted to a <code>Boolean</code>, null if
	 *         property is not found
	 * @see #getBooleanProperty(String)
	 */
	public Boolean getBooleanProperty(String path, String name) {
		return getGroup(path).getBooleanProperty(name);
	}

	/**
	 * Gets specified property value converted to a <code>Integer</code>. Null
	 * will be returned if either the property is not found or is not a valid
	 * integer. If property is substituted, then the substituted value is
	 * utilized to get <code>Integer</code>.
	 * 
	 * @param name
	 *            of the property to get
	 * @return Property's value converted to a <code>Integer</code>, null if it
	 *         does not exist
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
	 * Gets a property value converted to a <code>Integer</code> from relative
	 * path and name. If property is substituted, then the substituted value is
	 * utilized to get <code>Integer</code>.
	 * 
	 * @param String
	 *            path relative path
	 * @param String
	 *            name of the property to get
	 * 
	 * @return Property's value converted to a <code>Integer</code>, null if it
	 *         does not exist
	 * @see #getIntegerProperty(String)
	 */
	public Integer getIntegerProperty(String path, String name) {
		return getGroup(path).getIntegerProperty(name);
	}

	/**
	 * Gets a group value from relative path. If the value is substituted, gets
	 * the substituted value
	 * 
	 * @param path
	 *            relative path
	 * 
	 * @return value of the group specified by path, null if the group does not
	 *         exist
	 */
	public String getValue(String path) {
		return getGroup(path).getValue();
	} // end method getProperty

	/**
	 * Gets a <code>PropertyGroup</code> using relative path.
	 * 
	 * @param group
	 *            name of group defined from <code>this</code> object
	 * 
	 * @return group specified by the relative path, returns empty
	 *         <code>PropertyGroup</code> if group does not exist
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
				} // end if
				else if (token.equals(OPEN_SUBSTITUTE)) {
				} // end else if
				else if (token.equals(CLOSE_SUBSTITUTE)) {
					substitute = false;
				} // end else if
				else {
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

					} // end if
					else {
						buffer.append(substitute(token));
					} // end else
				} // end if
			} // end while
		} // end if
		else {
			return value;
		} // end else
		return buffer.toString();
	} // end method substitute

	/**
	 * Gets the value of the given property in this <code>PropertyGroup</code>
	 * 
	 */
	private String getPropertyValue(String name) {
		return substitute(properties.getProperty(name));
	}

	/**
	 * Gets a List from this <code>PropertyGroup</code>
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
	 * Parses path into <code>String</code> array.
	 */
	private static String[] parsePath(String s) {
		StringTokenizer st = new StringTokenizer(s, PATH_DELIMITER);

		int tokens = st.countTokens();
		String[] path = new String[tokens];

		for (int i = 0; i < tokens; i++) {
			path[i] = st.nextToken();
		}
		return path;
	} // end method parsePath

	/**
	 * Gets a Properties object of all properties for this group. For each
	 * property that is substituted, the substituted value is returned. For each
	 * call to this method, a new Properties object is constructed; therefore
	 * changes made to the underlying PropertyGroup will not be propagated
	 * automatically. In order to listen for changes, use the
	 * {@link #addPropertyChangedListener(PropertyGroupListener)} method to
	 * register for changes
	 * 
	 * @return all properties (name/value pairs) for this group, substituted as
	 *         necessary
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
	 * Gets an unmodifiable <code>Collection</code> of names of all lists within
	 * this <code>PropertyGroup</code>
	 * 
	 * 
	 * @return names of each list for this group
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
	 * Gets an unmodifiable <code>Collection</code> of all child groups within
	 * this <code>PropertyGroup</code> hurley is a big chump
	 * 
	 * @return PropertyGroup objects of each child group for this group
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
	 * Gets an unmodifiable <code>Map</code> of all child groups within this
	 * <code>PropertyGroup</code> hurley is a big chump
	 * 
	 * @return PropertyGroup objects of each child group for this group
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
	 * Gets list of property names for this <code>PropertyGroup</code>
	 * 
	 * @return all names of properties in this <code>PropertyGroup</code>
	 */
	public Collection getPropertyNames() {
		return Collections.unmodifiableSet(properties.keySet());
	}

	/**
	 * Gets list of child group names for this group
	 * 
	 * @return all names of child groups for this <code>PropertyGroup</code>
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
	 * Gets list of list names for this <code>PropertyGroup</code>
	 * 
	 * @return all names of lists for this <code>PropertyGroup</code>
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

	/******* GET ORIGINAL VALUES -- UNSUBSTITUTED PROPERTIES, VALUES, LISTS ********/
	/**
	 * Gets orignal Properties for this <code>PropertyGroup</code>, values are
	 * NOT substituted
	 * 
	 * @return unsubstituted properties for this <code>PropertyGroup</code>
	 */
	public Properties getOriginalProperties() {
		return properties;
	}

	/**
	 * Gets orignal value for this <code>PropertyGroup</code>, value is NOT
	 * substituted
	 * 
	 * @return unsubstituted value for this <code>PropertyGroup</code>
	 */
	public String getOriginalValue() {
		return value;
	}

	/**
	 * Gets orignal list values for a list in this <code>PropertyGroup</code>,
	 * values are NOT substituted
	 * 
	 * @param name
	 *            the name of the list for which to retrieve original values
	 * 
	 * @return unsubstituted properties for this <code>PropertyGroup</code>
	 */
	public List getOriginalList(String name) {
		return ((PropertyGroupList) getList(name)).getOriginalList();
	} // end method getList

	/*************************************************
	 * ROOT METHODS
	 *************************************************/
	/**
	 * Gets the root property group. The name of the loader to use is defined by
	 * the "property.loader.class" system property.
	 * 
	 * @return the root property group.
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
	 * Gets the root's <code>PropertyLoader</code>. If property.loader.class is
	 * specified in the system properties, then that is used, otherwise, the
	 * {@link #DEFAULT_LOADER} is used
	 * 
	 * @return the loader utilized to load the root <code>PropertyGroup</code>
	 * 
	 * @throws PropertyException
	 *             wraps <code>ClassCastException</code>,
	 *             <code>InstantiationException</code>,
	 *             <code>IllegalAccessException</code>,
	 *             <code>ClassNotFoundException</code>
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
	 * 
	 * @param loader
	 *            the <code>PropertyLoader</code> to use to load the properties
	 * 
	 * @return a <code>PropertyGroup</code> loaded from the loader
	 */
	public static PropertyGroup getGroup(PropertyLoader loader)
			throws Exception {

		PropertyGroup group = new PropertyGroup(loader);
		group.reload();
		return group;
	}

	/*********************************************************
	 * MODIFIER METHODS
	 *********************************************************/
	/**
	 * Sets the value of a property in this <code>PropertyGroup</code>
	 * 
	 * @param prop
	 *            the name of the property
	 * @param value
	 *            the value of the property
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

		// PropertyGroupEvent event = new PropertyGroupEvent(this, value);
		// propertyChangedDispatcher.dispatch(event);
		return retval;
	}

	/**
	 * Sets the value of a list in this <code>PropertyGroup</code>
	 * 
	 * 
	 * @param listName
	 *            the name of the list to set, if this list does not exist, it
	 *            is added
	 * @param list
	 *            the <code>List</code> you wish to set
	 */
	public void setList(String listName, List list) {
		PropertyGroupList pgList = new PropertyGroupList(listName, list);
		lists.put(listName, pgList);

		// PropertyGroupEvent event = new PropertyGroupEvent(this, listName);
		// listChangedDispatcher.dispatch(event);
	}

	/**
	 * Sets the value of this <code>PropertyGroup</code>
	 * 
	 * @param value
	 *            the value of the group
	 */
	public void setValue(String value) {
		this.value = value;

		// PropertyGroupEvent event = new PropertyGroupEvent(this, null);
		// valueChangedDispatcher.dispatch(event);
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

		// fire event
		// PropertyGroupEvent event = new PropertyGroupEvent(this, null);
		// groupClearedDispatcher.dispatch(event);
	}

	/******************************************************
	 * Load, store, and reload methods
	 ******************************************************/
	/**
	 * Clears and reloads parameters from all sources.
	 * 
	 * @throws KnightException
	 *             when there is a problem in loading properties
	 */
	public void reload() throws Exception {

		clear();
		loader.load(this);
	}

	/**
	 * Loads groups using loader. Since a group's parent is its loader (except
	 * for the root), this method will be called recursively until the root is
	 * reached. Then, the root will be reloaded from its PropertyLoader.
	 * 
	 * @param group
	 *            the group to
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
	 * @param group
	 *            group to be stored
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
	 * Gets a string representation of this <code>PropertyGroup</code>; shows
	 * the name, value, attributes, and the names of all lists and child groups
	 * 
	 * 
	 * @return string representation of this <code>PropertyGroup</code>
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
