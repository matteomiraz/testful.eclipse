package testful.gui.operator;

public class Result {
	final public boolean isSuccess;
	final public String message;
	final public Object returned;
	
	public Result(boolean isSuccess) {
		this.isSuccess = isSuccess;
		if (isSuccess) this.message = "Operation completed successfully.";
		else this.message = "Impossible to complete operation.";
		this.returned = null;
	}
	
	public Result(boolean isSuccess, String message) {
		this.isSuccess = isSuccess;
		this.message = message;
		this.returned = null; 
	}
	
	public Result(boolean isSuccess, Object returned) {
		this.isSuccess = isSuccess;
		if (isSuccess) this.message = "Operation completed successfully.";
		else this.message = "Impossible to complete operation.";
		this.returned = returned; 
	}
	
	public Result(boolean isSuccess, String message, Object returned) {
		this.isSuccess = isSuccess;
		this.message = message;
		this.returned = returned; 
	}
}
