/*
 * MagicDrawSessionService.java
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
package Services.MagicDrawSession;

import java.util.Collection;
import java.util.stream.Collectors;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListener;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import Reactive.ObservableValue;
import io.reactivex.Observable;

/**
 * The {@linkplain MagicDrawSessionService} is the service providing easier access to the MagicDraw {@linkplain Sessions}s
 */
@Annotations.ExludeFromCodeCoverageGeneratedReport
public class MagicDrawSessionService implements IMagicDrawSessionService
{
    /**
     * The {@linkplain ProjectEventListener} to monitor project open and closed in Cameo/MagicDraw
     */
    private final IMagicDrawProjectEventListener projectEventListener;
        
    /**
     * Initializes a new {@linkplain MagicDrawSessionService}
     * 
     * @param projectEventListener the {@linkplain IMagicDrawProjectEventListener}
     */
    public MagicDrawSessionService(IMagicDrawProjectEventListener projectEventListener)
    {
        this.projectEventListener = projectEventListener;
    }
    
    /**
     * Gets the open Document ({@linkplain Project}) from the running instance of Cameo/MagicDraw
     * 
     * @return the {@linkplain Project}
     */
    @Override
    public Project GetProject() 
    {
        return this.projectEventListener.OpenDocument().Value();
    }
    
    /**
     * Gets an {@linkplain Observable} of {@linkplain Boolean} indicating the subscribers whenever the open document gets saved
     * 
     * @return an {@linkplain Observable} of {@linkplain Boolean}
     */
    @Override
    public Observable<Boolean> ProjectSaved() 
    {
        return this.projectEventListener.ProjectSaved();
    }
    
    /**
     * Backing field for {@linkplain #GetSessionEvent()}
     */
    private ObservableValue<Boolean> sessionEvent = new ObservableValue<>();
    
    /**
     * Gets the Session Event {@linkplain ObservableValue}
     * 
     * @return an {@linkplain ObservableValue} of {@linkplain Boolean}
     */
    @Override
    public ObservableValue<Boolean> GetSessionEvent()
    {
        return this.sessionEvent;
    }
    
    /**
     * Gets an {@linkplain Observable} of {@linkplain Boolean} indicating the subscribers whenever the session has been updated
     * 
     * @return an {@linkplain Observable} of {@linkplain Boolean}
     */
    @Override
    public Observable<Boolean> SessionUpdated()
    {
        return this.sessionEvent.Observable();
    }
    
    /**
     * Gets an {@linkplain Observable} of {@linkplain Boolean} indicating if Cameo/MagicDraw has an open document
     * 
     * @return a {@linkplain Observable} of {@linkplain Boolean}
     */
    @Override
    public Observable<Boolean> HasAnyOpenSessionObservable()
    {
        return this.projectEventListener.HasOneDocumentOpen().Observable();
    }
    
    /**
     * Gets a value indicating if Cameo/MagicDraw has an open document
     * 
     * @return a {@linkplain boolean}
     */
    @Override
    public boolean HasAnyOpenSession()
    {
        return this.projectEventListener.HasOneDocumentOpen().Value().booleanValue();
    }
    
    /**
     * Gets all the open project element flatten as one list
     * 
     * @return a {@linkplain Collection} of {@linkplain Element}
     */
    @Override
    public Collection<Element> GetAllProjectElements()
    {
        return this.GetProject().getAllElements().stream().filter(x -> x instanceof Element).map(Element.class::cast).collect(Collectors.toList());
    }
    
    /**
     * Gets the open project elements
     * 
     * @return a {@linkplain Collection} of {@linkplain Element}
     */
    @Override
    public Collection<Element> GetProjectElements()
    {
        return this.GetProject().getPrimaryModel().getPackagedElement().stream().filter(x -> x instanceof Element).map(Element.class::cast).collect(Collectors.toList());
    }
    
    /**
     * Gets the project root package
     * 
     * @return a {@linkplain Package}
     */
    @Override
    public Package GetModel()
    {
        return this.GetProject().getPrimaryModel();
    }

    /**
     * Gets the {@linkplain Project} name from the {@linkplain Session}
     * 
     * @return a name of the {@linkplain Project}
     */
	@Override
	public String GetProjectName()
	{
		return this.GetProject().getName();
	}
}
