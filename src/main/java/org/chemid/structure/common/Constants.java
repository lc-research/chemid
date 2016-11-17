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

public class Constants {

    public static class ChemSpiderConstants {
        public static String TOKEN = "327be9cb-76c5-48cf-97df-9da48db88e85";
        public static Integer CONNECTION_TIMEOUT = 180000;
        public static Integer SO_TIME_OUT = 18000;
        public static Integer THREAD_TIME_OUT = 1000;
    }

    public static final class PubChemClient {

        public static final String REQUEST_URL = "https://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi";
        public static final String ESEARCH_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pccompound&usehistory=y&retmax=0&term=";
        public static final int ITEM_NUMBER = 0;
        public static final String PUBCHEM_RESOURCES = "dbclient/pubchem/";
        public static final String PUBCHEM_DOWNLOAD_PAYLOAD_FILENAME = "download.xml";
        public static final String PUBCHEM_REQUEST_WebEnv_NAME = "WebEnv";
        public static final String PUBCHEM_REQUEST_RESULT_COUNT = "Count";

        public static final String PUBCHEM_REQUEST_QueryKey_NAME = "QueryKey";
        public static final String PUBCHEM_PAYLOAD_WebEnv_NAME = "PCT-Entrez_webenv";
        public static final String PUBCHEM_PAYLOAD_QueryKey_NAME = "PCT-Entrez_query-key";
        public static final String CHECK_QUERY_FILE_NAME = "checkQuery.xml";
        public static final String CHECK_QUERY_REQUEST_ID_TAG_NAME = "PCT-Request_reqid";
        public static final String CHECK_QUERY_WAITING_REQUEST_ID_TAG = "<PCT-Waiting_reqid>";
        public static final String CHECK_QUERY_WAITING_REQUEST_ID_TAG_NAME = "PCT-Waiting_reqid";
        public static final String PUG_QUERY_QUEUED_STATUS_TAG_NAME = "<PCT-Status value=\"queued\"/>";
        public static final String PUG_QUERY_RUNNING_STATUS_TAG_NAME = "<PCT-Status value=\"running\"/>";
        public static final String PUG_QUERY_SDF_DOWNLOAD_URL = "PCT-Download-URL_url";

    }
}
