package ezvcard.property;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.TelephoneTypeParameter;
import ezvcard.util.TelUri;

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
 * A telephone number.
 * 
 * <p>
 * <b>Code sample</b>
 * </p>
 * 
 * <pre class="brush:java">
 * VCard vcard = new VCard();
 * TelephoneType tel = new TelephoneType(&quot;(123) 555-6789&quot;);
 * tel.addType(TelephoneTypeParameter.HOME);
 * tel.setPref(2); //the second-most preferred
 * vcard.addTelephoneNumber(tel);
 * 
 * TelUri uri = new TelUri.Builder(&quot;+1-800-555-9876&quot;).extension(&quot;111&quot;).build();
 * tel = new TelephoneType(uri);
 * tel.addType(TelephoneTypeParameter.WORK);
 * tel.setPref(1); //the most preferred
 * vcard.addTelephoneNumber(tel);
 * </pre>
 * 
 * <p>
 * <b>Property name:</b> {@code TEL}
 * </p>
 * <p>
 * <b>Supported versions:</b> {@code 2.1, 3.0, 4.0}
 * </p>
 * @author Michael Angstadt
 */
public class TelephoneType extends VCardType implements HasAltId {
	private String text;
	private TelUri uri;

	/**
	 * Creates a telephone property.
	 * @param text the telephone number (e.g. "(123) 555-6789")
	 */
	public TelephoneType(String text) {
		setText(text);
	}

	/**
	 * Creates a telephone property.
	 * @param uri a "tel" URI representing the telephone number (vCard 4.0 only)
	 */
	public TelephoneType(TelUri uri) {
		setUri(uri);
	}

	/**
	 * Gets the telephone number as a text value.
	 * @return the telephone number or null if the text value is not set
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the telephone number as a text value.
	 * @param text the telephone number
	 */
	public void setText(String text) {
		this.text = text;
		uri = null;
	}

	/**
	 * Gets a "tel" URI representing the phone number.
	 * <p>
	 * <b>Supported versions:</b> {@code 4.0}
	 * </p>
	 * @return the "tel" URI or null if it is not set
	 */
	public TelUri getUri() {
		return uri;
	}

	/**
	 * Sets a "tel" URI representing the phone number.
	 * <p>
	 * <b>Supported versions:</b> {@code 4.0}
	 * </p>
	 * @param uri the "tel" URI
	 */
	public void setUri(TelUri uri) {
		text = null;
		this.uri = uri;
	}

	/**
	 * Gets all the TYPE parameters.
	 * @return the TYPE parameters or empty set if there are none
	 */
	public Set<TelephoneTypeParameter> getTypes() {
		Set<String> values = subTypes.getTypes();
		Set<TelephoneTypeParameter> types = new HashSet<TelephoneTypeParameter>(values.size());
		for (String value : values) {
			types.add(TelephoneTypeParameter.get(value));
		}
		return types;
	}

	/**
	 * Adds a TYPE parameter.
	 * @param type the TYPE parameter to add
	 */
	public void addType(TelephoneTypeParameter type) {
		subTypes.addType(type.getValue());
	}

	/**
	 * Removes a TYPE parameter.
	 * @param type the TYPE parameter to remove
	 */
	public void removeType(TelephoneTypeParameter type) {
		subTypes.removeType(type.getValue());
	}

	@Override
	public List<Integer[]> getPids() {
		return super.getPids();
	}

	@Override
	public void addPid(int localId, int clientPidMapRef) {
		super.addPid(localId, clientPidMapRef);
	}

	@Override
	public void removePids() {
		super.removePids();
	}

	@Override
	public Integer getPref() {
		return super.getPref();
	}

	@Override
	public void setPref(Integer pref) {
		super.setPref(pref);
	}

	//@Override
	public String getAltId() {
		return subTypes.getAltId();
	}

	//@Override
	public void setAltId(String altId) {
		subTypes.setAltId(altId);
	}

	@Override
	protected void _validate(List<String> warnings, VCardVersion version, VCard vcard) {
		if (uri == null && text == null) {
			warnings.add("Property has neither a URI nor a text value associated with it.");
		}

		if (uri != null && (version == VCardVersion.V2_1 || version == VCardVersion.V3_0)) {
			warnings.add("\"tel\" URIs are not supported by vCard version " + version.getVersion() + ".  The URI will be converted to a string.  Some data may be lost.");
		}

		for (TelephoneTypeParameter type : getTypes()) {
			if (type == TelephoneTypeParameter.PREF) {
				//ignore because it is converted to a PREF parameter for 4.0 vCards
				continue;
			}

			if (!type.isSupported(version)) {
				warnings.add("Type value \"" + type.getValue() + "\" is not supported in version " + version.getVersion() + ".");
			}
		}
	}
}