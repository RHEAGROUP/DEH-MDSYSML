/*
 * OpenBrowserPanelAction.java
 *
 * Copyright (c) 2015-2019 RHEA System S.A.
 *
 * Author: Sam Geren�, Alex Vorobiev, Nathanael Smiechowski 
 *
 * This file is part of CDP4-SDKJ Community Edition
 *
 * The DEH-MDSYSML is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * The DEH-MDSYSML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package Actions.ToolBar;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jidesoft.docking.DockingManager;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import App.AppContainer;
import Utils.ImageLoader.ImageLoader;
import ViewModels.Interfaces.IHubBrowserPanelViewModel;
import Views.MDHubBrowserPanel;

/**
 * The {@link OpenHubBrowserPanelAction} is a {@link MDAction} that can be added to one toolbar in Cameo or MagicDraw
 */
@SuppressWarnings("serial")
public class OpenHubBrowserPanelAction extends MDAction
{
    /**
     * The current class logger
     */
    private Logger logger = LogManager.getLogger();
    
    /**
     * The {@link MDHubBrowserPanel} instance
     */
    private MDHubBrowserPanel hubBrowserPanel;
    
    /**
     * Initializes a new {@link OpenHubBrowserPanelAction}
     * @param id the id of the panel
     * @param name the
     */
    public OpenHubBrowserPanelAction()
    {
         super("Hub Browser", "Open/Close the Hub Browser Panel", null, null);      
         this.setLargeIcon(ImageLoader.GetIcon("icon16.png"));
    }
    
    /**
    * Commands the {@link MDHubBrowserPanel} to show or hide
    */
    public void actionPerformed(ActionEvent e)
    {            
        try
        {
            Application applicationInstance = Application.getInstance();
            MainFrame mainFrame = applicationInstance.getMainFrame();
            DockingManager dockingManager = mainFrame.getDockingManager();
            Collection<String> allFrames = dockingManager.getAllFrames();
            for(String key : allFrames)
            {
                if(key == MDHubBrowserPanel.PanelDockKey)
                {
                    this.hubBrowserPanel.ShowHide(dockingManager);
                    return;
                }
            }
                            
            this.hubBrowserPanel = new MDHubBrowserPanel();
            this.hubBrowserPanel.SetDataContext(AppContainer.Container.getComponent(IHubBrowserPanelViewModel.class));
            dockingManager.addFrame(this.hubBrowserPanel);
        }
        catch (Exception exception) 
        {
            this.logger.error(String.format("OpenHubBrowserPanelAction actionPerformed has thrown an exception %s \n\r %s", exception.toString(), exception.getStackTrace()));
            throw exception;
        }
    }    
}
