// The contents of this file are subject to the Mozilla Public License Version
// 1.1
//(the "License"); you may not use this file except in compliance with the
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo
// Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package org.columba.addressbook.gui.dialog.group;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.columba.addressbook.folder.AbstractFolder;
import org.columba.addressbook.gui.list.AddressbookDNDListView;
import org.columba.addressbook.gui.list.AddressbookListModel;
import org.columba.addressbook.model.IBasicModelPartial;
import org.columba.addressbook.model.IContactModelPartial;
import org.columba.addressbook.model.IGroupModel;
import org.columba.addressbook.util.AddressbookResourceLoader;
import org.columba.core.gui.base.ButtonWithMnemonic;
import org.columba.core.gui.base.DoubleClickListener;
import org.columba.core.gui.base.SingleSideEtchedBorder;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class EditGroupDialog extends JDialog implements ActionListener,
		KeyListener {

	private AddressbookDNDListView parentList;

	private AddressbookListModel parentMembers;

	private AddressbookDNDListView groupList;

	private AddressbookListModel groupMembers;

	private JButton addButton;

	private JButton removeButton;

	private JLabel nameLabel;

	private JLabel descriptionLabel;

	private JTextField nameTextField;

	private JTextField descriptionTextField;

	private boolean result;

	private ButtonWithMnemonic okButton;

	private ButtonWithMnemonic cancelButton;

	private IGroupModel group;

	private AbstractFolder parentFolder;

	/**
	 * Constructor
	 *
	 * @param frame
	 *            parent frame
	 * @param groupNode
	 *            null, if you want to create a new group. Otherwise, the
	 *            groupNode will be modified.
	 */
	public EditGroupDialog(JFrame frame, IGroupModel group,
			AbstractFolder parentFolder) {
		super(frame, true);

		this.group = group;
		this.parentFolder = parentFolder;

		result = false;

		setTitle(AddressbookResourceLoader.getString("dialog",
				"editgroupdialog", "contact_list_editor")); //$NON-NLS-1$

		// set title
		initComponents();

		layoutComponents();

		updateComponents(true);

		pack();

		setLocationRelativeTo(null);

		setVisible(true);
	}

	private JPanel createGroupNamePanel() {
		JPanel panel = new JPanel();
		FormLayout layout = new FormLayout(
				"12px, right:default, 6px, default:grow", ""); //$NON-NLS-1$ //$NON-NLS-2$

		DefaultFormBuilder b = new DefaultFormBuilder(layout, panel);
		b.setRowGroupingEnabled(true);
		b.setLeadingColumnOffset(1);

		b.appendSeparator(AddressbookResourceLoader.getString("dialog",
				"editgroupdialog", "description_3")); //$NON-NLS-1$

		b.append(nameLabel);
		b.append(nameTextField);

		b.append(descriptionLabel);
		b.append(descriptionTextField);

		return panel;
	}

	private JPanel createGroupPanel() {
		JPanel panel = new JPanel();
		FormLayout layout = new FormLayout(
				"fill:default:grow, 12dlu, fill:default:grow", //$NON-NLS-1$
				"default, 4px, fill:default:grow, 6px, default"); //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout, panel);
		CellConstraints cc = new CellConstraints();

		builder.addSeparator(AddressbookResourceLoader.getString("dialog",
				"editgroupdialog", "addressbook"), cc.xy(1, 1)); //$NON-NLS-1$
		builder.addSeparator(AddressbookResourceLoader.getString("dialog",
				"editgroupdialog", "group_members"), cc.xy(3, 1)); //$NON-NLS-1$

		builder.add(new JScrollPane(parentList), cc.xy(1, 3));
		builder.add(new JScrollPane(groupList), cc.xy(3, 3));

		builder.add(addButton, cc.xy(1, 5));
		builder.add(removeButton, cc.xy(3, 5));

		return panel;
	}

	private void layoutComponents() {
		getContentPane().setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		FormLayout layout = new FormLayout("fill:default:grow", //$NON-NLS-1$
				"default, 16px, fill:default:grow"); //$NON-NLS-1$

		CellConstraints cc = new CellConstraints();
		mainPanel.setLayout(layout);

		mainPanel.add(createGroupNamePanel(), cc.xy(1, 1));
		mainPanel.add(createGroupPanel(), cc.xy(1, 3));

		getContentPane().add(mainPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(new SingleSideEtchedBorder(SwingConstants.TOP));

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 6, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);

		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void initComponents() {
		nameLabel = new JLabel(AddressbookResourceLoader.getString("dialog",
				"editgroupdialog", "name")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		nameTextField = new JTextField();

		descriptionLabel = new JLabel(AddressbookResourceLoader.getString(
				"dialog", "editgroupdialog", "description_2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		descriptionTextField = new JTextField();

		groupMembers = new AddressbookListModel();
		parentList = new AddressbookDNDListView(groupMembers);
		parentList.setMinimumSize(new Dimension(200, 300));
		parentList.setAcceptDrop(false);
		parentList.addMouseListener(new DoubleClickListener(){
			public void doubleClick(MouseEvent ev) {
				actionPerformed(new ActionEvent(parentList, 0, "ADD"));
			}

		});

		parentMembers = new AddressbookListModel();
		Map<String, IContactModelPartial> l = parentFolder.getContactItemMap();

		for (Iterator<IContactModelPartial> it = l.values().iterator(); it.hasNext();) {
			IContactModelPartial p = (IContactModelPartial) it.next();
			parentMembers.addElement(p);
		}
		parentList.setModel(parentMembers);

		groupMembers = new AddressbookListModel();
		groupList = new AddressbookDNDListView(groupMembers);
		groupList.setMinimumSize(new Dimension(200, 300));

		addButton = new JButton("Add"); //$NON-NLS-1$
		addButton.addActionListener(this);
		addButton.setActionCommand("ADD"); //$NON-NLS-1$

		removeButton = new JButton("Remove"); //$NON-NLS-1$
		removeButton.addActionListener(this);
		removeButton.setActionCommand("REMOVE"); //$NON-NLS-1$

		okButton = new ButtonWithMnemonic(AddressbookResourceLoader.getString(
				"global", "ok")); //$NON-NLS-1$ //$NON-NLS-2$
		okButton.setActionCommand("OK"); //$NON-NLS-1$
		okButton.addActionListener(this);

		cancelButton = new ButtonWithMnemonic(AddressbookResourceLoader
				.getString("global", "cancel")); //$NON-NLS-1$ //$NON-NLS-2$
		cancelButton.setActionCommand("CANCEL"); //$NON-NLS-1$
		cancelButton.addActionListener(this);

		getRootPane().setDefaultButton(okButton);
		getRootPane().registerKeyboardAction(this, "CANCEL", //$NON-NLS-1$
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public boolean getResult() {
		return result;
	}

	public void updateComponents(boolean b) {
		if (b) {
			// gettext
			nameTextField.setText(group.getName()); //$NON-NLS-1$
			descriptionTextField.setText(group.getDescription()); //$NON-NLS-1$

			groupMembers = new AddressbookListModel();

			String[] m = group.getMembers();

			try {
				Map<String, IContactModelPartial> l = parentFolder.getContactItemMap();

				for (int i = 0; i < m.length; i++) {

					IContactModelPartial item = l.get(m[i]);

					groupMembers.addElement(item);
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			this.groupList.setModel(groupMembers);
		} else {
			// settext
			group.setName(nameTextField.getText()); //$NON-NLS-1$
			group.setDescription(descriptionTextField.getText()); //$NON-NLS-1$

			// remove all children
			group.removeAllMembers();

			// add children
			for (int i = 0; i < groupMembers.getSize(); i++) {
				group.addMember(groupMembers.get(i).getId());
			}
		}
	}

	/**
	 * Add headeritem from ComboBox to List
	 *
	 */
	private void addHeaderItem() {
		Object[] list = parentList.getSelectedValues();

		for (int i = 0; i < list.length; i++) {
			groupMembers.addElement((IBasicModelPartial) list[i]);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals("CANCEL")) { //$NON-NLS-1$
			result = false;
			setVisible(false);
		} else if (command.equals("OK")) { //$NON-NLS-1$

			if (nameTextField.getText().length() == 0) {
				JOptionPane
						.showMessageDialog(
								this,
								AddressbookResourceLoader
										.getString(
												"dialog",
												"editgroupdialog", "you_must_enter_a_name_for_the_group")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				return;
			}

			result = true;

			updateComponents(false);

			setVisible(false);
		} else if (command.equals("ADD")) { //$NON-NLS-1$
			addHeaderItem();
		} else if (command.equals("REMOVE")) { //$NON-NLS-1$
			Object[] list = groupList.getSelectedValues();

			for (int i = 0; i < list.length; i++) {
				groupMembers.removeElement((IBasicModelPartial) list[i]);
			}
		}
	}

	/** ************************* KeyListener **************************** */
	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		char ch = e.getKeyChar();

		if (ch == KeyEvent.VK_ENTER) {
			addHeaderItem();
		}
	}
}