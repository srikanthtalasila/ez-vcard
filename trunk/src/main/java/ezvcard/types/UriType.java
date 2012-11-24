package ezvcard.types;

import java.util.List;

import org.w3c.dom.Element;

import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;
import ezvcard.util.HCardUtils;
import ezvcard.util.XCardUtils;

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
 * Represents a type whose value is a URI.
 * @author Michael Angstadt
 */
public class UriType extends TextType {
	/**
	 * @param name the type name (e.g. "URL")
	 */
	public UriType(String name) {
		super(name);
	}

	/**
	 * @param name the type name (e.g. "URL")
	 * @param uri the type value
	 */
	public UriType(String name, String uri) {
		super(name, uri);
	}

	@Override
	protected void doUnmarshalValue(Element element, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		setValue(XCardUtils.getFirstChildText(element, "uri"));
	}

	@Override
	protected void doMarshalValue(Element parent, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		XCardUtils.appendChild(parent, "uri", getValue(), version);
	}

	@Override
	protected void doUnmarshalHtml(org.jsoup.nodes.Element element, List<String> warnings) {
		String href = HCardUtils.getAbsUrl(element, "href");
		if (href.length() > 0) {
			setValue(href);
		} else {
			super.doUnmarshalHtml(element, warnings);
		}
	}
}
