package jp.ac.nii.prl.mape.kb.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
@RequestMapping("/kb")
public class KBController {

	@RequestMapping(value="get/{bx}", method=RequestMethod.GET)
	public String get(@PathVariable String bx) {
		return null;
	}
	
	@RequestMapping(value="put/{bx}", method=RequestMethod.POST)
	public ResponseEntity<?> put(@PathVariable String bx, @RequestBody String view) {
		
		// create response
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	}
}
