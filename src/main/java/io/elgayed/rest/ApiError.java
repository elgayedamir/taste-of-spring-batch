package io.elgayed.rest;

/**
 * This class serves as a DTO for errors returned by the REST API end-points 
 */
public class ApiError {

	private String code;
	private String description;

	public ApiError(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
