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
package org.columba.addressbook.folder.importfilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.columba.addressbook.folder.AbstractFolder;
import org.columba.addressbook.model.ContactModel;
import org.columba.addressbook.model.EmailModel;
import org.columba.addressbook.model.PhoneModel;
import org.columba.addressbook.util.AddressbookResourceLoader;

/**
 * @version 1.0
 * @author
 */
public class MozillaCSVAddressbookImporter extends DefaultAddressbookImporter {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger("org.columba.addressbook.folder.importfilter"); //$NON-NLS-1$

	public MozillaCSVAddressbookImporter() {
		super();
	}

	public MozillaCSVAddressbookImporter(File sourceFile,
			AbstractFolder destinationFolder) {
		super(sourceFile, destinationFolder);
	}

	public void importAddressbook(File file) throws Exception {
		LOG.info("importing addressbook::::"); //$NON-NLS-1$

		BufferedReader in = new BufferedReader(new FileReader(file));
		String str;

		while ((str = in.readLine()) != null) {
			// start parsing line
			int counter = -1;
			ContactModel card = new ContactModel();

			StringBuffer token = new StringBuffer();
			int pos = 0;

			while (pos < str.length()) {
				char ch = str.charAt(pos);

				if (ch == ',') {
					// found new token
					counter++;

					if (counter == 0) {
						card.setGivenName(token.toString());
					} else if (counter == 1) {
						card.setFamilyName(token.toString());
					} else if (counter == 2) {
						// backwards compatibility
						card.setSortString(token.toString());
					} else if (counter == 3) {
						card.setNickName(token.toString());
					} else if (counter == 4) {
						card.addEmail(new EmailModel(token.toString(),
								EmailModel.TYPE_WORK));
					} else if (counter == 5) {
						card.addEmail(new EmailModel(token.toString(),
								EmailModel.TYPE_HOME));
					} else if (counter == 8) {
						card.addPhone(new PhoneModel(token.toString(),
								PhoneModel.TYPE_BUSINESS_PHONE));
					} else if (counter == 9) {
						card.addPhone(new PhoneModel(token.toString(),
								PhoneModel.TYPE_HOME_PHONE));
					}

					token = new StringBuffer();
				} else {
					token.append(ch);
				}

				pos++;
			}

			saveContact(card);
		}

		in.close();
	}

	public String getDescription() {
		return AddressbookResourceLoader.getString("dialog",
				"addressbookimport", "mozillacsvaddressbook_description");
	}
}
