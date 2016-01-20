// Copyright © 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.viewer.client.widgets;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.risevision.viewer.client.ViewerEntryPoint;
import com.risevision.viewer.client.data.ViewerDataController;
import com.risevision.viewer.client.info.Global;
import com.risevision.viewer.client.utils.ViewerHtmlUtils;

public class ViewerPreviewWidget extends PopupPanel implements ClickHandler {
	private static ViewerPreviewWidget instance;
	
	private AbsolutePanel contentPanel = new AbsolutePanel();
	private HTML logoDiv = new HTML();
	
	private Anchor itemButton = new Anchor();
	private Anchor signUpButton = new Anchor("<div class='btn btn-primary' style='padding-right:2px'><span style='white-space:nowrap;'>Sign Up Free<img src='../images/google.svg' style='height: 14px;width: 30px;top: 4px;position: relative;'></span></div>", true);
	private Anchor loginButton = new Anchor("<div class='btn btn-link'><span style='white-space:nowrap;'>Sign In</span></div>", true);
	private Anchor hideButton = new Anchor("<div class='btn btn-default'><img src='../images/compress.png' style='height: 22px;width: 27px;'></div>", true);
	private Anchor showButton = new Anchor("<div class='btn btn-default'><img src='../images/expand.png' style='height: 22px;width: 27px;'></div>", true);
	private HTML alertPanel = new HTML("<div class='alert-panel'>Your media will appear in the order it is downloaded, and once all media is available it will show in the order specified in your Playlist.</div>");
	
	public ViewerPreviewWidget() {
		super(false, false);
		
		add(contentPanel);
		contentPanel.add(loginButton, 899, 21);
		contentPanel.add(signUpButton, 753, 21);
		contentPanel.add(hideButton, 12, 21);
		contentPanel.add(showButton, 12, 21);
		contentPanel.add(alertPanel, 327, 21);

		showButton.setVisible(false);
		
		styleControls();
		initHandlers();
		initItemButton();
	}
	
	private void styleControls() {
		resizeLarge();
		
		contentPanel.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		
		addStyleName("drop-shadow");
	
		contentPanel.getElement().getStyle().setBackgroundColor("rgba(255,255,255,1)");		
		this.getElement().getStyle().setProperty("zIndex", "999");
		
		logoDiv.getElement().addClassName("logo-style");
		logoDiv.getElement().getStyle().setCursor(Cursor.POINTER);
		
		itemButton.addStyleName("link-action");
	}
	
	private void resizeLarge() {
		contentPanel.setSize("1000px", "69px");
		contentPanel.getElement().getStyle().setOpacity(1);
		contentPanel.getElement().getStyle().setBackgroundColor("rgba(255,255,255,1)");
	}
	
	private void initHandlers() {
		logoDiv.addClickHandler(this);
		hideButton.addClickHandler(this);
		showButton.addClickHandler(this);
		itemButton.addClickHandler(this);
		itemButton.setTarget("_blank");
		loginButton.addClickHandler(this);
		loginButton.setHref("https://apps.risevision.com/signin");
		loginButton.setTarget("_blank");
		signUpButton.addClickHandler(this);
		signUpButton.setHref("https://apps.risevision.com/signup");
		signUpButton.setTarget("_blank");
	}
	
	private void initItemButton() {
		if (ViewerEntryPoint.getId() != null && !ViewerEntryPoint.getId().isEmpty()) {
			String title;
			String url = Global.APPS_URL;

			// [AD] only add button for Template Presentations
			// Uncomment code when new Editor shows User friendly message for
			// "Cannot access this Presentation/Schedule"
			if (ViewerDataController.isTemplate()) {
				title = "Copy " + ViewerDataController.getItemName();
				url += "/editor/workspace/new/?copyOf=" + ViewerEntryPoint.getId();
//			}
//			else {
//				title = "Edit " + ViewerDataController.getItemName();
//				if (ViewerEntryPoint.isPresentation()) {
//					url += "/editor/workspace/" + ViewerEntryPoint.getId() + "/";
//				}
//				else {
//					url += "/schedules/details/" + ViewerEntryPoint.getId();
//				}
//				url += "?cid=" + ViewerDataController.getItemCompanyId();
//			}

				itemButton.setHTML("<div class='btn btn-primary'><span style='text-overflow:ellipsis;display:block;overflow:hidden;max-width:220px;'>" + title + "</span></div>");
				contentPanel.add(itemButton, 75, 22);
				itemButton.setHref(url);
			}
		}
	}
	
	public void onClick(ClickEvent event) {
		Object sender = event.getSource();

		if (sender instanceof Anchor) {
			trackEvent("Click", ((Anchor) sender).getText());
			
			if (sender == hideButton) {
				showElements(false);
				contentPanel.setSize("79px", "69px");
				contentPanel.getElement().getStyle().setOpacity(0.6);
				contentPanel.getElement().getStyle().setBackgroundColor("rgba(255,255,255,.5)");		
			}
			else if (sender == showButton) {
				showElements(true);
				resizeLarge();
			}
		}
		
	}
	
	private void showElements(boolean show) {
		logoDiv.setVisible(show);
		itemButton.setVisible(show);
		alertPanel.setVisible(show);
		loginButton.setVisible(show);
		signUpButton.setVisible(show);
		
		hideButton.setVisible(show);
		showButton.setVisible(!show);		
	}
	
	public static ViewerPreviewWidget getInstance() {
		try {
			if (instance == null)
				instance = new ViewerPreviewWidget();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

	public void show() {
		super.show();
		super.setPopupPosition(0, -10);
	}
	
	private void trackEvent(String action, String label) {
		ViewerHtmlUtils.trackAnalyticsEvent("PreviewBar", action, label);
	}
	
	public static String getPreviewUrl() {
		return Window.Location.getHref();
	}
}
