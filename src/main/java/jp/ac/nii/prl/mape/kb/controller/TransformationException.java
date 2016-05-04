package jp.ac.nii.prl.mape.kb.controller;

import org.springframework.web.client.RestClientException;

public class TransformationException extends RestClientException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2807815466193611409L;

	public TransformationException(String msg) {
		super(msg);
	}

	public TransformationException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
