package es.uvigo.esei.dai.hybridserver;

import java.util.List;

public interface Controller {
	public String getContentPage(String uuid);

	public List<String> getList();

	public void putPages(String uuid, String content);

	public void removePages(String uuid);

	public boolean containsPage(String uuid);
}
