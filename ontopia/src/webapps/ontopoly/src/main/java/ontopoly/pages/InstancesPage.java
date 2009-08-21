package ontopoly.pages;


import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import ontopoly.components.CreateInstanceFunctionBoxPanel;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.components.InstanceSearchPanel;
import ontopoly.components.LinkFunctionBoxPanel;
import ontopoly.components.LinkPanel;
import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;
import ontopoly.components.TreePanel;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pojos.TopicNode;
import ontopoly.utils.TreeModels;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class InstancesPage extends OntopolyAbstractPage {

  protected TopicTypeModel topicTypeModel;
  
  public InstancesPage() {	  
  }
  
  public InstancesPage(PageParameters parameters) {
	super(parameters);
	
    this.topicTypeModel = new TopicTypeModel(parameters.getString("topicMapId"), parameters.getString("topicId"));
    
    // Adding part containing title and help link
    createTitle();
    
    // Add list of instances
    TopicType topicType = topicTypeModel.getTopicType();
    
    if(topicType.isLargeInstanceSet()) {
      add(new InstanceSearchPanel("contentPanel", topicTypeModel));
    } else {
      // create a tree
      Panel treePanel = new TreePanel("contentPanel", TreeModels.createInstancesTreeModel(topicTypeModel.getTopicType(), isAdministrationEnabled())) {
        @Override
        protected void populateNode(WebMarkupContainer container, String id, TreeNode treeNode, int level) {
          DefaultMutableTreeNode mTreeNode = (DefaultMutableTreeNode)treeNode; 
          final TopicNode node = (TopicNode)mTreeNode.getUserObject();
          // create link with label
          container.add(new LinkPanel(id) {
            @Override
            protected Label newLabel(String id) {
              Topic topic = node.getTopic();
              final boolean isSystemTopic = topic.isSystemTopic();
              return new Label(id, new Model(topic.getName())) {
                @Override
                protected void onComponentTag(final ComponentTag tag) {
                  if (isSystemTopic)
                    tag.put("class", "italic");
                  super.onComponentTag(tag);              
                }
              };
            }
            @Override
            protected Link newLink(String id) {
              PageParameters params = new PageParameters();            
              params.put("topicMapId", node.getTopicMapId());
              params.put("topicId", node.getTopicId());            
              params.put("topicTypeId", topicTypeModel.getTopicType().getId());
              return new BookmarkablePageLink(id, InstancePage.class, params);
            }
          });
        }
      };
      treePanel.setOutputMarkupId(true);
      add(treePanel);
    }
    
    // Function boxes
    createFunctionBoxes();
    
    // initialize parent components
    initParentComponents();    
  }

  @Override
  protected int getMainMenuIndex() {
    return INSTANCES_PAGE_INDEX_IN_MAINMENU; 
  }
  
  private void createTitle() {
    // Adding part containing title and help link
    add(new TitleHelpPanel("titlePartPanel", 
        new PropertyModel(topicTypeModel, "name"), new HelpLinkResourceModel("help.link.instancespage")));
  }

  private void createFunctionBoxes() {

    add(new FunctionBoxesPanel("functionBoxes") {
      @Override
      protected List getFunctionBoxesList(String id) {
        List list = new ArrayList();
        TopicType topicType = topicTypeModel.getTopicType();
        if (!topicType.isAbstract() && !topicType.isReadOnly()) {
          list.add(new CreateInstanceFunctionBoxPanel(id, getTopicMapModel()) {
            @Override
            protected Class getInstancePageClass() {
              return InstancePage.class;
            }
            @Override
            protected IModel getTitleModel() {
              return new ResourceModel("instances.create.text");
            }
            @Override
            protected Topic createInstance(TopicMap topicMap, String name) {
              TopicType topicType = topicTypeModel.getTopicType();
              return topicType.createInstance(name);
            }
            
          });
        }
        list.add(new LinkFunctionBoxPanel(id) {
          @Override
          public boolean isVisible() {
            return true;
          }
          @Override
          protected Component getLabel(String id) {
            return new Label(id, new ResourceModel("edit.topic.type"));
          }
          @Override
          protected Component getLink(String id) {
            TopicMap tm = getTopicMapModel().getTopicMap();
            TopicType tt = topicTypeModel.getTopicType();
            PageParameters params = new PageParameters();
            params.put("topicMapId", tm.getId());
            params.put("topicId", tt.getId());
            params.put("ontology", "true");
            //TODO direct link to correct instance page
            return new OntopolyBookmarkablePageLink(id, InstancePage.class, params, tt.getName());
          }
        });
        
        return list;
      }
    });
  }

  @Override
  public void onDetach() {
    topicTypeModel.detach();
    super.onDetach();
  }
   
}
