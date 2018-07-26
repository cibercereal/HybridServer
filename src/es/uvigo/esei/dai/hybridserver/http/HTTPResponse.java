package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HTTPResponse {

	private HTTPResponseStatus status;
	private String version;
	private String content;
	private final Map<String, String> parameters = new LinkedHashMap<>();

	public HTTPResponse() {
		this(HTTPResponseStatus.S500);
	}

	public HTTPResponse(HTTPResponseStatus stat) {
		this.content = "";
		this.version = "HTTP/1.1";
		this.status = stat;
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version.toUpperCase();
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		return this.parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return this.parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		if (this.parameters.containsKey(name)) {
			String value = this.parameters.get(name);
			this.parameters.remove(name);
			return value;
		} else {
			return "";
		}
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public List<String> listParameters() {
		List<String> list = new ArrayList<>();
		list.addAll(this.getParameters().keySet());
		return list;
	}

	public void print(Writer writer) throws IOException {
		writer.write(
				this.getVersion() + " " + this.getStatus().getCode() + " " + this.getStatus().getStatus() + "\r\n");

		for (Entry<String, String> element : this.getParameters().entrySet()) {
			writer.write(element.getKey() + ": " + element.getValue() + "\r\n");
		}

		if (this.getClass() != null && this.getContent().length() > 0) {
			writer.write(HTTPHeaders.CONTENT_LENGTH.getHeader() + ": " + this.getContent().length() + "\r\n");
		}

		writer.write("\r\n");

		if (this.getClass() != null && this.getContent().length() > 0) {
			writer.write(this.getContent());
		}

		writer.flush();
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
