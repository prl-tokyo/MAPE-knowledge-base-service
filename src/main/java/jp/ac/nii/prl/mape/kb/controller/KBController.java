package jp.ac.nii.prl.mape.kb.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jp.ac.nii.prl.mape.kb.bx.BXRunner;
import jp.ac.nii.prl.mape.kb.properties.HaskellProperties;

@RestController
@Component
@RequestMapping("/kb")
public class KBController {
	
	private final HaskellProperties haskellProperties;
	private final BXRunner bxRunner;
	
	private static final Logger logger = LoggerFactory.getLogger(KBController.class);
	
	@Autowired
	public KBController(HaskellProperties haskellProperties, BXRunner bxRunner) {
		this.haskellProperties = haskellProperties;
		this.bxRunner = bxRunner;
	}

	@RequestMapping(value="get/{bx}", method=RequestMethod.GET)
	public String getNoParam(@PathVariable String bx) {
		return get(bx, "nothing");
	}
	
	@RequestMapping(value="get/{bx}/{param}", method=RequestMethod.GET)
	public String get(@PathVariable String bx, @PathVariable String param) {
		String view = "";
		switch(bx) {
		case "failure":
			bxGet("autoscalingFailure", param);
			view = bxGet(bx, param);
			break;
		case "autoscaling":
			view = bxGet(bx, param);
			break;
		case "firewall":
			view = bxGet(bx, param);
			break;
		default:
			throw new TransformationException(String.format("Cannot run transformation %s", bx));
		}
		return view;
	}
	
	public String bxGet(String bx, String param) {
		logger.info(String.format("Running GET transformation %s with param %s", bx, param));
		String view;
		try {
			view = bxRunner.get(bx,  param, 
					haskellProperties.getExecutable(), haskellProperties.getJsonPath());
		} catch (IOException | InterruptedException e) {
			throw new TransformationException("Error in GET transformation");
		}
		return view;
	}
	
	@RequestMapping(value="put/{bx}", method=RequestMethod.POST)
	public ResponseEntity<?> putNoParam(@PathVariable String bx, @RequestBody String view) {
		return put(bx, "nothing", view);
	}
	
	@RequestMapping(value="put/{bx}/{param}", method=RequestMethod.POST)
	public ResponseEntity<?> put(@PathVariable String bx, 
			@PathVariable String param,
			@RequestBody String view) {
		
		boolean res = false;
		
		switch(bx) {
		case "failure":
			res = bxPut(bx, param, view);
			break;
		case "autoscaling":
			res = bxPut(bx, param, view);
			if (!res) break;
			res = bxPut("autoscalingFailure", param, "");
			break;
		case "firewall":
			res = bxPut(bx, param, view);
			break;
		default:
			logger.error(String.format("Can't perform put transformation %s", bx));
			res = false;
			break;
		}
		
		if (!res) {
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<>(null, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// create response
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	}
	
	public boolean bxPut(String bx, String param, String view) {
		
		logger.info(String.format("Running PUT transformation %s with param %s", bx, param));
		
		if (bx == "autoscalingFailure") {
			
		}
		
		if (!bxRunner.put(bx, view, param, haskellProperties.getExecutable(), haskellProperties.getJsonPath())) {
			return false;
		}
		
		logger.info(String.format("PUT transformation %s completed", bx));
		
		return true;
	}
	
	@RequestMapping(value="source", method=RequestMethod.POST)
	public ResponseEntity<?> updateSource(@RequestBody String source) {
		
		logger.info("Updating source");
		
		Path path = Paths.get(haskellProperties.getJsonPath() + "source.json");
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(source);
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			logger.trace(ex.getStackTrace().toString());
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<>(null, httpHeaders, HttpStatus.FORBIDDEN);
		}
		
		logger.info("Source updated");
		
		// create response
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
	}
}
