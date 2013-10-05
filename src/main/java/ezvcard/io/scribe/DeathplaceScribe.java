package ezvcard.io.scribe;

import java.util.List;

import ezvcard.VCardDataType;
import ezvcard.VCardVersion;
import ezvcard.io.json.JCardValue;
import ezvcard.io.xml.XCardElement;
import ezvcard.parameter.VCardParameters;
import ezvcard.property.Deathplace;

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
 */

/**
 * Marshals {@link Deathplace} properties.
 * @author Michael Angstadt
 */
public class DeathplaceScribe extends VCardPropertyScribe<Deathplace> {
	public DeathplaceScribe() {
		super(Deathplace.class, "DEATHPLACE");
	}

	@Override
	protected VCardDataType _defaultDataType(VCardVersion version) {
		return VCardDataType.TEXT;
	}

	@Override
	protected VCardDataType _dataType(Deathplace property, VCardVersion version) {
		if (property.getText() != null) {
			return VCardDataType.TEXT;
		}
		if (property.getUri() != null) {
			return VCardDataType.URI;
		}
		return _defaultDataType(version);
	}

	@Override
	protected String _writeText(Deathplace property, VCardVersion version) {
		String text = property.getText();
		if (text != null) {
			return escape(text);
		}

		String uri = property.getUri();
		if (uri != null) {
			return uri;
		}

		return "";
	}

	@Override
	protected Deathplace _parseText(String value, VCardDataType dataType, VCardVersion version, VCardParameters parameters, List<String> warnings) {
		Deathplace property = new Deathplace();
		value = unescape(value);

		if (dataType == VCardDataType.TEXT) {
			property.setText(value);
			return property;
		}

		if (dataType == VCardDataType.URI) {
			property.setUri(value);
			return property;
		}

		property.setText(value);
		return property;
	}

	@Override
	protected void _writeXml(Deathplace property, XCardElement parent) {
		String text = property.getText();
		if (text != null) {
			parent.append(VCardDataType.TEXT, text);
			return;
		}

		String uri = property.getUri();
		if (uri != null) {
			parent.append(VCardDataType.URI, uri);
			return;
		}

		parent.append(VCardDataType.TEXT, "");
	}

	@Override
	protected Deathplace _parseXml(XCardElement element, VCardParameters parameters, List<String> warnings) {
		Deathplace property = new Deathplace();

		String text = element.first(VCardDataType.TEXT);
		if (text != null) {
			property.setText(text);
			return property;
		}

		String uri = element.first(VCardDataType.URI);
		if (uri != null) {
			property.setUri(uri);
			return property;
		}

		throw missingXmlElements(VCardDataType.TEXT, VCardDataType.URI);
	}

	@Override
	protected JCardValue _writeJson(Deathplace property) {
		String text = property.getText();
		if (text != null) {
			return JCardValue.single(text);
		}

		String uri = property.getUri();
		if (uri != null) {
			return JCardValue.single(uri);
		}

		return JCardValue.single("");
	}

	@Override
	protected Deathplace _parseJson(JCardValue value, VCardDataType dataType, VCardParameters parameters, List<String> warnings) {
		Deathplace property = new Deathplace();
		String valueStr = value.asSingle();

		if (dataType == VCardDataType.TEXT) {
			property.setText(valueStr);
			return property;
		}
		if (dataType == VCardDataType.URI) {
			property.setUri(valueStr);
			return property;
		}

		property.setText(valueStr);
		return property;
	}
}
