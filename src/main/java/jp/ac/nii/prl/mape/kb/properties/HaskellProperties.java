package jp.ac.nii.prl.mape.kb.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app.haskell")
@Component
public class HaskellProperties {

	private String executable = "";

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}
}
