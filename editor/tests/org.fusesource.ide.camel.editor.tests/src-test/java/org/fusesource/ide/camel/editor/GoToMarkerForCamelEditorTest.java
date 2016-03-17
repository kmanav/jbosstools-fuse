/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor;


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.validation.diagram.IFuseMarker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GoToMarkerForCamelEditorTest {

	@Mock
	private CamelEditor camelEditor;
	@Mock
	private CamelDesignEditor designEditor;
	@Mock
	private CamelGlobalConfigEditor configEditor;
	@Mock
	private StructuredTextEditor sourceEditor;
	@Mock
	private IGotoMarker sourceGotoMarker;
	@Mock
	private IMarker marker;
	@Mock
	private IResource resource;
	@Mock
	private Node xmlNode;

	private GoToMarkerForCamelEditor goToMarkerForCamelEditor;


	@Before
	public void setup() throws CoreException {
		goToMarkerForCamelEditor = new GoToMarkerForCamelEditor(camelEditor);
		doReturn(sourceEditor).when(camelEditor).getSourceEditor();
		doReturn(sourceGotoMarker).when(sourceEditor).getAdapter(IGotoMarker.class);
		doReturn(designEditor).when(camelEditor).getDesignEditor();
		doReturn(configEditor).when(camelEditor).getGlobalConfigEditor();
		doReturn(true).when(marker).exists();
		doReturn(10).when(marker).getAttribute(IMarker.LINE_NUMBER);
	}

	@Test
	public void testGotoMarkerSource() throws Exception {
		goToMarkerForCamelEditor.gotoMarker(marker);
		verify(sourceEditor).getAdapter(IGotoMarker.class);
		verify(sourceGotoMarker).gotoMarker(marker);
		verify(camelEditor).setActiveEditor(sourceEditor);
	}

	@Test
	public void testGotoMarkerDesignEditor() throws Exception {
		doReturn("nodeId").when(marker).getAttribute(IFuseMarker.CAMEL_ID);

		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		endPoint.setId("nodeId");
		route.addChildElement(endPoint);
		endPoint.setParent(route);
		doReturn(camelFile).when(designEditor).getModel();

		goToMarkerForCamelEditor.gotoMarker(marker);

		verify(camelEditor).setActiveEditor(designEditor);
		// Ensure Source Editor is no called
		verify(sourceEditor, Mockito.never()).getAdapter(IGotoMarker.class);
		verify(sourceGotoMarker, Mockito.never()).gotoMarker(marker);
	}

	@Test
	public void testGotoMarkerDesignEditorFallBackSource() throws Exception {
		doReturn("nodeId").when(marker).getAttribute(IFuseMarker.CAMEL_ID);

		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		endPoint.setId("nodeIdDifferent");
		route.addChildElement(endPoint);
		endPoint.setParent(route);
		doReturn(camelFile).when(designEditor).getModel();

		goToMarkerForCamelEditor.gotoMarker(marker);
		verify(sourceEditor).getAdapter(IGotoMarker.class);
		verify(sourceGotoMarker).gotoMarker(marker);
		verify(camelEditor).setActiveEditor(sourceEditor);
	}

}
