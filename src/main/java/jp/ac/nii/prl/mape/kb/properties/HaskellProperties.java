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
	private String jsonPath = "";
	
	public String getExecutable() {
		return executable;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}
}
