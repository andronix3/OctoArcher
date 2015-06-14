/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.imagero.uio.Transformer;
import com.imagero.uio.io.IOutils;

public class PrimitiveResourceReader implements ResourceReader<Object> {

    final static String types[] = { "I[", "S[", "B[", "L[", "F[", "D[" };

    public Object create(Resource data) {
	String type = data.getType();
	if (!checkType(type)) {
	    throw new IllegalArgumentException("Unsupported resource type: " + data.getType());
	}
	InputStream in = data.getInputStream();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
	    IOutils.copy(in, out);
	    byte[] bdata = out.toByteArray();
	    char c = type.charAt(0);
	    switch (c) {
	    case 'I':
		int[] idata = new int[bdata.length / 4];
		Transformer.byteToInt(bdata, 0, idata.length, idata, 0, true);
		return idata;
	    case 'S':
		short[] sdata = new short[bdata.length / 2];
		Transformer.byteToShort(bdata, 0, sdata.length, sdata, 0, true);
		return sdata;
	    case 'B':
		return bdata;
	    case 'L':
		long[] ldata = new long[bdata.length / 8];
		Transformer.byteToLong(bdata, 0, ldata.length, ldata, 0, true);
		return ldata;
	    case 'F':
		float[] fdata = new float[bdata.length / 4];
		Transformer.byteToFloat(bdata, 0, fdata.length, fdata, 0, true);
		return fdata;
	    case 'D':
		double[] ddata = new double[bdata.length / 8];
		Transformer.byteToDouble(bdata, 0, ddata.length, ddata, 0, true);
		return ddata;
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	return null;
    }

    public String getProviderString() {
	return "Imagero.com";
    }

    public String[] getSupportedTypes() {
	return types;
    }

    boolean checkType(String type) {
	for (int i = 0; i < types.length; i++) {
	    if (types[i].equalsIgnoreCase(type)) {
		return true;
	    }
	}
	return false;
    }
}
