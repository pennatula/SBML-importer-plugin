package org.pathvisio.sbml;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

public class ValidatePanel extends JPanel implements ActionListener {
	
	JFileChooser fc;
	JButton openButton;
	JButton validateButton;
	static JTextArea textArea;
	final JLabel statusbar = new JLabel(
			"Output of your selection will appear here", SwingConstants.RIGHT);
	static String filename;

	public ValidatePanel() {

		super(new BorderLayout());
		// create a file chooser
		fc = new JFileChooser();
		// filtering the files based on their extensions
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"SBML(Systems Biology Markup Language) (.sbml,.xml)", "sbml",
				"xml");
		fc.setFileFilter(filter);
		textArea = new JTextArea();
		//textArea.setEnabled(false);
		JScrollPane areaPane = new JScrollPane(textArea);
		areaPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaPane.setPreferredSize(new Dimension(250, 250));
		openButton = new JButton("Open");
		validateButton = new JButton("Validate the file");
		openButton.addActionListener(this);
		validateButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		buttonPanel.setLayout(gridbag);
		c.gridwidth = GridBagConstraints.REMAINDER; // last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		buttonPanel.add(statusbar, c);
		buttonPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Button Pane"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		buttonPanel.add(openButton);
		buttonPanel.add(validateButton);
		buttonPanel.add(statusbar);

		add(buttonPanel, BorderLayout.CENTER);

		statusbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		JPanel outputPanel = new JPanel();
		GridBagLayout gridbag1 = new GridBagLayout();
		GridBagConstraints c1 = new GridBagConstraints();
		outputPanel.setLayout(gridbag1);
		c1.gridwidth = GridBagConstraints.REMAINDER; // last
		c1.anchor = GridBagConstraints.WEST;
		c1.weightx = 1.0;
		outputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Output Pane"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		outputPanel.add(textArea);
		add(outputPanel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == openButton) {

			int returnVal = fc.showOpenDialog(ValidatePanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				filename = file.getPath();

				statusbar.setText("You chose" + " " + file.getName());
			} else {

				statusbar.setText("You cancelled.");
			}
		}

		else if (e.getSource() == validateButton) {

			validate();
		}

	}

	public void validate() {

		String selectFile = ValidatePanel.filename;

		System.out.println("the file is " + selectFile);
		SBMLReader reader = new SBMLReader();
		SBMLDocument document = null;
		long start, stop;

		start = System.currentTimeMillis();
		try {
			document = reader.readSBML(selectFile);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stop = System.currentTimeMillis();

		if (document.getErrorCount() > 0) {
			print("Encountered the following errors while reading the SBML file:\n");
			document.printErrors(System.out);
			print("\nFurther consistency checking and validation aborted.\n");
			System.exit(1);
		} else {
			long errors = document.checkConsistency();
			long s = document.getErrorLog().getErrorCount();
			System.out.println(s);
			long size = new File(selectFile).length();

			println("            filename: " + selectFile);
			println("           file size: " + size);
			println("      read time (ms): " + (stop - start));
			println(" validation error(s): " + errors);

			if (errors > 0) {
				document.printErrors(System.out);
				System.exit(1);
			}
		}
	}

	static void print(String msg) {
		System.out.println(msg);
	}

	static void println(String msg) {
		System.out.println(msg);
	}
}
