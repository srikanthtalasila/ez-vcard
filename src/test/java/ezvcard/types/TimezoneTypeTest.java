package ezvcard.types;

import static ezvcard.util.TestUtils.assertIntEquals;
import static ezvcard.util.TestUtils.assertJCardValue;
import static ezvcard.util.TestUtils.assertMarshalXml;
import static ezvcard.util.TestUtils.assertWarnings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import ezvcard.VCard;
import ezvcard.VCardDataType;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CannotParseException;
import ezvcard.io.CompatibilityMode;
import ezvcard.io.SkipMeException;
import ezvcard.util.HtmlUtils;
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
	private final List<String> warnings = new ArrayList<String>();
	private final CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
	private final VCardSubTypes subTypes = new VCardSubTypes();
	private final VCard vcard = new VCard();

	private final Integer hourOffset = -5;
	private final Integer minuteOffset = 30;
	private final String offsetStrExtended = "-05:30";
	private final String offsetStrBasic = "-0530";
	private final String textStr = "America/New_York";
	private final String offsetWithText = offsetStrExtended + ";EDT;" + textStr;
	private final TimezoneType offset = new TimezoneType(hourOffset, minuteOffset);
	private final TimezoneType text = new TimezoneType(textStr);
	private final TimezoneType offsetAndText = new TimezoneType(hourOffset, minuteOffset, textStr);
	private final TimezoneType empty = new TimezoneType();
	private TimezoneType t;

	@Before
	public void before() {
		t = new TimezoneType();
		subTypes.clear();
		warnings.clear();
	}

	@Test
	public void validate() {
		assertWarnings(2, empty.validate(VCardVersion.V2_1, vcard));
		assertWarnings(1, empty.validate(VCardVersion.V3_0, vcard));
		assertWarnings(1, empty.validate(VCardVersion.V4_0, vcard));

		assertWarnings(0, offset.validate(VCardVersion.V2_1, vcard));
		assertWarnings(0, offset.validate(VCardVersion.V3_0, vcard));
		assertWarnings(0, offset.validate(VCardVersion.V4_0, vcard));

		assertWarnings(1, text.validate(VCardVersion.V2_1, vcard));
		assertWarnings(0, text.validate(VCardVersion.V3_0, vcard));
		assertWarnings(0, text.validate(VCardVersion.V4_0, vcard));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setOffset_minute_less_than_0() {
		new TimezoneType().setOffset(0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setOffset_minute_greater_than_59() {
		new TimezoneType().setOffset(0, 60);
	}

	@Test
	public void setOffset_null_hour() {
		t.setOffset(null, 30);
		assertIntEquals(0, t.getHourOffset());
		assertIntEquals(30, t.getMinuteOffset());
	}

	@Test
	public void setOffset_null_minute() {
		t.setOffset(-5, null);
		assertEquals(Integer.valueOf(-5), t.getHourOffset());
		assertIntEquals(0, t.getMinuteOffset());
	}

	@Test
	public void setOffset_null_hour_and_minute() {
		t.setOffset(null, null);
		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
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

	public void marshalSubTypes_offset_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = offset.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_offset_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = offset.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_offset_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = offset.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(1, subTypes.size());
		assertEquals(VCardDataType.UTC_OFFSET, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = text.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = text.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(1, subTypes.size());
		assertEquals(VCardDataType.TEXT, subTypes.getValue());
	}

	@Test
	public void marshalSubTypes_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = text.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_offset_and_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = offsetAndText.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_offset_and_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = offsetAndText.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_offset_and_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = offsetAndText.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalText_offset_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = offset.marshalText(version, compatibilityMode);

		assertEquals(offsetStrBasic, actual);
	}

	@Test
	public void marshalText_offset_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = offset.marshalText(version, compatibilityMode);

		assertEquals(offsetStrExtended, actual);
	}

	@Test
	public void marshalText_offset_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = offset.marshalText(version, compatibilityMode);

		assertEquals(offsetStrBasic, actual);
	}

	@Test(expected = SkipMeException.class)
	public void marshalText_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		text.marshalText(version, compatibilityMode);
	}

	@Test
	public void marshalText_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = text.marshalText(version, compatibilityMode);

		assertEquals(textStr, actual);
	}

	@Test
	public void marshalText_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = text.marshalText(version, compatibilityMode);

		assertEquals(textStr, actual);
	}

	@Test
	public void marshalText_offset_and_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = offsetAndText.marshalText(version, compatibilityMode);

		assertEquals(offsetStrBasic, actual);
	}

	@Test
	public void marshalText_offset_and_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = offsetAndText.marshalText(version, compatibilityMode);

		assertEquals(offsetStrExtended, actual);
	}

	@Test
	public void marshalText_offset_and_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = offsetAndText.marshalText(version, compatibilityMode);

		assertEquals(textStr, actual);
	}

	@Test(expected = SkipMeException.class)
	public void marshalText_no_offset_or_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		empty.marshalText(version, compatibilityMode);
	}

	@Test(expected = SkipMeException.class)
	public void marshalText_no_offset_or_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		empty.marshalText(version, compatibilityMode);
	}

	@Test(expected = SkipMeException.class)
	public void marshalText_no_offset_or_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		empty.marshalText(version, compatibilityMode);
	}

	/////////////////////////////////////////////////

	@Test
	public void marshalXml_offset() {
		assertMarshalXml(offset, "<utc-offset>" + offsetStrBasic + "</utc-offset>");
	}

	@Test
	public void marshalXml_text() {
		assertMarshalXml(text, "<text>" + textStr + "</text>");
	}

	@Test
	public void marshalXml_offset_and_text() {
		assertMarshalXml(offsetAndText, "<text>" + textStr + "</text>");
	}

	@Test(expected = SkipMeException.class)
	public void marshalXml_no_offset_or_text() {
		assertMarshalXml(empty, "");
	}

	/////////////////////////////////////////////////

	@Test
	public void marshalJson_offset() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = offset.marshalJson(version);

		assertJCardValue(VCardDataType.UTC_OFFSET, offsetStrExtended, value);
	}

	@Test
	public void marshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = text.marshalJson(version);

		assertJCardValue(VCardDataType.TEXT, textStr, value);
	}

	@Test
	public void marshalJson_offset_and_text() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = offsetAndText.marshalJson(version);

		assertJCardValue(VCardDataType.TEXT, textStr, value);
	}

	@Test(expected = SkipMeException.class)
	public void marshalJson_no_offset_or_text() {
		VCardVersion version = VCardVersion.V4_0;
		empty.marshalJson(version);
	}

	/////////////////////////////////////////////////

	@Test
	public void unmarshalText_2_1_offset() {
		VCardVersion version = VCardVersion.V2_1;
		t.unmarshalText(subTypes, offsetStrExtended, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalText_2_1_invalid_offset() {
		VCardVersion version = VCardVersion.V2_1;
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalText_2_1_text() {
		VCardVersion version = VCardVersion.V2_1;
		subTypes.setValue(VCardDataType.TEXT);
		t.unmarshalText(subTypes, offsetWithText, version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_3_0_offset() {
		VCardVersion version = VCardVersion.V3_0;
		t.unmarshalText(subTypes, offsetStrExtended, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_3_0_invalid_offset() {
		VCardVersion version = VCardVersion.V3_0;
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertWarnings(1, warnings);
	}

	@Test
	public void unmarshalText_3_0_text() {
		VCardVersion version = VCardVersion.V3_0;
		subTypes.setValue(VCardDataType.TEXT);
		t.unmarshalText(subTypes, offsetWithText, version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(offsetWithText, t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_offset__no_value() {
		VCardVersion version = VCardVersion.V4_0;
		t.unmarshalText(subTypes, offsetStrExtended, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_invalid_offset__no_value() {
		VCardVersion version = VCardVersion.V4_0;
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_offset__utc_offset_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.UTC_OFFSET);
		t.unmarshalText(subTypes, offsetStrExtended, version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalText_4_0_invalid_offset__utc_offset_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.UTC_OFFSET);
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_4_0_offset__text_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.TEXT);
		t.unmarshalText(subTypes, offsetStrExtended, version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(offsetStrExtended, t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_invalid_offset__text_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.TEXT);
		t.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_text__no_value() {
		VCardVersion version = VCardVersion.V4_0;
		t.unmarshalText(subTypes, textStr, version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertWarnings(0, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalText_4_0_text__utc_offset_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.UTC_OFFSET);
		t.unmarshalText(subTypes, textStr, version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_4_0_text__text_value() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.TEXT);
		t.unmarshalText(subTypes, textStr, version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertWarnings(0, warnings);
	}

	/////////////////////////////////////////////////

	@Test(expected = CannotParseException.class)
	public void unmarshalXml_empty() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalXml_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append(VCardDataType.TEXT, textStr);
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_offset() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append(VCardDataType.UTC_OFFSET, offsetStrExtended);
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalXml_invalid_offset() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append(VCardDataType.UTC_OFFSET, "invalid");
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalXml_offset_and_text() {
		//text is preferred
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append(VCardDataType.TEXT, textStr);
		xe.append(VCardDataType.UTC_OFFSET, offsetStrExtended);
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_invalid_offset_and_text() {
		//text is preferred
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TimezoneType.NAME.toLowerCase());
		xe.append(VCardDataType.TEXT, textStr);
		xe.append(VCardDataType.UTC_OFFSET, "invalid");
		t.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertWarnings(0, warnings);
	}

	/////////////////////////////////////////////////

	@Test
	public void unmarshalHtml_offset() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<div>" + offsetStrExtended + "</div>");

		t.unmarshalHtml(element, warnings);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalHtml_invalid_offset() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<div>invalid</div>");

		t.unmarshalHtml(element, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertWarnings(1, warnings);
	}

	@Test
	public void unmarshalHtml_text() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<div>" + offsetStrExtended + ";EDT;" + textStr + "</div>");

		t.unmarshalHtml(element, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(offsetStrExtended + ";EDT;" + textStr, t.getText());
		assertWarnings(1, warnings);
	}

	/////////////////////////////////////////////////

	@Test
	public void unmarshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.TEXT, textStr);

		t.unmarshalJson(subTypes, value, version, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_text_other_data_type() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.BOOLEAN, textStr);

		t.unmarshalJson(subTypes, value, version, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals(textStr, t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_offset() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.UTC_OFFSET, offsetStrExtended);

		t.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalJson_invalid_offset() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.UTC_OFFSET, "invalid");

		t.unmarshalJson(subTypes, value, version, warnings);
	}

	@Test
	public void unmarshalJson_offset_other_data_type() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.BOOLEAN, offsetStrExtended);

		t.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(hourOffset, t.getHourOffset());
		assertEquals(minuteOffset, t.getMinuteOffset());
		assertNull(t.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_invalid_offset_other_data_type() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.BOOLEAN, "invalid");

		t.unmarshalJson(subTypes, value, version, warnings);

		assertNull(t.getHourOffset());
		assertNull(t.getMinuteOffset());
		assertEquals("invalid", t.getText());
		assertWarnings(0, warnings);
	}
}
