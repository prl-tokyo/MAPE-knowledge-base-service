package jp.ac.nii.prl.mape.kb.properties;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app.haskell")
@Component
public class HaskellProperties {

	@NotEmpty
	@Valid
	private String executable = "";

	@NotEmpty
	@Valid
	private String sourcePath;
	
	public String getExecutable() {
		return executable;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
}
