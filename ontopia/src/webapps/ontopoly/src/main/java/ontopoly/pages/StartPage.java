package ontopoly.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.OntopolyRepository;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapReference;
import ontopoly.components.CreateNewTopicMapPanel;
import ontopoly.components.FooterPanel;
import ontopoly.components.HeaderPanel;
import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicMapReferenceModel;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class StartPage extends AbstractProtectedOntopolyPage {
  
  public StartPage(PageParameters parameters) throws IOException {
    super(parameters);
    
    add(new HeaderPanel("header"));
    add(new FooterPanel("footer"));
    
    // Adding part containing title and help link
    createTitle();

    // Ontopoly Topic Maps    
    addOntopolyTopicMapsSection();

    // Other Topic Maps
    addOtherTopicMapsSection();
    
    // Create new topic map
    addCreateNewTopicMapSection();
  }

  private void addOntopolyTopicMapsSection() {
    IModel eachTopicMapModel = new Model() {
      public Object getObject() {
        List existingOntopolyTopicMaps = new ArrayList();
        
        OntopolyRepository repository = OntopolyContext.getOntopolyRepository();
        List ontTopicMaps = repository.getOntopolyTopicMaps();
        Iterator it = ontTopicMaps.iterator();
        while (it.hasNext()) {
          TopicMapReference topicMapReference = (TopicMapReference) it.next();
          if (topicMapReference.isPresent()) {
            existingOntopolyTopicMaps.add(topicMapReference);
          }
        }
        return existingOntopolyTopicMaps; 
      }
    };

    ListView eachTopicMap = new ListView("eachOntopolyTopicMap",
        eachTopicMapModel) {
      protected void populateItem(ListItem item) {
        final TopicMapReference ref = (TopicMapReference) item.getModelObject();
        PageParameters pageParameters = new PageParameters("topicMapId="
            + ref.getId());
        BookmarkablePageLink link = new OntopolyBookmarkablePageLink(
            "ontTMLink", InstanceTypesPage.class, pageParameters, ref.getName());
        item.add(link);
      }
    };
    add(eachTopicMap);

    final IModel eachMissingTopicMapModel = new Model() {
      public Object getObject() {
        List missingOntopolyTopicMaps = new ArrayList();
        
        OntopolyRepository repository = OntopolyContext.getOntopolyRepository();
        List ontTopicMaps = repository.getOntopolyTopicMaps();      
        Iterator it = ontTopicMaps.iterator();
        while (it.hasNext()) {
          TopicMapReference topicMapReference = (TopicMapReference) it.next();
          if (!topicMapReference.isPresent()) {
            missingOntopolyTopicMaps.add(topicMapReference);
          }
        }
        return missingOntopolyTopicMaps; 
      }
    };    

    final WebMarkupContainer missingTopicMapContainer = new WebMarkupContainer(
        "missingTopicMapContainer") {
      public boolean isVisible() {
        if (((List)eachMissingTopicMapModel.getObject()).size() == 0) {
          return false;
        }
        return true;
      }
    };
    missingTopicMapContainer.setOutputMarkupPlaceholderTag(true);
    add(missingTopicMapContainer);

    eachTopicMap = new ListView("eachMissingOntopolyTopicMap",
        eachMissingTopicMapModel) {
      protected void populateItem(ListItem item) {
        TopicMapReference ref = (TopicMapReference) item.getModelObject();
        final TopicMapReferenceModel topicMapReferenceModel = new TopicMapReferenceModel(ref);
        item.add(new Label("missingOntTMTitle", ref.getName()));
        item.add(new Label("missingOntTMFilename", ref.getId()));

        AjaxFallbackLink removeLink = new AjaxFallbackLink("missingOntTMDeleteLink") {
          public void onClick(AjaxRequestTarget target) {
            // delete the item representing the topicMapReference from the list eachMissingOntopolyTopicMap.
            ((List)eachMissingTopicMapModel.getObject()).remove(topicMapReferenceModel.getTopicMapReference());
            // delete the missing topic map from the system topic map.
            topicMapReferenceModel.getTopicMapReference().delete();
            if (target != null) {
              target.addComponent(missingTopicMapContainer);
            }
          }
        };
        item.add(removeLink);
      }
    };
    missingTopicMapContainer.add(eachTopicMap);
  }
  
  private void addOtherTopicMapsSection() {
    // Alt. 2 Make a loadabledetachableModel for repository
    IModel eachNonOntopolyTopicMapModel = new Model() {
      public Object getObject() {
        OntopolyRepository repository = OntopolyContext.getOntopolyRepository();
        return repository.getNonOntopolyTopicMaps();
      }
    }; 

    ListView eachTopicMap = new ListView("eachNonOntopolyTopicMap", eachNonOntopolyTopicMapModel) {
      protected void populateItem(ListItem item) {
        final TopicMapReference ref = (TopicMapReference) item.getModelObject();
        Map pageParameterMap = new HashMap();
        pageParameterMap.put("topicMapId",ref.getId());
        
        BookmarkablePageLink link = new OntopolyBookmarkablePageLink(
            "nonOntTMLink", ConvertPage.class, new PageParameters(pageParameterMap), ref.getName());
        item.add(link);
      }
    };
    add(eachTopicMap);
  }
  
  private void addCreateNewTopicMapSection() {  
    // TODO Implement functionality to handle "" or null values from textfield.
    add(new CreateNewTopicMapPanel("createNewTopicMapPanel", OntopolyContext.getOntopolyRepository()));
  }

  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel",
          new ResourceModel("ontopias.topic.map.editor"), new HelpLinkResourceModel("help.link.startpage")));
  }
  
}
