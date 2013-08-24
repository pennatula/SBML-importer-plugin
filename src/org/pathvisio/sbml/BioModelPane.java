// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2013 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.sbml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.ProgressDialog;

import uk.ac.ebi.biomodels.ws.BioModelsWSClient;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Biomodels side panel
 * @author anwesha
 */
public class BioModelPane extends JPanel implements ActionListener {
	
	SBMLPlugin plugin;
	public static Border etch = BorderFactory.createEtchedBorder();
	JComboBox clientDropdown;

	JTable resultTable;
	int i = 0;

	private JRadioButton allbtn;
	private JRadioButton curbtn;
	private JRadioButton noncurbtn;

	private JScrollPane resultspane;

	private JTextField bmname;
	private JTextField chebiID;
	private JTextField authortxt;
	private JTextField pubref;
	
	private JLabel tipLabel1;
	private JLabel tipLabel2;
	private JLabel tipLabel3;
	private JLabel tipLabel4;

	public BioModelPane(final SBMLPlugin plugin) {

		this.plugin = plugin;

		setLayout(new BorderLayout());

		Action allActions = new AbstractAction("alaxns") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							etch, "Models"));
					search(e.getActionCommand());
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(BioModelPane.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching Biomodels", ex);
				}
			}

		};

		bmname = new JTextField();
		bmname.setActionCommand("d");

		chebiID = new JTextField();
		chebiID.setActionCommand("e");
		
		authortxt = new JTextField();
		authortxt.setActionCommand("f");
		
		pubref = new JTextField();
		pubref.setActionCommand("g");

		tipLabel1 = new JLabel(
				"Tip: use Biomodel name (e.g.:'Tyson1991 - Cell Cycle 6 var')");
		tipLabel2 = new JLabel(
				"Tip: use Biomodel name (e.g.:'Tyson1991 - Cell Cycle 6 var')");
		tipLabel3 = new JLabel(
				"Tip: use Biomodel name (e.g.:'Tyson1991 - Cell Cycle 6 var')");
		tipLabel4 = new JLabel(
				"Tip: use Biomodel name (e.g.:'Tyson1991 - Cell Cycle 6 var')");

		tipLabel1.setFont(new Font("SansSerif", Font.ITALIC, 11));
		tipLabel2.setFont(new Font("SansSerif", Font.ITALIC, 11));
		tipLabel3.setFont(new Font("SansSerif", Font.ITALIC, 11));
		tipLabel4.setFont(new Font("SansSerif", Font.ITALIC, 11));

		bmname.addActionListener(allActions);
		chebiID.addActionListener(allActions);
		authortxt.addActionListener(allActions);
		pubref.addActionListener(allActions);
		
		JPanel searchBox = new JPanel();
		FormLayout layoutf = new FormLayout(
				"p,3dlu,120px,2dlu,100px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints ccf = new CellConstraints();

		JPanel displaypanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		displaypanel.setBorder(BorderFactory.createTitledBorder(etch,
				"Browse BioModels"));

		allbtn = new JRadioButton("ALL");
		allbtn.setActionCommand("a");

		curbtn = new JRadioButton("CURATED");
		curbtn.setActionCommand("b");

		noncurbtn = new JRadioButton("NON-CURATED");
		noncurbtn.setActionCommand("c");

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(allbtn);
		group.add(curbtn);
		group.add(noncurbtn);

		noncurbtn.addActionListener(allActions);
		curbtn.addActionListener(allActions);
		allbtn.addActionListener(allActions);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		displaypanel.add(allbtn, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		displaypanel.add(curbtn, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 2;
		c.gridy = 0;
		displaypanel.add(noncurbtn, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		displaypanel.add(new JLabel("Biomodel name"), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 1;
		displaypanel.add(bmname, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 2;
		displaypanel.add(tipLabel1, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 4;
		displaypanel.add(new JLabel("Chebi ID"), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 4;
		displaypanel.add(chebiID, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 5;
		displaypanel.add(tipLabel2, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 7;
		displaypanel.add(new JLabel("Author"), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 7;
		displaypanel.add(authortxt, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 8;
		displaypanel.add(tipLabel3, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 10;
		displaypanel.add(new JLabel("Publication"), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 10;
		displaypanel.add(authortxt, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 11;
		displaypanel.add(tipLabel4, c);

		Vector<String> clients = new Vector<String>(plugin.getClients()
				.keySet());
		Collections.sort(clients);

		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				String strValue = SBMLPlugin.shortClientName(value.toString());
				return super.getListCellRendererComponent(list, strValue,
						index, isSelected, cellHasFocus);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0; // reset to default
		c.weighty = 1.0; // request any extra vertical space
		c.anchor = GridBagConstraints.PAGE_END; // bottom of space
		c.insets = new Insets(10, 0, 0, 0); // top padding
		c.gridx = 1; // aligned with button 2
		c.gridwidth = 2; // 2 columns wide
		c.gridy = 2; // third row
		displaypanel.add(clientDropdown, c);

		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);
		searchBox.add(displaypanel, ccf.xyw(1, 1, 8));

		add(searchBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();

					try {

						ResultTableModel model = (ResultTableModel) target
								.getModel();
						File tmpDir = new File(plugin.getTmpDir(), SBMLPlugin
								.shortClientName(model.clientName));
						tmpDir.mkdirs();
						plugin.openPathwayWithProgress(
								plugin.getClients().get(model.clientName),
								model.getValueAt(row, 0).toString(), 0, tmpDir);

					} catch (Exception ex) {
						JOptionPane.showMessageDialog(BioModelPane.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
	}

	private void search(final String code) throws RemoteException,
			InterruptedException, ExecutionException {
		// final String query = pubXref.getText();

		String clientName = clientDropdown.getSelectedItem().toString();
		final BioModelsWSClient client = plugin.getClients().get(clientName);

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), "", pk, true, true);
		// final ArrayList<String> results2 = new ArrayList<String>();
		SwingWorker<String[], Void> sw = new SwingWorker<String[], Void>() {
			protected String[] doInBackground() throws Exception {
				pk.setTaskName("Searching Biomodels");
				String[] results = null;
				try {
					if (code.equalsIgnoreCase("a")) {
						results = client.getAllModelsId();
					} else if (code.equalsIgnoreCase("b")) {
						results = client.getAllCuratedModelsId();
					} else if (code.equalsIgnoreCase("c")) {
						results = client.getAllNonCuratedModelsId();
					} else if (code.equalsIgnoreCase("d")) {
						results = client.getModelsIdByName(bmname.getText());
					} else if (code.equalsIgnoreCase("e")) {
						results = client.getModelsIdByChEBI(chebiID.getText());
					} else if (code.equalsIgnoreCase("f")) {
						results = client.getModelsIdByPerson(authortxt.getText());
					} else if (code.equalsIgnoreCase("g")) {
						results = client.getModelsIdByPublication(pubref.getText());
					} else {
						JOptionPane.showMessageDialog(null,
								"Please Enter a Search Query", "ERROR",
								JOptionPane.ERROR_MESSAGE);
					}

				} catch (Exception e) {
					throw e;
				} finally {
					pk.finished();
				}

				return results;
			}
		};

		sw.execute();
		d.setVisible(true);

		resultTable.setModel(new ResultTableModel(sw.get(), clientName));
		resultTable.setRowSorter(new TableRowSorter<TableModel>(resultTable
				.getModel()));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}

class ResultTableModel extends AbstractTableModel {
	String[] results;
	String[] columnNames = new String[] { "Name" };
	String clientName;

	public ResultTableModel(String[] results, String clientName) {
		this.clientName = clientName;
		this.results = results;

	}

	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return results.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		String r = results[rowIndex];

		return r;
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}
}
