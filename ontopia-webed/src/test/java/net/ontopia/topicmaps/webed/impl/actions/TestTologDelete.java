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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.TologDelete;

public class TestTologDelete extends AbstractWebedTestCase {
  
  public TestTologDelete(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad, query
    3. Bad TMObj
  */


  public void testNormalOperation() throws java.io.IOException{
    
    //make action
    ActionIF action = new TologDelete();
    String query = "instance-of($TEAMS, team)?";
    String topicId = getTopicById(tm, "tromso").getObjectId();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(query), topicId);
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test      
    TopicIF topic = getTopicById(tm, "tromso");
    assertFalse("Map still has teams", topic != null);
  }
  
  public void testBadQuery() throws java.io.IOException{
    
    //make action
    ActionIF action = new TologDelete();
    String query = "instance-of($TEAMS, team)";
    String topicId = getTopicById(tm, "tromso").getObjectId();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(query), topicId);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
            
    }catch (ActionRuntimeException e){
    }
  }
  
  public void testParams() throws java.io.IOException{
    
    //make action
    ActionIF action = new TologDelete();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("mama"), "papa");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
            
    }catch (ActionRuntimeException e){
    }
  }
  

}
