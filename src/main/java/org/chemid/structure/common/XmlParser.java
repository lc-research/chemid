/*
 * Copyright (c) 2016, ChemID. (http://www.chemid.org)
 *
 * ChemID licenses this file to you under the Apache License V 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.chemid.structure.common;

import org.chemid.structure.exception.ChemIDStructureException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

public class XmlParser {
    /**
     * @param xmlRecords
     * @return doc :Document
     */
    public static Document stringToXML(String xmlRecords) throws ChemIDStructureException {

        DocumentBuilder db = null;
        Document doc = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            doc = db.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ChemIDStructureException("Error occurred in xml Parser StringToXML : ", e);
        }

        return doc;
    }


    /**
     * @param fileName
     * @param location
     * @return doc :Document
     */
    public static Document getXMLPayload(String fileName, String location) throws ChemIDStructureException {
        Document doc = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(location + fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            doc = dBuilder.parse(new File(resource.getPath()));
        } catch (SAXException | IOException | ParserConfigurationException e) {

            throw new ChemIDStructureException("Error occurred in xml Parser getXMLPayload : ", e);
        }


        return doc;
    }

    /**
     * @param doc: Document
     * @return output : String content of document
     */
    public static String getStringFromDocument(Document doc) throws ChemIDStructureException {
        String output = null;
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            output = writer.toString();

        } catch (TransformerException e) {

            throw new ChemIDStructureException("Error occurred in xml Parser getStringFromDocument : ", e);

        }

        return output;
    }
}
