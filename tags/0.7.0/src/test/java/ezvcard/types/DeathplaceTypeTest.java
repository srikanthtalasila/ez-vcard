package ezvcard.types;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ezvcard.VCard;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;
import ezvcard.parameters.ValueParameter;
import ezvcard.util.XmlUtils;

/*
 Copyright (c) 2012, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * @author Michael Angstadt
 */
public class DeathplaceTypeTest {
	@Test
	public void marshal() throws Exception {
		VCardVersion version = VCardVersion.V4_0;
		List<String> warnings = new ArrayList<String>();
		CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
		String actual;
		VCardSubTypes subTypes;

		//text
		DeathplaceType t = new DeathplaceType();
		t.setText("Mount St. Helens");
		actual = t.marshalValue(version, warnings, compatibilityMode);
		subTypes = t.marshalSubTypes(version, warnings, compatibilityMode, new VCard());
		assertEquals("Mount St. Helens", actual);
		assertEquals(ValueParameter.TEXT, subTypes.getValue());

		//URI
		t = new DeathplaceType();
		t.setUri("geo:46.176502,-122.191658");
		actual = t.marshalValue(version, warnings, compatibilityMode);
		subTypes = t.marshalSubTypes(version, warnings, compatibilityMode, new VCard());
		assertEquals("geo:46.176502\\,-122.191658", actual);
		assertEquals(ValueParameter.URI, subTypes.getValue());
	}

	@Test
	public void marshalXml() throws Exception {
		VCardVersion version = VCardVersion.V4_0;
		List<String> warnings = new ArrayList<String>();
		CompatibilityMode compatibilityMode = CompatibilityMode.RFC;

		//text
		DeathplaceType t = new DeathplaceType();
		t.setText("Mount St. Helens");
		String expectedXml = "<deathplace xmlns=\"" + VCardVersion.V4_0.getXmlNamespace() + "\">";
		expectedXml += "<text>Mount St. Helens</text>";
		expectedXml += "</deathplace>";
		Document expectedDoc = XmlUtils.toDocument(expectedXml);
		Document actualDoc = XmlUtils.toDocument("<deathplace xmlns=\"" + VCardVersion.V4_0.getXmlNamespace() + "\" />");
		Element element = XmlUtils.getRootElement(actualDoc);
		t.marshalValue(element, version, warnings, compatibilityMode);
		assertXMLEqual(expectedDoc, actualDoc);

		//URI
		t = new DeathplaceType();
		t.setUri("geo:46.176502,-122.191658");
		expectedXml = "<deathplace xmlns=\"" + VCardVersion.V4_0.getXmlNamespace() + "\">";
		expectedXml += "<uri>geo:46.176502,-122.191658</uri>";
		expectedXml += "</deathplace>";
		expectedDoc = XmlUtils.toDocument(expectedXml);
		actualDoc = XmlUtils.toDocument("<deathplace xmlns=\"" + VCardVersion.V4_0.getXmlNamespace() + "\" />");
		element = XmlUtils.getRootElement(actualDoc);
		t.marshalValue(element, version, warnings, compatibilityMode);
		assertXMLEqual(expectedDoc, actualDoc);
	}

	@Test
	public void unmarshal() throws Exception {
		VCardVersion version = VCardVersion.V4_0;
		List<String> warnings = new ArrayList<String>();
		CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
		VCardSubTypes subTypes = new VCardSubTypes();

		//text
		DeathplaceType t = new DeathplaceType();
		t.unmarshalValue(subTypes, "Mount St. Helens", version, warnings, compatibilityMode);
		assertEquals("Mount St. Helens", t.getText());
		assertNull(t.getUri());

		//URI
		t = new DeathplaceType();
		subTypes.setValue(ValueParameter.URI);
		t.unmarshalValue(subTypes, "geo:46.176502\\,-122.191658", version, warnings, compatibilityMode);
		assertNull(t.getText());
		assertEquals("geo:46.176502,-122.191658", t.getUri());
	}

	@Test
	public void unmarshalXml() throws Exception {
		VCardVersion version = VCardVersion.V4_0;
		List<String> warnings = new ArrayList<String>();
		CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
		VCardSubTypes subTypes = new VCardSubTypes();

		//text
		DeathplaceType t = new DeathplaceType();
		String xml = "<deathplace xmlns=\"" + VCardVersion.V4_0.getXmlNamespace() + "\">";
		xml += "<text>Mount St. Helens</text>";
		xml += "</deathplace>";
		Element element = XmlUtils.getRootElement(XmlUtils.toDocument(xml));
		t.unmarshalValue(subTypes, element, version, warnings, compatibilityMode);
		assertEquals("Mount St. Helens", t.getText());
		assertNull(t.getUri());

		//URI
		t = new DeathplaceType();
		xml = "<deathplace xmlns=\"" + VCardVersion.V4_0.getXmlNamespace() + "\">";
		xml += "<uri>geo:46.176502,-122.191658</uri>";
		xml += "</deathplace>";
		element = XmlUtils.getRootElement(XmlUtils.toDocument(xml));
		t.unmarshalValue(subTypes, element, version, warnings, compatibilityMode);
		assertNull(t.getText());
		assertEquals("geo:46.176502,-122.191658", t.getUri());
	}
}