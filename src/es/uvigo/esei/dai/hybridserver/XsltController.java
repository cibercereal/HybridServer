package es.uvigo.esei.dai.hybridserver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class XsltController implements Controller {
	private XsltDBDAO dao;
	private Servers servers;


	public XsltController(XsltDBDAO dao, List<ServerConfiguration> listServicios) {
		this.dao = dao;
		this.servers = new Servers(listServicios);
	}

	public String getContentPage(String uuid) {
		String toret = dao.getContentPage(uuid);
		if (toret == null) {
			try {
				toret = servers.getContent("xslt", uuid);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toret;
	}

	public List<String> getList() {
		List<String> toret = new ArrayList<>();
		toret.add("<h1>LOCAL HOST</h1>");
		for (String s : dao.getList()) {
			toret.add("<li><a href=\"xslt?uuid=" + s + "\">" + s + "</a></li> ");
		}
		
		try {

			for (String s : servers.uuidList("xslt")) {
				toret.add(s);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toret;
	}

	public void putPages(String uuid, String content) {
		dao.putPages(uuid, content);
	}

	public void removePages(String uuid) {
		dao.removePages(uuid);
	}

	public boolean containsPage(String uuid) {
		return dao.containsPage(uuid);
	}

	public String getXsd(String xsd) {
		String toret = dao.getXsd(xsd);
		if (toret == null) {
			try {
				toret = servers.getContent("uuidxsd", xsd);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toret;

	}
}
