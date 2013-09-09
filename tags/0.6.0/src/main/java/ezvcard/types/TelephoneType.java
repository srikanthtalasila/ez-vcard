package ezvcard.types;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import ezvcard.VCard;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;
import ezvcard.parameters.TelephoneTypeParameter;
import ezvcard.parameters.ValueParameter;
import ezvcard.util.HCardUtils;
import ezvcard.util.VCardStringUtils;
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
 * A telephone number.
 * 
 * <pre>
 * VCard vcard = new VCard();
 * TelephoneType tel = new TelephoneType(&quot;+1 123-555-6789&quot;);
 * tel.addType(TelephoneTypeParameter.HOME);
 * tel.setPref(2); //the second-most preferred
 * vcard.addTelephoneNumber(tel);
 * tel = new TelephoneType(&quot;+1 800-555-9876;ext=111&quot;);
 * tel.addType(TelephoneTypeParameter.WORK);
 * tel.setPref(1); //the most preferred
 * vcard.addTelephoneNumber(tel);
 * </pre>
 * 
 * <p>
 * vCard property name: TEL
 * </p>
 * <p>
 * vCard versions: 2.1, 3.0, 4.0
 * </p>
 * @author Michael Angstadt
 */
public class TelephoneType extends MultiValuedTypeParameterType<TelephoneTypeParameter> {
	public static final String NAME = "TEL";

	private String value;

	public TelephoneType() {
		this(null);
	}

	/**
	 * @param telNumber the telephone number
	 */
	public TelephoneType(String telNumber) {
		super(NAME);
		this.value = telNumber;
	}

	/**
	 * Gets the telephone number.
	 * @return the telephone number
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the telephone number.
	 * @param value the telephone number
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets all PID parameter values.
	 * <p>
	 * vCard versions: 4.0
	 * </p>
	 * @return the PID values or empty set if there are none
	 * @see VCardSubTypes#getPids
	 */
	public Set<Integer[]> getPids() {
		return subTypes.getPids();
	}

	/**
	 * Adds a PID value.
	 * <p>
	 * vCard versions: 4.0
	 * </p>
	 * @param localId the local ID
	 * @param clientPidMapRef the ID used to reference the property's globally
	 * unique identifier in the CLIENTPIDMAP property.
	 * @see VCardSubTypes#addPid(int, int)
	 */
	public void addPid(int localId, int clientPidMapRef) {
		subTypes.addPid(localId, clientPidMapRef);
	}

	/**
	 * Removes all PID values.
	 * <p>
	 * vCard versions: 4.0
	 * </p>
	 * @see VCardSubTypes#removePids
	 */
	public void removePids() {
		subTypes.removePids();
	}

	/**
	 * Gets the preference value.
	 * <p>
	 * vCard versions: 4.0
	 * </p>
	 * @return the preference value or null if it doesn't exist
	 * @see VCardSubTypes#getPref
	 */
	public Integer getPref() {
		return subTypes.getPref();
	}

	/**
	 * Sets the preference value.
	 * <p>
	 * vCard versions: 4.0
	 * </p>
	 * @param pref the preference value or null to remove
	 * @see VCardSubTypes#setPref
	 */
	public void setPref(Integer pref) {
		subTypes.setPref(pref);
	}

	/**
	 * Gets the ALTID.
	 * <p>
	 * vCard versions: 4.0
	 * </p>
	 * @return the ALTID or null if it doesn't exist
	 * @see VCardSubTypes#getAltId
	 */
	public String getAltId() {
		return subTypes.getAltId();
	}

	/**
	 * Sets the ALTID.
	 * <p>
	 * vCard versions: 4.0
	 * </p>
	 * @param altId the ALTID or null to remove
	 * @see VCardSubTypes#setAltId
	 */
	public void setAltId(String altId) {
		subTypes.setAltId(altId);
	}

	@Override
	protected TelephoneTypeParameter buildTypeObj(String type) {
		TelephoneTypeParameter param = TelephoneTypeParameter.valueOf(type);
		if (param == null) {
			param = new TelephoneTypeParameter(type);
		}
		return param;
	}

	@Override
	protected void doMarshalSubTypes(VCardSubTypes copy, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode, VCard vcard) {
		if (version == VCardVersion.V4_0) {
			copy.setValue(ValueParameter.URI);
		} else {
			copy.setValue(null);
		}

		//replace "TYPE=pref" with "PREF=1"
		if (version == VCardVersion.V4_0) {
			if (getTypes().contains(TelephoneTypeParameter.PREF)) {
				copy.removeType(TelephoneTypeParameter.PREF.getValue());
				copy.setPref(1);
			}
		} else {
			copy.setPref(null);

			//find the TEL with the lowest PREF value in the vCard
			TelephoneType mostPreferred = null;
			for (TelephoneType tel : vcard.getTelephoneNumbers()) {
				Integer pref = tel.getPref();
				if (pref != null) {
					if (mostPreferred == null || pref < mostPreferred.getPref()) {
						mostPreferred = tel;
					}
				}
			}
			if (this == mostPreferred) {
				copy.addType(TelephoneTypeParameter.PREF.getValue());
			}
		}
	}

	@Override
	protected void doMarshalValue(StringBuilder sb, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		String value = writeValue(version);
		sb.append(VCardStringUtils.escape(value));
	}

	@Override
	protected void doUnmarshalValue(String value, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		value = VCardStringUtils.unescape(value.trim());
		parseValue(value);
	}

	@Override
	protected void doMarshalValue(Element parent, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		String value = writeValue(version);
		XCardUtils.appendChild(parent, "uri", value, version);
	}

	@Override
	protected void doUnmarshalValue(Element element, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		String value = XCardUtils.getFirstChildText(element, "text", "uri");
		if (value != null) {
			parseValue(value);
		}
	}

	@Override
	protected void doUnmarshalHtml(org.jsoup.nodes.Element element, List<String> warnings) {
		List<String> types = HCardUtils.getTypes(element);
		for (String type : types) {
			subTypes.addType(type);
		}

		String tel = null;
		String href = element.attr("href");
		if (href.length() > 0) {
			Pattern p = Pattern.compile("^tel:(.*?)$", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(href);
			if (m.find()) {
				tel = m.group(1);
			}
		}
		if (tel == null) {
			tel = HCardUtils.getElementValue(element);
		}
		setValue(tel);
	}

	private void parseValue(String value) {
		if (value.matches("(?i)tel:.*")) {
			//remove "tel:"
			value = (value.length() > 4) ? value.substring(4) : "";
		}
		setValue(value);
	}

	private String writeValue(VCardVersion version) {
		String value = this.value;
		if (version == VCardVersion.V4_0) {
			value = "tel:" + value;
		}
		return value;
	}
}