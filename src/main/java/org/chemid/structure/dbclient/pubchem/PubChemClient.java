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

package org.chemid.structure.dbclient.pubchem;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.chemid.structure.dbclient.pubchem.beans.pubChemESearch;
import org.chemid.structure.common.Constants;
import org.chemid.structure.common.RestClient;
import org.chemid.structure.common.XmlParser;
import org.glassfish.jersey.client.ClientConfig;
import org.w3c.dom.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

public class PubChemClient {

    private pubChemESearch pubChemESearch;
    private RestClient restClient;

    public pubChemESearch getPubChemESearchRequestParameters(String massRange) {

        try {
            this.restClient = new RestClient();
            //Get request to http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pccompound&usehistory=y&retmax=0&term=100:100.01[exactmass]

            Invocation.Builder invocationBuilder = restClient.getWebResource(Constants.PubChemClient.ESEARCH_URL + massRange).
                    request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.get();
            String resp = response.readEntity(String.class);
            Document doc = XmlParser.StringToXML(resp);
            pubChemESearch = new pubChemESearch();
            pubChemESearch.setWebEnv(doc.getElementsByTagName(Constants.PubChemClient.PUBCHEM_REQUEST_WebEnv_NAME).
                    item(Constants.PubChemClient.ITEM_NUMBER).
                    getFirstChild().getNodeValue());
            pubChemESearch.setQueryKey(doc.getElementsByTagName(Constants.PubChemClient.PUBCHEM_REQUEST_QueryKey_NAME).
                    item(Constants.PubChemClient.ITEM_NUMBER).
                    getFirstChild().getNodeValue());
            //generate wenenv, query key, count
            return pubChemESearch;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDownloadURL(String massRange) {

        try {
            //set webEnv, querykey to eSearch
            pubChemESearch = getPubChemESearchRequestParameters(massRange);

            //create document with querykey and webenv
            Document xmlPayload = XmlParser.getXMLPayload(Constants.PubChemClient.PUBCHEM_DOWNLOAD_PAYLOAD_FILENAME,
                    Constants.PubChemClient.PUBCHEM_RESOURCES);
            xmlPayload.getElementsByTagName(Constants.PubChemClient.PUBCHEM_PAYLOAD_QueryKey_NAME).
                    item(Constants.PubChemClient.ITEM_NUMBER).setTextContent(pubChemESearch.getQueryKey());
            xmlPayload.getElementsByTagName(Constants.PubChemClient.PUBCHEM_PAYLOAD_WebEnv_NAME).
                    item(Constants.PubChemClient.ITEM_NUMBER).setTextContent(pubChemESearch.getWebEnv());

            return pubQuery(XmlParser.getStringFromDocument(xmlPayload));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String pubQuery(String xmlPayload) {

        try {
            //post request with query key, webenv

            this.restClient = new RestClient();
            Invocation.Builder invocationBuilder = restClient.getWebResource(Constants.PubChemClient.REQUEST_URL).
                    request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.entity(xmlPayload, MediaType.TEXT_PLAIN));
            //response with waitning req id

            String resp = response.readEntity(String.class);

            while (resp.contains(Constants.PubChemClient.PUG_QUERY_QUEUED_STATUS_TAG_NAME) ||
                    resp.contains(Constants.PubChemClient.PUG_QUERY_RUNNING_STATUS_TAG_NAME)) {
                Thread.sleep(1000);
                if (resp.contains(Constants.PubChemClient.CHECK_QUERY_WAITING_REQUEST_ID_TAG)) {
                    // request with equest id
                    resp = checkQuery(XmlParser.StringToXML(resp).getElementsByTagName(Constants.PubChemClient.
                            CHECK_QUERY_WAITING_REQUEST_ID_TAG_NAME).item(Constants.PubChemClient.ITEM_NUMBER).
                            getFirstChild().getNodeValue());
                }
            }
            // url of sdf file
            return XmlParser.StringToXML(resp).getElementsByTagName(Constants.PubChemClient.PUG_QUERY_SDF_DOWNLOAD_URL).
                    item(Constants.PubChemClient.ITEM_NUMBER).getFirstChild().getNodeValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String checkQuery(String requestID) {

        try {
            Document xmlPayload = XmlParser.getXMLPayload(Constants.PubChemClient.CHECK_QUERY_FILE_NAME,
                    Constants.PubChemClient.PUBCHEM_RESOURCES);
            xmlPayload.getElementsByTagName(Constants.PubChemClient.CHECK_QUERY_REQUEST_ID_TAG_NAME).
                    item(Constants.PubChemClient.ITEM_NUMBER).setTextContent(requestID);
            ClientConfig config = new ClientConfig();

            Client client = ClientBuilder.newClient(config);

            WebTarget target = client.target(Constants.PubChemClient.REQUEST_URL);

            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_XML);
            Response response = invocationBuilder.post(Entity.entity(xmlPayload, MediaType.TEXT_PLAIN));
            return response.readEntity(String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StringBuilder getSDFBuffer(String Url) {
//read sdf file by url
        URL url = null;
        try {
            url = new URL(Url);
            GZIPInputStream gzis = new GZIPInputStream(url.openStream());
            InputStreamReader reader = new InputStreamReader(gzis);
            BufferedReader in = new BufferedReader(reader);
            StringBuilder stringBuffer = new StringBuilder("");
            String readed;
            while ((readed = in.readLine()) != null) {
                stringBuffer.append(readed);
                stringBuffer.append("\n");
            }
            return stringBuffer;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String saveFile(String Url, String location) {
        URL url = null;
        String savedPath = "not Saved";

        try {
            url = new URL(Url);
            File dir = new File(location);
            dir.mkdirs();
            String fileName = new SimpleDateFormat("yyyyMMddhhmm'.zip'").format(new Date());
            File tmp = new File(dir, fileName);
            tmp.createNewFile();
            FileUtils.copyURLToFile(url, tmp);
            savedPath = "";
            if(location.endsWith("/")){
                savedPath= location+fileName;

            }else {
                savedPath = location + '/' + fileName;
            }        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedPath;
    }
}
