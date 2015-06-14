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

import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;

public class ICC_ProfileResourceReader implements ResourceReader<ICC_Profile> {
    static final String[] types = { "pf" };

    public ICC_Profile create(Resource data) {
	if (!checkType(data.getType())) {
	    throw new IllegalArgumentException("Unsupported resource type: " + data.getType());
	}
	InputStream in = data.getInputStream();
	ICC_Profile profile = null;
	try {
	    profile = ICC_Profile.getInstance(in);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return profile;
    }

    public String getProviderString() {
	return "imagero.com";
    }

    public String[] getSupportedTypes() {
	return types;
    }

    boolean checkType(String type) {
	String[] types = getSupportedTypes();
	for (int i = 0; i < types.length; i++) {
	    if (types[i].equalsIgnoreCase(type)) {
		return true;
	    }
	}
	return false;
    }
}
