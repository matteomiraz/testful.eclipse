package testful.gui.control;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


public class ControlInteger extends ControlText {

	public ControlInteger(Composite parent, Integer value, ITestfulControl<Integer> control) {
		super(parent, Integer.toString(value), createTextControl(control));
	}

	public ControlInteger(Composite parent, GridData gdt, Integer value, ITestfulControl<Integer> control) {
		super(parent, gdt, Integer.toString(value), createTextControl(control));
	}

	private static ITestfulControl<String> createTextControl(final ITestfulControl<Integer> control) {
		return ControlText.getRequired(
				new ITestfulControl<String>() {
					@Override
					public void update(String newValue) throws Exception {
						control.update(Integer.parseInt(newValue));
					}
				});
	}

	public static ITestfulControl<Integer> getNegative(final ITestfulControl<Integer> config) {
		return new ITestfulControl<Integer>() {
			@Override
			public void update(Integer newValue) throws Exception {
				if(newValue >= 0) throw new Exception("Value must be a negative number.");

				config.update(newValue);
			}
		};
	}

	public static ITestfulControl<Integer> getNotPositive(final ITestfulControl<Integer> config) {
		return new ITestfulControl<Integer>() {
			@Override
			public void update(Integer newValue) throws Exception {
				if(newValue > 0) throw new Exception("Value must be a negative number or zero.");

				config.update(newValue);
			}
		};
	}

	public static ITestfulControl<Integer> getNotNegative(final ITestfulControl<Integer> config) {
		return new ITestfulControl<Integer>() {
			@Override
			public void update(Integer newValue) throws Exception {
				if(newValue < 0) throw new Exception("Value must be a positive number or zero.");

				config.update(newValue);
			}
		};
	}

	public static ITestfulControl<Integer> getPositive(final ITestfulControl<Integer> config) {
		return new ITestfulControl<Integer>() {
			@Override
			public void update(Integer newValue) throws Exception {
				if(newValue <= 0) throw new Exception("Value must be a positive number.");

				config.update(newValue);
			}
		};
	}
}
