/*
 * IMagicDrawMappingConfigurationService.java
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
package Services.MappingConfiguration;

import java.util.Collection;
import java.util.UUID;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;

import Enumerations.MappingDirection;
import ViewModels.Interfaces.IMappedElementRowViewModel;
import cdp4common.engineeringmodeldata.Parameter;

/**
 * The {@linkplain IMagicDrawMappingConfigurationService} is the main interface definition for the {@linkplain MagicDrawMappingConfigurationService}
 */
public interface IMagicDrawMappingConfigurationService extends IMappingConfigurationService<ExternalIdentifier>
{
    /**
     * Loads the mapping configuration and generates the map result respectively
     * 
     * @param elements a {@linkplain Collection} of {@code TDstElement}
     * @return a {@linkplain Collection} of {@linkplain IMappedElementRowViewModel}
     */
    Collection<IMappedElementRowViewModel> LoadMapping(Collection<Class> elements);

    /**
     * Adds or updates the mapping information between the selected {@linkplain ActualFiniteState} represented by its id, to the corresponding {@linkplain Property} also represented by its id
     * 
     * @param actualFiniteStateId the {@linkplain ActualFiniteState} id
     * @param parameterId the {@linkplain Parameter} id
     * @param mappingDirection the {@linkplain MappingDirection}
     */
    void AddOrUpdateSelectedActualFiniteStateToExternalIdentifierMap(UUID actualFiniteStateId, UUID parameterId, MappingDirection mappingDirection);
}
