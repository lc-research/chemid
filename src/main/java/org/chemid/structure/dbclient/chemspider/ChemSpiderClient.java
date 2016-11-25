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
package org.chemid.structure.dbclient.chemspider;

import org.apache.axis2.transaction.TransactionConfiguration;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.io.IOUtils;
import org.chemid.structure.common.Constants;
import org.chemid.structure.dbclient.chemspider.generated.MassSpecAPIStub;
import org.chemid.structure.dbclient.chemspider.generated.MassSpecAPIStub.*;
import org.chemid.structure.dbclient.chemspider.generated.SearchStub;
import org.chemid.structure.dbclient.chemspider.generated.SearchStub.AsyncSimpleSearch;
import org.chemid.structure.dbclient.chemspider.generated.SearchStub.GetAsyncSearchResultResponse;
import org.chemid.structure.dbclient.chemspider.generated.SearchStub.GetAsyncSearchStatusResponse;
import org.chemid.structure.exception.CatchException;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.*;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * ChemSpider client to download chemical structures from ChemSpider web services.
 */
public class ChemSpiderClient {

    private static ChemSpiderClient client;
    protected String token = Constants.ChemSpiderConstants.TOKEN;
    protected IAtomContainer[] candidates = null;
    protected boolean verbose;
    private Integer CONNECTION_TIMEOUT = Constants.ChemSpiderConstants.CONNECTION_TIMEOUT;
    private Integer SO_TIME_OUT = Constants.ChemSpiderConstants.SO_TIME_OUT;

    private ChemSpiderClient(String token, boolean verbose) {
        this.token = token;
        this.verbose = verbose;
    }

    public static ChemSpiderClient getInstance(String token, boolean verbose) {
        if (client == null) {
            client = new ChemSpiderClient(token, verbose);
            return client;
        }
        return client;
    }

    /**
     * Get the status of the search
     *
     * @param rid   : Transaction id
     * @param token : Security token
     * @return
     */
    public static String get_Search_GetAsyncSearchStatus_Results(String rid, String token) throws CatchException {
        String Output = null;
        try {
            final SearchStub thisSearchStub = new SearchStub();
            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            SearchStub.GetAsyncSearchStatus GetAsyncSearchStatusInput =
                    new SearchStub.GetAsyncSearchStatus();
            GetAsyncSearchStatusInput.setRid(rid);
            GetAsyncSearchStatusInput.setToken(token);
            final GetAsyncSearchStatusResponse thisGetAsyncSearchStatusResponse =
                    thisSearchStub.getAsyncSearchStatus(GetAsyncSearchStatusInput);
            Output = thisGetAsyncSearchStatusResponse.getGetAsyncSearchStatusResult().toString();
        } catch (Exception e) {
            throw new CatchException("Error occurred while downloading chemspider :" + e.getMessage());
        }
        return Output;
    }

    /**
     * Query the ChemSpider Database by Mass and Error values.
     *
     * @param mass  : Experimental mass value.
     * @param error : Instrumentation error.
     * @return The string containing the list of CSID values of resultant molecules.
     * @throws CatchException
     */
    public String getChemicalStructuresByMass(Double mass, Double error, String location) throws CatchException {
        String sdfPath = null;

        try {
            MassSpecAPIStub massSpecAPIStub = new MassSpecAPIStub();
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, SO_TIME_OUT);
            massSpecAPIStub._getServiceClient().getOptions().setCallTransportCleanup(true);
            SearchByMassAsync searchByMassAsync = new SearchByMassAsync();
            searchByMassAsync.setMass(mass);
            searchByMassAsync.setRange(error);
            searchByMassAsync.setToken(this.token);
            SearchByMassAsyncResponse massAsyncResponse = null;

            massAsyncResponse = massSpecAPIStub.searchByMassAsync(searchByMassAsync);

            SearchStub thisSearchStub = new SearchStub();
            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, SO_TIME_OUT);
            SearchStub.GetAsyncSearchResult GetAsyncSearchResultInput = new SearchStub.GetAsyncSearchResult();
            GetAsyncSearchResultInput.setRid(massAsyncResponse.getSearchByMassAsyncResult());
            GetAsyncSearchResultInput.setToken(token);

            GetAsyncSearchResultResponse thisGetAsyncSearchResultResponse =
                    thisSearchStub.getAsyncSearchResult(GetAsyncSearchResultInput);

            //list of CIDs
            int[] Output = thisGetAsyncSearchResultResponse.getGetAsyncSearchResultResult().get_int();
            if (Output.length > 0) {
                sdfPath = getChemicalStructuresByCsids(Output, location);
                thisSearchStub.cleanup();
                massSpecAPIStub._getServiceClient().cleanupTransport();
                massSpecAPIStub.cleanup();
            }
        } catch (Exception e) {
            throw new CatchException("Error occurred while downloading chemspider results: " + e.getMessage());
        }
        return sdfPath;
    }

    /**
     * Query the database by CSIDs.
     *
     * @param _csids : CSID of a molecule
     * @return : String containing the molecules in sdf format.
     * @throws CatchException
     */
    public String getChemicalStructuresByCsids(int[] _csids, String location) throws CatchException {
        String sdfPath = null;

        try {
            Vector<Integer> uniqueCsidArray = new Vector<Integer>();
            for (int _csid : _csids) {
                if (!uniqueCsidArray.contains(_csid))
                    uniqueCsidArray.add(_csid);
            }
            MassSpecAPIStub massSpecAPIStub = new MassSpecAPIStub();
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, SO_TIME_OUT);
            Vector<String> csids = new Vector<String>();

            if (this.verbose) System.out.println("Downloading compounds from ChemSpider");
            if (uniqueCsidArray.size() == 1) {
                this.candidates = new IAtomContainer[1];
                GetRecordMol getRecordMol = new GetRecordMol();
                getRecordMol.setCsid(String.valueOf(uniqueCsidArray.get(0)));
                getRecordMol.setToken(this.token);
                GetRecordMolResponse grmr = massSpecAPIStub.getRecordMol(getRecordMol);
                try {
                    // getAtomContainerFromString convert string to IAtomContainer
                    Vector<IAtomContainer> cons = this.getAtomContainerFromString(grmr.getGetRecordMolResult());
                    csids.add(String.valueOf(0));
                    this.candidates[0] = cons.get(0);

                } catch (Exception e) {
                    throw new CatchException("Error occurred while downloading chemspider :" + e.getMessage());
                }
            } else {
                AsyncSimpleSearch asyncSimpleSearch = new AsyncSimpleSearch();
                String query = "";
                if (uniqueCsidArray.size() != 0) query += uniqueCsidArray.get(0);
                for (int i = 1; i < uniqueCsidArray.size(); i++)
                    query += "," + uniqueCsidArray.get(i);
                asyncSimpleSearch.setQuery(query);
                asyncSimpleSearch.setToken(this.token);
                SearchStub thisSearchStub = new SearchStub();
                thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
                thisSearchStub._getServiceClient().getOptions().
                        setProperty(HTTPConstants.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
                thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, SO_TIME_OUT);

                sdfPath = this.downloadCompressedSDF(
                        thisSearchStub.asyncSimpleSearch(asyncSimpleSearch).getAsyncSimpleSearchResult(), massSpecAPIStub, location);
            }
            massSpecAPIStub._getServiceClient().cleanupTransport();
            massSpecAPIStub.cleanup();
        } catch (Exception e) {
            throw new CatchException("Error occurred while downloading chemspider getChemicalStructuresByCsids: " + e.getMessage());

        }
        return sdfPath;
    }

    /**
     * Download the molecules in sdf format.
     *
     * @param rid  : Transaction id
     * @param stub : MassSpecAPI instance.
     * @return : String of all molecules in sdf format.
     */
    protected String downloadCompressedSDF(String rid, MassSpecAPIStub stub, String location) throws CatchException {
        String savedFile = "not saved";
        TransactionConfiguration tc = new TransactionConfiguration();
        tc.setTransactionTimeout(Integer.MAX_VALUE);
        stub._getServiceClient().getAxisConfiguration().setTransactionConfig(tc);
        GetCompressedRecordsSdf getCompressedRecordsSdf = new GetCompressedRecordsSdf();
        boolean status_ok = false;
        while (!status_ok) {
            String status = get_Search_GetAsyncSearchStatus_Results(rid, token);
            if (status.equals("ResultReady")) {
                status_ok = true;
            } else {
                try {

                    Thread.sleep(Constants.ChemSpiderConstants.THREAD_TIME_OUT);
                } catch (Exception e) {
                    throw new CatchException("Error occurred while downloading chemspider downloadCompressedSDF: " + e.getMessage());
                }
            }
        }
        getCompressedRecordsSdf.setRid(rid);
        getCompressedRecordsSdf.setToken(this.token);
        getCompressedRecordsSdf.setEComp(ECompression.eGzip);
        GetCompressedRecordsSdfResponse getCompressedRecordsSdfResponse = null;
        javax.activation.DataHandler dh = null;
        try {
            getCompressedRecordsSdfResponse = stub.getCompressedRecordsSdf(getCompressedRecordsSdf);

            dh = getCompressedRecordsSdfResponse.getGetCompressedRecordsSdfResult();

        } catch (Exception e) {
            throw new CatchException("Problem retrieving ChemSpider webservices: " + e.getMessage());
        }

        if (dh != null) {
            try {
                InputStream is = dh.getInputStream();
                File dir = new File(location);
                dir.mkdirs();
                String fileName = new SimpleDateFormat("yyyyMMddhhmm'.zip'").format(new Date());
                File tmp = new File(dir, fileName);
                OutputStream os = new FileOutputStream(tmp);
// This will copy the file from the two streams
                IOUtils.copy(is, os);
                savedFile = "";
// This will close two streams catching exception
                IOUtils.closeQuietly(os);
                IOUtils.closeQuietly(is);
                if (location.endsWith("/")) {
                    savedFile = location + fileName;

                } else {
                    savedFile = location + '/' + fileName;
                }
            } catch (Exception e) {
                throw new CatchException("Problem saving ChemSpider results: " + e.getMessage());
            }
        }
        return savedFile;
    }

    /**
     * Read the sdf string and get the iAtomContainer object.
     *
     * @param sdfString : The String containing chemical structures in sdf format.
     * @return : The list of structures as iAtomContainer objects.
     * @throws CatchException
     */
    protected Vector<IAtomContainer> getAtomContainerFromString(String sdfString) throws CatchException {
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(sdfString));
        List<IAtomContainer> containersList;
        Vector<IAtomContainer> ret = new Vector<IAtomContainer>();
        try {
            ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
            containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
            for (IAtomContainer container : containersList) {
                ret.add(container);
            }

            reader.close();
        } catch (Exception e) {
            throw new CatchException("Problem getting atom container : " + e.getMessage());
        }
        return ret;
    }
}