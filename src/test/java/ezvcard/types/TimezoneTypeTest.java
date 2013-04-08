package ezvcard.types;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import ezvcard.VCard;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;
import ezvcard.io.SkipMeException;
import ezvcard.parameters.ValueParameter;
import ezvcard.util.HtmlUtils;
import ezvcard.util.JCardDataType;
import ezvcard.util.JCardValue;
import ezvcard.util.XCardElement;

/*
 Copyright (c) 2013, Michael Angstadt
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
public class TimezoneTypeTest {
	final List<String> warnings = new ArrayList<String>();
	final CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
	final VCardSubTypes subTypes = new VCardSubTypes();
	final VCard vcard = new VCard();

	final Integer hourOffset = -5;
	final Integer minuteOffset = 30;
	final String offsetStr = "-05:30";
	final String textStr = "America/New_York";
	final String offsetWithText = offsetStr + ";EDT;" + textStr;
	final TimezoneType offset = new TimezoneType(hourOffset, minuteOffset);
	final TimezoneType text = new TimezoneType(textStr);
	final TimezoneType offsetAndText = new TimezoneType(hourOffset, minuteOffset, textStr);
	final TimezoneType empty = new TimezoneType();

	@Before
	public void before() {
		subTypes.clear();
		warnings.clear();
	}

	@Test(expected = IllegalArgumentException.class)
	public void setMinuteOffset_less_than_0() {
		new TimezoneType().setMinuteOffset(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setMinuteOffset_greater_than_59() {
		new TimezoneType().setMinuteOffset(60);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_minuteOffset_less_than_0() {
		new TimezoneType(0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_minuteOffset_greater_than_59() {
		new TimezoneType(0, 60);
	}

	@Test
	public void toTimeZone() {
		TimezoneType t = new TimezoneType(-5, 30);
		TimeZone tz = t.toTimeZone();
		assertEquals(-(5 * 1000 * 60 * 60 + 30 * 1000 * 60), tz.getRawOffset());
	}

	@Test
	public void toTimeZone_text() {
		TimezoneType t = new TimezoneType("America/New_York");
		TimeZone tz = t.toTimeZone();
		assertNull(tz);
	}

	//TODO check subtypes size, check warnings size

	public void marshalSubTypes_offset_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = offset.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(0, subTypes.size());
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_offset_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = offset.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(0, subTypes.size());
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_offset_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = offset.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertEquals(ValueParameter.UTC_OFFSET, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = text.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertEquals(ValueParameter.TEXT, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = text.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertEquals(ValueParameter.TEXT, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = text.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertEquals(ValueParameter.TEXT, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_offset_and_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = offsetAndText.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertEquals(ValueParameter.TEXT, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_offset_and_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = offsetAndText.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertEquals(ValueParameter.TEXT, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_offset_and_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = offsetAndText.marshalSubTypes(version, warnings, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertEquals(ValueParameter.TEXT, subTypes.getValue());
	}

	@Test
	public void marshalText_offset_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = offset.marshalText(version, warnings, compatibilityMode);

		assertEquals(offsetStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_offset_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = offset.marshalText(version, warnings, compatibilityMode);

		assertEquals(offsetStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_offset_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = offset.marshalText(version, warnings, compatibilityMode);

		assertEquals(offsetStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = text.marshalText(version, warnings, compatibilityMode);

		assertEquals(textStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = text.marshalText(version, warnings, compatibilityMode);

		assertEquals(textStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = text.marshalText(version, warnings, compatibilityMode);

		assertEquals(textStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_offset_and_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = offsetAndText.marshalText(version, warnings, compatibilityMode);

		assertEquals(offsetStr + ";" + textStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_offset_and_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = offsetAndText.marshalText(version, warnings, compatibilityMode);

		assertEquals(offsetStr + ";" + textStr, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_offset_and_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = offsetAndText.marshalText(version, warnings, compatibilityMode);

		assertEquals(textStr, actual); //4.0 just outputs the text
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void marshalText_no_offset_or_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		empty.marshalText(version, warnings, compatibilityMode);
	}

	@Test(expected = SkipMeException.class)
	public void marshalText_no_offset_or_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		empty.marshalText(version, warnings, compatibilityMode);
	}

	@Test(expected = SkipMeException.class)
	public void marshalText_no_offset_or_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		empty.marshalText(version, warnings, compatibilityMode);
	}

	/////////////////////////////////////////////////

	@Test
	public void marshalXml_offset() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append("utc-offset", offsetStr);
		Document expectedDoc = xe.document();
		xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		Document actualDoc = xe.document();
		offset.marshalXml(xe.element(), version, warnings, compatibilityMode);

		assertXMLEqual(expectedDoc, actualDoc);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalXml_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.text(textStr);
		Document expectedDoc = xe.document();
		xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		Document actualDoc = xe.document();
		text.marshalXml(xe.element(), version, warnings, compatibilityMode);

		assertXMLEqual(expectedDoc, actualDoc);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalXml_offset_and_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.text(textStr);
		Document expectedDoc = xe.document();
		xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		Document actualDoc = xe.document();
		offsetAndText.marshalXml(xe.element(), version, warnings, compatibilityMode);

		assertXMLEqual(expectedDoc, actualDoc);
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void marshalXml_no_offset_or_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		empty.marshalXml(xe.element(), version, warnings, compatibilityMode);
	}

	/////////////////////////////////////////////////

	@Test
	public void marshalJson_offset() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = offset.marshalJson(version, warnings);
		assertEquals(JCardDataType.UTC_OFFSET, value.getDataType());
		assertFalse(value.isStructured());

		//@formatter:off
		@SuppressWarnings("unchecked")
		List<List<Object>> expectedValues = Arrays.asList(
			Arrays.asList(new Object[]{ offsetStr })
		);
		//@formatter:on
		assertEquals(expectedValues, value.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = text.marshalJson(version, warnings);
		assertEquals(JCardDataType.TEXT, value.getDataType());
		assertFalse(value.isStructured());

		//@formatter:off
		@SuppressWarnings("unchecked")
		List<List<Object>> expectedValues = Arrays.asList(
			Arrays.asList(new Object[]{ textStr })
		);
		//@formatter:on
		assertEquals(expectedValues, value.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalJson_offset_and_text() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = offsetAndText.marshalJson(version, warnings);
		assertEquals(JCardDataType.TEXT, value.getDataType());
		assertFalse(value.isStructured());

		//@formatter:off
		@SuppressWarnings("unchecked")
		List<List<Object>> expectedValues = Arrays.asList(
			Arrays.asList(new Object[]{ textStr })
		);
		//@formatter:on
		assertEquals(expectedValues, value.getValues());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void marshalJson_no_offset_or_text() {
		VCardVersion version = VCardVersion.V4_0;
		empty.marshalJson(version, warnings);
	}

	/////////////////////////////////////////////////

	@Test
	public void unmarshalText_2_1_offset() {
		VCardVersion version = VCardVersion.V2_1;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, offsetStr, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void unmarshalText_2_1_invalid_offset() {
		VCardVersion version = VCardVersion.V2_1;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_2_1_offset_and_text() {
		VCardVersion version = VCardVersion.V2_1;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, offsetWithText, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertEquals(";EDT;" + textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_3_0_offset() {
		VCardVersion version = VCardVersion.V3_0;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, offsetStr, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void unmarshalText_3_0_invalid_offset() {
		VCardVersion version = VCardVersion.V3_0;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_3_0_offset_and_text() {
		VCardVersion version = VCardVersion.V3_0;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, offsetWithText, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertEquals(";EDT;" + textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_4_0_offset__no_value() {
		VCardVersion version = VCardVersion.V4_0;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, offsetStr, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_4_0_invalid_offset__no_value() {
		VCardVersion version = VCardVersion.V4_0;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_4_0_offset__utc_offset_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(ValueParameter.UTC_OFFSET);
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, offsetStr, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void unmarshalText_4_0_invalid_offset__utc_offset_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(ValueParameter.UTC_OFFSET);
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_4_0_offset__text_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(ValueParameter.TEXT);
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, offsetStr, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_4_0_invalid_offset__text_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(ValueParameter.TEXT);
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_4_0_text__no_value() {
		VCardVersion version = VCardVersion.V4_0;
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, textStr, version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void unmarshalText_4_0_text__utc_offset_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(ValueParameter.UTC_OFFSET);
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, textStr, version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_4_0_text__text_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(ValueParameter.TEXT);
		TimezoneType t = new TimezoneType();
		t.unmarshalText(subTypes, textStr, version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	/////////////////////////////////////////////////

	@Test(expected = SkipMeException.class)
	public void unmarshalXml_empty() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		TimezoneType t = new TimezoneType();
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalXml_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.text(textStr);
		TimezoneType t = new TimezoneType();
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalXml_offset() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append("utc-offset", offsetStr);
		TimezoneType t = new TimezoneType();
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void unmarshalXml_invalid_offset() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append("utc-offset", "invalid");
		TimezoneType t = new TimezoneType();
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalXml_offset_and_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.text(textStr);
		xe.append("utc-offset", offsetStr);
		TimezoneType t = new TimezoneType();
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalXml_invalid_offset_and_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.text(textStr);
		xe.append("utc-offset", "invalid");
		TimezoneType t = new TimezoneType();
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertEquals(1, warnings.size());
	}

	/////////////////////////////////////////////////

	@Test
	public void unmarshalHtml_offset() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<div>" + offsetStr + "</div>");

		TimezoneType t = new TimezoneType();
		t.unmarshalHtml(element, warnings);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void unmarshalHtml_invalid_offset() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<div>invalid</div>");

		TimezoneType t = new TimezoneType();
		t.unmarshalHtml(element, warnings);
	}

	@Test
	public void unmarshalHtml_offset_and_text() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<div>" + offsetStr + ";EDT;" + textStr + "</div>");

		TimezoneType t = new TimezoneType();
		t.unmarshalHtml(element, warnings);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertEquals(";EDT;" + textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	/////////////////////////////////////////////////

	@Test
	public void unmarshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.text(textStr);

		TimezoneType t = new TimezoneType();
		t.unmarshalJson(subTypes, value, version, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalJson_text_other_data_type() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = new JCardValue();
		value.setDataType(JCardDataType.BOOLEAN);
		value.addValues(textStr);

		TimezoneType t = new TimezoneType();
		t.unmarshalJson(subTypes, value, version, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalJson_offset() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.utcOffset(hourOffset, minuteOffset);

		TimezoneType t = new TimezoneType();
		t.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test(expected = SkipMeException.class)
	public void unmarshalJson_invalid_offset() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.utcOffset();
		value.addValues("invalid");

		TimezoneType t = new TimezoneType();
		t.unmarshalJson(subTypes, value, version, warnings);
	}

	@Test
	public void unmarshalJson_offset_other_data_type() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = new JCardValue();
		value.setDataType(JCardDataType.BOOLEAN);
		value.addValues(offsetStr);

		TimezoneType t = new TimezoneType();
		t.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalJson_invalid_offset_other_data_type() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = new JCardValue();
		value.setDataType(JCardDataType.BOOLEAN);
		value.addValues("invalid");

		TimezoneType t = new TimezoneType();
		t.unmarshalJson(subTypes, value, version, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertEquals(0, warnings.size());
	}
}
