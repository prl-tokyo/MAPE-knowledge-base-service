package jp.ac.nii.prl.mape.kb.bx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jp.ac.nii.prl.mape.kb.controller.TransformationException;

@Component
public class BXRunner {
	
	Logger logger = LoggerFactory.getLogger(BXRunner.class);

	public synchronized boolean put(String bx, String view, String param, 
			String executable, String path) {
		if (bx != "autoscalingFailure") {
			Path viewPath = Paths.get(String.format("%s/%s.json", path, bx));
			try (BufferedWriter writer = Files.newBufferedWriter(viewPath)) {
			writer.write(view);
			} catch (IOException ex) {
				return false;
			}
		}
		
		String cmd = String.format("%s put %s %s", 
				path + "/" + executable, 
				bx,
				param);
		System.out.println(cmd);
		logger.error("Running command " + cmd);
		ProcessBuilder pb = new ProcessBuilder(path + "/" + executable, "put", bx, param);
		pb.directory(new File(path));
		Process p;
		try {
			p = pb.start();
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
			logger.error(String.format("BiGUL program returned %s for BX %s. Aborting.", p.exitValue(), bx));
			throw new TransformationException("Transformation failed");
		}
		return true;
	}
	
	public synchronized String get(String bx, String param, String executable, String path) throws IOException, InterruptedException {
		StringBuilder view = new StringBuilder();
		System.out.println(param);
		String cmd = String.format("%s get %s %s", 
				path + "/" + executable, 
				bx, 
				param);
		logger.info(String.format("Executing: %s", cmd));
		ProcessBuilder pb = new ProcessBuilder(path + "/" + executable, "get", bx, param);
		pb.directory(new File(path));
		Process p = pb.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String result = new BufferedReader(new InputStreamReader(is))
				  .lines().collect(Collectors.joining("\n"));
		logger.error(result);
		if (p.exitValue() != 0) {
			logger.error(String.format("BiGUL program returned %s. Aborting.", p.exitValue()));
			throw new TransformationException("Transformation failed");
		}
		logger.info("Transformation completed");
		Path viewPath = Paths.get(String.format("%s/%s.json",  path, bx));
		List<String> allLines = Files.readAllLines(viewPath);
			for (String line:allLines)
				view.append(line);
		logger.info("View read");
		return view.toString();
	}
}
