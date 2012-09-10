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

package org.columba.mail.gui.messageframe;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.columba.api.gui.frame.IContainer;
import org.columba.api.gui.frame.IDock;
import org.columba.api.gui.frame.IDockable;
import org.columba.core.gui.frame.FrameManager;
import org.columba.core.io.DiskIO;
import org.columba.mail.command.IMailFolderCommandReference;
import org.columba.mail.gui.frame.AbstractMailFrameController;
import org.columba.mail.gui.frame.TableViewOwner;
import org.columba.mail.gui.frame.ThreePaneMailFrameController;
import org.columba.mail.gui.table.ITableController;
import org.columba.mail.util.MailResourceLoader;

/**
 * Mail frame controller which contains a message viewer only.
 * <p>
 * Note that this frame depends on its parent frame controller for viewing
 * messages.
 * 
 * @see org.columba.mail.gui.action.NextMessageAction
 * @see org.columba.mail.gui.action.PreviousMessageAction
 * @see org.columba.mail.gui.action.NextUnreadMessageAction
 * @see org.columba.mail.gui.action.PreviousMessageAction
 * 
 * @author fdietz
 */
public class MessageFrameController extends AbstractMailFrameController
		implements TableViewOwner {

	IMailFolderCommandReference treeReference;

	IMailFolderCommandReference tableReference;

	FixedTableSelectionHandler tableSelectionHandler;

	private ThreePaneMailFrameController parentController;

	private IDockable messageViewerDockable;
	
	/**
	 * @param viewItem
	 */
	public MessageFrameController() {
		super(FrameManager.getInstance().createCustomViewItem("Messageframe"));

//		messageViewerDockable = registerDockable("mail_messageviewer", MailResourceLoader.getString(
//				"global", "dockable_messageviewer"),
//				messageController, null);
		
		tableSelectionHandler = new FixedTableSelectionHandler(tableReference);
		getSelectionManager().addSelectionHandler(tableSelectionHandler);

	}

	/**
	 * @param parent
	 *            parent frame controller
	 */
	public MessageFrameController(ThreePaneMailFrameController parent) {
		this();

		this.parentController = parent;

	}

	/**
	 * 
	 * @see org.columba.mail.gui.frame.MailFrameInterface#getTableSelection()
	 */
	public IMailFolderCommandReference getTableSelection() {
		return tableReference;
	}

	/**
	 * 
	 * @see org.columba.mail.gui.frame.MailFrameInterface#getTreeSelection()
	 */
	public IMailFolderCommandReference getTreeSelection() {
		return treeReference;
	}

	/**
	 * @param references
	 */
	public void setTreeSelection(IMailFolderCommandReference references) {
		treeReference = references;
	}

	/**
	 * @param references
	 */
	public void setTableSelection(IMailFolderCommandReference references) {
		tableReference = references;

		// TODO: re-enable feature, the following code violates our
		// design, accessing folders is only allowed in Command.execute()
		/*
		 * try { // Get the subject from the cached Header AbstractMessageFolder
		 * folder = (AbstractMessageFolder) references .getSourceFolder();
		 * IColumbaHeader header = folder.getHeaderList().get(
		 * references.getUids()[0]); String subject = (String)
		 * header.get("columba.subject");
		 * 
		 * getContainer().getFrame().setTitle(subject); } catch (Exception e) {
		 * LOG.warning(e.toString()); }
		 */

		tableSelectionHandler.setSelection(tableReference);
	}

	/**
	 * @see org.columba.mail.gui.frame.TableViewOwner#getTableController()
	 */
	public ITableController getTableController() {
		if (parentController == null)
			return null;

		// pass it along to parent frame
		return parentController.getTableController();
	}

	/**
	 * @see org.columba.api.gui.frame.IContentPane#getComponent()
	 */
//	public JComponent getComponent() {
//		JPanel panel = new JPanel();
//		panel.setLayout(new BorderLayout());
//
//		panel.add(messageController, BorderLayout.CENTER);
//
//		
//
//		return panel;
//	}

	/**
	 * @see org.columba.api.gui.frame.IFrameMediator#getString(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String getString(String sPath, String sName, String sID) {
		return MailResourceLoader.getString(sPath, sName, sID);
	}

	/**
	 * @see org.columba.core.gui.frame.DockFrameController#loadDefaultPosition()
	 */
	public void loadDefaultPosition() {

		//super.dock(messageViewerDockable, IDock.REGION.CENTER);

	}

	/** *********************** container callbacks ************* */

	
	/**
	 * @see org.columba.api.gui.frame.IFrameMediator#extendMenu(org.columba.api.gui.frame.IContainer)
	 */
	public void extendMenu(IContainer container) {
		try {
			InputStream is = DiskIO
					.getResourceStream("org/columba/mail/action/messageframe_menu.xml");

			container.extendMenu(this, is);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.columba.api.gui.frame.IFrameMediator#extendToolBar(org.columba.api.gui.frame.IContainer)
	 */
	public void extendToolBar(IContainer container) {
		try {
			InputStream is = DiskIO
					.getResourceStream("org/columba/mail/config/messageframe_toolbar.xml");

			container.extendToolbar(this, is);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JPanel getContentPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(messageController, BorderLayout.CENTER);

		

		return panel;
	}

}