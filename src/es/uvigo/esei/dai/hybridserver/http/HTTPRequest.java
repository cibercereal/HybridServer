package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {

	private HTTPRequestMethod method;
	private String resourceChain;
	private String HttpVersion;
	private String resourceName;
	private Map<String, String> resourceParamethers = new LinkedHashMap<>();
	private String[] resourcePath = {};
	private Map<String, String> headerParamethers = new LinkedHashMap<>();
	private int contentLength;
	private String content;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		String[] pResChain;
		String[] resPar;

		BufferedReader buf = new BufferedReader(reader);
		String line = buf.readLine();
		String[] text = line.split(" ");

		try {
			this.method = HTTPRequestMethod.valueOf(text[0]);
			this.resourceChain = text[1];
			this.HttpVersion = text[2];
			pResChain = this.resourceChain.split("\\?");

			this.resourceName = pResChain[0].substring(1);

			if (!this.resourceName.equals("")) {
				this.resourcePath = resourceName.split("/");
			}
		} catch (IllegalArgumentException | NullPointerException | ArrayIndexOutOfBoundsException e) {
			throw new HTTPParseException("Parse exception line 1 Request");
		}

		switch (method) {
		case GET:
		case DELETE:

			text[1] = text[1].replaceAll("^.*\\?", "");

			if (this.resourceChain != text[1]) {
				resPar = pResChain[1].split("&");

				for (int i = 0; i < resPar.length; i++) {
					String[] keyValue = resPar[i].split("=");
					this.resourceParamethers.put(keyValue[0], keyValue[1]);
				}
			}

			try {
				String[] headPar;
				while (!(line = buf.readLine()).equals("")) {
					headPar = line.split(":");
					this.headerParamethers.put(headPar[0], headPar[1].trim());
				}

			} catch (IllegalArgumentException | NullPointerException | ArrayIndexOutOfBoundsException e) {
				throw new HTTPParseException("Error in header paramethers");
			}

			this.contentLength = 0;
			break;
		case POST:

			try {
				String[] headPar;
				while (!(line = buf.readLine()).equals("")) {
					headPar = line.split(":");
					this.headerParamethers.put(headPar[0], headPar[1].trim());
				}

			} catch (IllegalArgumentException | NullPointerException | ArrayIndexOutOfBoundsException e) {
				throw new HTTPParseException("Error in header paramethers");
			}

			this.contentLength = Integer.parseInt(this.headerParamethers.get("Content-Length"));

			char[] content_text = new char[contentLength];
			buf.read(content_text);
			this.content = new String(content_text);

			String type = this.headerParamethers.get("Content-Type");

			if (type != null && type.startsWith("application/x-www-form-urlencoded")) {
				this.content = URLDecoder.decode(this.content, "UTF-8");
			}

			resPar = this.content.split("&");

			for (int i = 0; i < resPar.length; i++) {
				String[] keyValue = resPar[i].split("=");
				this.resourceParamethers.put(keyValue[0], keyValue[1]);
			}
			break;

		default:
			throw new HTTPParseException();
		}

	}

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return this.resourceChain;
	}

	public String[] getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return this.resourceParamethers;
	}

	public String getHttpVersion() {
		return this.HttpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return this.headerParamethers;
	}

	public String getContent() {
		return this.content;
	}

	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
