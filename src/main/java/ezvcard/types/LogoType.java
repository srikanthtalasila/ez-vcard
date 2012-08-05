package ezvcard.types;

import ezvcard.VCardSubTypes;
import ezvcard.parameters.ImageTypeParameter;

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
 * Represents the LOGO type.
 * @author Michael Angstadt
 */
public class LogoType extends BinaryType<ImageTypeParameter> {
	public static final String NAME = "LOGO";

	public LogoType() {
		super(NAME);
	}
	
	/**
	 * @param url the URL to the logo
	 * @param type the content type
	 */
	public LogoType(String url, ImageTypeParameter type){
		super(NAME, url, type);
	}
	
	/**
	 * @param data the binary data
	 * @param type the content type
	 */
	public LogoType(byte[] data, ImageTypeParameter type){
		super(NAME, data, type);
	}
	
	/**
	 * Gets the language that the address is written in.
	 * @return the language or null if not set
	 * @see VCardSubTypes#getLanguage
	 */
	public String getLanguage() {
		return subTypes.getLanguage();
	}

	/**
	 * Sets the language that the address is written in.
	 * @param language the language or null to remove
	 * @see VCardSubTypes#setLanguage
	 */
	public void setLanguage(String language) {
		subTypes.setLanguage(language);
	}

	@Override
	protected ImageTypeParameter buildTypeObj(String type) {
		ImageTypeParameter param = ImageTypeParameter.valueOf(type);
		if (param == null) {
			param = new ImageTypeParameter(type, "image/" + type, null);
		}
		return param;
	}
	
	@Override
	protected ImageTypeParameter buildMediaTypeObj(String mediaType) {
		ImageTypeParameter p = ImageTypeParameter.findByMediaType(mediaType);
		if (p == null) {
			int slashPos = mediaType.indexOf('/');
			String type;
			if (slashPos == -1 || slashPos < mediaType.length() - 1) {
				type = "";
			} else {
				type = mediaType.substring(slashPos + 1);
			}
			p = new ImageTypeParameter(type, mediaType, null);
		}
		return p;
	}
}
