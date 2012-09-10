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
package org.columba.addressbook.facade;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.columba.addressbook.folder.AbstractFolder;
import org.columba.addressbook.folder.IContactFolder;
import org.columba.addressbook.folder.IFolder;
import org.columba.addressbook.folder.IGroupFolder;
import org.columba.addressbook.gui.tree.AddressbookTreeModel;
import org.columba.addressbook.gui.tree.util.SelectAddressbookFolderDialog;
import org.columba.addressbook.model.ContactModel;
import org.columba.addressbook.model.IContactModel;
import org.columba.addressbook.model.IContactModelPartial;
import org.columba.addressbook.model.IGroupModel;
import org.columba.api.exception.StoreException;

/**
 * Provides high-level contact management methods.
 * 
 * @author fdietz
 */
public final class ContactFacade implements IContactFacade {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger("org.columba.addressbook.facade"); //$NON-NLS-1$

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#addContact(int,
	 *      IContactItem)
	 */
	public void addContact(String uid, IContactItem contactItem)
			throws StoreException {
		if (contactItem == null)
			throw new IllegalArgumentException("IContactItem is null");

		if (uid == null)
			throw new IllegalArgumentException("uid == null");

		checkContactItemValidity(contactItem);

		AbstractFolder selectedFolder = (AbstractFolder) AddressbookTreeModel
				.getInstance().getFolder(uid);

		IContactModel card = createContactModel(contactItem);

		try {
			if (selectedFolder.findByEmailAddress(card.getPreferredEmail()) == null)
				selectedFolder.add(card);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ContactModel createContactModel(IContactItem contactItem) {
		return new ContactModel(contactItem);

	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#addContact(int,
	 *      IContactItem)
	 */
	public void addContacts(String uid, IContactItem[] contactItems)
			throws StoreException {
		if (uid == null)
			throw new IllegalArgumentException("uid == null");

		if (contactItems == null)
			throw new IllegalArgumentException(
					"Zero IContactItem's were specified to add to addressbook folder");

		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		IContactFolder folder = (IContactFolder) model.getFolder(uid);

		for (int i = 0; i < contactItems.length; i++) {
			
			// skip if contact item is not valid
			try {
				checkContactItemValidity(contactItems[i]);
			}
			catch (IllegalArgumentException e) {
				continue;
			}
			
			IContactModel card = createContactModel(contactItems[i]);

			// check if contact with given email address exists already
			if (folder.findByEmailAddress(card.getPreferredEmail()) == null)
				folder.add(card);

		}
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#addContact(IContactItem)
	 */
	public void addContacts(IContactItem[] contactItems) throws StoreException {

		if (contactItems == null)
			throw new IllegalArgumentException(
					"Zero IContactItem's were specified to add to addressbook folder");

		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		SelectAddressbookFolderDialog dialog = new SelectAddressbookFolderDialog(
				model);
		if (dialog.success()) {
			IFolder folder = dialog.getSelectedFolder();
			String uid = folder.getId();

			addContacts(uid, contactItems);
		} else
			return;
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#addContact(IContactItem)
	 */
	public void addContact(IContactItem contactItem) throws StoreException {
		if (contactItem == null)
			throw new IllegalArgumentException("IContactItem is null");

		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		SelectAddressbookFolderDialog dialog = new SelectAddressbookFolderDialog(
				model);
		if (dialog.success()) {
			IFolder folder = dialog.getSelectedFolder();
			String uid = folder.getId();

			addContact(uid, contactItem);
		} else
			return;
	}

	private void checkContactItemValidity(IContactItem contactItem) {
		if (contactItem.getFirstName() != null) {
			// check if it contains comma character
			if (contactItem.getFirstName().indexOf(",") != -1)
				throw new IllegalArgumentException(
						"Firstname contains illegal character <,>");
		}

		if (contactItem.getLastName() != null) {
			// check if it contains comma character
			if (contactItem.getLastName().indexOf(",") != -1)
				throw new IllegalArgumentException(
						"Lastname contains illegal character <,>");
		}

	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#getAllHeaderItems(java.lang.String)
	 */
	public List<IHeaderItem> getAllHeaderItems(String folderId,
			boolean flattenGroupItems) throws StoreException {
		if (folderId == null)
			throw new IllegalArgumentException("folderId == null");

		Vector<IHeaderItem> v = new Vector<IHeaderItem>();
		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		IFolder f = model.getFolder(folderId);
		if (f == null)
			return v;

		if (!(f instanceof IContactFolder))
			return v;

		IContactFolder folder = (IContactFolder) f;
		try {
			Iterator<IContactModelPartial> it = folder.getHeaderItemList()
					.iterator();
			while (it.hasNext()) {
				IContactModelPartial itemPartial = it.next();

				IContactItem item = createContactItem(itemPartial);

				v.add(item);
			}

			List<IGroupItem> groupList = getAllGroups(folderId);

			if (flattenGroupItems) {
				// retrieve all contact items and add those to the list only
				Iterator<IGroupItem> it2 = groupList.iterator();
				while (it2.hasNext()) {
					IGroupItem groupItem = it2.next();
					List<IContactItem> l = groupItem.getContacts();
					v.addAll(l);
				}
			} else {
				// simply all all group items to the list
				v.addAll(groupList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return v;
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#getAllContacts(java.lang.String)
	 */
	public List<IContactItem> getAllContacts(String folderId) {
		if (folderId == null)
			throw new IllegalArgumentException("folderId == null");

		Vector<IContactItem> v = new Vector<IContactItem>();
		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		IFolder f = model.getFolder(folderId);
		if (f == null)
			return v;

		if (!(f instanceof IContactFolder))
			return v;

		IContactFolder folder = (IContactFolder) f;
		try {
			Iterator<IContactModelPartial> it = folder.getHeaderItemList()
					.iterator();
			while (it.hasNext()) {
				IContactModelPartial contactModel = it.next();

				IContactItem item = createContactItem(contactModel);

				v.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return v;
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#getAllGroups(java.lang.String)
	 */
	public List<IGroupItem> getAllGroups(String folderId) {
		if (folderId == null)
			throw new IllegalArgumentException("folderId = null");

		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		IFolder f = (IFolder) model.getFolder(folderId);
		if (f == null)
			throw new IllegalArgumentException("contact folder does not exist");

		Vector<IGroupItem> v = new Vector<IGroupItem>();

		if (!(f instanceof IContactFolder))
			return v;

		IContactFolder contactFolder = (IContactFolder) f;

		// add group items
		for (int i = 0; i < f.getChildCount(); i++) {
			IGroupFolder groupFolder = (IGroupFolder) f.getChildAt(i);
			IGroupModel group = groupFolder.getGroup();

			IGroupItem groupItem = new GroupItem(folderId);
			groupItem.setName(group.getName());
			groupItem.setDescription(group.getDescription());

			String[] members = group.getMembers();
			Map<String, IContactModelPartial> map = contactFolder
					.getContactItemMap(members);
			Iterator<IContactModelPartial> it = map.values().iterator();
			while (it.hasNext()) {
				IContactModelPartial partial = it.next();
				IContactItem item = createContactItem(partial);
				groupItem.addContact(item);
			}
			v.add(groupItem);
		}

		return v;
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#findByEmailAddress(java.lang.String,
	 *      java.lang.String)
	 */
	public String findByEmailAddress(String folderId, String emailAddress)
			throws StoreException {
		if (folderId == null)
			throw new IllegalArgumentException("folderId = null");
		if (emailAddress == null)
			throw new IllegalArgumentException("emailAddress == null");

		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		IFolder f = (IFolder) model.getFolder(folderId);
		if (f == null)
			throw new IllegalArgumentException("contact folder does not exist");

		if (!(f instanceof IContactFolder))
			return null;

		IContactFolder contactFolder = (IContactFolder) f;
		String id = contactFolder.findByEmailAddress(emailAddress);
		return id;
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#findByName(java.lang.String,
	 *      java.lang.String)
	 */
	public String findByName(String folderId, String name)
			throws StoreException, IllegalArgumentException {
		if (folderId == null)
			throw new IllegalArgumentException("folderId = null");
		if (name == null)
			throw new IllegalArgumentException("name == null");

		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		IFolder f = (IFolder) model.getFolder(folderId);
		if (f == null)
			throw new IllegalArgumentException("contact folder does not exist");

		if (!(f instanceof IContactFolder))
			return null;

		IContactFolder contactFolder = (IContactFolder) f;
		String id = contactFolder.findByName(name);
		return id;
	}

	private IContactItem createContactItem(IContactModelPartial itemPartial) {
		IContactItem item = new ContactItem(itemPartial.getId(), itemPartial
				.getName(), itemPartial.getFirstname(), itemPartial
				.getLastname(), itemPartial.getAddress());

		return item;
	}

	/**
	 * @see org.columba.addressbook.facade.IContactFacade#getContactItem(java.lang.String,
	 *      java.lang.String)
	 */
	public IContactItem getContactItem(String folderId, String contactId)
			throws StoreException, IllegalArgumentException {
		if (folderId == null)
			throw new IllegalArgumentException("folderId = null");
		if (contactId == null)
			throw new IllegalArgumentException("contactId == null");
		AddressbookTreeModel model = AddressbookTreeModel.getInstance();
		IFolder f = (IFolder) model.getFolder(folderId);
		if (f == null)
			throw new IllegalArgumentException("contact folder does not exist");

		if (!(f instanceof IContactFolder))
			return null;

		IContactFolder contactFolder = (IContactFolder) f;
		Map<String, IContactModelPartial> map = contactFolder
				.getContactItemMap(new String[] { contactId });
		if (map.isEmpty())
			return null;

		IContactModelPartial partial = map.get(contactId);

		return createContactItem(partial);
	}

}