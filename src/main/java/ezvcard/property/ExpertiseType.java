package ezvcard.property;

import java.util.EnumSet;
import java.util.Set;

import ezvcard.VCardVersion;
import ezvcard.parameter.ExpertiseLevelParameter;
import ezvcard.parameter.VCardSubTypes;

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
 * Defines a professional subject area that the person has knowledge of. For
 * example, if the person is a Java software engineer, he or she might list
 * technologies such as "servlets", "SOAP", and "Spring".
 * 
 * <p>
 * <b>Code sample</b>
 * </p>
 * 
 * <pre class="brush:java">
 * VCard vcard = new VCard();
 * ExpertiseType expertise = new ExpertiseType(&quot;Java programming&quot;);
 * expertise.setLevel(ExpertiseLevelParameter.EXPERT);
 * vcard.addExpertise(expertise);
 * </pre>
 * 
 * <p>
 * <b>Property name:</b> {@code EXPERTISE}
 * </p>
 * <p>
 * <b>Supported versions:</b> {@code 4.0}
 * </p>
 * @author Michael Angstadt
 * @see <a href="http://tools.ietf.org/html/rfc6715">RFC 6715</a>
 */
public class ExpertiseType extends TextType implements HasAltId {
	/**
	 * Creates an expertise property.
	 * @param skill the skill (e.g. "Java programming")
	 */
	public ExpertiseType(String skill) {
		super(skill);
	}

	@Override
	public Set<VCardVersion> _supportedVersions() {
		return EnumSet.of(VCardVersion.V4_0);
	}

	/**
	 * Gets the level of knowledge the person has for this skill.
	 * @return the skill level (e.g. "beginner") or null if not set
	 * @see VCardSubTypes#getLevel
	 */
	public ExpertiseLevelParameter getLevel() {
		String value = subTypes.getLevel();
		return (value == null) ? null : ExpertiseLevelParameter.get(value);
	}

	/**
	 * Sets the level of knowledge the person has for this skill.
	 * @param level the skill level (e.g. "beginner") or null to remove
	 * @see VCardSubTypes#setLevel
	 */
	public void setLevel(ExpertiseLevelParameter level) {
		subTypes.setLevel(level.getValue());
	}

	@Override
	public Integer getIndex() {
		return super.getIndex();
	}

	@Override
	public void setIndex(Integer index) {
		super.setIndex(index);
	}

	/**
	 * Gets the TYPE parameter.
	 * @return the TYPE value (typically, this will be either "work" or "home")
	 * or null if it doesn't exist
	 */
	public String getType() {
		return subTypes.getType();
	}

	/**
	 * Sets the TYPE parameter.
	 * @param type the TYPE value (this should be either "work" or "home") or
	 * null to remove
	 */
	public void setType(String type) {
		subTypes.setType(type);
	}

	@Override
	public String getLanguage() {
		return super.getLanguage();
	}

	@Override
	public void setLanguage(String language) {
		super.setLanguage(language);
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
}
