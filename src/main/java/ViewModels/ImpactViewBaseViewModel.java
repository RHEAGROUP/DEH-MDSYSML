/*
 * ImpactViewBaseViewModel.java
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
package ViewModels;

import org.netbeans.swing.outline.OutlineModel;

import DstController.IDstController;
import Enumerations.MappingDirection;
import HubController.IHubController;
import Utils.Ref;
import static Utils.Operators.Operators.AreTheseEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import ViewModels.ObjectBrowser.ElementDefinitionTree.Rows.ElementUsageRowViewModel;
import ViewModels.ObjectBrowser.Interfaces.IHaveContainedRows;
import ViewModels.ObjectBrowser.Interfaces.IRowViewModel;
import ViewModels.ObjectBrowser.Interfaces.IThingRowViewModel;
import ViewModels.ObjectBrowser.Rows.ThingRowViewModel;
import cdp4common.commondata.DefinedThing;
import cdp4common.commondata.Thing;
import cdp4common.engineeringmodeldata.ElementDefinition;
import cdp4common.engineeringmodeldata.Iteration;
import cdp4common.engineeringmodeldata.Parameter;
import cdp4common.engineeringmodeldata.ParameterOrOverrideBase;
import cdp4common.engineeringmodeldata.ParameterOverride;

/**
 * The {@linkplain ImpactViewBaseViewModel} is the main abstract base view model for the impact views tab panel
 * such as the {@linkplain RequirementImpactViewViewModel} and the {@linkplain ElementDefinitionImpactViewViewModel}
 * 
 * @param <TThing> the type of {@linkplain Thing} the concrete class deals with
 */
public abstract class ImpactViewBaseViewModel<TThing extends Thing> extends ObjectBrowserViewModel
{
    /**
     * The {@linkplain IDstController}
     */
    protected IDstController dstController;
    
    /**
     * The {@linkplain Class} of the {@linkplain TThing} for future check
     */
    private Class<TThing> clazz;

    /**
     * Initializes a new {@linkplain ImpactViewBaseViewModel}
     * 
     * @param HubController the {@linkplain IHubController} instance
     * @param dstController the {@linkplain IDstController} instance
     * @param clazz the {@linkplain Class} of the {@linkplain TThing} for future check
     */
    protected ImpactViewBaseViewModel(IHubController hubController, IDstController dstController, Class<TThing> clazz)
    {
        super(hubController);
        this.dstController = dstController;
        this.clazz = clazz;

        this.InitializesObservables();
    }

    /**
     * Initializes the needed subscription on {@linkplain Observable}
     */
    @SuppressWarnings("unchecked")
    private void InitializesObservables()
    {
        this.dstController.GetDstMapResult()
            .ItemsAdded()
            .subscribe(x -> this.ComputeDifferences(), e -> this.logger.catching(e));
        
        this.dstController.GetDstMapResult()
            .IsEmptyObservable()
            .subscribe(isEmpty ->
            {
                if(Boolean.TRUE.equals(isEmpty))
                {
                    this.UpdateBrowserTrees(this.hubController.GetIsSessionOpen());
                }
            });
        
        this.dstController.GetSelectedDstMapResultForTransfer()
            .ItemsAdded()
            .subscribe(x ->
            {
                for(Thing thing : x.stream()
                        .filter(t -> this.clazz.isInstance(t) 
                                || (this.clazz == ElementDefinition.class && (t instanceof Parameter || t instanceof ParameterOverride)))
                        .collect(Collectors.toList()))
                {
                    this.logger.debug(String.format("Setting is selected TRUE for %s", thing.getUserFriendlyName()));
                    this.SwitchIsSelected(thing, true);
                }
                
                this.shouldRefreshTree.Value(true);
            });

        this.dstController.GetSelectedDstMapResultForTransfer()
            .ItemRemoved()
            .subscribe(x -> 
            {
                this.logger.debug(String.format("Setting is selected FALSE for %s", x.getUserFriendlyName()));
                this.SwitchIsSelected(x, false);
                this.shouldRefreshTree.Value(true);
            });
    }

    /**
     * Sets is selected property on the row view model that represents the provided {@linkplain Thing}
     * 
     * @param thing The {@linkplain Thing} to find the corresponding row view model
     * @param shouldSelect a value indicating whether the row view model should set as selected
     */
    @SuppressWarnings("unchecked")
    private void SwitchIsSelected(Thing thing, boolean shouldSelect)
    {
        IThingRowViewModel<TThing> viewModel = null;
        
        if(thing instanceof Parameter && this.clazz.isInstance(thing.getContainer()) && thing.getContainer() instanceof ElementDefinition)
        {
            IThingRowViewModel<TThing> parentViewModel = this.GetRowViewModelFromThing((TThing)thing.getContainer());
            
            for (IThingRowViewModel<TThing> childRow : ((IHaveContainedRows<IThingRowViewModel<TThing>>)parentViewModel).GetContainedRows())
            {
                if(AreTheseEquals(childRow.GetThing().getIid(), thing.getIid()))
                {
                    viewModel = childRow;
                }
            }            
        }    
        else if(this.clazz.isInstance(thing))
        {
            viewModel = this.GetRowViewModelFromThing((TThing)thing);
        }
        
        if(viewModel == null)
        {
            return;
        }
        
        this.SwitchIsSelected(viewModel, shouldSelect);
    }
    
    /**
     * Sets is selected property on the provided row view model that represents the provided {@linkplain Thing}
     * 
     * @param rowViewModel the {@linkplain IThingRowViewModel}
     * @param shouldSelect a value indicating whether the row view model should set as selected
     */
    private void SwitchIsSelected(IThingRowViewModel<?> rowViewModel, boolean shouldSelect)
    {
        if(!rowViewModel.GetIsSelected() && shouldSelect)
        {
            rowViewModel.SetIsSelected(true);
        }
        else if(rowViewModel.GetIsSelected() && !shouldSelect)
        {
            rowViewModel.SetIsSelected(false);            
        }        
    }

    /**
     * Gets the {@linkplain IThingRowViewModel} that represent the {@linkplain Thing}
     * 
     * @param thing the {@linkplain TThing} 
     * @return the {@linkplain IThingRowViewModel} of {@linkplain TThing}
     */
    protected abstract IThingRowViewModel<TThing> GetRowViewModelFromThing(TThing thing);
    
    /**
     * Updates this view model {@linkplain TreeModel}
     * 
     * @param isConnected a value indicating whether the session is open
     */
    @Override
    protected void UpdateBrowserTrees(Boolean isConnected)
    {
        if(Boolean.TRUE.equals(isConnected))
        {
            this.SetOutlineModel(this.hubController.GetOpenIteration());
        }
    
        this.isTheTreeVisible.Value(isConnected);
    }
    
    /**
     * Verifies that the two provided {@linkplain Thing} have the same id
     * 
     * @param thing0 the first {@linkplain Thing} to compare
     * @param thing1 the second {@linkplain Thing} to compare
     * @return a {@linkplain boolean}
     */
    protected boolean DoTheseThingsRepresentTheSameThing(Thing thing0, Thing thing1)
    {
        return AreTheseEquals(thing0.getIid(), thing1.getIid());
    }

    /**
     * Verifies that the provided {@linkplain Thing} is new or modified
     * 
     * @param thing the thing to verify
     * @return a value indicating whether the thing is new or modified
     */
    protected <TCurrentThing extends Thing> boolean IsThingNewOrModified(Thing thing, Class<TCurrentThing> clazz)
    {
        return thing.getOriginal() != null || !this.hubController.TryGetThingById(thing.getIid(), new Ref<TCurrentThing>(clazz));
    }

    /**
     * Computes the differences between the current model and the elements present in the {@linkplain IDstController}.{@linkplain GetDstMapResult}
     */
    @SuppressWarnings("unchecked")
    protected void ComputeDifferences()
    {
        Iteration iteration = this.hubController.GetOpenIteration().clone(false);
        
        for (DefinedThing thing : this.dstController.GetDstMapResult()
                .stream().map(x -> x.GetHubElement()).collect(Collectors.toList()))
        {
            if(this.clazz.isInstance(thing))
            {
                this.ComputeDifferences(iteration, (TThing)thing);
            }
        }
        
        this.SetOutlineModel(iteration);
    }

    /**
     * Creates a new {@linkplain OutlineModel} based on the provided {@linkplain Iteration}
     * 
     * @param iteration the {@linkplain Iteration}
     */
    protected abstract OutlineModel CreateNewModel(Iteration iteration);
    
    /**
     * Computes the difference for the provided {@linkplain Thing}
     * 
     * @param iteration the cloned iteration for display purpose
     * @param thing the current {@linkplain Thing} thing
     */
    protected abstract void ComputeDifferences(Iteration iteration, TThing thing);
    
    /**
     * Updates the {@linkplain browserTreeModel} based on the provided {@linkplain Iteration}
     * 
     * @param iteration the {@linkplain Iteration}
     */
    protected void SetOutlineModel(Iteration iteration)
    {
        OutlineModel model = this.CreateNewModel(iteration);
        this.UpdateHighlightOnRows(model);
        this.browserTreeModel.Value(model);
    }

    /**
     * Updates the <code>IsHighlighted</code> property on each row of the specified model
     * 
     * @param model the {@linkplain OutlineModel}
     */
    @SuppressWarnings("unchecked")
    private void UpdateHighlightOnRows(OutlineModel model)
    {
        Object root = model.getRoot();
        
        if(root instanceof IHaveContainedRows)
        {
            this.UpdateHighlightOnRows((IHaveContainedRows<IRowViewModel>) root, false);
        }
    }

    /**
     * Updates the <code>IsHighlighted</code> property on each row of the specified model
     * 
     * @param rowViewModel a {@linkplain IHaveContainedRows} row view model
     * @param a value indicating whether child rows should be highlighted
     */
    @SuppressWarnings("unchecked")
    private void UpdateHighlightOnRows(IHaveContainedRows<IRowViewModel> rowViewModel, boolean shouldHighlight)
    {
        for (IRowViewModel row : rowViewModel.GetContainedRows())
        {
            if(row instanceof IThingRowViewModel)
            {
                IThingRowViewModel<?> thingRowViewModel = (IThingRowViewModel<?>)row;
                
                boolean isHighlighted = shouldHighlight || this.dstController.GetDstMapResult().stream()
                        .anyMatch(r -> AreTheseEquals(r.GetHubElement().getIid(), thingRowViewModel.GetThing().getIid()));
                
                thingRowViewModel.SetIsHighlighted(isHighlighted);
                thingRowViewModel.GetParent().SetIsHighlighted(isHighlighted);
                
                if(row instanceof IHaveContainedRows)
                {
                    this.UpdateHighlightOnRows((IHaveContainedRows<IRowViewModel>)row, isHighlighted);
                }
            }
        }
    }

    /**
     * Compute eligible rows where the represented {@linkplain Thing} can be transfered,
     * and return the filtered collection for feedback application on the tree
     * 
     * @param selectedRow the selected row view model {@linkplain IThingRowViewModel}
     */
    @Override
    public void OnSelectionChanged(ThingRowViewModel<Thing> selectedRow) 
    {
        if(selectedRow != null && selectedRow.GetThing() != null)
        {
            Ref<Boolean> refShouldSelect = new Ref<>(Boolean.class, null);
                        
            Collection<IThingRowViewModel<?>> allSelectableRows = this.GetAllSelectableRows(selectedRow, null);
            
            for(IThingRowViewModel<?> rowViewModel : allSelectableRows)
            {
                this.AddOrRemoveSelectedRowToTransfer(rowViewModel, refShouldSelect);
            }

            if(selectedRow.GetThing() instanceof ParameterOrOverrideBase)
            {
                if(refShouldSelect.Get().booleanValue())
                {
                    selectedRow.GetParent().SetIsSelected(true);
                    this.AddOrRemoveSelectedRowToTransfer((IThingRowViewModel<?>) selectedRow.GetParent(), refShouldSelect);
                }
            }
        }
    }

    /**
     * Gets all the select-able rows recursively from the children of the provided {@linkplain IThingRowViewModel}, 
     * and adds them to the specified {@linkplain Collection} of {@linkplain IThingRowViewModel}
     * 
     * @param selectedRow the {@linkplain IThingRowViewModel}
     * @param selectableRows {@linkplain Collection} of {@linkplain IThingRowViewModel}
     * @return the {@linkplain Collection} of select-able {@linkplain IThingRowViewModel}
     */
    @SuppressWarnings("unchecked")
    private Collection<IThingRowViewModel<?>> GetAllSelectableRows(IThingRowViewModel<?> selectedRow, Collection<IThingRowViewModel<?>> selectableRows)
    {
        if(selectableRows == null)
        {
            selectableRows = new ArrayList<>();
        }
        
        if(selectedRow == null || !selectedRow.GetIsHighlighted())
        {
            return selectableRows;
        }

        this.logger.debug(String.format("Current row %s is eligible %s", selectedRow.GetThing().getUserFriendlyName(), this.dstController.GetDstMapResult().stream()
                .anyMatch(x -> AreTheseEquals(x.GetHubElement().getIid(), selectedRow.GetThing().getIid())
                        || AreTheseEquals(x.GetHubElement().getIid(), selectedRow.GetThing().getContainer().getIid()))));
        
        if(this.dstController.GetDstMapResult().stream()
                    .anyMatch(x -> AreTheseEquals(x.GetHubElement().getIid(), selectedRow.GetThing().getIid())
                            || AreTheseEquals(x.GetHubElement().getIid(), selectedRow.GetThing().getContainer().getIid())))
        {
            selectableRows.add(selectedRow);
        }
                
        if(selectedRow instanceof IHaveContainedRows)
        {
            for (IThingRowViewModel<?> childRow : ((IHaveContainedRows<IThingRowViewModel<?>>)selectedRow).GetContainedRows())
            {
                if(childRow instanceof ElementUsageRowViewModel)
                {
                    this.GetAllSelectableRows(this.GetRowViewModelFromThing((TThing) ((ElementUsageRowViewModel)childRow).GetThing().getElementDefinition()), selectableRows);
                }
                
                this.GetAllSelectableRows(childRow, selectableRows);
            }
        }
        
        return selectableRows;
    }

    /**
     * Adds or remove the {@linkplain Thing} to/from the relevant collection depending on the {@linkplain MappingDirection}
     * 
     * @param rowViewModel the {@linkplain IThingRowViewModel} that represents the {@linkplain Thing}
     * @param shouldSelect a {@linkplain Ref} of a {@linkplain Boolean} value which indicates whether the provided rowViewModel should be selected
     */
    private void AddOrRemoveSelectedRowToTransfer(IThingRowViewModel<?> rowViewModel, Ref<Boolean> shouldSelect)
    {
        if(!shouldSelect.HasValue())
        {
            shouldSelect.Set(rowViewModel.SwitchIsSelectedValue());
        }
        
        if(shouldSelect.Get().booleanValue() && this.dstController.GetSelectedDstMapResultForTransfer().stream()
                .noneMatch(x -> AreTheseEquals(rowViewModel.GetThing().getIid(), x.getIid())))
        {
            this.dstController.GetSelectedDstMapResultForTransfer().add(rowViewModel.GetThing());
        }
        else if(!shouldSelect.Get().booleanValue())
        {
            this.dstController.GetSelectedDstMapResultForTransfer().RemoveOne(rowViewModel.GetThing());
        }

        this.SwitchIsSelected(rowViewModel, shouldSelect.Get().booleanValue());
    }
}
