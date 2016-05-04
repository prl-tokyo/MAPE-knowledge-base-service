package jp.ac.nii.prl.mape.kb.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

import jp.ac.nii.prl.mape.kb.properties.HaskellProperties;

@RestController
@Component
@RequestMapping("/kb")
public class KBController {
	
	private final HaskellProperties haskellProperties;
	
	private static final Logger logger = LoggerFactory.getLogger(KBController.class);
	
	@Autowired
	public KBController(HaskellProperties haskellProperties) {
		this.haskellProperties = haskellProperties;
	}

	@RequestMapping(value="get/{bx}/{param}", method=RequestMethod.GET)
	public String get(@PathVariable String bx, @PathVariable String param) {
		logger.info(String.format("Running GET transformation %s with param %s", bx, param));
		StringBuilder view = new StringBuilder();
		System.out.println(param);
		String cmd = String.format("%s get %s %s/%s.json %s", 
				haskellProperties.getExecutable(), 
				bx, 
				haskellProperties.getJsonPath(),
				bx, 
				param);
		logger.debug(String.format("Executing: %s", cmd));
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.trace(e.getStackTrace().toString());
			throw new TransformationException("Could not execute transformation");
		}
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			logger.trace(e.getStackTrace().toString());
			throw new TransformationException("Transformation failed");
		}
		if (p.exitValue() != 0) {
			logger.error(String.format("BiGUL program returned %s. Aborting.", p.exitValue()));
			throw new TransformationException("Transformation failed");
		}
		logger.info("Transformation completed");
		Path path = Paths.get(String.format("%s/%s.json",  haskellProperties.getJsonPath(), bx));
		try {
			List<String> allLines = Files.readAllLines(path);
			for (String line:allLines)
				view.append(line);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.trace(e.getStackTrace().toString());
			return "ERROR: could not read view";
		}
		logger.info("View read");
		return view.toString();
	}
	
	@RequestMapping(value="put/{bx}/{param}", method=RequestMethod.POST)
	public ResponseEntity<?> put(@PathVariable String bx, 
			@PathVariable String param, 
			@RequestBody String view) {
		
		logger.info(String.format("Running PUT transformation %s with param %s", bx, param));
		
		Path path = Paths.get(String.format("%s/%s.json", haskellProperties.getJsonPath(), bx));
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
		    writer.write(view);
		} catch (IOException ex) {
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<>(null, httpHeaders, HttpStatus.FORBIDDEN);
		}
		
		String cmd = String.format("%s put %s %s/%s.json %s", 
				haskellProperties.getExecutable(), 
				bx,
				haskellProperties.getJsonPath(),
				bx, 
				param);
		System.out.println(cmd);
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.trace(e.getStackTrace().toString());
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<>(null, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			logger.trace(e.getStackTrace().toString());
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<>(null, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		logger.info(String.format("PUT transformation %s completed", bx));
		
		// create response
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
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
