package testful.gui.wizard.control;


public interface ITestfulControl<T> {
	public void update(T newValue) throws Exception;
}
