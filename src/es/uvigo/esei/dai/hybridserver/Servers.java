package es.uvigo.esei.dai.hybridserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Servers {
	private List<ServerConfiguration> configList;

	public Servers(List<ServerConfiguration> configList) {
		this.configList = configList;
	}

	// Método que devuelve el servicio web.
	private webServices getWebService(ServerConfiguration serverConfiguration) throws MalformedURLException {
		URL url = new URL(serverConfiguration.getWsdl());
		QName name = new QName(serverConfiguration.getNamespace(), serverConfiguration.getService());
		try {
			Service service = Service.create(url, name);
			webServices webService = service.getPort(webServices.class);
			return webService;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	

	public String getContent(String request, String uuid) throws MalformedURLException {
		String content = null;
		ServerConfiguration serverConfiguration = null;
		if (configList != null) {
			Iterator<ServerConfiguration> itServices = configList.iterator();

			switch (request) {
			case "html":
				while (itServices.hasNext() && (content == "" || content == null)) {

					serverConfiguration = itServices.next();

					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content = webService.getContentHTML(uuid);
					}
				}
				break;

			case "xml":
				while (itServices.hasNext() && (content == "" || content == null)) {
					serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content = webService.getContentXML(uuid);
					}
				}
				break;

			case "xslt":
				while (itServices.hasNext() && (content == "" || content == null)) {
					serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content = webService.getContentXSLT(uuid);
					}
				}
				break;

			case "xsd":
				while (itServices.hasNext() && (content == "" || content == null)) {
					 serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content = webService.getContentXSD(uuid);
					}
				}
				break;

			case "uuidxsd":
				while (itServices.hasNext() && (content == "" || content == null)) {
					serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content = webService.getXSDAsoc(uuid);
					}
				}
				break;

			}

		}
		return content;
	}

	public List<String> uuidList(String request) throws MalformedURLException {
		List<String> content = new ArrayList<String>();
		ServerConfiguration serverConfiguration = null;
		if (configList != null) {
			Iterator<ServerConfiguration> itServices = configList.iterator();

			switch (request) {
			case "html":
				while (itServices.hasNext()) {
					serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);

					if (webService != null) {
						content.add("<h1>" + serverConfiguration.getName() + " </h1>");
						for (String s : webService.getUUIDsHTML()) {
							content.add("<li>" + "<a href=\"" + serverConfiguration.getHttpAddress() + "html?uuid=" + s
									+ "\">" + s + "</a></li>");
						}

					}
				}
				break;

			case "xml":
				while (itServices.hasNext()) {
					serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content.add("<h1>" + serverConfiguration.getName() + " </h1>");
						for (String s : webService.getUUIDsXML()) {
							content.add("<li>" + "<a href=\"" + serverConfiguration.getHttpAddress() + "xml?uuid=" + s
									+ "\">" + s + "</a></li>");
						}
					}
				}
				break;

			case "xslt":
				while (itServices.hasNext()) {
					serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content.add("<h1>" + serverConfiguration.getName() + " </h1>");
						for (String s : webService.getUUIDsXSLT()) {
							content.add("<li>" + "<a href=\"" + serverConfiguration.getHttpAddress() + "xslt?uuid=" + s
									+ "\">" + s + "</a></li>");
						}
					}
				}
				break;

			case "xsd":
				while (itServices.hasNext()) {
					serverConfiguration = itServices.next();
					webServices webService = getWebService(serverConfiguration);
					if (webService != null) {
						content.add("<h1>" + serverConfiguration.getName() + " </h1>");
						for (String s : webService.getUUIDsXSD()) {
							content.add("<li>" + "<a href=\"" + serverConfiguration.getHttpAddress() + "xsd?uuid=" + s
									+ "\">" + s + "</a></li>");
						}
					}
				}
				break;

			}

		}
		return content;
	}
}
