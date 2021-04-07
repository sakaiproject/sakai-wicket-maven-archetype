#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.tool.pages;

import javax.servlet.http.HttpServletRequest;


import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.migrate.StringResourceModelMigration;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import lombok.extern.slf4j.Slf4j;


import ${package}.logic.ProjectLogic;
import ${package}.logic.SakaiProxy;


/**
 * This is our base page for our Sakai app. It sets up the containing markup and top navigation.
 * All top level pages should extend from this page so as to keep the same navigation. The content for those pages will
 * be rendered in the main area below the top nav.
 * 
 * <p>It also allows us to setup the API injection and any other common methods, which are then made available in the other pages.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@Slf4j
public class BasePage extends WebPage implements IHeaderContributor {

	@SpringBean(name="${package}.logic.SakaiProxy")
	protected SakaiProxy sakaiProxy;
	
	@SpringBean(name="${package}.logic.ProjectLogic")
	protected ProjectLogic projectLogic;
	
	Link<Void> firstLink;
	Link<Void> secondLink;
	Link<Void> thirdLink;
	
	FeedbackPanel feedbackPanel;
	
	public BasePage() {
		
		log.debug("BasePage()");
		
		
		//first link
		firstLink = new Link<Void>("firstLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				
				setResponsePage(new FirstPage());
			}
		};
		firstLink.add(new Label("firstLinkLabel",new ResourceModel("link.first")).setRenderBodyOnly(true));
		firstLink.add(new AttributeModifier("title", new ResourceModel("link.first.tooltip")));
		add(firstLink);
		
		
		
		//second link
		secondLink = new Link<Void>("secondLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new SecondPage());
			}
		};
		secondLink.add(new Label("secondLinkLabel",new ResourceModel("link.second")).setRenderBodyOnly(true));
		secondLink.add(new AttributeModifier("title", new ResourceModel("link.second.tooltip")));
		add(secondLink);
		
		
		
		//third link
		thirdLink = new Link<Void>("thirdLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new ThirdPage());
			}
		};
		StringResourceModel stringResourceModel = StringResourceModelMigration.of("link.third", null, new String[] {"3"});
		thirdLink.add(new Label("thirdLinkLabel", stringResourceModel).setRenderBodyOnly(true));
		thirdLink.add(new AttributeModifier("title", new ResourceModel("link.third.tooltip")));
		add(thirdLink);
		
		
		// Add a FeedbackPanel for displaying our messages
        feedbackPanel = new FeedbackPanel("feedback"){
        	
        	@Override
        	protected Component newMessageDisplayComponent(final String id, final FeedbackMessage message) {
        		final Component newMessageDisplayComponent = super.newMessageDisplayComponent(id, message);

        		if(message.getLevel() == FeedbackMessage.ERROR ||
        			message.getLevel() == FeedbackMessage.DEBUG ||
        			message.getLevel() == FeedbackMessage.FATAL ||
        			message.getLevel() == FeedbackMessage.WARNING){
        			add(AttributeModifier.replace("class", "alertMessage"));
        		} else if(message.getLevel() == FeedbackMessage.INFO){
        			add(AttributeModifier.replace("class", "success"));        			
        		} 

        		return newMessageDisplayComponent;
        	}
        };
        add(feedbackPanel); 
		
    }
	
	/**
	 * Helper to clear the feedbackpanel display.
	 * @param f	FeedBackPanel
	 */
	public void clearFeedback(FeedbackPanel f) {
		if(!f.hasFeedbackMessage()) {
			f.add(AttributeModifier.replace("class", ""));
		}
	}
	
	
	/**
	 * This block adds the required wrapper markup to style it like a Sakai tool. 
	 * Add to this any additional CSS or JS references that you need.
	 * 
	 */
	public void renderHead(IHeaderResponse response) {
		//get the Sakai skin header fragment from the request attribute
		HttpServletRequest request = (HttpServletRequest)getRequest().getContainerRequest();
		
		response.render(StringHeaderItem.forString((String)request.getAttribute("sakai.html.head")));
		response.render(OnLoadHeaderItem.forScript("setMainFrameHeight( window.name )"));
		
		
		//Tool additions (at end so we can override if required)
		response.render(StringHeaderItem.forString("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"));
		//response.renderCSSReference("css/my_tool_styles.css");
		//response.renderJavascriptReference("js/my_tool_javascript.js");
	}
	
	
	/** 
	 * Helper to disable a link. Add the Sakai class 'current'.
	 */
	protected void disableLink(Link<Void> l) {
		l.add(new AttributeAppender("class", new Model<String>("current"), " "));
		l.setEnabled(false);
	}
	
	
	
}
