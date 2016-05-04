package jp.ac.nii.prl.mape.kb.controller;

import org.springframework.web.client.RestClientException;

public class FileReadException extends RestClientException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7675248077094546503L;

	public FileReadException(String msg) {
		super(msg);
	}

	public FileReadException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
