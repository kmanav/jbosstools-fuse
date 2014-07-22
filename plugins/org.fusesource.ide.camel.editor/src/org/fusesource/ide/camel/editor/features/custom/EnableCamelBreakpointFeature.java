/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;

/**
 * @author lhein
 */
public class EnableCamelBreakpointFeature extends SetEndpointBreakpointFeature {
	
	/**
	 * creates the feature
	 * 
	 * @param fp
	 */
	public EnableCamelBreakpointFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement _pe = context.getPictogramElements()[0] instanceof Connection ? ((Connection) context.getPictogramElements()[0])
                .getStart().getParent() : context.getPictogramElements()[0];
        final Object bo = getBusinessObjectForPictogramElement(_pe);
       
        if (bo instanceof AbstractNode) {
        	AbstractNode _ep = (AbstractNode) bo;
            try {
            	IFile contextFile = getContextFile();
            	String fileName = contextFile.getName();
            	String projectName = contextFile.getProject().getName();
            	
            	IBreakpoint bp = CamelDebugUtils.getBreakpointForSelection(_ep.getId(), fileName, projectName);
            	if (bp != null && bp.isEnabled() == false) {
            		bp.setEnabled(true);
            	}
            } catch (CoreException e) {
                final IDiagramContainer container = getDiagramBehavior().getDiagramContainer();
                final Shell shell;
                if (container instanceof RiderDesignEditor) {
                    shell = ((RiderDesignEditor) container).getEditorSite().getShell();
                } else {
                    shell = Display.getCurrent().getActiveShell();
                }
                MessageDialog.openError(shell, "Error on enabling breakpoint", e.getStatus().getMessage());
                return;
            }
        }
        getDiagramBehavior().refreshRenderingDecorators(_pe);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "Enable Breakpoint";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Enables the breakpoint on the selected endpoint node";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getImageId()
	 */
	@Override
	public String getImageId() {
		return ImageProvider.IMG_GREENDOT;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		ICustomContext cc = (ICustomContext) context;
		PictogramElement _pe = cc.getPictogramElements()[0] instanceof Connection ? ((Connection) cc.getPictogramElements()[0])
                .getStart().getParent() : cc.getPictogramElements()[0];
        final Object bo = getBusinessObjectForPictogramElement(_pe);
       
        if (bo instanceof AbstractNode) {
        	AbstractNode _ep = (AbstractNode) bo;
        	IFile contextFile = getContextFile();
        	String fileName = contextFile.getName();
        	String projectName = contextFile.getProject().getName();
        	if (_ep.supportsBreakpoint()) {
        		IBreakpoint bp = CamelDebugUtils.getBreakpointForSelection(_ep.getId(), fileName, projectName);
        		try {
        			return bp != null && bp.isEnabled() == false;
        		} catch (CoreException ex) {
        			Activator.getLogger().error(ex);
        		}
        	}
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		return isAvailable(context);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.features.custom.SetEndpointBreakpointFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return false;
	}
}
