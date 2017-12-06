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
    public static final String DEFAULT_LOCATION = "D://";
    public static final String ZIP_FILE_NAME = "yyyyMMddhhmm'.zip'";
    public static final String LOCATION_SEPARATOR = "/";
    public static final String SDF_FILE_NAME = "yyyyMMddhhmm'.sdf'";
public static final String ZERO_COMPOUNDS_ERROR_LOG = "something going wrong.Failed to retrieve data from databases";
public static final String NO_COMPOUNDS = "0 compounds!";
    public static class ChemSpiderConstants {
        public static final String CHEMSPIDER_DB_NAME = "chemspider";
        public static final String CHEM_SPIDER_TOKEN = "327be9cb-76c5-48cf-97df-9da48db88e85";
        public static final Integer SERVICE_CONNECTION_TIME_OUT = 180000;
        public static final Integer CONNECTION_TIME_OUT = 18000;
        public static final String CHEM_SPIDER_RESULT_STATUS = "ResultReady";
        public static final String CHEMSPIDER_CSID = "CSID";
        public static String TOKEN = "327be9cb-76c5-48cf-97df-9da48db88e85";
        public static Integer CONNECTION_TIMEOUT = 180000;
        public static Integer SO_TIME_OUT = 18000;
        public static Integer THREAD_TIME_OUT = 1000;


    }

    public static class HMDBConstants {
        public static final String HMDB_RESOURCES = "dbclient/hmdb/";
        public static final String HMDB_OUTPUT_FILE = "hmdb.sdf";
        public static final String HMDB_MOLECULAR_WEIGHT = "MIMW";
        public static final String HMDB_DB_NAME = "hmdb";
        public static final String HMDB_ID = "ID";
        public static final String FILE_NOT_FOUND = "Resource file not found!";
    }

    public static final class PubChemClient {
        public static final String PUBCHEM_ISOTOPIC_ATOM_COUNT = "PUBCHEM_ISOTOPIC_ATOM_COUNT";
        public static final String PUBCHEM_DB_NAME = "pubchem";
        public static final String E_SEARCH_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pccompound&usehistory=y&retmax=0&term=";
        public static final String PUBCHEM_REQUEST_WEB_ENV_NAME = "WebEnv";
        public static final String PUBCHEM_REQUEST_QUERY_KEY_NAME = "QueryKey";
        public static final String PUB_CHEM_DOWNLOAD_PAYLOAD_FILE_NAME = "download.xml";
        public static final String PUBCHEM_PAYLOAD_QUERY_KEY_NAME = "PCT-Entrez_query-key";
        public static final String PUBCHEM_PAYLOAD_WEB_ENV_NAME = "PCT-Entrez_webenv";
        public static final int PUBCHEM_THREAD_SLEEP_TIME = 1000;
        public static final String REQUEST_URL = "https://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi";
        public static final int ITEM_NUMBER = 0;
        public static final String PUBCHEM_RESOURCES = "dbclient/pubchem/";
        public static final String PUBCHEM_REQUEST_RESULT_COUNT = "Count";
        public static final String CHECK_QUERY_FILE_NAME = "checkQuery.xml";
        public static final String CHECK_QUERY_REQUEST_ID_TAG_NAME = "PCT-Request_reqid";
        public static final String CHECK_QUERY_WAITING_REQUEST_ID_TAG = "<PCT-Waiting_reqid>";
        public static final String CHECK_QUERY_WAITING_REQUEST_ID_TAG_NAME = "PCT-Waiting_reqid";
        public static final String PUG_QUERY_QUEUED_STATUS_TAG_NAME = "<PCT-Status value=\"queued\"/>";
        public static final String PUG_QUERY_RUNNING_STATUS_TAG_NAME = "<PCT-Status value=\"running\"/>";
        public static final String PUG_QUERY_SDF_DOWNLOAD_URL = "PCT-Download-URL_url";

    }


}
