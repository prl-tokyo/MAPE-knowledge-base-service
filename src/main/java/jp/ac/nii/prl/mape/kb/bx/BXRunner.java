package jp.ac.nii.prl.mape.kb.bx;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jp.ac.nii.prl.mape.kb.controller.TransformationException;

@Component
public class BXRunner {
	
	Logger logger = LoggerFactory.getLogger(BXRunner.class);

	public synchronized boolean put(String bx, String view, String param, 
			String executable, String jsonPath) {
		if (bx != "autoscalingFailure") {
			Path path = Paths.get(String.format("%s/%s.json", jsonPath, bx));
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(view);
			} catch (IOException ex) {
				return false;
			}
		}
		
		String source = "";
		switch(bx) {
		
		case "autoscaling": 
			source = String.format("%s/autoscalingFailure.json", jsonPath);
			break;
		case "failure": 
			source = String.format("%s/autoscalingFailure.json", jsonPath);
			break;
		case "firewall": 
			source = String.format("%s/source.json", jsonPath);
			break;
		case "autoscalingFailure":
			source = String.format("%s/source.json", jsonPath);
		default:
			logger.error("Don't know which source to select");
			break;
		}
		
		String cmd = String.format("%s put %s %s %s/%s.json %s", 
				executable, 
				bx,
				source,
				jsonPath,
				bx, 
				param);
		System.out.println(cmd);
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.trace(e.getStackTrace().toString());
			return false;
		}
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			logger.trace(e.getStackTrace().toString());
			return false;
		}
		
		if (p.exitValue() != 0) {
			logger.error(String.format("BiGUL program returned %s. Aborting.", p.exitValue()));
			throw new TransformationException("Transformation failed");
		}
		return true;
	}
	
	public synchronized String get(String bx, String param, String executable, String jsonPath) throws IOException, InterruptedException {
		StringBuilder view = new StringBuilder();
		System.out.println(param);
		String cmd = String.format("%s get %s %s/%s.json %s", 
				executable, 
				bx, 
				jsonPath,
				bx, 
				param);
		logger.debug(String.format("Executing: %s", cmd));
		Process p = null;
		p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		if (p.exitValue() != 0) {
			logger.error(String.format("BiGUL program returned %s. Aborting.", p.exitValue()));
			throw new TransformationException("Transformation failed");
		}
		logger.info("Transformation completed");
		Path path = Paths.get(String.format("%s/%s.json",  jsonPath, bx));
		List<String> allLines = Files.readAllLines(path);
			for (String line:allLines)
				view.append(line);
		logger.info("View read");
		return view.toString();
	}
}
