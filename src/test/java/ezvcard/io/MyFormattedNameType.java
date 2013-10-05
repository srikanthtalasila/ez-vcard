package ezvcard.io;

import java.util.List;

import ezvcard.VCardDataType;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.json.JCardValue;
import ezvcard.io.xml.XCardElement;
import ezvcard.types.VCardType;
import ezvcard.types.scribes.VCardPropertyScribe;
import ezvcard.util.HCardElement;

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
public class MyFormattedNameType extends VCardType {
	public String value;

	public MyFormattedNameType(String value) {
		this.value = value;
	}

	public static class MyFormattedNameScribe extends VCardPropertyScribe<MyFormattedNameType> {
		public MyFormattedNameScribe() {
			super(MyFormattedNameType.class, "FN");
		}

		@Override
		protected VCardDataType _defaultDataType(VCardVersion version) {
			return VCardDataType.get("name");
		}

		@Override
		protected String _writeText(MyFormattedNameType property, VCardVersion version) {
			return property.value.toUpperCase();
		}

		@Override
		protected MyFormattedNameType _parseText(String value, VCardDataType dataType, VCardVersion version, VCardSubTypes parameters, List<String> warnings) {
			return new MyFormattedNameType(value.toUpperCase());
		}

		@Override
		protected void _writeXml(MyFormattedNameType property, XCardElement parent) {
			parent.append("name", property.value);
		}

		@Override
		protected MyFormattedNameType _parseXml(XCardElement element, VCardSubTypes parameters, List<String> warnings) {
			return new MyFormattedNameType(element.first("name").toUpperCase());
		}

		@Override
		protected MyFormattedNameType _parseHtml(HCardElement element, List<String> warnings) {
			return new MyFormattedNameType(element.value().toUpperCase());
		}

		@Override
		protected JCardValue _writeJson(MyFormattedNameType property) {
			return JCardValue.single(property.value.toUpperCase());
		}

		@Override
		protected MyFormattedNameType _parseJson(JCardValue value, VCardDataType dataType, VCardSubTypes parameters, List<String> warnings) {
			return new MyFormattedNameType(value.asSingle().toUpperCase());
		}
	}
}
