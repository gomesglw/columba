//The contents of this file are subject to the Mozilla Public License Version 1.1
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
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.

package org.columba.mail.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.columba.ristretto.message.Header;
import org.junit.Assert;
import org.junit.Test;

public class PassiveHeaderParserInputStreamTest {

    @Test
	public void testFullRead() throws IOException {
		String header = "Subject: nbla\r\n\r\n some message blabla\r\n";
		
		PassiveHeaderParserInputStream test = new PassiveHeaderParserInputStream(new ByteArrayInputStream(header.getBytes()));
		
		byte[] dummy = new byte[10000];

		Assert.assertFalse(test.isHeaderAvailable());
		
		Assert.assertEquals( header.length(), test.read(dummy));
		Assert.assertTrue(test.isHeaderAvailable());
		
		Header parsedHeader = test.getHeader();
		
		Assert.assertEquals("nbla", parsedHeader.get("Subject"));
		
	}

    @Test
	public void testPartRead() throws IOException {
		String header = "Subject: nbla\r\n\r\nsome message blabla\r\n";
		
		PassiveHeaderParserInputStream test = new PassiveHeaderParserInputStream(new ByteArrayInputStream(header.getBytes()));
		
		Assert.assertFalse(test.isHeaderAvailable());
		
		for( int i=0; i<17; i++) {
			Assert.assertFalse(test.isHeaderAvailable());
			test.read();
		}
		
		Assert.assertTrue(test.isHeaderAvailable());
		
		Header parsedHeader = test.getHeader();
		
		Assert.assertEquals("nbla", parsedHeader.get("Subject"));
		
	}

}
