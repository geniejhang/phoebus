/*******************************************************************************
 * Copyright (c) 2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.epics.pva.server;

import java.util.concurrent.CountDownLatch;
import java.util.logging.LogManager;

import org.epics.pva.PVASettings;

/** PVA Server Demo that logs all received search requests
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SearchMonitorDemo
{
    public static void main(String[] args) throws Exception
    {
        LogManager.getLogManager().readConfiguration(PVASettings.class.getResourceAsStream("/pva_logging.properties"));

        final CountDownLatch done = new CountDownLatch(1);

        final SearchHandler search_handler = (seq, cid, name, addr) ->
        {
            System.out.println(addr + " searches for " + name + " (seq " + seq + ")");
            if (name.equals("QUIT"))
                done.countDown();
            // Proceed with default search handler
            return false;
        };

        try
        (
            // Create PVA Server (auto-closed)
            final PVAServer server = new PVAServer(search_handler);
        )
        {
            done.await();
        }

        System.out.println("Done.");
    }
}
