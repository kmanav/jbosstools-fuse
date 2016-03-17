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

package org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.pages.DataFormatSelectionPage;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormatModel;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author lhein
 */
public class NewDataFormatWizard extends Wizard implements GlobalConfigurationTypeWizard {

    private DataFormatModel dfModel;
	private Element dataformatNode;

	private DataFormatSelectionPage dataFormatSelectionPage;
	private DataFormat dataformatSelected;
	private CamelFile camelFile;

	public NewDataFormatWizard(CamelFile camelFile, DataFormatModel dfModel) {
		this.dfModel = dfModel;
		this.camelFile = camelFile;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		setWindowTitle(UIMessages.newGlobalConfigurationTypeDataFormatWizardDialogTitle);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		this.dataFormatSelectionPage = new DataFormatSelectionPage(dfModel);
		addPage(dataFormatSelectionPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		this.dataformatNode = null;
		return super.performCancel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		dataformatSelected = dataFormatSelectionPage.getDataFormatSelected();
		final String prefixNS = camelFile.getCamelContext().getXmlNode().getPrefix();
		dataformatNode = camelFile.createElement(dataformatSelected.getName(), prefixNS); // $NON-NLS-1$
		dataformatNode.setAttribute("id", dataFormatSelectionPage.getId()); //$NON-NLS-1$
		handleDescriptionNode(prefixNS);
		return true;
	}
	
	/**
	 * @param prefixNS
	 */
	private void handleDescriptionNode(final String prefixNS) {
		final String description = dataFormatSelectionPage.getDescriptionCreated();
		if (description != null && !description.isEmpty()) {
			Element descriptionNode = camelFile.getDocument().createElementNS(prefixNS, "description");
			descriptionNode.appendChild(camelFile.getDocument().createTextNode(description));
			dataformatNode.appendChild(descriptionNode);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard#getGlobalConfigrationElementNode()
	 */
	@Override
	public Element getGlobalConfigurationElementNode() {
		return this.dataformatNode;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard#setGlobalConfigrationElementNode(org.w3c.dom.Node)
	 */
	@Override
	public void setGlobalConfigurationElementNode(Element node) {
		this.dataformatNode = node;
	}
	
}
