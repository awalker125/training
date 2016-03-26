package uk.co.aw125.training.exceptions;

public class TrainingRuntimeException extends RuntimeException {

	public TrainingRuntimeException(String message) {
		super(message);
	}

	public TrainingRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
