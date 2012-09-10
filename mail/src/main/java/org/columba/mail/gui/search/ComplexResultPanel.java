package org.columba.mail.gui.search;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.columba.core.gui.search.api.IResultPanel;
import org.columba.core.search.api.IResultEvent;
import org.columba.core.search.api.ISearchResult;
import org.columba.mail.resourceloader.IconKeys;
import org.columba.mail.resourceloader.MailImageLoader;

public class ComplexResultPanel implements IResultPanel {

	private ResourceBundle bundle;

	private String providerTechnicalName;

	private String criteriaTechnicalName;

	private ResultList list;

	public ComplexResultPanel(String providerTechnicalName) {
		super();

		this.providerTechnicalName = providerTechnicalName;

		bundle = ResourceBundle.getBundle("org.columba.mail.i18n.search");

		list = new ResultList();

	}

	public String getSearchCriteriaTechnicalName() {
		return "";
	}

	public String getProviderTechnicalName() {
		return providerTechnicalName;
	}

	public JComponent getView() {
		return list;
	}

	public ImageIcon getIcon() {
		return MailImageLoader.getSmallIcon(IconKeys.MESSAGE_READ);
	}

	public String getTitle(String searchTerm) {
		String result = bundle.getString("provider_title");
		return result;
	}

	public String getDescription(String searchTerm) {
		String result = bundle.getString("provider_description");
		return result;
	}

	public void resultArrived(IResultEvent event) {
		if (!event.getProviderName().equals(this.providerTechnicalName))
			return;

		List<ISearchResult> result = event.getSearchResults();

		Iterator<ISearchResult> it = result.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}

		list.revalidate();
	}

	public void clearSearch(IResultEvent event) {

	}

	public void reset(IResultEvent event) {
		list.clear();
	}

	public void finished(IResultEvent event) {
	}

}