/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.logbook.olog.ui;

import org.phoebus.framework.nls.NLS;

public class Messages
{
    public static String Add,                              
                         Add_Tooltip,
                         Apply,
                         Attachments,
                         BrowseButton,
                         Cancel,
                         CancelTooltip,
                         Clear,                                
                         Clear_Tooltip,
                         ClipboardButton,
                         CreateLogbookEntry,
                         CurrentDate,
                         Date,
                         DownloadSelected,
                         EmbedImage,
                         EmbedImageDialogTitle,
                         File,
                         FileSave,
                         FileSaveFailed,
                         ImageWidth,
                         ImageHeight,
                         Level,
                         Logbooks,
                         LogbookServiceUnavailableTitle,
                         LogbookServiceHasNoLogbooks,
                         LogbooksTitle,
                         LogbooksTooltip,
                         MarkupHelp,
                         NoClipboardContent,
                         Normal,
                         Num_Selected,
                         Password,
                         Properties,
                         Remove,
                         Remove_Tooltip,
                         ScalingFactor,
                         Search,
                         SearchAvailableItems,
                         Selected,
                         SelectFile,
                         SelectFolder,
                         SelectLevelTooltip,
                         Submit,
                         SubmitTooltip,
                         Tags,
                         TagsTitle,
                         TagsTooltip,
                         Text,
                         Title,
                         Username;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(Messages.class);
    }

    private Messages() 
    {
        // prevent instantiation
    }
}
