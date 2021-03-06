package es.uvigo.esei.dai.hybridserver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class XmlController implements Controller {
	private XmlDBDAO dao;
	private Servers servers;


	public XmlController(XmlDBDAO dao, List<ServerConfiguration> listServicios) {
		this.dao = dao;
		this.servers = new Servers(listServicios);
	}

	public String getContentPage(String uuid) {
		String toret = dao.getContentPage(uuid);
		if (toret == null) {
			try {
				toret = servers.getContent("xml", uuid);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toret;
	}

	public List<String> getList() {
		List<String> toret = new ArrayList<>();
		for (String s : dao.getList()) {
			toret.add("<li><a href=\"xml?uuid=" + s + "\">" + s + "</a></li> ");
		}
		toret.add(0,"<h1>LOCAL HOST</h1>");
		try {

			for (String s : servers.uuidList("xml")) {
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

}
