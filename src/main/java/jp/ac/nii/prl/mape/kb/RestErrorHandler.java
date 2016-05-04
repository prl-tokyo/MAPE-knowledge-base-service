package jp.ac.nii.prl.mape.kb;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jp.ac.nii.prl.mape.kb.controller.FileReadException;
import jp.ac.nii.prl.mape.kb.controller.TransformationException;

@ControllerAdvice
public class RestErrorHandler {
	
	@ExceptionHandler(TransformationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleTransformationException(TransformationException ex) {
		
	}
	
	@ExceptionHandler(FileReadException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleFileReadException(FileReadException ex) {
		
	}

}
