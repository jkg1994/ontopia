/*
 * #!
 * Ontopia RDF
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
package net.ontopia.topicmaps.xml;

import java.util.List;
import net.ontopia.utils.TestFileUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TMXMLWriterSpecialRDFTestCase extends TMXMLWriterSpecialTestCase {

  @Parameterized.Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "x-in", ".rdf");
  }

  public TMXMLWriterSpecialRDFTestCase(String root, String filename) {
    super(root, filename);
  }
}
