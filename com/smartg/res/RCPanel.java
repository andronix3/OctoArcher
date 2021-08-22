package com.smartg.res;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

@SuppressWarnings("serial")
public class RCPanel extends JPanel {

	private final JTextField directoryField = new JTextField(20);
	private final JTextField packageField = new JTextField(20);
	private final JTextField defTypeField = new JTextField(10);

	private final JButton directoryButton = new JButton("...");

	private final JButton goButton = new JButton("Start");
	private final JButton closeButton = new JButton("Close");

	private final ObjectProperty<Function<Window, Window>> cbHandler = new SimpleObjectProperty<>();
	private Map<String, List<String>> packageMap;


	public RCPanel() {
		cbHandler.set(new Function<Window, Window>() {
			@Override
			public Window apply(Window w) {
				return w;
			}
		});

		JLabel destDirLabel = new JLabel("Destination Directory:");
		JLabel srcPkgLabel = new JLabel("Source Package:");
		JLabel defTypeLabel = new JLabel("Def. Type:");
		
		srcPkgLabel.setPreferredSize(destDirLabel.getPreferredSize());
		srcPkgLabel.setHorizontalAlignment(JTextField.RIGHT);

		defTypeLabel.setPreferredSize(destDirLabel.getPreferredSize());
		defTypeLabel.setHorizontalAlignment(JTextField.RIGHT);

		directoryField.setEditable(false);

		setLayout(new BorderLayout());
		Box main = Box.createVerticalBox();
		
		main.add(Box.createVerticalStrut(10));

		Box directoryPanel = Box.createHorizontalBox();
		directoryPanel.add(Box.createHorizontalStrut(10));
		directoryPanel.add(destDirLabel);
		directoryPanel.add(Box.createHorizontalStrut(10));
		directoryPanel.add(directoryField);
		directoryPanel.add(directoryButton);
		directoryPanel.add(Box.createHorizontalStrut(10));
		
		main.add(directoryPanel);
		main.add(Box.createVerticalStrut(10));

		Box packagePanel = Box.createHorizontalBox();
		packagePanel.add(Box.createHorizontalStrut(10));
		packagePanel.add(srcPkgLabel);
		packagePanel.add(Box.createHorizontalStrut(10));
		packagePanel.add(packageField);
		packagePanel.add(Box.createRigidArea(directoryButton.getPreferredSize()));
		packagePanel.add(Box.createHorizontalStrut(10));
		
		main.add(packagePanel);
		main.add(Box.createVerticalStrut(10));
		
		Box defTypeBox = Box.createHorizontalBox();
		defTypeBox.add(Box.createHorizontalStrut(10));
		defTypeBox.add(defTypeLabel);
		defTypeBox.add(Box.createHorizontalStrut(10));
		defTypeBox.add(defTypeField);
		defTypeBox.add(Box.createHorizontalGlue());
		defTypeBox.add(Box.createRigidArea(directoryButton.getPreferredSize()));
		defTypeBox.add(Box.createHorizontalStrut(10));

		main.add(defTypeBox);
		main.add(Box.createVerticalStrut(10));

		Box buttonPanel = Box.createHorizontalBox();
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(goButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(closeButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		main.add(buttonPanel);
		
		main.add(Box.createVerticalStrut(10));
		add(main, BorderLayout.NORTH);

		directoryButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = chooser.showOpenDialog(main);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				directoryField.setText(selectedFile.getAbsolutePath());
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleClose();
			}
		});
		packageMap = createPackageMap();
		
		packageField.getDocument().addUndoableEditListener(e -> {
			String text = packageField.getText();
			
		});
		
		goButton.addActionListener(e-> {
			String[] imports = new String[] { "com.smartg.res.Resource", "com.smartg.res.AbstractResource" };
			String[] interfaces = new String[] { "Resource" };
			String _extends = "AbstractResource";
			ResourceCreator rc = new ResourceCreator(directoryField.getText(), packageField.getText(), imports, interfaces, _extends);

		});
	}
	
	private Map<String, List<String>> createPackageMap() {
		Package[] packages = Package.getPackages();
		List<String> ppList = new ArrayList<>();
		for (Package p : packages) {
			ppList.add(p.getName());
		}
		ppList.sort((o1, o2) -> o1.compareTo(o2));
		Map<String, List<String>> map = new LinkedHashMap<>();
		ppList.forEach(e-> {
			String[] split = e.split("\\.");
			int a = 0;
			while(++a < split.length) {
				StringBuilder kb = new StringBuilder();
				for(int i = 0; i < a; i++) {
					kb.append(split[i]).append(i < a - 1? "." : "");
				}
				String key = kb.toString();
				List<String> list = map.get(key);
				if(list == null) {
					list = new ArrayList<>();
					map.put(key, list);
				}
				StringBuilder sb = new StringBuilder();
				for(int i = a; i < split.length; i++) {
					sb.append(split[i]).append(i < split.length - 1 ? ".": "");
				}
				list.add(sb.toString());
			}
		});
		map.entrySet().forEach(e-> System.out.println(e.getKey() + "::" + e.getValue()));
		return map;
	}

	protected void handleClose() {
		cbHandler.get().apply(SwingUtilities.getWindowAncestor(this));
	}

	public void addCloseButtonHandler(Function<Window, Window> handler) {
		SwingUtilities.invokeLater(() -> {
			Function<Window, Window> andThen = handler.andThen(cbHandler.get());
			cbHandler.set(andThen);
		});
	}

	public static void main(String[] args) {
		RCPanel panel = new RCPanel();
		panel.addCloseButtonHandler(e -> {
			System.exit(0);
			return e;
		});
		JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
