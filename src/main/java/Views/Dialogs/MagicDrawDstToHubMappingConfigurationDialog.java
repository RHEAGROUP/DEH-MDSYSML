/*
 * DstMappingConfigurationDialog.java
 *
 * Copyright (c) 2020-2021 RHEA System S.A.
 *
 * Author: Sam Gerené, Alex Vorobiev, Nathanael Smiechowski 
 *
 * This file is part of DEH-MDSYSML
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
package Views.Dialogs;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import Enumerations.MappingDirection;
import ViewModels.Dialogs.Interfaces.IDstToHubMappingConfigurationDialogViewModel;
import ViewModels.MagicDrawObjectBrowser.Rows.ClassRowViewModel;
import ViewModels.MagicDrawObjectBrowser.Rows.ElementRowViewModel;
import Views.MagicDrawObjectBrowser;

/**
 * The {@linkplain MagicDrawDstToHubMappingConfigurationDialog} is the dialog view to allow to configure a mapping 
 * to be defined between a selection of DST elements and the hub element
 */
@SuppressWarnings("serial")
@Annotations.ExludeFromCodeCoverageGeneratedReport
public class MagicDrawDstToHubMappingConfigurationDialog extends MappingConfigurationDialog<IDstToHubMappingConfigurationDialogViewModel, Element, Class, ElementRowViewModel<?>>
{
    /**
     * Initializes a new {@linkplain MagicDrawDstToHubMappingConfigurationDialog}
     */
    public MagicDrawDstToHubMappingConfigurationDialog()
    {
        super(MappingDirection.FromDstToHub, new MagicDrawObjectBrowser());
    }
}