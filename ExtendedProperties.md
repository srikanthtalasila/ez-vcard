

The vCard standard allows for "extended" properties to exist in a vCard.  An extended property is a custom property whose name starts with "X-" and is not part of the vCard specification.  Many email clients make use of extended properties to store additional data.

# 1 Basic Usage #

To retrieve extended properties from a [VCard](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html) object, use the [getExtendedProperties(String)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getExtendedProperties(java.lang.String)) method.  This will return a list of [RawProperty](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/property/RawProperty.html) objects, which contain the values of each property.

```
VCard vcard = ...
List<RawProperty> managers = vcard.getExtendedProperties("X-MS-MANAGER");
for (RawProperty manager : managers){
  System.out.println("Manager: " + manager.getValue());
}
```

A complete list of all the vCard's extended properties can be retrieved using the [getExtendedProperties()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getExtendedProperties()) method.

```
VCard vcard = ...
List<RawProperty> extendedProperties = vcard.getExtendedProperties();
for (RawProperty property : extendedProperties){
  System.out.println(property.getValue());
}
```

To add an extended property to a vCard, call the [VCard.addExtendedProperty(String, String)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#addExtendedProperty(java.lang.String,%20java.lang.String)) method.  This method returns the [RawProperty](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/property/RawProperty.html) object that was added to the vCard, allowing you to make further modifications to the property if necessary.

```
VCard vcard = new VCard();
RawProperty spouse = vcard.addExtendedProperty("X-SPOUSE", "Jane Doe");
spouse.setGroup("item1");
spouse.addParameter("X-GENDER", "female");
```

# 2 Creating a custom property class #

In addition to using the [RawProperty](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/property/RawProperty.html) class to get/set the value of an extended property, you can also can create your own custom property class.  This is useful if the property value is in a format that requires some sort of complex parsing.  It requires the creation of two classes: a **property class** and a **scribe class**.


---

**NOTE:**

This is how all of the standard properties are implemented in ez-vcard, so feel free to take a look at the source code in the "[ezvcard.property](https://code.google.com/p/ez-vcard/source/browse/#svn%2Ftrunk%2Fsrc%2Fmain%2Fjava%2Fezvcard%2Fproperty)" and "[ezvcard.io.scribe](https://code.google.com/p/ez-vcard/source/browse/#svn%2Ftrunk%2Fsrc%2Fmain%2Fjava%2Fezvcard%2Fio%2Fscribe)" packages for more examples.

---


## 2.1 Property class ##

The **property class** holds the value of the property.  Its only requirement is that it has to extend the [VCardProperty](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/property/VCardProperty.html) class.  Otherwise, it's a [POJO](https://en.wikipedia.org/wiki/POJO).

The example below shows a property class for a property that holds the person's favorite colors.

```
public class FavoriteColors extends VCardProperty {
  private List<String> colors = new ArrayList<String>();

  public List<String> getColors() {
    return colors;
  }

  public void addColor(String color) {
    colors.add(color);
  }

  public String getLanguage() {
    //"parameters" is a protected field of "VCardProperty"
    return parameters.getLanguage();
  }

  public void setLanguage(String lang) {
    parameters.setLanguage(lang);
  }

  //optional
  //validates the property's data
  //invoked when "VCard.validate()" is called
  @Override
  protected void _validate(List<String> warnings, VCardVersion version, VCard vcard) {
    if (colors.isEmpty()) {
      warnings.add("No colors are defined.");
    }

    if (colors.contains("periwinkle") && version == VCardVersion.V4_0) {
      warnings.add("Periwinkle is deprecated in vCard 4.0.");
    }
  }
}
```

### 2.1.1 `_validate` method ###

The `_validate` method is optional.  It is used to verify the correctness of the property's data model.  It is invoked when the [VCard.validate()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#validate(ezvcard.VCardVersion)) method is called.

| **Parameter** | **Description** |
|:--------------|:----------------|
| `List<String> warnings` | The list that all validation warnings should be added to. |
| `VCardVersion version` | The vCard version that the vCard is being validated under. |
| `VCard vcard` | The vCard that is being validated. |

## 2.2 Scribe class ##

The **scribe class** is responsible for reading and writing the property to a file or other data stream.  The example below shows a scribe class for our "FavoriteColors" property.


---

**NOTE:**

Do not be alarmed at the size of this class.  Most of the methods do not require an implementation.  The behavior of the default implementations are described in the Javadocs for [VCardPropertyScribe](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/scribe/VCardPropertyScribe.html).

---


```
public static class FavoriteColorsScribe extends VCardPropertyScribe<FavoriteColors> {
  public FavoriteColorsScribe() {
    super(FavoriteColors.class, "X-FAV-COLORS");
  }

  //required
  //defines the property's default data type
  @Override
  protected VCardDataType _defaultDataType(VCardVersion version) {
    return null; //should always be "null" for extended properties
  }

  //optional
  //determines the data type based on the property value
  @Override
  protected VCardDataType _dataType(FavoriteColors property, VCardVersion version) {
    return VCardDataType.TEXT;
  }

  //optional
  //tweaks the property's parameters before the property is written
  @Override
  protected void _prepareParameters(FavoriteColors property, VCardParameters copy, VCardVersion version, VCard vcard) {
    if (copy.getLanguage() == null) {
      copy.setLanguage("en");
    }
  }

  //required
  //writes the property's value to a plain-text vCard
  @Override
  protected String _writeText(FavoriteColors property, VCardVersion version) {
    return list(property.getColors());
  }

  //required
  //parses the property's value from a plain-text vCard
  @Override
  protected FavoriteColors _parseText(String value, VCardDataType dataType, VCardVersion version, VCardParameters parameters, List<String> warnings) {
    FavoriteColors prop = new FavoriteColors();
    for (String color : list(value)) {
      prop.addColor(color);
    }
    return prop;
  }

  //optional
  //writes the property to an XML document (xCard)
  @Override
  protected void _writeXml(FavoriteColors property, XCardElement element) {
    for (String color : property.getColors()) {
      element.append(VCardDataType.TEXT, color);
    }
  }

  //optional
  //parses the property from an XML document (xCard)
  @Override
  protected FavoriteColors _parseXml(XCardElement element, VCardParameters parameters, List<String> warnings) {
    List<String> colors = element.all(VCardDataType.TEXT);
    if (colors.isEmpty()) {
      throw new CannotParseException("No <text> elements found.");
    }

    FavoriteColors property = new FavoriteColors();
    for (String color : colors) {
      property.addColor(color);
    }
    return property;
  }

  //optional
  //parses the property value from an HTML page (hCard)
  @Override
  protected FavoriteColors _parseHtml(HCardElement element, List<String> warnings) {
    FavoriteColors property = new FavoriteColors();

    String lang = element.attr("lang");
    property.setLanguage((lang.length() == 0) ? null : lang);

    property.getColors().addAll(element.allValues("color")); //gets the hCard values of all descendant elements that have a CSS class named "color"

    return property;
  }

  //optional
  //writes the property to a JSON stream (jCard)
  @Override
  protected JCardValue _writeJson(FavoriteColors property) {
    return JCardValue.multi(property.getColors());
  }

  //optional
  //parses the property value from a JSON stream (jCard)
  @Override
  protected FavoriteColors _parseJson(JCardValue value, VCardDataType dataType, VCardParameters parameters, List<String> warnings) {
    FavoriteColors property = new FavoriteColors();
    for (String color : value.asMulti()) {
      property.addColor(color);
    }
    return property;
  }
}
```

### 2.2.1 `Constructor` ###

The constructor calls the parent class constructor, passing in the property class and the property name.  Note that extended property names must begin with "X-".

### 2.2.2 `_defaultDataType` ###

This method is only used by the standard property classes.  It should always return "null" for extended property scribes.

### 2.2.3 `_dataType` ###

Determines the property's data type.  This causes a "VALUE" parameter to be added to the property when written to a plain-text vCard.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `FavoriteColors property` | The property object that is being written. |
| `VCardVersion version` | The version of the vCard that is being generated. |

### 2.2.4 `_prepareParameters` ###

Allows the property's parameters to be tweaked before the property is written.  A copy of the property's parameters is passed into this method so that the data in the original property object will not be modified.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `FavoriteColors property` | The property object that is being written. |
| `VCardParameters copy` | A copy of the property's parameters.  All modifications must be made to this copy. |
| `VCardVersion version` | The version of the vCard that is being generated. |
| `VCard vcard`       | The vCard that is being written. |

In the above example, a "LANGUAGE" parameter is added if one is not present.

### 2.2.5 `_writeText` ###

Generates the plain-text representation of the property.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `FavoriteColors property` | The property object that is being written. |
| `VCardVersion version` | The version of the vCard that is being generated. |

Our example uses the [list(Collection)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/scribe/VCardPropertyScribe.html#list(java.util.Collection)) utility method to generate a comma-delimited list that safely escapes all special characters.

**Special characters:** It is recommended that all special vCard characters be backslash-escaped if they do not have special meanings within the property value.  These special characters are: comma (`,`), semicolon (`;`), and backslash (`\`).  The [VCardPropertyScribe.escape()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/scribe/VCardPropertyScribe.html#escape(java.lang.String)) method can be used to do this.  Newline escaping and line folding do not need to be handled here (they are handled in the [VCardWriter](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/text/VCardWriter.html) class).

**SkipMeException:** If it is determined that the property should NOT be included in the vCard, a [SkipMeException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/SkipMeException.html) can be thrown.  This exception will prevent the writer from adding the property to the final vCard stream.

**Example:** Our `FavoriteColorsScribe` class will produce a vCard property that looks something like this:

`X-FAV-COLORS;VALUE=text;LANGUAGE=en:red,green,blue,escaped\,comma`

### 2.2.6 `_parseText` ###

Parses the plain-text representation of the property.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `String value`      | The property value, as read off the wire. |
| `VCardDataType dataType` | The property's data type.  This will be either the value of the VALUE parameter, or the property's default data type if no VALUE parameter is present). |
| `VCardVersion version` | The version of the vCard that is being parsed. |
| `VCardParameters parameters` | The property's parameters.  These parameters will be assigned to the property object when the `_parseText` method returns. |
| `List<String> warnings` | Any non-critical parsing problems can be added to this list. |

Our example uses the [list(String)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/scribe/VCardPropertyScribe.html#list(java.lang.String)) utility method to parse a comma-delimited list.

**SkipMeException:** If it is determined that the property should NOT be added to the [VCard](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html) object, a [SkipMeException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/SkipMeException.html) can be thrown.  The exception message will be logged as a warning.  Warnings can be retrieved using the [VCardReader.getWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/text/VCardReader.html#getWarnings()) method.

**CannotParseException:** If the property value cannot be parsed, a [CannotParseException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/CannotParseException.html) can be thrown.  This will cause the property to be unmarshalled as a `RawProperty` instead.  These properties can be retrieved by calling the [VCard.getExtendedProperty()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getExtendedProperty(java.lang.String)) method.  Also, a warning will be added to the parser's warnings list.  Warnings can be retrieved using the [VCardReader.getWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/text/VCardReader.html#getWarnings()) method.

### 2.2.8 `_writeXml` ###

Generates the XML (xCard) representation of the property.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `FavoriteColors property` | The property object that is being written. |
| `XCardElement element` | Represents the property's XML element.  This class wraps xCard functionality around a raw "`org.w3c.dom.Element`" object.  The raw `Element` object can be retrieved by calling [XCardElement.element()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/xml/XCardElement.html#element()), and can be modified as needed. |

In our example, the [XCardElement.append()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/xml/XCardElement.html#append(ezvcard.VCardDataType,java.lang.String)) method is used to add child `<text>` elements to the property's XML element.  These elements will belong to the xCard XML namespace.

**SkipMeException:** If it is determined that the property should NOT be included in the vCard, a [SkipMeException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/SkipMeException.html) can be thrown.  This exception will prevent the writer from adding the property to the XML document.

**Example:**  Our `FavoriteColorsScribe` class will produce an XML element that looks something like this:

```
<x-fav-colors xmlns="urn:ietf:params:xml:ns:vcard-4.0">
  <parameters>
    <!-- Note:
    The language parameter value ("en") is wrapped in a "language-tag" element because "language" is a standard vCard property.
    Non-standard parameters are wrapped in a "unknown" element by default.
    --> 
    <language><language-tag>en</language-tag></language>
  </parameters>
  <text>red</text>
  <text>green</text>
  <text>blue</text>
</x-fav-colors>
```

### 2.2.9 `_parseXml` ###

Parses the XML (xCard) representation of the property.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `XCardElement element` | Represents the property's XML element.  This class wraps xCard functionality around a raw "`org.w3c.dom.Element`" object.  The raw `Element` object can be retrieved by calling [XCardElement.element()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/xml/XCardElement.html#element()). |
| `VCardParameters parameters` | The property's parameters.  These parameters will be assigned to the property object when the `_parseXml` method returns. |
| `List<String> warnings` | Any non-critical parsing problems can be added to this list. |

**SkipMeException:** If it is determined that the property should NOT be added to the [VCard](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html) object, a [SkipMeException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/SkipMeException.html) can be thrown.  The exception message will be logged as a warning.  Warnings can be retrieved using the [XCardDocument.getParseWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/xml/XCardDocument.html#getParseWarnings()) method.

**CannotParseException:** If the property value cannot be parsed, a [CannotParseException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/CannotParseException.html) can be thrown.  This will cause the property to be parsed as an [XML](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/property/Xml.html) property instead.  XML properties can be retrieved by making the following method call: [VCard.getProperties(Xml.class)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getProperties(java.lang.Class)).  Also, a warning will be added to the parser's warnings list.  Warnings can be retrieved using the [XCardDocument.getParseWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/xml/XCardDocument.html#getParseWarnings()) method.

### 2.2.10 `_parseHtml` ###

Parses the HTML (hCard) representation of the property.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `HCardElement element` | Represents the property's HTML element. |
| `List<String> warnings` | Any non-critical parsing problems can be added to this list. |

In our example, we look for a "lang" HTML attribute and assign its value to the "language" parameter.  We then look for all sub-elements that have a "class=color" attribute, and add their values to the property.

**SkipMeException:** If it is determined that the property should NOT be added to the [VCard](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html) object, a [SkipMeException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/SkipMeException.html) can be thrown.  The exception message will be logged as a warning.  Warnings can be retrieved using the [HCardReader.getWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/html/HCardReader.html#getWarnings()) method.

**CannotParseException:** If the property value cannot be parsed, a [CannotParseException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/CannotParseException.html) can be thrown.  This will cause the property to be parsed as a `RawProperty` instead.  These properties can be retrieved by calling the [VCard.getExtendedProperty()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getExtendedProperty(java.lang.String)) method.  Also, a warning will be added to the parser's warnings list.  Warnings can be retrieved using the [HCardReader.getWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/html/HCardReader.html#getWarnings()) method.

### 2.2.11 `_writeJson` ###

Generates the JSON (jCard) representation of the property.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `FavoriteColors property` | The property object that is being written. |

Our example uses the [JCardValue.multi()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardValue.html) factory method to create a new [JCardValue](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardValue.html) instance which contains a list of values.

The [JCardValue](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardValue.html) class contains the following factory methods to aid in the construction the three most typical types of jCard values.

  * **single** - Defines the value as a single value, such as a single string.  Most properties are single valued.
  * **multi** - Defines the value as a list of values (such as our `FavoriteColors` property).
  * **structured** - Defines the value as a structured value (i.e. a "list of lists", such as the N property).

Objects may be passed into these methods.  Primitive wrapper objects, such as `Integer` and `Boolean`, will be converted to their appropriate JSON data type.  All other objects will be passed into the JSON stream as strings (their `toString()` method will be invoked).  Null values will be converted to empty strings.

```
JCardValue value = JCardValue.single("one");
//yields: ["propName", {}, "text", "one"]
```

```
JCardValue value = JCardValue.multi("one", 2, true);
//yields: ["propName", {}, "text", "one", 2, true]
```

```
JCardValue value = JCardValue.structured(1, Arrays.asList(2, 3), 4);
//yields: ["propName", {}, "integer", [1, [2, 3], 4]]
```

**SkipMeException:** If it is determined that the property should NOT be included in the vCard, a [SkipMeException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/SkipMeException.html) can be thrown.  This exception will prevent the marshaller from adding the property to the final jCard document.  The exception message will be logged as a warning.  Warnings can be retrieved using the [JCardWriter.getWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardWriter.html#getWarnings()) method.

**Example:** Our `FavoriteColorsScribe` class will produce a jCard property that looks something like this:

`["x-fav-colors", { "language":"en" }, "text", "red", "green", "blue"]`

### 2.2.12 `_parseJson` ###

Parses the JSON (hCard) representation of the property.

| **Method argument** | **Description** |
|:--------------------|:----------------|
| `JCardValue value`  | Represents the property's jCard value. |
| `VCardDataType dataType` | The property's data type. |
| `VCardParameters parameters` | The property's parameters.  These parameters will be assigned to the property object when the `_parseJson` method returns. |
| `List<String> warnings` | Any non-critical parsing problems can be added to this list. |

In our example, the [JCardValue.asMulti()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardValue.html) method is called to convert the raw JSON value to a multi-valued list, where each value is added to the property object.

The [JCardValue](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardValue.html) class has helper methods to aid in the retrieval of the three most typical types of jCard values.

  * **asSingle** - Gets the value of a property that contains a single value.  Most properties are single valued.
  * **asMulti** - Gets the value of a property that contains multiple values (such as our `FavoriteColors` property).
  * **asStructured** - Gets the value of a property that contains a structured value (i.e. a "list of lists", such as the N property).

**SkipMeException:** If it is determined that the property should NOT be added to the [VCard](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html) object, a [SkipMeException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/SkipMeException.html) can be thrown.  The exception message will be logged as a warning.  Warnings can be retrieved using the [JCardReader.getWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardReader.html#getWarnings()) method.

**CannotParseException:** If the property value cannot be parsed, a [CannotParseException](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/CannotParseException.html) can be thrown.  This will cause the property to be unmarshalled as a `RawProperty` instead.  These properties can be retrieved by calling the [VCard.getExtendedProperty()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getExtendedProperty(java.lang.String)) method.  Also, a warning will be added to the parser's warnings list.  Warnings can be retrieved using the [JCardReader.getWarnings()](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/io/json/JCardReader.html#getWarnings()) method.

## 2.3 Usage ##

### 2.3.1 Reading ###

Before a vCard is parsed, the extended property scribe must be registered with the reader object.  Then, once a vCard has been read, the instances of the extended property can be retrieved using the [VCard.getProperty(Class)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getProperty(java.lang.Class)) or [VCard.getProperties(Class)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#getProperties(java.lang.Class)) methods.

```
//using "Ezvcard" class
Reader reader = ...
VCard vcard = Ezvcard.parse(reader).register(new FavoriteColorsScribe()).first();
reader.close();

FavoriteColors first = vcard.getProperty(FavoriteColors.class);
List<FavoriteColors> all = vcard.getProperties(FavoriteColors.class);
```

```
//using reader class
Reader reader = ...
VCardReader vcr = new VCardReader(reader);
vcr.registerScribe(new FavoriteColorsScribe());
VCard vcard = vcr.readNext();
reader.close();

FavoriteColors first = vcard.getProperty(FavoriteColors.class);
List<FavoriteColors> all = vcard.getProperties(FavoriteColors.class);
```

### 2.3.2 Writing ###

To add an instance of an extended property class to a vCard, call the [VCard.addProperty(VCardProperty)](https://ez-vcard.googlecode.com/svn/javadocs/latest/ezvcard/VCard.html#addProperty(ezvcard.property.VCardProperty)) method.  Then, register the property's scribe with the writer class before writing the vCard.

```
VCard vcard = new VCard();

FavoriteColors favColors = new FavoriteColors();
favColors.addColor("red");
favColors.addColor("green");
favColors.addColor("blue");
vcard.addProperty(favColors);

Writer writer = ...

//using "Ezvcard" class
Ezvcard.write(vcard).register(new FavoriteColorsScribe()).go(writer);
writer.close();

//using writer class
VCardWriter vcw = new VCardWriter(writer);
vcw.registerScribe(new FavoriteColorsScribe());
vcw.write(vcard);
writer.close();
```