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
package org.columba.mail.folder.outbox;

import java.io.InputStream;

import org.columba.mail.composer.SendableMessage;
import org.columba.mail.config.FolderItem;
import org.columba.mail.folder.headercache.BerkeleyDBHeaderList;
import org.columba.mail.folder.mbox.CachedMboxFolder;
import org.columba.mail.folder.mh.CachedMHFolder;
import org.columba.mail.message.ColumbaMessage;
import org.columba.ristretto.message.Attributes;
import org.columba.ristretto.message.Flags;

/**
 * Additionally to {@CachedMHFolder}is capable of saving
 * {@link SendableMessage}objects.
 * <p>
 * It is used to store messages to send them later all at once.
 * 
 * @author fdietz
 */
public class OutboxFolder extends CachedMboxFolder {

	private SendListManager[] sendListManager = new SendListManager[2];


	public OutboxFolder(FolderItem item, String path) {
		super(item, path);
		
		try {
			((BerkeleyDBHeaderList)getHeaderList()).setHeaderBinding(new OutboxHeaderBinding());
		} catch (Exception e) {
		}
		
		sendListManager[0] = new SendListManager();
		sendListManager[1] = new SendListManager();
	}

	public SendableMessage getSendableMessage(Object uid) throws Exception {
		ColumbaMessage message = getMessage(uid);

		SendableMessage sendableMessage = new SendableMessage(message);

		return sendableMessage;
	}

	/**
	 * 
	 * OutboxFolder doesn't allow adding messages, in comparison to other
	 * regular mailbox folders.
	 * 
	 * @see org.columba.mail.folder.FolderTreeNode#supportsAddMessage()
	 */
	public boolean supportsAddMessage() {
		return false;
	}

	/**
	 * The outbox folder doesnt allow adding folders to it.
	 * 
	 * @param newFolderType
	 *            folder to check..
	 * @return false always.
	 */
	public boolean supportsAddFolder(String newFolderType) {
		return false;
	}

	/**
	 * Returns if this folder type can be moved.
	 * 
	 * @return false always.
	 */
	public boolean supportsMove() {
		return false;
	}

	/**
	 * @see org.columba.mail.folder.IMailbox#addMessage(java.io.InputStream,
	 *      org.columba.ristretto.message.Attributes)
	 */
	public Object addMessage(InputStream in, Attributes attributes, Flags flags)
			throws Exception {
		Object uid = super.addMessage(in, attributes, flags);
		setAttribute(uid, "columba.recipients", attributes
				.get("columba.recipients"));

		return uid;
	}

}