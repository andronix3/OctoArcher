/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * http://fresc.imagero.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.smartg.res;

import java.io.Writer;
import java.io.IOException;

/**
 * This class allows to filter character stream and write also something before and after of current buffer content.
 * for example we want to write long java string:
 * someting like:
 * protected String s = "aaaaaaabbbbbbbbbccccccccc"
 *      + "ddddddddddeeeeeeeeeeeefffffffffgggggggg"
 *      + "hhhhhhhhhhjjjjjjjjjjjjkkkkkkkkkllllllll";
 *
 * <pre>
 * we implement prepend() and append() as follow:
 * class BFWriter extends BufferedFilterWriter {
 *      protected void prepend(int cnt) {
 *          if(cnt == 0) {
 *              out.write("protected String s = \"");
 *          }
 *          else {
 *              out.write("+ \"");
 *          }
 *      }
 *
 *      protected void append(int cnt) {
 *          if(buf.length == size) {
 *              out.write("\"\n");
 *          }
 *          else {
 *              out.write("\";\n");
 *          }
 *      }
 * }
 * </pre>
 * @author Andrey Kuznetsov
 */
public abstract class BufferedFilterWriter extends Writer {
    protected Writer out;

    private char[] buf;
    int size;
    int p;

    /**
     * Create a new BWriter, with given buffer size.
     *
     * @param size  the size of the buffer.
     */
    public BufferedFilterWriter(int size, Writer out) {
        if (size < 1) {
            throw new IllegalArgumentException("Buffer size can't be lesser as 1");
        }
        this.out = out;
        buf = new char[size];
        this.size = size;
        lock = buf;
    }

    /**
     * Write a single character.
     */
    public void write(int c) throws IOException {
        final char c0 = (char) c;
        append0(c0);
    }

    int cnt = 0;

    void append0(char c0) throws IOException {
        buf[p++] = c0;
        if (p == buf.length) {
            p = 0;
            prepend(cnt);
            out.write(buf);
            append(cnt);
            cnt++;
        }
    }

    public abstract void prepend(int cnt) throws IOException;

    public abstract void append(int cnt) throws IOException;

    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Array of characters
     * @param  off   Offset from which to start writing characters
     * @param  len   Number of characters to write
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        else if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            append0(cbuf[i + off]);
        }
    }

    /**
     * Write a string.
     */
    public void write(String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            append0(str.charAt(i));
        }
    }

    /**
     * Write a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start writing characters
     * @param  len  Number of characters to write
     */
    public void write(String str, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            append0(str.charAt(i + off));
        }
    }

    /**
     * Return the buffer's current value as a string.
     */
    public String toString() {
        return buf.toString();
    }

    public void flush() throws IOException {
        if(p > 0) {
            prepend(cnt);
            out.write(buf, 0, p);
            append(cnt);
            cnt++;
        }
    }

    public void close() throws IOException {
        flush();
    }
}
