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

import com.smartg.java.util.HashBag;

/**
 * ResourceReaderFactory.
 * ResourceReader should register itself by ResourceReaderFactory.
 *
 * @author Andrei Kouznetsov
 * <br>
 *         Date: 15.07.2004
 *         Time: 14:25:50
 */
public class ResourceReaderFactory {
    private HashBag<String, ResourceReader<?>> decoders = new HashBag<String, ResourceReader<?>>();

    public static final ResourceReaderFactory factory = new ResourceReaderFactory();

    private ResourceReaderFactory() {

    }

    /**
     * Register ResourceReader
     * @param p ResourceReader
     * @param type String (resource type)
     */
    public void addProvider(ResourceReader<?> p, String type) {
        decoders.put(type, p);
    }

    /**
     * get array of ResourceReader(s) for given resource type
     * @param type String (resource type)
     * @return ResourceReader array
     */
    public static ResourceReader<?>[] list(String type) {
        int count = factory.decoders.getCount(type);
        ResourceReader<?>[] pps = new ResourceReader[count];
        for(int i = 0; i < pps.length; i++) {
            pps[i] = factory.decoders.get(type, i);
        }
        return pps;
    }

    /**
     * Determine how much ResourceReaders was registered for given resource type
     * @param type String (resource type)
     * @return int
     */
    public static int getProviderCount(String type) {
        return factory.decoders.getCount(type);
    }

    /**
     * get ResourceReader for given resource type at given index
     * @param type String (resource type)
     * @param index index of ResourceReader
     * @return ResourceReader
     */
    public static ResourceReader<?> get(String type, int index) {
        return factory.decoders.get(type, index);
    }

    /**
     * Deregister ResourceReader from factory
     * @param type String (resource type)
     * @param index index of ResourceReader
     * @return ResourceReader
     */
    public static ResourceReader<?> removeProvider(String type, int index) {
        return factory.decoders.remove(type, index);
    }

    static {
        ImageResourceReader p = new ImageResourceReader();
        for(int i = 0; i < ImageResourceReader.types.length; i++) {
            factory.addProvider(p, ImageResourceReader.types[i]);
        }
        ICC_ProfileResourceReader icc = new ICC_ProfileResourceReader();
        for(int i = 0; i < ICC_ProfileResourceReader.types.length; i++) {
            factory.addProvider(icc, ICC_ProfileResourceReader.types[i]);
        }
        PrimitiveResourceReader pri = new PrimitiveResourceReader();
        for(int i = 0; i < PrimitiveResourceReader.types.length; i++) {
            factory.addProvider(pri, PrimitiveResourceReader.types[i]);
        }
    }
}
