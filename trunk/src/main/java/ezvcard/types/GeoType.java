package ezvcard.types;

import java.util.List;

import ezvcard.VCard;
import ezvcard.VCardDataType;
import ezvcard.VCardVersion;
import ezvcard.io.CannotParseException;
import ezvcard.io.CompatibilityMode;
import ezvcard.util.GeoUri;
import ezvcard.util.HCardElement;
import ezvcard.util.JCardValue;
import ezvcard.util.VCardFloatFormatter;
import ezvcard.util.VCardStringUtils;
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
 * <p>
 * A set of latitude/longitude coordinates. There is no rule for what these
 * coordinates must represent, but the meaning could vary depending on the value
 * of {@link KindType}:
 * </p>
 * 
 * <table border="1">
 * <tr>
 * <th>KIND value</th>
 * <th>GEO meaning</th>
 * </tr>
 * <tr>
 * <td>"individual"</td>
 * <td>the location of the person's home or workplace.</td>
 * </tr>
 * <tr>
 * <td>"group"</td>
 * <td>the location of the group's meeting place.</td>
 * </tr>
 * <tr>
 * <td>"org"</td>
 * <td>the coordinates of the organization's headquarters.</td>
 * </tr>
 * <tr>
 * <td>"location"</td>
 * <td>the coordinates of the location itself.</td>
 * </tr>
 * </table>
 * 
 * <p>
 * <b>Code sample</b>
 * </p>
 * 
 * <pre>
 * VCard vcard = new VCard();
 * GeoType geo = new GeoType(-123.456, 12.54);
 * vcard.setGeo(geo);
 * </pre>
 * 
 * <p>
 * <b>Property name:</b> <code>GEO</code>
 * </p>
 * <p>
 * <b>Supported versions:</b> <code>2.1, 3.0, 4.0</code>
 * </p>
 * @author Michael Angstadt
 */
public class GeoType extends VCardType implements HasAltId {
	public static final String NAME = "GEO";
	private GeoUri uri;

	/**
	 * Creates an empty geo property.
	 */
	public GeoType() {
		this(null);
	}

	/**
	 * Creates a geo property.
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public GeoType(Double latitude, Double longitude) {
		this(new GeoUri.Builder(latitude, longitude).build());
	}

	/**
	 * Creates a geo property.
	 * @param uri the geo URI
	 */
	public GeoType(GeoUri uri) {
		super(NAME);
		this.uri = uri;
	}

	/**
	 * Gets the latitude.
	 * @return the latitude
	 */
	public Double getLatitude() {
		return (uri == null) ? null : uri.getCoordA();
	}

	/**
	 * Sets the latitude.
	 * @param latitude the latitude
	 */
	public void setLatitude(Double latitude) {
		if (uri == null) {
			uri = new GeoUri.Builder(latitude, null).build();
		} else {
			uri = new GeoUri.Builder(uri).coordA(latitude).build();
		}
	}

	/**
	 * Gets the longitude.
	 * @return the longitude
	 */
	public Double getLongitude() {
		return (uri == null) ? null : uri.getCoordB();
	}

	/**
	 * Sets the longitude.
	 * @param longitude the longitude
	 */
	public void setLongitude(Double longitude) {
		if (uri == null) {
			uri = new GeoUri.Builder(null, longitude).build();
		} else {
			uri = new GeoUri.Builder(uri).coordB(longitude).build();
		}
	}

	/**
	 * Gets the raw object used for storing the GEO information. This can be
	 * used to supplement the GEO value with additional information (such as
	 * altitude or level of accuracy). Geo URIs are only supported by vCard
	 * version 4.0. Only latitude and longitude values are used when marshalling
	 * to earlier vCard versions.
	 * @return the geo URI object or null if not set
	 * @see <a href="http://tools.ietf.org/html/rfc5870">RFC 5870</a>
	 */
	public GeoUri getGeoUri() {
		return uri;
	}

	/**
	 * Sets the raw object used for storing the GEO information. This can be
	 * used to supplement the GEO value with additional information (such as
	 * altitude or level of accuracy). Geo URIs are only supported by vCard
	 * version 4.0. Only latitude and longitude values are used when marshalling
	 * to earlier vCard versions.
	 * @param uri the geo URI object
	 * @see <a href="http://tools.ietf.org/html/rfc5870">RFC 5870</a>
	 */
	public void setGeoUri(GeoUri uri) {
		this.uri = uri;
	}

	/**
	 * Gets the TYPE parameter.
	 * <p>
	 * <b>Supported versions:</b> <code>4.0</code>
	 * </p>
	 * @return the TYPE value (typically, this will be either "work" or "home")
	 * or null if it doesn't exist
	 */
	public String getType() {
		return subTypes.getType();
	}

	/**
	 * Sets the TYPE parameter.
	 * <p>
	 * <b>Supported versions:</b> <code>4.0</code>
	 * </p>
	 * @param type the TYPE value (this should be either "work" or "home") or
	 * null to remove
	 */
	public void setType(String type) {
		subTypes.setType(type);
	}

	/**
	 * Gets the MEDIATYPE parameter.
	 * <p>
	 * <b>Supported versions:</b> <code>4.0</code>
	 * </p>
	 * @return the media type or null if not set
	 */
	public String getMediaType() {
		return subTypes.getMediaType();
	}

	/**
	 * Sets the MEDIATYPE parameter.
	 * <p>
	 * <b>Supported versions:</b> <code>4.0</code>
	 * </p>
	 * @param mediaType the media type or null to remove
	 */
	public void setMediaType(String mediaType) {
		subTypes.setMediaType(mediaType);
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
	protected void doMarshalText(StringBuilder sb, VCardVersion version, CompatibilityMode compatibilityMode) {
		sb.append(write(version));
	}

	@Override
	protected void doUnmarshalText(String value, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		value = VCardStringUtils.unescape(value);
		parse(value, version, warnings);
	}

	@Override
	protected void doMarshalXml(XCardElement parent, CompatibilityMode compatibilityMode) {
		parent.append(VCardDataType.URI, write(parent.version()));
	}

	@Override
	protected void doUnmarshalXml(XCardElement element, List<String> warnings, CompatibilityMode compatibilityMode) {
		String value = element.first(VCardDataType.URI);
		if (value != null) {
			parse(value, element.version(), warnings);
			return;
		}

		throw missingXmlElements(VCardDataType.URI);
	}

	@Override
	protected void doUnmarshalHtml(HCardElement element, List<String> warnings) {
		String latitudeStr = element.firstValue("latitude");
		if (latitudeStr == null) {
			throw new CannotParseException("Latitude missing.");
		}

		Double latitude;
		try {
			latitude = Double.parseDouble(latitudeStr);
		} catch (NumberFormatException e) {
			throw new CannotParseException("Could not parse latitude: " + latitudeStr);
		}

		String longitudeStr = element.firstValue("longitude");
		if (longitudeStr == null) {
			throw new CannotParseException("Longitude missing.");
		}

		Double longitude;
		try {
			longitude = Double.parseDouble(longitudeStr);
		} catch (NumberFormatException e) {
			throw new CannotParseException("Could not parse longitude: " + longitudeStr);
		}

		uri = new GeoUri.Builder(latitude, longitude).build();
	}

	@Override
	protected JCardValue doMarshalJson(VCardVersion version) {
		return JCardValue.single(VCardDataType.URI, write(version));
	}

	@Override
	protected void doUnmarshalJson(JCardValue value, VCardVersion version, List<String> warnings) {
		parse(value.getSingleValued(), version, warnings);
	}

	@Override
	protected void _validate(List<String> warnings, VCardVersion version, VCard vcard) {
		if (getLatitude() == null) {
			warnings.add("Latitude is missing.");
		}
		if (getLongitude() == null) {
			warnings.add("Longitude is missing.");
		}
	}

	private void parse(String value, VCardVersion version, List<String> warnings) {
		if (value == null || value.length() == 0) {
			return;
		}

		switch (version) {
		case V2_1:
		case V3_0:
			String split[] = value.split(";");
			if (split.length != 2) {
				throw new CannotParseException("Incorrect data format.  Value must contain a latitude and longitude, separated by a semi-colon.");
			}

			String latitudeStr = split[0];
			Double latitude;
			try {
				latitude = Double.valueOf(latitudeStr);
			} catch (NumberFormatException e) {
				throw new CannotParseException("Could not parse latitude: " + latitudeStr);
			}

			String longitudeStr = split[1];
			Double longitude;
			try {
				longitude = Double.valueOf(longitudeStr);
			} catch (NumberFormatException e) {
				throw new CannotParseException("Could not parse longtude: " + longitudeStr);
			}

			uri = new GeoUri.Builder(latitude, longitude).build();
			break;
		case V4_0:
			try {
				uri = GeoUri.parse(value);
			} catch (IllegalArgumentException e) {
				throw new CannotParseException("Invalid geo URI.");
			}
			break;
		}
	}

	private String write(VCardVersion version) {
		if (uri == null) {
			return "";
		}

		switch (version) {
		case V2_1:
		case V3_0:
			VCardFloatFormatter formatter = new VCardFloatFormatter(6);
			StringBuilder sb = new StringBuilder();

			sb.append(formatter.format(getLatitude()));
			sb.append(';');
			sb.append(formatter.format(getLongitude()));

			return sb.toString();
		case V4_0:
			return uri.toString(6);
		}
		return null; //needed to prevent compilation error
	}
}
