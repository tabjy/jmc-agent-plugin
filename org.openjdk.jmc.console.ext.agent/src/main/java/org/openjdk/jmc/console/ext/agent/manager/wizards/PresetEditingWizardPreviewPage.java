/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020, Red Hat Inc. All rights reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The contents of this file are subject to the terms of either the Universal Permissive License
 * v 1.0 as shown at http://oss.oracle.com/licenses/upl
 *
 * or the following license:
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjdk.jmc.console.ext.agent.manager.wizards;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.openjdk.jmc.console.ext.agent.manager.model.IPreset;
import org.openjdk.jmc.console.ext.agent.tabs.editor.internal.XmlEditor;
import org.openjdk.jmc.ui.misc.DisplayToolkit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class PresetEditingWizardPreviewPage extends WizardPage {
	private static final String PAGE_NAME = "Agent Preset Editing";
	private static final String MESSAGE_PRESET_EDITING_WIZARD_PREVIEW_PAGE_TITLE = "Preview Preset";
	private static final String MESSAGE_PRESET_EDITING_WIZARD_PREVIEW_PAGE_DESCRIPTION = "Inspects the generated XML before it is saved. Click Back to make any modifications if needed. Click Finish to save.";

	private final IPreset preset;

	protected PresetEditingWizardPreviewPage(IPreset preset) {
		super(PAGE_NAME);

		this.preset = preset;
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		setTitle(MESSAGE_PRESET_EDITING_WIZARD_PREVIEW_PAGE_TITLE);
		setDescription(MESSAGE_PRESET_EDITING_WIZARD_PREVIEW_PAGE_DESCRIPTION);

		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite container = new Composite(sc, SWT.NONE);
		sc.setContent(container);

		// TODO: create preview page control here
		container.setLayout(new FillLayout());

		try {
			createPreviewEditor(container);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	private void createPreviewEditor(Composite container) throws PartInitException {
		XmlEditor editor = new XmlEditor();
		IEditorSite site = new PreviewEditorSite();
		IEditorInput input = new PreviewEditorInput("<jfragent>\n</jfragent>");
		editor.init(site, input);

		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayout(new FillLayout());
		editor.createPartControl(parent);
	}

	private static class PreviewEditorSite implements IEditorSite {

		@Override
		public IEditorActionBarContributor getActionBarContributor() {
			return null;
		}

		@Override
		public IActionBars getActionBars() {
			return null;
		}

		@Override
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider iSelectionProvider, boolean b) {
			// noop
		}

		@Override
		public void registerContextMenu(
			String s, MenuManager menuManager, ISelectionProvider iSelectionProvider, boolean b) {
			// noop
		}

		@Override
		public String getId() {
			return "";
		}

		@Override
		public String getPluginId() {
			return "";
		}

		@Override
		public String getRegisteredName() {
			return null;
		}

		@Override
		public void registerContextMenu(String s, MenuManager menuManager, ISelectionProvider iSelectionProvider) {
			// noop
		}

		@Override
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider iSelectionProvider) {
			// noop
		}

		@Override
		public IKeyBindingService getKeyBindingService() {
			return null;
		}

		@Override
		public IWorkbenchPart getPart() {
			return null;
		}

		@Override
		public IWorkbenchPage getPage() {
			return null;
		}

		@Override
		public ISelectionProvider getSelectionProvider() {
			return null;
		}

		@Override
		public Shell getShell() {
			return Display.getCurrent().getActiveShell();
		}

		@Override
		public IWorkbenchWindow getWorkbenchWindow() {
			return new IWorkbenchWindow() {
				@Override
				public boolean close() {
					return false;
				}

				@Override
				public IWorkbenchPage getActivePage() {
					return null;
				}

				@Override
				public IWorkbenchPage[] getPages() {
					return new IWorkbenchPage[0];
				}

				@Override
				public IPartService getPartService() {
					return new IPartService() {
						@Override
						public void addPartListener(IPartListener iPartListener) {
							
						}

						@Override
						public void addPartListener(IPartListener2 iPartListener2) {

						}

						@Override
						public IWorkbenchPart getActivePart() {
							return null;
						}

						@Override
						public IWorkbenchPartReference getActivePartReference() {
							return null;
						}

						@Override
						public void removePartListener(IPartListener iPartListener) {

						}

						@Override
						public void removePartListener(IPartListener2 iPartListener2) {

						}
					};
				}

				@Override
				public ISelectionService getSelectionService() {
					return null;
				}

				@Override
				public Shell getShell() {
					return PreviewEditorSite.this.getShell();
				}

				@Override
				public IWorkbench getWorkbench() {
					return null;
				}

				@Override
				public boolean isApplicationMenu(String s) {
					return false;
				}

				@Override
				public IWorkbenchPage openPage(String s, IAdaptable iAdaptable) throws WorkbenchException {
					return null;
				}

				@Override
				public IWorkbenchPage openPage(IAdaptable iAdaptable) throws WorkbenchException {
					return null;
				}

				@Override
				public void run(boolean b, boolean b1, IRunnableWithProgress iRunnableWithProgress)
						throws InvocationTargetException, InterruptedException {
					DisplayToolkit.inDisplayThread().execute(() -> {
						try {
							iRunnableWithProgress.run(new IProgressMonitor() {
								@Override
								public void beginTask(String s, int i) {
									
								}
	
								@Override
								public void done() {
	
								}
	
								@Override
								public void internalWorked(double v) {
	
								}
	
								@Override
								public boolean isCanceled() {
									return false;
								}
	
								@Override
								public void setCanceled(boolean b) {
	
								}
	
								@Override
								public void setTaskName(String s) {
	
								}
	
								@Override
								public void subTask(String s) {
	
								}
	
								@Override
								public void worked(int i) {
	
								}
							});
						} catch (InvocationTargetException | InterruptedException e) {
							e.printStackTrace();
						}
					});
				}

				@Override
				public void setActivePage(IWorkbenchPage iWorkbenchPage) {

				}

				@Override
				public IExtensionTracker getExtensionTracker() {
					return null;
				}

				@Override
				public boolean isClosing() {
					return false;
				}

				@Override
				public void addPageListener(IPageListener iPageListener) {

				}

				@Override
				public void addPerspectiveListener(IPerspectiveListener iPerspectiveListener) {

				}

				@Override
				public void removePageListener(IPageListener iPageListener) {

				}

				@Override
				public void removePerspectiveListener(IPerspectiveListener iPerspectiveListener) {

				}

				@Override
				public <T> T getService(Class<T> aClass) {
					return null;
				}

				@Override
				public boolean hasService(Class<?> aClass) {
					return false;
				}
			};
		}

		@Override
		public void setSelectionProvider(ISelectionProvider iSelectionProvider) {
			// noop
		}

		@Override
		public <T> T getAdapter(Class<T> aClass) {
			return null;
		}

		@Override
		public <T> T getService(Class<T> aClass) {
			return null;
		}

		@Override
		public boolean hasService(Class<?> aClass) {
			return false;
		}
	}

	private static class PreviewEditorInput extends PlatformObject implements IStorageEditorInput {

		String source;
		IStorage storage;

		public PreviewEditorInput(String source) {
			this.source = source;
			this.storage = new PreviewEditorStorage(source);
		}

		@Override
		public IStorage getStorage() throws CoreException {
			return storage;
		}

		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public IPersistableElement getPersistable() {
			return null;
		}

		@Override
		public String getToolTipText() {
			return null;
		}
	}

	private static class PreviewEditorStorage implements IStorage {

		private String source;

		public PreviewEditorStorage(String source) {
			this.source = source;
		}

		@Override
		public InputStream getContents() {
			return new ByteArrayInputStream(source.getBytes());
		}

		@Override
		public IPath getFullPath() {
			return null;
		}

		@Override
		public String getName() {
			return "preview.xml";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public <T> T getAdapter(Class<T> aClass) {
			return null;
		}
	}
}
