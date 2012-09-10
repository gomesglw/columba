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
package org.columba.mail.folder.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.columba.core.command.NullWorkerStatusController;
import org.columba.mail.command.MailFolderCommandReference;
import org.columba.mail.folder.AbstractFolderTst;
import org.columba.mail.folder.AbstractMessageFolder;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author redsolo
 */
public class MarkFolderAsReadCommandTest extends AbstractFolderTst {
    /**
     * @param factory folder factory
     */
    public MarkFolderAsReadCommandTest(Class factory) {
        super(factory);
    }

    /**
     * Test to mark a folder as read.
     * @throws Exception thrown by the command itself.
     */
    @Test
    public void testExecute() throws Exception {
        AbstractMessageFolder folder = getSourceFolder();

        Object uid1 = folder.addMessage(createMessageStream("sub1", "body1"));
        Object uid2 = folder.addMessage(createMessageStream("sub2", "body2"));
        Object uid3 = folder.addMessage(createMessageStream("sub3", "body3"));
        Object uid4 = folder.addMessage(createMessageStream("sub4", "body4"));
        Object uid5 = folder.addMessage(createMessageStream("sub5", "body5"));

        Assert.assertNotNull("Msg 1's uid was null", uid1);
        Assert.assertNotNull("Msg 2's uid was null", uid2);
        Assert.assertNotNull("Msg 3's uid was null", uid3);

        folder.markMessage(new Object[]{uid1, uid3, uid5}, MarkMessageCommand.MARK_AS_UNREAD);
        folder.markMessage(new Object[]{uid2, uid4}, MarkMessageCommand.MARK_AS_READ);

        Assert.assertNotNull("Flags for msg 1 was null", folder.getFlags(uid1));
        Assert.assertFalse("Msg 1 is read", folder.getFlags(uid1).getSeen());
        Assert.assertNotNull("Flags for msg 2 was null", folder.getFlags(uid2));
        Assert.assertTrue("Msg 2 is read", folder.getFlags(uid2).getSeen());
        Assert.assertNotNull("Flags for msg 3 was null", folder.getFlags(uid3));
        Assert.assertFalse("Msg 3 is read", folder.getFlags(uid3).getSeen());

        MailFolderCommandReference references = new MailFolderCommandReference(folder);
        MarkFolderAsReadCommand command = new MarkFolderAsReadCommand(references);
        command.execute(NullWorkerStatusController.getInstance());

        Assert.assertEquals("Message 1 isnt read", true, folder.getFlags(uid1).getSeen());
        Assert.assertEquals("Message 2 isnt read", true, folder.getFlags(uid2).getSeen());
        Assert.assertEquals("Message 3 isnt read", true, folder.getFlags(uid3).getSeen());
        Assert.assertEquals("Message 4 isnt read", true, folder.getFlags(uid4).getSeen());
        Assert.assertEquals("Message 5 isnt read", true, folder.getFlags(uid5).getSeen());
    }

    /**
     * Creates a inputstream as a valid email
     * @param subject the subject of email
     * @param body the body
     * @return an input stream
     * @throws IOException thrown if there is some real strange things with the writers
     */
    private InputStream createMessageStream(String subject, String body) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        writer.write("Subject: " + subject + "\n");
        writer.write("\n");
        writer.write(body);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
