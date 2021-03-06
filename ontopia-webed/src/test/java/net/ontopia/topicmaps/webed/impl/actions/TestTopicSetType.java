/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.Collections;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.topic.SetType;

public class TestTopicSetType extends AbstractWebedTestCase {
  
  public TestTopicSetType(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
   
  */
  
  public void testNormalOperation() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = (TopicIF) topic.getTypes().iterator().next();
    TopicIF newtyp = makeTopic(tm, "snus");
    int numSLs = topic.getTypes().size();
    
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, newtyp.getObjectId());
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("We still have same type", topic.getTypes().iterator().next() == type);
    assertFalse("The type is not correct", topic.getTypes().iterator().next() != newtyp);
    
  }

  public void testBadParam1() throws java.io.IOException{
    TopicIF newtyp = makeTopic(tm, "snus");
    
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters("topic", newtyp.getObjectId());
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("topic is String-type");
    }catch (ActionRuntimeException e){
    }
  }

  public void testBadParam2() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    
    // make action
    ActionIF action = new SetType();
    
    // build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();

    // execute    
    action.perform(params, response);

    // verify that the topic no longer has any types
    assertFalse("Topic still has a type", !topic.getTypes().isEmpty());
  }


  public void testNoParam() throws java.io.IOException{
    TopicIF newtyp = makeTopic(tm, "snus");
    
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("Collection is empty");
    }catch (ActionRuntimeException e){
    }
  }

}
