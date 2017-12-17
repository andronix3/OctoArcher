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

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

import com.imagero.java.awt.ImageTracker;

/**
 * Sample implementation of ResourceReader.
 * Read image embedded as Resource in Java class file.
 *
 * @see Resource
 *
 * @author Andrei Kouznetsov
 * <br>
 * Date: 05.08.2004
 * Time: 14:49:15
 */
public class ImageResourceReader implements ResourceReader<Image> {
    final static String types [] = {"jpg", "gif", "bmp", "png", /*"tiff", "tif", "mng", "jng",*/ "jpeg"};


    private static ImageTracker tracker = new ImageTracker();

    public String[] getSupportedTypes() {
        return types;
    }

    public String getProviderString() {
        return "imagero.com";
    }

    public Image create(Resource data) {
        if (!checkType(data.getType())) {
            throw new IllegalArgumentException("wrong res type: " + data.getType());
        }

        byte[] b = null;

        try {
            InputStream in = data.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int read = in.read(buffer);
            while (read > 0) {
                bout.write(buffer, 0, read);
                read = in.read(buffer);
            }
            b = bout.toByteArray();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        if (b != null) {
            Image image = Toolkit.getDefaultToolkit().createImage(b);
            new ImageIcon(image);
            return image;
        }
        return null;
    }

    protected void loadImage(Image image) {
    	tracker.loadImage(image, 0);
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
