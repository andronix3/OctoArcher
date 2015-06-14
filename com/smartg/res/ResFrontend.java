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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeUtils.GridHelper;

public class ResFrontend extends JPanel {

    private static final long serialVersionUID = 899925615247278681L;

    private String[] imports = new String[] { "com.smartg.res.Resource", "com.smartg.res.AbstractResource" };
    private String[] interfaces = new String[] { "Resource" };
    private String _extends = "AbstractResource";

    private JNodeLayout layout;

    private ArrayList<JTextField> textFields = new ArrayList<JTextField>();
    // ResourceCreator rc = new ResourceCreator(outputRootDir, destPackage,
    // imports, interfaces, _extends);

    private JFileChooser fd = new JFileChooser();

    private JLabel destPackageLabel = new JLabel("Package Name", SwingConstants.RIGHT);
    private JTextField destPackageText = createTextField(25);

    private JLabel outputDirectoryLabel = new JLabel("Output Directory", SwingConstants.RIGHT);
    private JTextField outputDirectoryText = createTextField(25);
    private JButton outputDirectoryButton = new SlimButton("...");

    private JLabel sourceDirectoryLabel = new JLabel("Source", SwingConstants.RIGHT);
    private JTextField sourceDirectoryText = createTextField(25);
    private JButton sourceDirectoryButton = new SlimButton("...");

    private JLabel defaultTypeLabel = new JLabel("Default Type", SwingConstants.RIGHT);
    private JTextField defaultTypeText = createTextField(5);

    private JCheckBox recursive = new JCheckBox("Recursive");

    private JButton go = new JButton("Go");

    public ResFrontend() {

	destPackageText.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		checkButton();
	    }
	});

	outputDirectoryButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fd.showOpenDialog(ResFrontend.this) == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fd.getSelectedFile();
		    String path = selectedFile.getAbsolutePath();
		    outputDirectoryText.setText(path);
		}
		checkButton();
	    }
	});

	outputDirectoryText.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		checkButton();
	    }
	});

	sourceDirectoryButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (fd.showOpenDialog(ResFrontend.this) == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fd.getSelectedFile();
		    String path = selectedFile.getAbsolutePath();
		    sourceDirectoryText.setText(path);
		}
		checkButton();
	    }
	});

	go.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		ResourceCreator rc = new ResourceCreator(outputDirectoryText.getText(), destPackageText.getText(), imports, interfaces, _extends);
		rc.defaultType = defaultTypeText.getText();
		rc.prefix = "";
		try {
		    rc.processFile(new File(sourceDirectoryText.getText()), recursive.isSelected());
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }
	});

	setBorder(new EmptyBorder(10, 10, 10, 10));

	LayoutNode.GridNode root = new LayoutNode.GridNode("root");
	root.setHorizontalAlignment(NodeAlignment.CENTER);
	root.setVerticalAlignment(NodeAlignment.CENTER);
	root.setHgap(5);
	root.setVgap(5);

	layout = new JNodeLayout(root);

	setLayout(layout);

	GridHelper gridHelper = new GridHelper(this, "root", 9);

	gridHelper.add(destPackageLabel, 2);
	gridHelper.add(destPackageText, 5);
	gridHelper.add(recursive, 2);

	gridHelper.add(outputDirectoryLabel, 2);
	gridHelper.add(outputDirectoryText, 6);
	gridHelper.add(outputDirectoryButton, 1);

	gridHelper.add(sourceDirectoryLabel, 2);
	gridHelper.add(sourceDirectoryText, 6);
	gridHelper.add(sourceDirectoryButton, 1);

	gridHelper.add(defaultTypeLabel, 2);
	gridHelper.add(defaultTypeText, 5);

	gridHelper.add(go, 2);

	checkButton();
    }

    private JTextField createTextField(int columnCount) {
	JTextField tf = new JTextField(columnCount);
	textFields.add(tf);
	return tf;
    }

    private void checkButton() {
	for (JTextField tf : textFields) {
	    if (tf.getText().isEmpty()) {
		go.setEnabled(false);
		return;
	    }
	}
	go.setEnabled(true);
    }

    /**
     * JButton without left and right margins.
     * 
     * @author andrey
     *
     */
    public static class SlimButton extends JButton {

	private static final long serialVersionUID = 6877552516359145151L;

	public SlimButton() {
	}

	public SlimButton(Icon icon) {
	    super(icon);
	}

	public SlimButton(String text) {
	    super(text);
	}

	public SlimButton(Action a) {
	    super(a);
	}

	public SlimButton(String text, Icon icon) {
	    super(text, icon);
	}

	public void setMargin(Insets m) {
	    m.left = 0;
	    m.right = 0;
	    super.setMargin(m);
	}
    }

    public static void main(String... s) {
	JFrame frame = new JFrame("Save resource as java source file");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.add(new ResFrontend());
	frame.pack();
	frame.setVisible(true);
    }
}