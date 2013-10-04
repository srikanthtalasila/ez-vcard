package ezvcard.io;

import java.io.IOException;
import java.io.Writer;

import ezvcard.util.org.apache.commons.codec.EncoderException;
import ezvcard.util.org.apache.commons.codec.net.QuotedPrintableCodec;

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
 * Automatically folds lines as they are written.
 * @author Michael Angstadt
 */
public class FoldedLineWriter extends Writer {
	private final Writer writer;
	private int curLineLength = 0;
	private Integer lineLength;
	private String indent;
	private String newline;

	/**
	 * Creates a folded line writer.
	 * @param writer the writer object to wrap
	 * @param lineLength the maximum length a line can be before it is folded
	 * (excluding the newline), or null disable folding
	 * @param indent the string to prepend to each folded line (e.g. a single
	 * space character)
	 * @param newline the newline sequence to use (e.g. "\r\n")
	 * @throws IllegalArgumentException if the line length is less than or equal
	 * to zero
	 * @throws IllegalArgumentException if the length of the indent string is
	 * greater than the max line length
	 */
	public FoldedLineWriter(Writer writer, Integer lineLength, String indent, String newline) {
		this.writer = writer;
		setLineLength(lineLength);
		setIndent(indent);
		this.newline = newline;
	}

	/**
	 * Writes a string, followed by a newline.
	 * @param str the text to write
	 * @throws IOException if there's a problem writing to the output stream
	 */
	public void writeln(String str) throws IOException {
		write(str);
		write(newline);
	}

	/**
	 * Writes a string.
	 * @param str the string to write
	 * @param quotedPrintable true if the string has been encoded in
	 * quoted-printable encoding, false if not
	 * @return this
	 * @throws IOException if there's a problem writing to the output stream
	 */
	public FoldedLineWriter append(CharSequence str, boolean quotedPrintable) throws IOException {
		write(str, quotedPrintable);
		return this;
	}

	/**
	 * Writes a string.
	 * @param str the string to write
	 * @param quotedPrintable true if the string has been encoded in
	 * quoted-printable encoding, false if not
	 * @throws IOException if there's a problem writing to the output stream
	 */
	public void write(CharSequence str, boolean quotedPrintable) throws IOException {
		write(str.toString().toCharArray(), 0, str.length(), quotedPrintable);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		write(cbuf, off, len, false);
	}

	/**
	 * Writes a portion of an array of characters.
	 * @param cbuf the array of characters
	 * @param off the offset from which to start writing characters
	 * @param len the number of characters to write
	 * @param quotedPrintable true to convert the string to "quoted-printable"
	 * encoding, false not to
	 * @throws IOException if there's a problem writing to the output stream
	 */
	public void write(char[] cbuf, int off, int len, boolean quotedPrintable) throws IOException {
		//encode to quoted-printable
		if (quotedPrintable) {
			QuotedPrintableCodec codec = new QuotedPrintableCodec();
			try {
				cbuf = codec.encode(new String(cbuf, off, len)).toCharArray();
				off = 0;
				len = cbuf.length;
			} catch (EncoderException e) {
				//only thrown if an unsupported charset is passed into the codec
				throw new RuntimeException(e);
			}
		}

		if (lineLength == null) {
			//if line folding is disabled, then write directly to the Writer
			writer.write(cbuf, off, len);
			return;
		}

		int effectiveLineLength = lineLength;
		if (quotedPrintable) {
			//"=" must be appended onto each line
			effectiveLineLength -= 1;
		}

		int encodedCharPos = -1;
		int start = off;
		int end = off + len;
		for (int i = start; i < end; i++) {
			char c = cbuf[i];

			//keep track of the quoted-printable characters to prevent them from being cut in two at a folding boundary
			if (encodedCharPos >= 0) {
				encodedCharPos++;
				if (encodedCharPos == 3) {
					encodedCharPos = -1;
				}
			}

			if (c == '\n') {
				writer.write(cbuf, start, i - start + 1);
				curLineLength = 0;
				start = i + 1;
				continue;
			}

			if (c == '\r') {
				if (i == end - 1 || cbuf[i + 1] != '\n') {
					writer.write(cbuf, start, i - start + 1);
					curLineLength = 0;
					start = i + 1;
				} else {
					curLineLength++;
				}
				continue;
			}

			if (c == '=' && quotedPrintable) {
				encodedCharPos = 0;
			}

			if (curLineLength >= effectiveLineLength) {
				//if the last characters on the line are whitespace, then exceed the max line length in order to include the whitespace on the same line
				//otherwise it will be lost because it will merge with the padding on the next line
				if (Character.isWhitespace(c)) {
					while (Character.isWhitespace(c) && i < end - 1) {
						i++;
						c = cbuf[i];
					}
					if (i >= end - 1) {
						//the rest of the char array is whitespace, so leave the loop
						break;
					}
				}

				//if we are in the middle of a quoted-printable encoded char, then exceed the max line length in order to print out the rest of the char
				if (encodedCharPos > 0) {
					i += 3 - encodedCharPos;
					if (i >= end - 1) {
						//the rest of the char array was an encoded char, so leave the loop
						break;
					}
				}

				writer.write(cbuf, start, i - start);
				if (quotedPrintable) {
					writer.write('=');
				}
				writer.write(newline);
				writer.write(indent);
				curLineLength = indent.length() + 1;
				start = i;

				continue;
			}

			curLineLength++;
		}

		writer.write(cbuf, start, end - start);
	}

	/**
	 * Closes the writer.
	 */
	@Override
	public void close() throws IOException {
		writer.close();
	}

	/**
	 * Flushes the writer.
	 */
	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	/**
	 * Gets the maximum length a line can be before it is folded (excluding the
	 * newline).
	 * @return the line length or null if folding is disabled
	 */
	public Integer getLineLength() {
		return lineLength;
	}

	/**
	 * Sets the maximum length a line can be before it is folded (excluding the
	 * newline).
	 * @param lineLength the line length or null to disable folding
	 * @throws IllegalArgumentException if the line length is less than or equal
	 * to zero
	 */
	public void setLineLength(Integer lineLength) {
		if (lineLength != null && lineLength <= 0) {
			throw new IllegalArgumentException("Line length must be greater than 0.");
		}
		this.lineLength = lineLength;
	}

	/**
	 * Gets the string that is prepended to each folded line.
	 * @return the indent string
	 */
	public String getIndent() {
		return indent;
	}

	/**
	 * Sets the string that is prepended to each folded line.
	 * @param indent the indent string (e.g. a single space character)
	 * @throws IllegalArgumentException if the length of the indent string is
	 * greater than the max line length
	 */
	public void setIndent(String indent) {
		if (lineLength != null && indent.length() >= lineLength) {
			throw new IllegalArgumentException("The length of the indent string must be less than the max line length.");
		}
		this.indent = indent;
	}

	/**
	 * Gets the newline sequence that is used to separate lines.
	 * @return the newline sequence
	 */
	public String getNewline() {
		return newline;
	}

	/**
	 * Sets the newline sequence that is used to separate lines
	 * @param newline the newline sequence
	 */
	public void setNewline(String newline) {
		this.newline = newline;
	}

	/**
	 * Gets the wrapped {@link Writer} object.
	 * @return the wrapped writer
	 */
	public Writer getWriter() {
		return writer;
	}
}
