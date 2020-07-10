package org.openjdk.jmc.console.ext.agent.manager.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.openjdk.jmc.ui.column.ColumnBuilder;
import org.openjdk.jmc.ui.column.ColumnManager;
import org.openjdk.jmc.ui.column.IColumn;
import org.openjdk.jmc.ui.misc.OptimisticComparator;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseWizardPage extends WizardPage {

	protected BaseWizardPage(String pageName) {
		super(pageName);
	}

	protected BaseWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	protected static Composite createComposite(Composite parent) {
		return new Composite(parent, SWT.NONE);
	}

	protected static Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	protected static Text createText(Composite parent, String hint) {
		Text text = new Text(parent, SWT.BORDER);
		text.setMessage(hint);
		text.setEnabled(true);
		return text;
	}

	protected static Text createMultiText(Composite parent, String hint) {
		Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		// FIXME: Multi line Text field (SWT.MULTI) does not support Text.setMessage
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=328832
		text.setMessage(hint);
		text.setEnabled(true);
		return text;
	}

	protected static Label createSeparator(Composite parent) {
		return new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
	}

	protected static Combo createCombo(Composite parent, String[] items) {
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(items);
		return combo;
	}

	protected static Button createButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		return button;
	}

	protected static Button createCheckbox(Composite parent, String text) {
		Button checkbox = new Button(parent, SWT.CHECK);
		checkbox.setText(text);
		return checkbox;
	}

	protected static Spinner createSpinner(Composite parent) {
		return new Spinner(parent, SWT.NONE);
	}

	protected static void setText(Text receiver, String text) {
		text = text == null ? "" : text;
		receiver.setText(text);
	}

	protected static void setText(Combo receiver, String text) {
		text = text == null ? "" : text;
		receiver.setText(text);
	}

	protected static Text createTextInput(Composite parent, int cols, String label, String hint) {
		Label l = createLabel(parent, label);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 0));

		Text t = createText(parent, hint);
		t.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, cols - 2, 0));

		return t;
	}

	protected static Text createMultiTextInput(Composite parent, int cols, String label, String hint) {
		Label l = createLabel(parent, label);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, cols, 0));

		Text t = createMultiText(parent, hint);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true, cols, 0);
		gd.heightHint = 100;
		t.setLayoutData(gd);

		return t;
	}

	protected static Text[] createMultiInputTextInput(Composite parent, int cols, String label, String[] hints) {
		Label l = createLabel(parent, label);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 0));

		Composite container = createComposite(parent);
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, cols - 2, 0));
		container.setLayout(new FillLayout());

		Text[] t = new Text[hints.length];
		for (int i = 0; i < hints.length; i++) {
			t[i] = createText(container, hints[i]);
		}

		return t;
	}

	protected static Combo createComboInput(Composite parent, int cols, String label, String[] items) {
		Label l = createLabel(parent, label);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 0));

		Combo c = createCombo(parent, items);
		c.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, cols - 2, 0));

		return c;
	}

	protected static Button createCheckboxInput(Composite parent, int cols, String text) {
		Button b = createCheckbox(parent, text);
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, cols, 0));

		return b;
	}

	protected static Spinner createSpinnerInput(Composite parent, int cols, String label) {
		Label l = createLabel(parent, label);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 0));

		Spinner s = createSpinner(parent);
		s.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, cols - 2, 0));

		return s;
	}

	protected static abstract class TableInspector extends Composite {
		public static final int MULTI = 1;
		public static final int SHOW_HEADER = 1 << 1;
		public static final int ADD_BUTTON = 1 << 2;
		public static final int EDIT_BUTTON = 1 << 3;
		public static final int DUPLICATE_BUTTON = 1 << 4;
		public static final int REMOVE_BUTTON = 1 << 5;
		public static final int IMPORT_FILES_BUTTON = 1 << 6;
		public static final int EXPORT_FILE_BUTTON = 1 << 7;

		private static final String LABEL_ADD_BUTTON = "Add...";
		private static final String LABEL_EDIT_BUTTON = "Edit";
		private static final String LABEL_DUPLICATE_BUTTON = "Duplicate";
		private static final String LABEL_REMOVE_BUTTON = "Remove";
		private static final String LABEL_IMPORT_FILES_BUTTON = "Import Files...";
		private static final String LABEL_EXPORT_FILE_BUTTON = "Export File...";

		private final int options;
		private final List<IColumn> columns = new ArrayList<>();
		private final TableViewer tableViewer;
		private final Composite buttonContainer;

		private Button addButton;
		private Button editButton;
		private Button duplicateButton;
		private Button removeButton;
		private Button importFilesButton;
		private Button exportFileButton;

		protected TableInspector(Composite parent, int options) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout(2, false));

			this.options = options;

			int style = SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION;
			if ((options & MULTI) != 0) {
				style |= SWT.MULTI;
			}
			tableViewer = new TableViewer(this, style);
			tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			addColumns();
			ColumnManager.build(tableViewer, columns, null);
			tableViewer.getTable().setHeaderVisible((options & SHOW_HEADER) != 0);

			buttonContainer = new Composite(this, SWT.NONE);
			buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, true));
			GridLayout layout = new GridLayout(1, true);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			buttonContainer.setLayout(layout);
			addButtons();

			bindListeners();
		}

		protected final void addColumn(String name, String id, ColumnLabelProvider labelProvider) {
			columns.add(new ColumnBuilder(name, id, labelProvider).comparator(new OptimisticComparator(labelProvider))
					.build());
		}

		protected final void addColumn(String id, ColumnLabelProvider labelProvider) {
			columns.add(new ColumnBuilder("", id, labelProvider).comparator(new OptimisticComparator(labelProvider))
					.build());
		}

		protected abstract void addColumns();

		protected final Button addButton(String text) {
			Button b = createButton(buttonContainer, text);
			b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			return b;
		}

		protected void addButtons() {
			if ((options & ADD_BUTTON) != 0) {
				addButton = addButton(LABEL_ADD_BUTTON);
			}

			if ((options & EDIT_BUTTON) != 0) {
				editButton = addButton(LABEL_EDIT_BUTTON);
			}

			if ((options & DUPLICATE_BUTTON) != 0) {
				duplicateButton = addButton(LABEL_DUPLICATE_BUTTON);
			}

			if ((options & REMOVE_BUTTON) != 0) {
				removeButton = addButton(LABEL_REMOVE_BUTTON);
			}

			if ((options & IMPORT_FILES_BUTTON) != 0) {
				importFilesButton = addButton(LABEL_IMPORT_FILES_BUTTON);
			}

			if ((options & EXPORT_FILE_BUTTON) != 0) {
				exportFileButton = addButton(LABEL_EXPORT_FILE_BUTTON);
			}
		}

		public Button getAddButton() {
			return addButton;
		}

		public Button getEditButton() {
			return editButton;
		}

		public Button getDuplicateButton() {
			return duplicateButton;
		}

		public Button getRemoveButton() {
			return removeButton;
		}

		public Button getImportFilesButton() {
			return importFilesButton;
		}

		public Button getExportFileButton() {
			return exportFileButton;
		}

		public void setContentProvider(IContentProvider contentProvider) {
			tableViewer.setContentProvider(contentProvider);
		}

		public void setInput(Object input) {
			tableViewer.setInput(input);
		}

		protected TableViewer getViewer() {
			return tableViewer;
		}

		protected void bindListeners() {
			if (addButton != null) {
				addButton.addListener(SWT.Selection, e -> onAddButtonSelected(tableViewer.getStructuredSelection()));
			}

			if (editButton != null) {
				editButton.addListener(SWT.Selection, e -> onEditButtonSelected(tableViewer.getStructuredSelection()));
			}

			if (duplicateButton != null) {
				duplicateButton.addListener(SWT.Selection,
						e -> onDuplicateButtonSelected(tableViewer.getStructuredSelection()));
			}

			if (removeButton != null) {
				removeButton.addListener(SWT.Selection,
						e -> onRemoveButtonSelected(tableViewer.getStructuredSelection()));
			}

			if (importFilesButton != null) {
				importFilesButton.addListener(SWT.Selection,
						e -> onImportFilesButtonSelected(tableViewer.getStructuredSelection()));
			}

			if (exportFileButton != null) {
				exportFileButton.addListener(SWT.Selection,
						e -> onExportFileButtonSelected(tableViewer.getStructuredSelection()));
			}

			tableViewer.addSelectionChangedListener(e -> toggleButtonAvailabilityBy(e.getStructuredSelection()));
			toggleButtonAvailabilityBy(tableViewer.getStructuredSelection());
		}

		protected void toggleButtonAvailabilityBy(IStructuredSelection selection) {
			if (addButton != null) {
				addButton.setEnabled(true);
			}

			if (editButton != null) {
				editButton.setEnabled(selection.size() == 1);
			}

			if (duplicateButton != null) {
				duplicateButton.setEnabled(selection.size() == 1);
			}

			if (removeButton != null) {
				removeButton.setEnabled(!selection.isEmpty());
			}

			if (importFilesButton != null) {
				importFilesButton.setEnabled(true);
			}

			if (exportFileButton != null) {
				exportFileButton.setEnabled(selection.size() == 1);
			}
		}

		protected void onExportFileButtonSelected(IStructuredSelection selection) {
		}

		protected void onImportFilesButtonSelected(IStructuredSelection selection) {
		}

		protected void onRemoveButtonSelected(IStructuredSelection selection) {
		}

		protected void onDuplicateButtonSelected(IStructuredSelection selection) {
		}

		protected void onEditButtonSelected(IStructuredSelection selection) {
		}

		protected void onAddButtonSelected(IStructuredSelection selection) {
		}
	}
}
