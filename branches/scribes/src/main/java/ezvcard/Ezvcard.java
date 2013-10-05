package ezvcard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;

import ezvcard.io.ScribeIndex;
import ezvcard.io.html.HCardPage;
import ezvcard.io.html.HCardReader;
import ezvcard.io.json.JCardParseException;
import ezvcard.io.json.JCardReader;
import ezvcard.io.json.JCardWriter;
import ezvcard.io.text.VCardReader;
import ezvcard.io.text.VCardWriter;
import ezvcard.io.xml.XCardDocument;
import ezvcard.types.VCardType;
import ezvcard.types.scribes.VCardPropertyScribe;
import ezvcard.util.IOUtils;

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
 * Contains chaining factory methods for parsing/writing vCards. They are
 * convenience methods that make use of the following classes:
 * </p>
 * 
 * 
 * <table border="1">
 * <tr>
 * <th></th>
 * <th>Reading</th>
 * <th>Writing</th>
 * </tr>
 * <tr>
 * <th>Plain text</th>
 * <td>{@link VCardReader}</td>
 * <td>{@link VCardWriter}</td>
 * </tr>
 * <tr>
 * <th>XML</th>
 * <td>{@link XCardDocument}</td>
 * <td>{@link XCardDocument}</td>
 * </tr>
 * <tr>
 * <th>HTML</th>
 * <td>{@link HCardReader}</td>
 * <td>{@link HCardPage}</td>
 * </tr>
 * <tr>
 * <th>JSON</th>
 * <td>{@link JCardReader}</td>
 * <td>{@link JCardWriter}</td>
 * </tr>
 * </table>
 * @author Michael Angstadt
 */
public class Ezvcard {
	/**
	 * The version of the library.
	 */
	public static final String VERSION;

	/**
	 * The project webpage.
	 */
	public static final String URL;

	static {
		InputStream in = null;
		try {
			in = Ezvcard.class.getResourceAsStream("/ez-vcard.properties");
			Properties props = new Properties();
			props.load(in);

			VERSION = props.getProperty("version");
			URL = props.getProperty("url");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * <p>
	 * Parses plain text vCards.
	 * </p>
	 * <p>
	 * Use {@link VCardReader} for more control over the parsing.
	 * </p>
	 * @param str the vCard string
	 * @return chainer object for completing the parse operation
	 * @see VCardReader
	 * @see <a href="http://www.imc.org/pdi/vcard-21.rtf">vCard 2.1</a>
	 * @see <a href="http://tools.ietf.org/html/rfc2426">RFC 2426 (3.0)</a>
	 * @see <a href="http://tools.ietf.org/html/rfc6350">RFC 6350 (4.0)</a>
	 */
	public static ParserChainTextString parse(String str) {
		return new ParserChainTextString(str);
	}

	/**
	 * <p>
	 * Parses plain text vCards.
	 * </p>
	 * <p>
	 * Use {@link VCardReader} for more control over the parsing.
	 * </p>
	 * @param file the vCard file
	 * @return chainer object for completing the parse operation
	 * @see VCardReader
	 * @see <a href="http://www.imc.org/pdi/vcard-21.rtf">vCard 2.1</a>
	 * @see <a href="http://tools.ietf.org/html/rfc2426">RFC 2426 (3.0)</a>
	 * @see <a href="http://tools.ietf.org/html/rfc6350">RFC 6350 (4.0)</a>
	 */
	public static ParserChainTextReader parse(File file) {
		return new ParserChainTextReader(file);
	}

	/**
	 * <p>
	 * Parses plain text vCards.
	 * </p>
	 * <p>
	 * Use {@link VCardReader} for more control over the parsing.
	 * </p>
	 * @param in the input stream
	 * @return chainer object for completing the parse operation
	 * @see VCardReader
	 * @see <a href="http://www.imc.org/pdi/vcard-21.rtf">vCard 2.1</a>
	 * @see <a href="http://tools.ietf.org/html/rfc2426">RFC 2426 (3.0)</a>
	 * @see <a href="http://tools.ietf.org/html/rfc6350">RFC 6350 (4.0)</a>
	 */
	public static ParserChainTextReader parse(InputStream in) {
		return parse(new InputStreamReader(in));
	}

	/**
	 * <p>
	 * Parses plain text vCards.
	 * </p>
	 * <p>
	 * Use {@link VCardReader} for more control over the parsing.
	 * </p>
	 * @param reader the reader
	 * @return chainer object for completing the parse operation
	 * @see VCardReader
	 * @see <a href="http://www.imc.org/pdi/vcard-21.rtf">vCard 2.1</a>
	 * @see <a href="http://tools.ietf.org/html/rfc2426">RFC 2426 (3.0)</a>
	 * @see <a href="http://tools.ietf.org/html/rfc6350">RFC 6350 (4.0)</a>
	 */
	public static ParserChainTextReader parse(Reader reader) {
		return new ParserChainTextReader(reader);
	}

	/**
	 * <p>
	 * Parses XML-encoded vCards (xCard) from a string.
	 * </p>
	 * <p>
	 * Use {@link XCardDocument} for more control over the parsing.
	 * </p>
	 * @param xml the XML document
	 * @return chainer object for completing the parse operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static ParserChainXmlString parseXml(String xml) {
		return new ParserChainXmlString(xml);
	}

	/**
	 * <p>
	 * Parses XML-encoded vCards (xCard) from a file.
	 * </p>
	 * <p>
	 * Use {@link XCardDocument} for more control over the parsing.
	 * </p>
	 * @param file the XML file
	 * @return chainer object for completing the parse operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static ParserChainXmlReader parseXml(File file) {
		return new ParserChainXmlReader(file);
	}

	/**
	 * <p>
	 * Parses XML-encoded vCards (xCard) from an input stream.
	 * </p>
	 * <p>
	 * Use {@link XCardDocument} for more control over the parsing.
	 * </p>
	 * @param in the input stream to the XML document
	 * @return chainer object for completing the parse operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static ParserChainXmlReader parseXml(InputStream in) {
		return new ParserChainXmlReader(in);
	}

	/**
	 * <p>
	 * Parses XML-encoded vCards (xCard) from a reader.
	 * </p>
	 * <p>
	 * Note that use of this method is discouraged. It ignores the character
	 * encoding that is defined within the XML document itself, and should only
	 * be used if the encoding is undefined or if the encoding needs to be
	 * ignored for whatever reason. The {@link #parseXml(InputStream)} method
	 * should be used instead, since it takes the XML document's character
	 * encoding into account when parsing.
	 * </p>
	 * <p>
	 * Use {@link XCardDocument} for more control over the parsing.
	 * </p>
	 * @param reader the reader to the XML document
	 * @return chainer object for completing the parse operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static ParserChainXmlReader parseXml(Reader reader) {
		return new ParserChainXmlReader(reader);
	}

	/**
	 * <p>
	 * Parses XML-encoded vCards (xCard).
	 * </p>
	 * <p>
	 * Use {@link XCardDocument} for more control over the parsing.
	 * </p>
	 * @param document the XML document
	 * @return chainer object for completing the parse operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static ParserChainXmlDom parseXml(Document document) {
		return new ParserChainXmlDom(document);
	}

	/**
	 * <p>
	 * Parses HTML-encoded vCards (hCard).
	 * </p>
	 * <p>
	 * Use {@link HCardReader} for more control over the parsing.
	 * </p>
	 * @param html the HTML page
	 * @return chainer object for completing the parse operation
	 * @see HCardReader
	 * @see <a href="http://microformats.org/wiki/hcard">hCard 1.0</a>
	 */
	public static ParserChainHtmlString parseHtml(String html) {
		return new ParserChainHtmlString(html);
	}

	/**
	 * <p>
	 * Parses HTML-encoded vCards (hCard).
	 * </p>
	 * <p>
	 * Use {@link HCardReader} for more control over the parsing.
	 * </p>
	 * @param file the HTML file
	 * @return chainer object for completing the parse operation
	 * @see HCardReader
	 * @see <a href="http://microformats.org/wiki/hcard">hCard 1.0</a>
	 */
	public static ParserChainHtmlReader parseHtml(File file) {
		return new ParserChainHtmlReader(file);
	}

	/**
	 * <p>
	 * Parses HTML-encoded vCards (hCard).
	 * </p>
	 * <p>
	 * Use {@link HCardReader} for more control over the parsing.
	 * </p>
	 * @param in the input stream to the HTML page
	 * @return chainer object for completing the parse operation
	 * @see HCardReader
	 * @see <a href="http://microformats.org/wiki/hcard">hCard 1.0</a>
	 */
	public static ParserChainHtmlReader parseHtml(InputStream in) {
		return parseHtml(new InputStreamReader(in));
	}

	/**
	 * <p>
	 * Parses HTML-encoded vCards (hCard).
	 * </p>
	 * <p>
	 * Use {@link HCardReader} for more control over the parsing.
	 * </p>
	 * @param reader the reader to the HTML page
	 * @return chainer object for completing the parse operation
	 * @see HCardReader
	 * @see <a href="http://microformats.org/wiki/hcard">hCard 1.0</a>
	 */
	public static ParserChainHtmlReader parseHtml(Reader reader) {
		return new ParserChainHtmlReader(reader);
	}

	/**
	 * <p>
	 * Parses HTML-encoded vCards (hCard).
	 * </p>
	 * <p>
	 * Use {@link HCardReader} for more control over the parsing.
	 * </p>
	 * @param url the URL of the webpage
	 * @return chainer object for completing the parse operation
	 * @see HCardReader
	 * @see <a href="http://microformats.org/wiki/hcard">hCard 1.0</a>
	 */
	public static ParserChainHtmlReader parseHtml(URL url) {
		return new ParserChainHtmlReader(url);
	}

	/**
	 * <p>
	 * Parses JSON-encoded vCards (jCard).
	 * </p>
	 * <p>
	 * Use {@link JCardReader} for more control over the parsing.
	 * </p>
	 * @param json the JSON string
	 * @return chainer object for completing the parse operation
	 * @see JCardReader
	 * @see <a
	 * href="http://tools.ietf.org/html/draft-ietf-jcardcal-jcard-03">jCard
	 * draft specification</a>
	 */
	public static ParserChainJsonString parseJson(String json) {
		return new ParserChainJsonString(json);
	}

	/**
	 * <p>
	 * Parses JSON-encoded vCards (jCard).
	 * </p>
	 * <p>
	 * Use {@link JCardReader} for more control over the parsing.
	 * </p>
	 * @param file the JSON file
	 * @return chainer object for completing the parse operation
	 * @see JCardReader
	 * @see <a
	 * href="http://tools.ietf.org/html/draft-ietf-jcardcal-jcard-03">jCard
	 * draft specification</a>
	 */
	public static ParserChainJsonReader parseJson(File file) {
		return new ParserChainJsonReader(file);
	}

	/**
	 * <p>
	 * Parses JSON-encoded vCards (jCard).
	 * </p>
	 * <p>
	 * Use {@link JCardReader} for more control over the parsing.
	 * </p>
	 * @param in the input stream
	 * @return chainer object for completing the parse operation
	 * @see JCardReader
	 * @see <a
	 * href="http://tools.ietf.org/html/draft-ietf-jcardcal-jcard-03">jCard
	 * draft specification</a>
	 */
	public static ParserChainJsonReader parseJson(InputStream in) {
		return new ParserChainJsonReader(in);
	}

	/**
	 * <p>
	 * Parses JSON-encoded vCards (jCard).
	 * </p>
	 * <p>
	 * Use {@link JCardReader} for more control over the parsing.
	 * </p>
	 * @param reader the reader
	 * @return chainer object for completing the parse operation
	 * @see JCardReader
	 * @see <a
	 * href="http://tools.ietf.org/html/draft-ietf-jcardcal-jcard-03">jCard
	 * draft specification</a>
	 */
	public static ParserChainJsonReader parseJson(Reader reader) {
		return new ParserChainJsonReader(reader);
	}

	/**
	 * <p>
	 * Marshals a vCard to its traditional, plain-text representation.
	 * </p>
	 * 
	 * <p>
	 * Use {@link VCardWriter} for more control over how the vCard is written.
	 * </p>
	 * @param vcard the vCard to marshal
	 * @return chainer object for completing the write operation
	 * @see VCardWriter
	 * @see <a href="http://www.imc.org/pdi/vcard-21.rtf">vCard 2.1</a>
	 * @see <a href="http://tools.ietf.org/html/rfc2426">RFC 2426 (3.0)</a>
	 * @see <a href="http://tools.ietf.org/html/rfc6350">RFC 6350 (4.0)</a>
	 */
	public static WriterChainTextSingle write(VCard vcard) {
		return new WriterChainTextSingle(vcard);
	}

	/**
	 * <p>
	 * Marshals multiple vCards to their traditional, plain-text representation.
	 * </p>
	 * 
	 * <p>
	 * Use {@link VCardWriter} for more control over how the vCards are written.
	 * </p>
	 * @param vcards the vCards to marshal
	 * @return chainer object for completing the write operation
	 * @see VCardWriter
	 * @see <a href="http://www.imc.org/pdi/vcard-21.rtf">vCard 2.1</a>
	 * @see <a href="http://tools.ietf.org/html/rfc2426">RFC 2426 (3.0)</a>
	 * @see <a href="http://tools.ietf.org/html/rfc6350">RFC 6350 (4.0)</a>
	 */
	public static WriterChainTextMulti write(VCard... vcards) {
		return write(Arrays.asList(vcards));
	}

	/**
	 * <p>
	 * Marshals multiple vCards to their traditional, plain-text representation.
	 * </p>
	 * 
	 * <p>
	 * Use {@link VCardWriter} for more control over how the vCards are written.
	 * </p>
	 * @param vcards the vCards to marshal
	 * @return chainer object for completing the write operation
	 * @see VCardWriter
	 * @see <a href="http://www.imc.org/pdi/vcard-21.rtf">vCard 2.1</a>
	 * @see <a href="http://tools.ietf.org/html/rfc2426">RFC 2426 (3.0)</a>
	 * @see <a href="http://tools.ietf.org/html/rfc6350">RFC 6350 (4.0)</a>
	 */
	public static WriterChainTextMulti write(Collection<VCard> vcards) {
		return new WriterChainTextMulti(vcards);
	}

	/**
	 * <p>
	 * Marshals a vCard to its XML representation (xCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link XCardDocument} for more control over how the vCard is written.
	 * </p>
	 * @param vcard the vCard to marshal
	 * @return chainer object for completing the write operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static WriterChainXmlSingle writeXml(VCard vcard) {
		return new WriterChainXmlSingle(vcard);
	}

	/**
	 * <p>
	 * Marshals multiple vCards to their XML representation (xCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link XCardDocument} for more control over how the vCards are
	 * written.
	 * </p>
	 * @param vcards the vCards to marshal
	 * @return chainer object for completing the write operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static WriterChainXmlMulti writeXml(VCard... vcards) {
		return writeXml(Arrays.asList(vcards));
	}

	/**
	 * <p>
	 * Marshals multiple vCards to their XML representation (xCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link XCardDocument} for more control over how the vCards are
	 * written.
	 * </p>
	 * @param vcards the vCard to marshal
	 * @return chainer object for completing the write operation
	 * @see XCardDocument
	 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
	 */
	public static WriterChainXmlMulti writeXml(Collection<VCard> vcards) {
		return new WriterChainXmlMulti(vcards);
	}

	/**
	 * <p>
	 * Marshals one or more vCards their HTML representation (hCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link HCardPage} for more control over how the vCards are written.
	 * </p>
	 * @param vcards the vCard(s) to marshal
	 * @return chainer object for completing the write operation
	 * @see HCardPage
	 * @see <a href="http://microformats.org/wiki/hcard">hCard 1.0</a>
	 */
	public static WriterChainHtml writeHtml(VCard... vcards) {
		return writeHtml(Arrays.asList(vcards));
	}

	/**
	 * <p>
	 * Marshals one or more vCards their HTML representation (hCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link HCardPage} for more control over how the vCards are written.
	 * </p>
	 * @param vcards the vCard(s) to marshal
	 * @return chainer object for completing the write operation
	 * @see HCardPage
	 * @see <a href="http://microformats.org/wiki/hcard">hCard 1.0</a>
	 */
	public static WriterChainHtml writeHtml(Collection<VCard> vcards) {
		return new WriterChainHtml(vcards);
	}

	/**
	 * <p>
	 * Marshals a vCard to its JSON representation (jCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link JCardWriter} for more control over how the vCard is written.
	 * </p>
	 * @param vcard the vCard to marshal
	 * @return chainer object for completing the write operation
	 * @see JCardWriter
	 * @see <a
	 * href="http://tools.ietf.org/html/draft-ietf-jcardcal-jcard-03">jCard
	 * draft specification</a>
	 */
	public static WriterChainJsonSingle writeJson(VCard vcard) {
		return new WriterChainJsonSingle(vcard);
	}

	/**
	 * <p>
	 * Marshals multiple vCards to their JSON representation (jCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link JCardWriter} for more control over how the vCards are written.
	 * </p>
	 * @param vcards the vCards to marshal
	 * @return chainer object for completing the write operation
	 * @see JCardWriter
	 * @see <a
	 * href="http://tools.ietf.org/html/draft-ietf-jcardcal-jcard-03">jCard
	 * draft specification</a>
	 */
	public static WriterChainJsonMulti writeJson(VCard... vcards) {
		return writeJson(Arrays.asList(vcards));
	}

	/**
	 * <p>
	 * Marshals multiple vCards to their JSON representation (jCard).
	 * </p>
	 * 
	 * <p>
	 * Use {@link JCardWriter} for more control over how the vCards are written.
	 * </p>
	 * @param vcards the vCards to marshal
	 * @return chainer object for completing the write operation
	 * @see JCardWriter
	 * @see <a
	 * href="http://tools.ietf.org/html/draft-ietf-jcardcal-jcard-03">jCard
	 * draft specification</a>
	 */
	public static WriterChainJsonMulti writeJson(Collection<VCard> vcards) {
		return new WriterChainJsonMulti(vcards);
	}

	static abstract class ParserChain<T> {
		final ScribeIndex index = new ScribeIndex();
		List<List<String>> warnings;

		@SuppressWarnings("unchecked")
		final T this_ = (T) this;

		/**
		 * Registers a property scribe.
		 * @param scribe the scribe
		 * @return this
		 */
		public T register(VCardPropertyScribe<? extends VCardType> scribe) {
			index.register(scribe);
			return this_;
		}

		/**
		 * Provides a list object that any unmarshal warnings will be put into.
		 * @param warnings the list object that will be populated with the
		 * warnings of each unmarshalled vCard. Each element of the list is the
		 * list of warnings for one of the unmarshalled vCards. Therefore, the
		 * size of this list will be equal to the number of parsed vCards. If a
		 * vCard does not have any warnings, then its warning list will be
		 * empty.
		 * @return this
		 */
		public T warnings(List<List<String>> warnings) {
			this.warnings = warnings;
			return this_;
		}

		/**
		 * Reads the first vCard from the stream.
		 * @return the vCard or null if there are no vCards
		 * @throws IOException if there's an I/O problem
		 * @throws SAXException if there's a problem parsing the XML
		 */
		public abstract VCard first() throws IOException, SAXException;

		/**
		 * Reads all vCards from the stream.
		 * @return the parsed vCards
		 * @throws IOException if there's an I/O problem
		 * @throws SAXException if there's a problem parsing the XML
		 */
		public abstract List<VCard> all() throws IOException, SAXException;
	}

	static abstract class ParserChainText<T> extends ParserChain<T> {
		boolean caretDecoding = true;
		final boolean closeWhenDone;

		private ParserChainText(boolean closeWhenDone) {
			this.closeWhenDone = closeWhenDone;
		}

		/**
		 * Sets whether the reader will decode characters in parameter values
		 * that use circumflex accent encoding (enabled by default).
		 * 
		 * @param enable true to use circumflex accent decoding, false not to
		 * @return this
		 * @see VCardReader#setCaretDecodingEnabled(boolean)
		 * @see <a href="http://tools.ietf.org/html/rfc6868">RFC 6868</a>
		 */
		public T caretDecoding(boolean enable) {
			caretDecoding = enable;
			return this_;
		}

		@Override
		public VCard first() throws IOException {
			VCardReader parser = constructReader();

			try {
				VCard vcard = parser.readNext();
				if (warnings != null) {
					warnings.add(parser.getWarnings());
				}
				return vcard;
			} finally {
				if (closeWhenDone) {
					IOUtils.closeQuietly(parser);
				}
			}
		}

		@Override
		public List<VCard> all() throws IOException {
			VCardReader parser = constructReader();

			try {
				List<VCard> vcards = new ArrayList<VCard>();
				VCard vcard;
				while ((vcard = parser.readNext()) != null) {
					if (warnings != null) {
						warnings.add(parser.getWarnings());
					}
					vcards.add(vcard);
				}
				return vcards;
			} finally {
				if (closeWhenDone) {
					IOUtils.closeQuietly(parser);
				}
			}
		}

		private VCardReader constructReader() throws IOException {
			VCardReader parser = _constructReader();
			parser.setScribeIndex(index);
			parser.setCaretDecodingEnabled(caretDecoding);
			return parser;
		}

		abstract VCardReader _constructReader() throws IOException;
	}

	/**
	 * Chainer class for parsing plain text vCards.
	 * @see Ezvcard#parse(InputStream)
	 * @see Ezvcard#parse(File)
	 * @see Ezvcard#parse(Reader)
	 */
	public static class ParserChainTextReader extends ParserChainText<ParserChainTextReader> {
		private final Reader reader;
		private final File file;

		private ParserChainTextReader(Reader reader) {
			super(false);
			this.reader = reader;
			this.file = null;
		}

		private ParserChainTextReader(File file) {
			super(true);
			this.reader = null;
			this.file = file;
		}

		@Override
		public ParserChainTextReader register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainTextReader warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		public ParserChainTextReader caretDecoding(boolean enable) {
			return super.caretDecoding(enable);
		}

		@Override
		@SuppressWarnings("resource")
		VCardReader _constructReader() throws IOException {
			return (reader != null) ? new VCardReader(reader) : new VCardReader(file);
		}
	}

	/**
	 * Chainer class for parsing plain text vCards.
	 * @see Ezvcard#parse(String)
	 */
	public static class ParserChainTextString extends ParserChainText<ParserChainTextString> {
		private final String text;

		private ParserChainTextString(String text) {
			super(false);
			this.text = text;
		}

		@Override
		public ParserChainTextString register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainTextString warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		public ParserChainTextString caretDecoding(boolean enable) {
			return super.caretDecoding(enable);
		}

		@Override
		VCardReader _constructReader() {
			return new VCardReader(text);
		}

		@Override
		public VCard first() {
			try {
				return super.first();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}

		@Override
		public List<VCard> all() {
			try {
				return super.all();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}
	}

	static abstract class ParserChainXml<T> extends ParserChain<T> {
		@Override
		public VCard first() throws IOException, SAXException {
			XCardDocument document = constructDocument();
			VCard vcard = document.parseFirst();
			if (warnings != null) {
				warnings.addAll(document.getParseWarnings());
			}
			return vcard;
		}

		@Override
		public List<VCard> all() throws IOException, SAXException {
			XCardDocument document = constructDocument();
			List<VCard> icals = document.parseAll();
			if (warnings != null) {
				warnings.addAll(document.getParseWarnings());
			}
			return icals;
		}

		private XCardDocument constructDocument() throws SAXException, IOException {
			XCardDocument parser = _constructDocument();
			parser.setScribeIndex(index);
			return parser;
		}

		abstract XCardDocument _constructDocument() throws IOException, SAXException;
	}

	/**
	 * Chainer class for parsing XML vCards.
	 * @see Ezvcard#parseXml(InputStream)
	 * @see Ezvcard#parseXml(File)
	 * @see Ezvcard#parseXml(Reader)
	 */
	public static class ParserChainXmlReader extends ParserChainXml<ParserChainXmlReader> {
		private final InputStream in;
		private final File file;
		private final Reader reader;

		private ParserChainXmlReader(InputStream in) {
			this.in = in;
			this.reader = null;
			this.file = null;
		}

		private ParserChainXmlReader(File file) {
			this.in = null;
			this.reader = null;
			this.file = file;
		}

		private ParserChainXmlReader(Reader reader) {
			this.in = null;
			this.reader = reader;
			this.file = null;
		}

		@Override
		public ParserChainXmlReader register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainXmlReader warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		XCardDocument _constructDocument() throws IOException, SAXException {
			if (in != null) {
				return new XCardDocument(in);
			}
			if (file != null) {
				return new XCardDocument(file);
			}
			return new XCardDocument(reader);
		}
	}

	/**
	 * Chainer class for parsing XML vCards.
	 * @see Ezvcard#parseXml(String)
	 */
	public static class ParserChainXmlString extends ParserChainXml<ParserChainXmlString> {
		private final String xml;

		private ParserChainXmlString(String xml) {
			this.xml = xml;
		}

		@Override
		public ParserChainXmlString register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainXmlString warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		XCardDocument _constructDocument() throws SAXException {
			return new XCardDocument(xml);
		}

		@Override
		public VCard first() throws SAXException {
			try {
				return super.first();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}

		@Override
		public List<VCard> all() throws SAXException {
			try {
				return super.all();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Chainer class for parsing XML vCards.
	 * @see Ezvcard#parseXml(Document)
	 */
	public static class ParserChainXmlDom extends ParserChainXml<ParserChainXmlDom> {
		private final Document document;

		private ParserChainXmlDom(Document document) {
			this.document = document;
		}

		@Override
		public ParserChainXmlDom register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainXmlDom warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		XCardDocument _constructDocument() {
			return new XCardDocument(document);
		}

		@Override
		public VCard first() {
			try {
				return super.first();
			} catch (IOException e) {
				//should never be thrown because we're reading from a DOM
				throw new RuntimeException(e);
			} catch (SAXException e) {
				//should never be thrown because we're reading from a DOM
				throw new RuntimeException(e);
			}
		}

		@Override
		public List<VCard> all() {
			try {
				return super.all();
			} catch (IOException e) {
				//should never be thrown because we're reading from a DOM
				throw new RuntimeException(e);
			} catch (SAXException e) {
				//should never be thrown because we're reading from a DOM
				throw new RuntimeException(e);
			}
		}
	}

	static abstract class ParserChainHtml<T> extends ParserChain<T> {
		String pageUrl;

		/**
		 * Sets the original URL of the webpage. This is used to resolve
		 * relative links and to set the SOURCE property on the vCard. Setting
		 * this property has no effect if reading from a {@link URL}.
		 * @param pageUrl the webpage URL
		 * @return this
		 */
		public T pageUrl(String pageUrl) {
			this.pageUrl = pageUrl;
			return this_;
		}

		@Override
		public VCard first() throws IOException {
			HCardReader parser = constructReader();

			VCard vcard = parser.readNext();
			if (warnings != null) {
				warnings.add(parser.getWarnings());
			}
			return vcard;
		}

		@Override
		public List<VCard> all() throws IOException {
			HCardReader parser = constructReader();

			List<VCard> vcards = new ArrayList<VCard>();
			VCard vcard;
			while ((vcard = parser.readNext()) != null) {
				if (warnings != null) {
					warnings.add(parser.getWarnings());
				}
				vcards.add(vcard);
			}
			return vcards;
		}

		private HCardReader constructReader() throws IOException {
			HCardReader parser = _constructReader();
			parser.setScribeIndex(index);
			return parser;
		}

		abstract HCardReader _constructReader() throws IOException;
	}

	/**
	 * Chainer class for parsing HTML vCards.
	 * @see Ezvcard#parseHtml(InputStream)
	 * @see Ezvcard#parseHtml(File)
	 * @see Ezvcard#parseHtml(Reader)
	 */
	public static class ParserChainHtmlReader extends ParserChainHtml<ParserChainHtmlReader> {
		private final Reader reader;
		private final File file;
		private final URL url;

		private ParserChainHtmlReader(Reader reader) {
			this.reader = reader;
			this.file = null;
			this.url = null;
		}

		private ParserChainHtmlReader(File file) {
			this.reader = null;
			this.file = file;
			this.url = null;
		}

		private ParserChainHtmlReader(URL url) {
			this.reader = null;
			this.file = null;
			this.url = url;
		}

		@Override
		public ParserChainHtmlReader register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainHtmlReader warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		public ParserChainHtmlReader pageUrl(String pageUrl) {
			return super.pageUrl(pageUrl);
		}

		@Override
		HCardReader _constructReader() throws IOException {
			if (reader != null) {
				return new HCardReader(reader, pageUrl);
			}

			if (file != null) {
				//Jsoup (presumably) closes the FileReader it creates
				return new HCardReader(file, pageUrl);
			}

			return new HCardReader(url);
		}
	}

	/**
	 * Chainer class for parsing HTML vCards.
	 * @see Ezvcard#parseHtml(String)
	 */
	public static class ParserChainHtmlString extends ParserChainHtml<ParserChainHtmlString> {
		private final String html;

		private ParserChainHtmlString(String html) {
			this.html = html;
		}

		@Override
		public ParserChainHtmlString register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainHtmlString warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		public ParserChainHtmlString pageUrl(String pageUrl) {
			return super.pageUrl(pageUrl);
		}

		@Override
		HCardReader _constructReader() {
			return new HCardReader(html, pageUrl);
		}

		@Override
		public VCard first() {
			try {
				return super.first();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}

		@Override
		public List<VCard> all() {
			try {
				return super.all();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}
	}

	static abstract class ParserChainJson<T> extends ParserChain<T> {
		final boolean closeWhenDone;

		private ParserChainJson(boolean closeWhenDone) {
			this.closeWhenDone = closeWhenDone;
		}

		/**
		 * @throws JCardParseException if the jCard syntax is incorrect (the
		 * JSON syntax may be valid, but it is not in the correct jCard format).
		 * @throws JsonParseException if the JSON syntax is incorrect
		 */
		@Override
		public VCard first() throws IOException {
			JCardReader parser = constructReader();

			try {
				VCard vcard = parser.readNext();
				if (warnings != null) {
					warnings.add(parser.getWarnings());
				}
				return vcard;
			} finally {
				if (closeWhenDone) {
					IOUtils.closeQuietly(parser);
				}
			}
		}

		/**
		 * @throws JCardParseException if the jCard syntax is incorrect (the
		 * JSON syntax may be valid, but it is not in the correct jCard format).
		 * @throws JsonParseException if the JSON syntax is incorrect
		 */
		@Override
		public List<VCard> all() throws IOException {
			JCardReader parser = constructReader();

			try {
				List<VCard> vcards = new ArrayList<VCard>();
				VCard vcard;
				while ((vcard = parser.readNext()) != null) {
					if (warnings != null) {
						warnings.add(parser.getWarnings());
					}
					vcards.add(vcard);
				}
				return vcards;
			} finally {
				if (closeWhenDone) {
					IOUtils.closeQuietly(parser);
				}
			}
		}

		private JCardReader constructReader() throws IOException {
			JCardReader parser = _constructReader();
			parser.setScribeIndex(index);
			return parser;
		}

		abstract JCardReader _constructReader() throws IOException;
	}

	/**
	 * Chainer class for parsing JSON-encoded vCards (jCard).
	 * @see Ezvcard#parseJson(InputStream)
	 * @see Ezvcard#parseJson(File)
	 * @see Ezvcard#parseJson(Reader)
	 */
	public static class ParserChainJsonReader extends ParserChainJson<ParserChainJsonReader> {
		private final InputStream in;
		private final File file;
		private final Reader reader;

		private ParserChainJsonReader(InputStream in) {
			super(false);
			this.in = in;
			this.reader = null;
			this.file = null;
		}

		private ParserChainJsonReader(File file) {
			super(true);
			this.in = null;
			this.reader = null;
			this.file = file;
		}

		private ParserChainJsonReader(Reader reader) {
			super(false);
			this.in = null;
			this.reader = reader;
			this.file = null;
		}

		@Override
		public ParserChainJsonReader register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainJsonReader warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		JCardReader _constructReader() throws IOException {
			if (in != null) {
				return new JCardReader(in);
			}
			if (file != null) {
				return new JCardReader(file);
			}
			return new JCardReader(reader);
		}
	}

	/**
	 * Chainer class for parsing JSON-encoded vCards (jCard).
	 * @see Ezvcard#parseJson(String)
	 */
	public static class ParserChainJsonString extends ParserChainJson<ParserChainJsonString> {
		private final String json;

		private ParserChainJsonString(String json) {
			super(false);
			this.json = json;
		}

		@Override
		public ParserChainJsonString register(VCardPropertyScribe<? extends VCardType> scribe) {
			return super.register(scribe);
		}

		@Override
		public ParserChainJsonString warnings(List<List<String>> warnings) {
			return super.warnings(warnings);
		}

		@Override
		JCardReader _constructReader() {
			return new JCardReader(json);
		}

		@Override
		public VCard first() {
			try {
				return super.first();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}

		@Override
		public List<VCard> all() {
			try {
				return super.all();
			} catch (IOException e) {
				//should never be thrown because we're reading from a string
				throw new RuntimeException(e);
			}
		}
	}

	static abstract class WriterChain<T> {
		final Collection<VCard> vcards;

		@SuppressWarnings("unchecked")
		final T this_ = (T) this;

		WriterChain(Collection<VCard> vcards) {
			this.vcards = vcards;
		}
	}

	static abstract class WriterChainText<T> extends WriterChain<T> {
		VCardVersion version;
		boolean prodId = true;
		boolean versionStrict = true;
		boolean caretEncoding = false;

		WriterChainText(Collection<VCard> vcards) {
			super(vcards);
		}

		/**
		 * <p>
		 * Sets the version that all the vCards will be marshalled to. The
		 * version that is attached to each individual {@link VCard} object will
		 * be ignored.
		 * </p>
		 * <p>
		 * If no version is passed into this method, the writer will look at the
		 * version attached to each individual {@link VCard} object and marshal
		 * it to that version. And if a {@link VCard} object has no version
		 * attached to it, then it will be marshalled to version 3.0.
		 * </p>
		 * @param version the version to marshal the vCards to
		 * @return this
		 */
		public T version(VCardVersion version) {
			this.version = version;
			return this_;
		}

		/**
		 * Sets whether or not to add a PRODID type to each vCard, saying that
		 * the vCard was generated by this library. For 2.1 vCards, the extended
		 * type X-PRODID is used, since PRODID is not supported by that version.
		 * @param include true to add PRODID (default), false not to
		 * @return this
		 */
		public T prodId(boolean include) {
			this.prodId = include;
			return this_;
		}

		/**
		 * Sets whether the writer will use circumflex accent encoding for vCard
		 * 3.0 and 4.0 parameter values (disabled by default).
		 * @param enable true to use circumflex accent encoding, false not to
		 * @return this
		 * @see VCardWriter#setCaretEncodingEnabled(boolean)
		 * @see <a href="http://tools.ietf.org/html/rfc6868">RFC 6868</a>
		 */
		public T caretEncoding(boolean enable) {
			this.caretEncoding = enable;
			return this_;
		}

		/**
		 * Sets whether properties that do not support the target version will
		 * be excluded from the written vCard.
		 * @param versionStrict true to exclude properties that do not support
		 * the target version, false to include them anyway (defaults to true)
		 * @return this
		 */
		public T versionStrict(boolean versionStrict) {
			this.versionStrict = versionStrict;
			return this_;
		}

		/**
		 * Writes the vCards to a string.
		 * @return the vCard string
		 */
		public String go() {
			StringWriter sw = new StringWriter();
			try {
				go(sw);
			} catch (IOException e) {
				//writing to a string
			}
			return sw.toString();
		}

		/**
		 * Writes the vCards to an output stream.
		 * @param out the output stream to write to
		 * @throws IOException if there's a problem writing to the output stream
		 */
		public void go(OutputStream out) throws IOException {
			VCardWriter vcardWriter = (version == null) ? new VCardWriter(out) : new VCardWriter(out, version);
			go(vcardWriter);
		}

		/**
		 * Writes the vCards to a file. If the file exists, it will be
		 * overwritten.
		 * @param file the file to write to
		 * @throws IOException if there's a problem writing to the file
		 */
		public void go(File file) throws IOException {
			go(file, false);
		}

		/**
		 * Writes the vCards to a file.
		 * @param file the file to write to
		 * @param append true to append onto the end of the file, false to
		 * overwrite it
		 * @throws IOException if there's a problem writing to the file
		 */
		public void go(File file, boolean append) throws IOException {
			VCardWriter vcardWriter = (version == null) ? new VCardWriter(file, append) : new VCardWriter(file, append, version);
			try {
				go(vcardWriter);
			} finally {
				IOUtils.closeQuietly(vcardWriter);
			}
		}

		/**
		 * Writes the vCards to a writer.
		 * @param writer the writer to write to
		 * @throws IOException if there's a problem writing to the writer
		 */
		public void go(Writer writer) throws IOException {
			VCardWriter vcardWriter = new VCardWriter(writer);
			vcardWriter.setTargetVersion(version);
			go(vcardWriter);
		}

		private void go(VCardWriter vcardWriter) throws IOException {
			vcardWriter.setAddProdId(prodId);
			vcardWriter.setCaretEncodingEnabled(caretEncoding);
			vcardWriter.setVersionStrict(versionStrict);

			for (VCard vcard : vcards) {
				if (version == null) {
					VCardVersion vcardVersion = vcard.getVersion();
					if (vcardVersion == null) {
						vcardVersion = VCardVersion.V3_0;
					}
					vcardWriter.setTargetVersion(vcardVersion);
				}
				vcardWriter.write(vcard);
			}
		}
	}

	/**
	 * Chainer class for writing plain text vCards
	 * @see Ezvcard#write(Collection)
	 * @see Ezvcard#write(VCard...)
	 */
	public static class WriterChainTextMulti extends WriterChainText<WriterChainTextMulti> {
		private WriterChainTextMulti(Collection<VCard> vcards) {
			super(vcards);
		}

		@Override
		public WriterChainTextMulti version(VCardVersion version) {
			return super.version(version);
		}

		@Override
		public WriterChainTextMulti prodId(boolean include) {
			return super.prodId(include);
		}

		@Override
		public WriterChainTextMulti caretEncoding(boolean enable) {
			return super.caretEncoding(enable);
		}

		@Override
		public WriterChainTextMulti versionStrict(boolean versionStrict) {
			return super.versionStrict(versionStrict);
		}
	}

	/**
	 * Chainer class for writing plain text vCards
	 * @see Ezvcard#write(VCard)
	 */
	public static class WriterChainTextSingle extends WriterChainText<WriterChainTextSingle> {
		private WriterChainTextSingle(VCard vcard) {
			super(Arrays.asList(vcard));
		}

		@Override
		public WriterChainTextSingle version(VCardVersion version) {
			return super.version(version);
		}

		@Override
		public WriterChainTextSingle prodId(boolean include) {
			return super.prodId(include);
		}

		@Override
		public WriterChainTextSingle caretEncoding(boolean enable) {
			return super.caretEncoding(enable);
		}

		@Override
		public WriterChainTextSingle versionStrict(boolean versionStrict) {
			return super.versionStrict(versionStrict);
		}
	}

	static abstract class WriterChainXml<T> extends WriterChain<T> {
		boolean prodId = true;
		boolean versionStrict = true;
		int indent = -1;

		WriterChainXml(Collection<VCard> vcards) {
			super(vcards);
		}

		/**
		 * Sets whether or not to add a PRODID type to each vCard, saying that
		 * the vCard was generated by this library.
		 * @param include true to add PRODID (default), false not to
		 * @return this
		 */
		public T prodId(boolean include) {
			this.prodId = include;
			return this_;
		}

		/**
		 * Sets the number of indent spaces to use for pretty-printing. If not
		 * set, then the XML will not be pretty-printed.
		 * @param indent the number of spaces in the indent string
		 * @return this
		 */
		public T indent(int indent) {
			this.indent = indent;
			return this_;
		}

		/**
		 * Sets whether properties that do not support xCard (vCard version 4.0)
		 * will be excluded from the written vCard.
		 * @param versionStrict true to exclude properties that do not support
		 * xCard, false to include them anyway (defaults to true)
		 * @return this
		 */
		public T versionStrict(boolean versionStrict) {
			this.versionStrict = versionStrict;
			return this_;
		}

		/**
		 * Writes the xCards to a string.
		 * @return the XML document
		 */
		public String go() {
			StringWriter sw = new StringWriter();
			try {
				go(sw);
			} catch (TransformerException e) {
				//writing to a string
			}
			return sw.toString();
		}

		/**
		 * Writes the xCards to an output stream.
		 * @param out the output stream to write to
		 * @throws TransformerException if there's a problem writing to the
		 * output stream
		 */
		public void go(OutputStream out) throws TransformerException {
			go(new OutputStreamWriter(out));
		}

		/**
		 * Writes the xCards to a file.
		 * @param file the file to write to
		 * @throws IOException if the file can't be opened
		 * @throws TransformerException if there's a problem writing to the file
		 */
		public void go(File file) throws IOException, TransformerException {
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				go(writer);
			} finally {
				IOUtils.closeQuietly(writer);
			}
		}

		/**
		 * Writes the xCards to a writer.
		 * @param writer the writer to write to
		 * @throws TransformerException if there's a problem writing to the
		 * writer
		 */
		public void go(Writer writer) throws TransformerException {
			XCardDocument doc = createXCardDocument();
			doc.write(writer, indent);
		}

		/**
		 * Generates an XML document object model (DOM) containing the xCards.
		 * @return the DOM
		 */
		public Document dom() {
			XCardDocument doc = createXCardDocument();
			return doc.getDocument();
		}

		private XCardDocument createXCardDocument() {
			XCardDocument doc = new XCardDocument();
			doc.setAddProdId(prodId);
			doc.setVersionStrict(versionStrict);

			for (VCard vcard : vcards) {
				doc.add(vcard);
			}

			return doc;
		}
	}

	/**
	 * Chainer class for writing XML vCards (xCard).
	 * @see Ezvcard#writeXml(Collection)
	 * @see Ezvcard#writeXml(VCard...)
	 */
	public static class WriterChainXmlMulti extends WriterChainXml<WriterChainXmlMulti> {
		private WriterChainXmlMulti(Collection<VCard> vcards) {
			super(vcards);
		}

		@Override
		public WriterChainXmlMulti prodId(boolean include) {
			return super.prodId(include);
		}

		@Override
		public WriterChainXmlMulti indent(int indent) {
			return super.indent(indent);
		}

		@Override
		public WriterChainXmlMulti versionStrict(boolean versionStrict) {
			return super.versionStrict(versionStrict);
		}
	}

	/**
	 * Chainer class for writing XML vCards (xCard).
	 * @see Ezvcard#writeXml(VCard)
	 */
	public static class WriterChainXmlSingle extends WriterChainXml<WriterChainXmlSingle> {
		private WriterChainXmlSingle(VCard vcard) {
			super(Arrays.asList(vcard));
		}

		@Override
		public WriterChainXmlSingle prodId(boolean include) {
			return super.prodId(include);
		}

		@Override
		public WriterChainXmlSingle indent(int indent) {
			return super.indent(indent);
		}

		@Override
		public WriterChainXmlSingle versionStrict(boolean versionStrict) {
			return super.versionStrict(versionStrict);
		}
	}

	/**
	 * Chainer class for writing HTML vCards (hCard).
	 * @see Ezvcard#writeHtml(Collection)
	 * @see Ezvcard#writeHtml(VCard...)
	 */
	public static class WriterChainHtml extends WriterChain<WriterChainHtml> {
		private WriterChainHtml(Collection<VCard> vcards) {
			super(vcards);
		}

		/**
		 * Writes the hCards to a string.
		 * @return the HTML page
		 */
		public String go() {
			StringWriter sw = new StringWriter();
			try {
				go(sw);
			} catch (IOException e) {
				//writing string
			}
			return sw.toString();
		}

		/**
		 * Writes the hCards to an output stream.
		 * @param out the output stream to write to
		 * @throws IOException if there's a problem writing to the output stream
		 */
		public void go(OutputStream out) throws IOException {
			go(new OutputStreamWriter(out));
		}

		/**
		 * Writes the hCards to a file.
		 * @param file the file to write to
		 * @throws IOException if there's a problem writing to the file
		 */
		public void go(File file) throws IOException {
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				go(writer);
			} finally {
				IOUtils.closeQuietly(writer);
			}
		}

		/**
		 * Writes the hCards to a writer.
		 * @param writer the writer to write to
		 * @throws IOException if there's a problem writing to the writer
		 */
		public void go(Writer writer) throws IOException {
			HCardPage page = new HCardPage();
			for (VCard vcard : vcards) {
				page.add(vcard);
			}
			page.write(writer);
		}
	}

	static abstract class WriterChainJson<T> extends WriterChain<T> {
		boolean prodId = true;
		boolean versionStrict = true;
		boolean indent = false;

		WriterChainJson(Collection<VCard> vcards) {
			super(vcards);
		}

		/**
		 * Sets whether or not to add a PRODID type to each vCard, saying that
		 * the vCard was generated by this library.
		 * @param include true to add PRODID (default), false not to
		 * @return this
		 */
		public T prodId(boolean include) {
			this.prodId = include;
			return this_;
		}

		/**
		 * Sets whether or not to pretty-print the JSON.
		 * @param indent true to pretty-print it, false not to (defaults to
		 * false)
		 * @return this
		 */
		public T indent(boolean indent) {
			this.indent = indent;
			return this_;
		}

		/**
		 * Sets whether properties that do not support jCard (vCard version 4.0)
		 * will be excluded from the written vCard.
		 * @param versionStrict true to exclude properties that do not support
		 * jCard, false to include them anyway (defaults to true)
		 * @return this
		 */
		public T versionStrict(boolean versionStrict) {
			this.versionStrict = versionStrict;
			return this_;
		}

		/**
		 * Writes the jCards to a string.
		 * @return the JSON string
		 */
		public String go() {
			StringWriter sw = new StringWriter();
			try {
				go(sw);
			} catch (IOException e) {
				//writing to a string
			}
			return sw.toString();
		}

		/**
		 * Writes the jCards to an output stream.
		 * @param out the output stream to write to
		 * @throws IOException if there's a problem writing to the output stream
		 */
		public void go(OutputStream out) throws IOException {
			go(new OutputStreamWriter(out));
		}

		/**
		 * Writes the jCards to a file.
		 * @param file the file to write to
		 * @throws IOException if there's a problem writing to the file
		 */
		public void go(File file) throws IOException {
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				go(writer);
			} finally {
				IOUtils.closeQuietly(writer);
			}
		}

		/**
		 * Writes the jCards to a writer.
		 * @param writer the writer to write to
		 * @throws IOException if there's a problem writing to the writer
		 */
		public void go(Writer writer) throws IOException {
			@SuppressWarnings("resource")
			JCardWriter jcardWriter = new JCardWriter(writer, vcards.size() > 1);
			jcardWriter.setAddProdId(prodId);
			jcardWriter.setIndent(indent);
			jcardWriter.setVersionStrict(versionStrict);
			try {
				for (VCard vcard : vcards) {
					jcardWriter.write(vcard);
				}
			} finally {
				jcardWriter.closeJsonStream();
			}
		}
	}

	/**
	 * Chainer class for writing JSON-encoded vCards (jCard).
	 * @see Ezvcard#writeJson(Collection)
	 * @see Ezvcard#writeJson(VCard...)
	 */
	public static class WriterChainJsonMulti extends WriterChainJson<WriterChainJsonMulti> {
		private WriterChainJsonMulti(Collection<VCard> vcards) {
			super(vcards);
		}

		@Override
		public WriterChainJsonMulti prodId(boolean include) {
			return super.prodId(include);
		}

		@Override
		public WriterChainJsonMulti indent(boolean indent) {
			return super.indent(indent);
		}

		@Override
		public WriterChainJsonMulti versionStrict(boolean versionStrict) {
			return super.versionStrict(versionStrict);
		}
	}

	/**
	 * Chainer class for writing JSON-encoded vCards (jCard).
	 * @see Ezvcard#writeJson(VCard)
	 */
	public static class WriterChainJsonSingle extends WriterChainJson<WriterChainJsonSingle> {
		private WriterChainJsonSingle(VCard vcard) {
			super(Arrays.asList(vcard));
		}

		@Override
		public WriterChainJsonSingle prodId(boolean include) {
			return super.prodId(include);
		}

		@Override
		public WriterChainJsonSingle indent(boolean indent) {
			return super.indent(indent);
		}

		@Override
		public WriterChainJsonSingle versionStrict(boolean versionStrict) {
			return super.versionStrict(versionStrict);
		}
	}

	private Ezvcard() {
		//hide
	}
}
