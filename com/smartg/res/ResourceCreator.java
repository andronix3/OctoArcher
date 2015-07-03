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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.imagero.uio.Transformer;
import com.imagero.uio.io.Base64;

/**
 * ResourceCreator. Utility class for embedding some data into Resource
 * (provides very simple GUI).
 */
public class ResourceCreator {

    ZipOutputStream zout;

    String outputRootDir;// = "F:/home/java/src/";
    String destPackage;// = "com.imagero.data2";
    protected String[] imports;
    protected String[] interfaces;
    protected String _extends;
    String[] extensions;

    boolean saveToZip;

    String defaultType;
    String prefix = "";

    /**
     * 
     * @param outputRootDir
     *            output directory
     * @param destPackage
     *            destination package
     * @param imports
     *            imports which should be included
     * @param interfaces
     *            interfaces which should be implemented
     */
    public ResourceCreator(String outputRootDir, String destPackage, String[] imports, String[] interfaces, String _extends) {
	this.outputRootDir = new File(outputRootDir).getAbsolutePath() + "\\";
	this.destPackage = destPackage;
	this.imports = imports;
	this.interfaces = interfaces;
	this._extends = _extends;
    }

    public void openWindow() throws IOException {
	String command = "Explorer " + outputRootDir + destPackage.replace('.', '\\');
	Runtime.getRuntime().exec(command);
    }

    int lineLength = 120;

    /**
     * saves data into java file
     * 
     * @param data
     *            data to embed
     * @param pw
     *            PrintWriter
     * @param name
     *            destination class name
     * @param type
     *            type of Resource
     * @see Resource#getType
     * @param dp
     *            destination package
     */
    protected void saveData(byte[] data, PrintWriter pw, String name, String type, String dp) throws IOException {
	pw.println("package " + dp + ";\n");

	if (imports != null && imports.length > 0) {
	    for (int i = 0; i < imports.length; i++) {
		if (imports[i] != null && imports[i].length() > 0) {
		    pw.println("import " + imports[i] + ";");
		}
	    }
	}

	String implem = "";
	if (interfaces != null && interfaces.length != 0) {
	    for (int i = 0; i < interfaces.length; i++) {
		if (interfaces[i] != null && interfaces[i].length() > 0) {
		    implem += " ";
		    implem += interfaces[i];
		    implem += ",";
		}
	    }
	    if (implem.length() > 0) {
		implem = " implements " + implem.substring(0, implem.length() - 1);
	    }
	}

	String _ext = _extends == null ? "" : " extends " + _extends + " ";
	pw.println("\npublic class " + name + _ext + implem + "{\n");

	pw.println("public String getType() {");
	pw.println("\treturn type;");
	pw.println("}\n");

	pw.println("protected String [] getAsString() {");
	pw.println("\treturn res;");
	pw.println("}\n");

	pw.println("\nprivate String type = \"" + type + "\";");

	pw.flush();

	ByteArrayInputStream bais = new ByteArrayInputStream(data);
	Base64.setLineLength(Integer.MAX_VALUE);
	BufferedFilterWriter writer = new BufferedFilterWriter(lineLength, pw) {
	    public void prepend(int cnt) throws IOException {
		if (cnt == 0) {
		    out.write("\nprivate String [] res = {\n\t\"");
		} else {
		    out.write("\t\"");
		}
	    }

	    public void append(int cnt) throws IOException {
		out.write("\",\n");
	    }
	};
	Base64.base64Encode(bais, writer);
	writer.flush();
	pw.write("};\n");

	pw.println("\n}");
    }

    public static void main(String args[]) throws IOException {
	JFrame frame = new JFrame();
	// JFileChooser fd = new JFileChooser("F:/home/java/src/com/icons");
	JFileChooser fd = new JFileChooser();
	// fd.setCurrentDirectory(new File("C:\\home\\java\\images"));
	// fd.setCurrentDirectory(new File("C:\\home\\java\\images\\2"));
	// fd.setCurrentDirectory(new
	// File("C:\\home\\java\\icons\\jlfgr\\jlfgr\\toolbarButtonGraphics\\media"));
	fd.setCurrentDirectory(new File("C:\\home\\java\\src\\projects\\poker\\processed\\"));
	// fd.setCurrentDirectory(new File("C:\\home\\java\\images"));
	// fd.setCurrentDirectory(new File("C:\\home\\java\\resources\\cmaps"));
	String defType = "png";
	String prefix = "";

	// fd.setCurrentDirectory(new File("C:\\home\\java\\jaicmm\\"));
	// fd.setCurrentDirectory(new
	// File("C:/home/java/src/ekit/com/abc/edit/icons/"));
	fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

	if (fd.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
	    final File selectedFile = fd.getSelectedFile();
	    System.out.println(selectedFile);
	    // String outputRootDir = "C:/home/java/src/edit/";
	    String outputRootDir = "C:\\home\\java\\src\\projects\\poker\\src\\";
	    // String outputRootDir = "C:\\home\\java\\src\\projects\\cmaps\\";
	    // String outputRootDir =
	    // "C:\\home\\java\\src\\projects\\commons\\icons\\";
	    // String outputRootDir = "C:/home/java/src/cmm/";
	    // String outputRootDir = "C:/home/java/alacarte/src/";
	    // String outputRootDir = "C:/home/java/src/jgui/src/";
	    // String outputRootDir = "C:/home/java/src/";
	    // String outputRootDir = "C:/maria/k0/";

	    // ResourceCreator rc = new ResourceCreator(outputRootDir,
	    // "gtest.i24", new String[]{"com.imagero.res.Resource",
	    // "com.imagero.res.AbstractResource"}, new String[]{"Resource"},
	    // "AbstractResource");
	    // ResourceCreator rc = new ResourceCreator(outputRootDir,
	    // "com.imagero.swing.plaf.images", new
	    // String[]{"com.imagero.res.Resource",
	    // "com.imagero.res.AbstractResource"}, new String[]{"Resource"},
	    // "AbstractResource");
	    // ResourceCreator rc = new ResourceCreator(outputRootDir,
	    // "com.imagero.images", new String[]{"com.imagero.res.Resource",
	    // "com.imagero.res.AbstractResource"}, new String[]{"Resource"},
	    // "AbstractResource");
	    // ResourceCreator rc = new ResourceCreator(outputRootDir,
	    // "com.imagero.icons", new String[]{"com.imagero.res.Resource",
	    // "com.imagero.res.AbstractResource"}, new String[]{"Resource"},
	    // "AbstractResource");
	    
	    String destPackage = "com.imagero.poker.icons";
	    String[] imports = new String[] { "com.smartg.res.Resource", "com.smartg.res.AbstractResource" };
	    String[] interfaces = new String[] { "Resource" };
	    String _extends = "AbstractResource";
	    ResourceCreator rc = new ResourceCreator(outputRootDir, destPackage, imports, interfaces, _extends);

	    rc.defaultType = defType;
	    rc.prefix = prefix;
	    // rc.setExtensions(new String[]{"gif"});
	    rc.processFile(selectedFile, true);
	}
	System.exit(0);
    }

    public File processFile(final File selectedFile, boolean recursive) throws IOException {
	final String ext = getExtension(selectedFile.getName());
	if (ext.equalsIgnoreCase("zip") || ext.equalsIgnoreCase("jar")) {
	    final ZipFile zip = new ZipFile(selectedFile);
	    File f = processZIP(zip, this.outputRootDir, this.destPackage);
	    zip.close();
	    return f;
	}
	return processDirectory(selectedFile, this.outputRootDir, this.destPackage, recursive);
    }

    private File processDirectory(final File selectedFile, String rootDir, String dp, boolean recursive) throws IOException {
	File rd = new File(rootDir + dp.replace('.', '/'));
	rd.mkdirs();
	System.out.println("root:" + rd);
	if (!selectedFile.isDirectory()) {
	    if (checkExtension(getExtension(selectedFile.getName()))) {
		return processFile(selectedFile, selectedFile.getName(), rootDir, dp);
	    }
	} else {
	    String[] fns = selectedFile.list();
	    for (int i = 0; i < fns.length; i++) {
		File f = new File(selectedFile, fns[i]);
		if (f.isDirectory() && recursive) {
		    System.out.println("directory:" + f.getName());
		    processDirectory(f, rootDir, dp + "." + f.getName(), recursive);
		} else {
		    if (checkExtension(getExtension(fns[i]))) {
			processFile(f, fns[i], rootDir, dp);
		    }
		}
	    }
	}
	return selectedFile;
    }

    /**
     * get file extension.
     * 
     * @param s
     *            filename
     * @return file extension or empty String
     */
    protected String getExtension(String s) {
	int i = s.lastIndexOf(".");
	if (i < 0) {
	    return "";
	}
	return s.substring(i + 1);
    }

    /**
     * define number of file types which should be processed
     * 
     * @param exts
     *            String array - supply empty array or null to process all files
     */
    public void setExtensions(String[] exts) {
	this.extensions = exts;
    }

    /**
     * Check if file with given extension should be processed
     * 
     * @param ext
     *            extension to check
     * @return true if file should be processed
     */
    protected boolean checkExtension(String ext) {
	if (extensions == null || extensions.length == 0) {
	    return true;
	}
	for (int i = 0; i < extensions.length; i++) {
	    String ext0 = extensions[i];
	    if (ext.equalsIgnoreCase(ext0)) {
		return true;
	    }
	}
	return false;
    }

    protected File processFile(File file, String filename, String rootDir, String dp) throws IOException {
	if (!file.exists()) {
	    // System.out.println("File not exist:" + file.getAbsolutePath());
	    return null;
	}
	byte[] b = new byte[(int) file.length()];
	DataInputStream in = new DataInputStream(new FileInputStream(file));
	in.readFully(b);
	in.close();

	return saveToFile(rootDir, filename, dp, b);
    }

    protected File processZIP(ZipFile zip, String rootDir, String dp) throws IOException {
	// System.out.println(zip.getName());
	Enumeration<? extends ZipEntry> entries = zip.entries();
	File f = null;
	while (entries.hasMoreElements()) {
	    ZipEntry entry = entries.nextElement();
	    if (entry.isDirectory()) {
		continue;
	    }
	    final String filename = entry.getName();
	    // System.out.println(filename);
	    long size = entry.getSize();
	    byte[] buf;
	    DataInputStream in = new DataInputStream(zip.getInputStream(entry));

	    if (size != -1 && size < Integer.MAX_VALUE) {
		buf = new byte[(int) size];
		in.readFully(buf);
		in.close();
	    } else {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while (true) {
		    int b = in.read();
		    if (b == -1) {
			break;
		    }
		    out.write(b);
		}
		buf = out.toByteArray();
	    }
	    if (saveToZip) {
		saveToZip(zout, filename, dp, buf);
	    } else {
		f = saveToFile(rootDir, filename, dp, buf);
	    }
	}
	return f;
    }

    protected File saveToFile(String rootDir, String filename, String destPackage, byte[] buf) {
	String output = rootDir + destPackage.replace('.', '\\');
	filename = filename.replace('-', '_');
	if (prefix != null) {
	    filename = prefix + filename;
	}
	int extStart = filename.lastIndexOf('.');
	int k = 0;
	if (extStart > 0) {
	    k = filename.length() - extStart;
	}
	String fn = filename.substring(0, 1).toUpperCase() + filename.substring(1, filename.length() - k) + ".java";

	File newfile = new File(output, fn);
	System.out.println(newfile.getAbsolutePath());
	new File(output).mkdirs();

	try {
	    FileWriter fw = new FileWriter(newfile);
	    PrintWriter pw = new PrintWriter(fw);
	    final String name = fn.substring(0, filename.length() - k);
	    String type = defaultType;
	    int beginIndex = filename.lastIndexOf(".") + 1;
	    if (beginIndex > 0) {
		type = filename.substring(beginIndex).toLowerCase();
	    }
	    saveData(buf, pw, name, type, destPackage);
	    pw.flush();
	    fw.close();
	    // System.out.println("written: " + newfile);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	return newfile;
    }

    public void save(String filename, byte[] buf) {
	save(filename, "B[", buf);
    }

    public void save(String filename, short[] buf) {
	int lenght = buf.length;
	byte[] dest = new byte[lenght * 2];
	Transformer.shortToByte(buf, 0, lenght, dest, 0, true);
	save(filename, "S[", dest);
    }

    public void save(String filename, int[] buf) {
	int lenght = buf.length;
	byte[] dest = new byte[lenght * 4];
	Transformer.intToByte(buf, 0, lenght, dest, 0, true);
	save(filename, "I[", dest);
    }

    public void save(String filename, long[] buf) {
	int lenght = buf.length;
	byte[] dest = new byte[lenght * 8];
	Transformer.longToByte(buf, 0, lenght, dest, 0, true);
	save(filename, "L[", dest);
    }

    public void save(String filename, float[] buf) {
	int lenght = buf.length;
	byte[] dest = new byte[lenght * 4];
	Transformer.floatToByte(buf, 0, lenght, dest, 0, true);
	save(filename, "F[", dest);
    }

    public void save(String filename, double[] buf) {
	int lenght = buf.length;
	byte[] dest = new byte[lenght * 8];
	Transformer.doubleToByte(buf, 0, lenght, dest, 0, true);
	save(filename, "D[", dest);
    }

    protected void save(String filename, String type, byte[] buf) {
	String output = outputRootDir + destPackage.replace('.', '\\');
	int extStart = filename.lastIndexOf('.');
	int k = 0;
	if (extStart > 0) {
	    k = filename.length() - extStart;
	}
	String fn = filename.substring(0, 1).toUpperCase() + filename.substring(1, filename.length() - k) + ".java";

	System.out.println(fn);
	File newfile = new File(output, fn);
	new File(output).mkdirs();

	try {
	    FileWriter fw = new FileWriter(newfile);
	    PrintWriter pw = new PrintWriter(fw);
	    final String name = fn.substring(0, filename.length() - k);
	    saveData(buf, pw, name, type, destPackage);
	    pw.flush();
	    fw.close();
	    // System.out.println("written: " + newfile);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    // private void createZip(File f) throws IOException {
    // final String ext = getExtension(f.getName());
    // if (ext.equalsIgnoreCase("ZIP")) {
    // zout = new ZipOutputStream(new BufferedOutputStream(new
    // FileOutputStream(f)));
    // }
    // else if (ext.equalsIgnoreCase("JAR")) {
    // zout = new JarOutputStream(new BufferedOutputStream(new
    // FileOutputStream(f)));
    // }
    // else {
    // throw new IOException("Unknown extension:" + ext);
    // }
    // }

    protected void saveToZip(ZipOutputStream out, String filename, String destPackage, byte[] buf) {
	String output = destPackage.replace('.', '/');
	String fn = filename.substring(0, filename.length() - 3) + "java";
	try {
	    // ZipEntry newfile = new ZipEntry(output + "/" + fn);
	    ZipEntry entry = new ZipEntry(output + "/" + fn);
	    out.putNextEntry(entry);

	    // FileWriter fw = new FileWriter(newfile);
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    PrintWriter pw = new PrintWriter(new OutputStreamWriter(bout));
	    final String name = filename.substring(0, filename.length() - 4);
	    final String type = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
	    saveData(buf, pw, name, type, destPackage);
	    pw.close();
	    out.write(bout.toByteArray());
	    out.closeEntry();

	    // System.out.println("written: " + newfile);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
