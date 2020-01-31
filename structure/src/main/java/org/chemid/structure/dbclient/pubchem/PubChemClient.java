/*
 *  Copyright (c) 2018, LC-Research. (http://www.lc-research.com)
 *
 *  LC-Research licenses this file to you under the Apache License V 2.0.
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 *  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations under the License.
 */

package org.chemid.structure.dbclient.pubchem;/*
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


import org.chemid.structure.exception.ChemIDStructureException;
import org.apache.commons.io.FileUtils;
import org.chemid.structure.common.Constants;
import org.chemid.structure.common.RestClient;
import org.chemid.structure.common.XmlParser;
import org.chemid.structure.dbclient.pubchem.beans.PubChemESearch;
import org.glassfish.jersey.client.ClientConfig;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * pubchem client to download chemical structures from pubchem web services.
 */
public class PubChemClient {

    private PubChemESearch pubChemESearch;
    private RestClient restClient;
    /**
     * constructor
     *
     * @param pubChemESearch
     */
    public PubChemClient(PubChemESearch pubChemESearch) {
        this.pubChemESearch = pubChemESearch;
    }

    /**
     * @param massRange
     * @return pubchemEsearch object
     * @throws ChemIDStructureException
     */
    public PubChemESearch getPubChemESearchRequestParameters(String massRange) throws ChemIDStructureException {

        this.restClient = new RestClient();
        //Get request to
        // massRange="100:100.01[exactmass]";
        Invocation.Builder invocationBuilder = restClient.getWebResource(Constants.PubChemClient.E_SEARCH_URL + massRange).request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.get();
        String resp = response.readEntity(String.class);
        Document doc = XmlParser.stringToXML(resp);
      //  System.out.println(doc.getElementsByTagName(Constants.PubChemClient.PUBCHEM_REQUEST_RESULT_COUNT).item(Constants.PubChemClient.ITEM_NUMBER).getFirstChild().getNodeName());
        int count = Integer.parseInt(doc.getElementsByTagName(Constants.PubChemClient.PUBCHEM_REQUEST_RESULT_COUNT).item(Constants.PubChemClient.ITEM_NUMBER).getFirstChild().getNodeValue());
       // System.out.println(count);
        if (count > 0) {
            pubChemESearch.setWebEnv(doc.getElementsByTagName(Constants.PubChemClient.PUBCHEM_REQUEST_WEB_ENV_NAME).item(Constants.PubChemClient.ITEM_NUMBER).getFirstChild().getNodeValue());
            pubChemESearch.setQueryKey(doc.getElementsByTagName(Constants.PubChemClient.PUBCHEM_REQUEST_QUERY_KEY_NAME).item(Constants.PubChemClient.ITEM_NUMBER).getFirstChild().getNodeValue());
          //  System.out.println(pubChemESearch.getQueryKey());
          //  System.out.println(pubChemESearch.getWebEnv());
        } else {
            pubChemESearch = null;
        }
        return pubChemESearch;

    }


    /**
     * @param massRange
     * @return url
     * @throws ChemIDStructureException
     */
    public String getDownloadURL(String massRange) throws ChemIDStructureException, ParserConfigurationException, IOException, SAXException {
        String downloadUrl = null;
        String filepath = "E:/chemid/structure/src/main/resources/dbclient/pubchem/download.xml";
        //System.out.println(filepath);
        //set webEnv, querykey to eSearch
        pubChemESearch = getPubChemESearchRequestParameters(massRange);

        if (pubChemESearch != null) {
            //create document with querykey and webenv
            String xmlFile = Constants.PubChemClient.PUB_CHEM_DOWNLOAD_PAYLOAD_FILE_NAME;
            String resource = Constants.PubChemClient.PUBCHEM_RESOURCES;
            //System.out.println(xmlFile);
           // System.out.println(resource);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document xmlPayload = docBuilder.parse(filepath);

            //Document xmlPayload = XmlParser.getXMLPayload(xmlFile, resource);
            System.out.println(xmlPayload);
            xmlPayload.getElementsByTagName(Constants.PubChemClient.PUBCHEM_PAYLOAD_QUERY_KEY_NAME).item(Constants.PubChemClient.ITEM_NUMBER).setTextContent(pubChemESearch.getQueryKey());
            xmlPayload.getElementsByTagName(Constants.PubChemClient.PUBCHEM_PAYLOAD_WEB_ENV_NAME).item(Constants.PubChemClient.ITEM_NUMBER).setTextContent(pubChemESearch.getWebEnv());
            downloadUrl = pubQuery(XmlParser.getStringFromDocument(xmlPayload));
            System.out.println(downloadUrl);
        }


        return downloadUrl;
    }

    /**
     * @param xmlPayload
     * @return url of sdf file
     * @throws ChemIDStructureException
     */
    public String pubQuery(String xmlPayload) throws ChemIDStructureException {
        String pubQuery = null;
        try {
            //post request with query key, webenv

            this.restClient = new RestClient();
            Invocation.Builder invocationBuilder = restClient.getWebResource(Constants.PubChemClient.REQUEST_URL).request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.entity(xmlPayload, MediaType.TEXT_PLAIN));
            //response with waitning req id

            String resp = response.readEntity(String.class);
           // System.out.println(resp);

            while (resp.contains(Constants.PubChemClient.PUG_QUERY_QUEUED_STATUS_TAG_NAME) || resp.contains(Constants.PubChemClient.PUG_QUERY_RUNNING_STATUS_TAG_NAME)) {
                Thread.sleep(Constants.PubChemClient.PUBCHEM_THREAD_SLEEP_TIME);
                if (resp.contains(Constants.PubChemClient.CHECK_QUERY_WAITING_REQUEST_ID_TAG)) {
                    // request with equest id
                    resp = checkQuery(XmlParser.stringToXML(resp).getElementsByTagName(Constants.PubChemClient.CHECK_QUERY_WAITING_REQUEST_ID_TAG_NAME).item(Constants.PubChemClient.ITEM_NUMBER).getFirstChild().getNodeValue());
                    System.out.println(resp);
                }
            }
            // url of sdf file
            String getUrl = Constants.PubChemClient.PUG_QUERY_SDF_DOWNLOAD_URL;
            int item = Constants.PubChemClient.ITEM_NUMBER;
            pubQuery = XmlParser.stringToXML(resp).getElementsByTagName(getUrl).item(item).getFirstChild().getNodeValue();
           // System.out.println("pub"+pubQuery);

        } catch (InterruptedException | ParserConfigurationException | IOException | SAXException e) {
            Thread.currentThread().interrupt();
            throw new ChemIDStructureException("Error occurred while downloading chemspider downloadCompressedSDF: ", e);
        }
        return pubQuery;
    }

    /**
     * @param requestID
     * @return verified url of sdf file
     * @throws ChemIDStructureException
     */
    public String checkQuery(String requestID) throws ChemIDStructureException, ParserConfigurationException, IOException, SAXException {
        //Document xmlPayload = null;
        String filepath = "E:/chemid/structure/src/main/resources/dbclient/pubchem/download.xml";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xmlPayload = docBuilder.parse(filepath);

        //xmlPayload = XmlParser.getXMLPayload(Constants.PubChemClient.CHECK_QUERY_FILE_NAME,Constants.PubChemClient.PUBCHEM_RESOURCES);
        System.out.println(xmlPayload);
        xmlPayload.getElementsByTagName(Constants.PubChemClient.CHECK_QUERY_REQUEST_ID_TAG_NAME).item(Constants.PubChemClient.ITEM_NUMBER).setTextContent(requestID);
        ClientConfig config = new ClientConfig();

        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(Constants.PubChemClient.REQUEST_URL);

        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.post(Entity.entity(xmlPayload, MediaType.TEXT_PLAIN));
        System.out.println("checkquery"+response.readEntity(String.class));
        return response.readEntity(String.class);
    }

    /**
     * @param fileUrl
     * @param location
     * @return sdf file saved location with file name
     * @throws ChemIDStructureException
     */
    public String saveFile(String fileUrl, String location) throws ChemIDStructureException {
        String savedPath = null;
        if (fileUrl != null) {
            try {
                URL url = new URL(fileUrl);
                File dir = new File(location);
                dir.mkdirs();
                String fileName = new SimpleDateFormat(Constants.ZIP_FILE_NAME).format(new Date());
                File tmp = new File(dir, fileName);
                tmp.createNewFile();
                FileUtils.copyURLToFile(url, tmp);
                if (location.endsWith("/")) {
                    savedPath = location + fileName;

                } else {
                    savedPath = location + '/' + fileName;
                }
            } catch (IOException e) {
                throw new ChemIDStructureException("Error occurred while saving PubChem results file : ", e);

            }
        }
       // System.out.println("saved_path"+savedPath);
        return savedPath;
    }
}
