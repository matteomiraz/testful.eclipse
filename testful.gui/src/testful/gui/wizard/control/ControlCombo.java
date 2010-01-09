package testful.gui.wizard.control;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class ControlCombo<T> {

	private Combo combo;

	public ControlCombo(Composite parent, T[] values, T defaultValue, ITestfulControl<T> control) {
		this(parent, getGridData(), values, defaultValue, control);
	}

	public ControlCombo(Composite parent, T[] values, T defaultValue) {
		this(parent, getGridData(), values, defaultValue, null);
	}

	public ControlCombo(Composite parent, GridData gdt, final T[] values, T defaultValue) {
		this(parent, gdt, values, defaultValue, null);
	}

	public ControlCombo(Composite parent, GridData gdt, final T[] values, T defaultValue, final ITestfulControl<T> control) {
		combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(gdt);

		for (int i = 0; i < values.length; i++) {
			combo.add(values[i].toString());
			if(values[i] == defaultValue) combo.select(i);
		}

		if(control != null) {
			combo.addListener(SWT.Selection, new Listener() {
				private int selection = combo.getSelectionIndex();

				@Override
				public void handleEvent(Event e) {
					try {
						if (combo.getSelectionIndex() > -1)
							control.update(values[combo.getSelectionIndex()]);
						else
							control.update(null);

						selection = combo.getSelectionIndex();

					} catch (Exception e1) {
						MessageDialog.openInformation(combo.getShell(), "Testful", "Insert value is not valid!\n" + e1.getMessage());
						combo.select(selection);
						combo.setFocus();
					}
				}
			});

			try {
				control.update(defaultValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static GridData getGridData() {
		GridData gdtH = new GridData();
		gdtH.horizontalAlignment = GridData.FILL;
		gdtH.grabExcessHorizontalSpace = true;
		return gdtH;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> ControlCombo<T> getEnumCombo(Composite parent, Class<T> type, T defaultValue, ITestfulControl<T> control) {
		T[] values = null;
		try {
			values = (T[]) type.getMethod("values").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ControlCombo<T>(parent, values, defaultValue, control);
	}

	public static ControlCombo<Boolean> getBooleanCombo(Composite parent, Boolean isTrue, ITestfulControl<Boolean> control) {
		return new ControlCombo<Boolean>(parent, new Boolean[] {true, false}, isTrue, control);
	}
}
