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
import org.chemid.structure.exception.ChemIDStructureException;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import javax.activation.DataHandler;
import java.io.*;
import java.lang.String;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

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
    private Integer connectionTimeout = Constants.ChemSpiderConstants.SERVICE_CONNECTION_TIME_OUT;
    private Integer soTimeOut = Constants.ChemSpiderConstants.CONNECTION_TIME_OUT;


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
    public static String getSearchGetAsyncSearchStatusResults(String rid, String token) throws ChemIDStructureException {
        String output = null;
        final SearchStub thisSearchStub;
        try {
            thisSearchStub = new SearchStub();

            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            SearchStub.GetAsyncSearchStatus getAsyncSearchStatusInput =
                    new SearchStub.GetAsyncSearchStatus();
            getAsyncSearchStatusInput.setRid(rid);
            getAsyncSearchStatusInput.setToken(token);
            final GetAsyncSearchStatusResponse thisGetAsyncSearchStatusResponse =
                    thisSearchStub.getAsyncSearchStatus(getAsyncSearchStatusInput);
            output = thisGetAsyncSearchStatusResponse.getGetAsyncSearchStatusResult().toString();
        } catch (RemoteException e) {
            throw new ChemIDStructureException("Error occurred while downloading chemspider :", e);
        }
        return output;
    }

    /**
     * @param dh
     * @param location
     * @return savedpath
     * @throws ChemIDStructureException
     */
    private static String saveFile(DataHandler dh, String location) throws ChemIDStructureException {
        String savedFile = null;
        InputStream is = null;
        try {
            is = dh.getInputStream();
            File dir = new File(location);
            dir.mkdirs();
            String fileName = new SimpleDateFormat(Constants.ZIP_FILE_NAME).format(new Date());
            File tmp = new File(dir, fileName);
            OutputStream os = new FileOutputStream(tmp);
            // This will copy the file from the two streams
            IOUtils.copy(is, os);
            // This will close two streams catching exception
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
            if (location.endsWith(Constants.LOCATION_SEPARATOR)) {
                savedFile = location + fileName;

            } else {
                savedFile = location + Constants.LOCATION_SEPARATOR + fileName;
            }
        } catch (IOException e) {
            throw new ChemIDStructureException("error occured while saving chemspider sdf file to the location", e);

        }
        return savedFile;
    }

    /**
     * Query the ChemSpider Database by Mass and Error values.
     *
     * @param mass     : Experimental mass value.
     * @param error    : Instrumentation error.
     * @param location : location where file to be save
     * @return The string containing the list of CSID values of resultant molecules.
     * @throws ChemIDStructureException
     */
    public String getChemicalStructuresByMass(Double mass, Double error, String location) throws ChemIDStructureException {
        String sdfPath = null;
        String timeOut = HTTPConstants.CONNECTION_TIMEOUT;

        try {
            MassSpecAPIStub massSpecAPIStub = new MassSpecAPIStub();
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(timeOut, connectionTimeout);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, soTimeOut);
            massSpecAPIStub._getServiceClient().getOptions().setCallTransportCleanup(true);
            SearchByMassAsync searchByMassAsync = new SearchByMassAsync();
            searchByMassAsync.setMass(mass);
            searchByMassAsync.setRange(error);
            searchByMassAsync.setToken(this.token);

            SearchByMassAsyncResponse massAsyncResponse = massSpecAPIStub.searchByMassAsync(searchByMassAsync);

            SearchStub thisSearchStub = new SearchStub();
            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            thisSearchStub._getServiceClient().getOptions().setProperty(timeOut, connectionTimeout);
            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, soTimeOut);
            SearchStub.GetAsyncSearchResult getAsyncSearchResultInput = new SearchStub.GetAsyncSearchResult();
            getAsyncSearchResultInput.setRid(massAsyncResponse.getSearchByMassAsyncResult());
            getAsyncSearchResultInput.setToken(token);

            GetAsyncSearchResultResponse thisGetAsyncSearchResultResponse =
                    thisSearchStub.getAsyncSearchResult(getAsyncSearchResultInput);

            //list of CIDs
            int[] output = thisGetAsyncSearchResultResponse.getGetAsyncSearchResultResult().get_int();

            if (output.length > 0) {
                sdfPath = getChemicalStructuresByCsids(IntStream.of(output).distinct().toArray(), location);
                thisSearchStub.cleanup();
                massSpecAPIStub._getServiceClient().cleanupTransport();
                massSpecAPIStub.cleanup();
            }

            return sdfPath;
        } catch (RemoteException e) {
            throw new ChemIDStructureException("Error occurred while downloading chemspider results: ", e);
        }
    }

    /**
     * Query the database by CSIDs.
     *
     * @param location  : location where file to be save.
     * @param csidsList : CSID of a molecule
     * @param location  : location of file to be save
     * @return : String containing the molecules in sdf format.
     * @throws ChemIDStructureException
     */
    public String getChemicalStructuresByCsids(int[] csidsList, String location) throws ChemIDStructureException {
        String sdfPath = null;
        List<IAtomContainer> cons = new ArrayList<>();
        String timeOut = HTTPConstants.CONNECTION_TIMEOUT;
        try {

            MassSpecAPIStub massSpecAPIStub = new MassSpecAPIStub();
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(timeOut, connectionTimeout);
            massSpecAPIStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, soTimeOut);
            List<String> csids = new ArrayList<>();

            if (csidsList.length == 1) {
                this.candidates = new IAtomContainer[1];
                GetRecordMol getRecordMol = new GetRecordMol();
                getRecordMol.setCsid(String.valueOf(csidsList[0]));
                getRecordMol.setToken(this.token);
                GetRecordMolResponse grmr = massSpecAPIStub.getRecordMol(getRecordMol);
                cons = this.getAtomContainerFromString(grmr.getGetRecordMolResult());
                csids.add(String.valueOf(0));
                this.candidates[0] = cons.get(0);

            } else if (csidsList.length > 0) {
                StringBuilder b = new StringBuilder();

                for (int csid : csidsList) {
                    b.append(csid);
                    b.append(",");
                }

                sdfPath = sendRequest(b.toString(), massSpecAPIStub, location);

            }
            massSpecAPIStub._getServiceClient().cleanupTransport();
            massSpecAPIStub.cleanup();

            return sdfPath;
        } catch (RemoteException e) {
            throw new ChemIDStructureException("Error occurred while downloading chemspider getChemicalStructuresByCsids: ", e);

        }
    }

    /**
     * @param query
     * @param massSpecAPIStub
     * @param location
     * @return
     * @throws ChemIDStructureException
     */
    private String sendRequest(String query, MassSpecAPIStub massSpecAPIStub, String location) throws ChemIDStructureException {
        String result = null;
        AsyncSimpleSearch asyncSimpleSearch = new AsyncSimpleSearch();

        asyncSimpleSearch.setQuery(query);
        asyncSimpleSearch.setToken(this.token);
        SearchStub thisSearchStub = null;
        try {
            thisSearchStub = new SearchStub();

            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, false);
            thisSearchStub._getServiceClient().getOptions().
                    setProperty(HTTPConstants.CONNECTION_TIMEOUT, connectionTimeout);
            thisSearchStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, soTimeOut);
            result = this.downloadCompressedSDF(thisSearchStub
                    .asyncSimpleSearch(asyncSimpleSearch)
                    .getAsyncSimpleSearchResult(), massSpecAPIStub, location);
        } catch (RemoteException e) {
            throw new ChemIDStructureException("Error occurred while downloading chemspider getChemicalStructuresByCsids: ", e);

        }
        return result;
    }

    /**
     * Download the molecules in sdf format.
     *
     * @param location : location where file to be save.
     * @param rid      : Transaction id
     * @param stub     : MassSpecAPI instance.
     * @return : String of all molecules in sdf format.
     */
    protected String downloadCompressedSDF(String rid, MassSpecAPIStub stub, String location) throws ChemIDStructureException {
        TransactionConfiguration tc = new TransactionConfiguration();
        tc.setTransactionTimeout(Integer.MAX_VALUE);
        stub._getServiceClient().getAxisConfiguration().setTransactionConfig(tc);
        boolean statusOk = false;
        while (!statusOk) {
            String status = getSearchGetAsyncSearchStatusResults(rid, token);
            if (status.equals(Constants.ChemSpiderConstants.CHEM_SPIDER_RESULT_STATUS)) {
                statusOk = true;
            } else {
                try {
                    Thread.sleep(Constants.ChemSpiderConstants.THREAD_TIME_OUT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ChemIDStructureException("Error occurred while downloading chemspider downloadCompressedSDF: ", e);
                }
            }
        }
        GetCompressedRecordsSdf getCompressedRecordsSdf = new GetCompressedRecordsSdf();
        getCompressedRecordsSdf.setRid(rid);
        getCompressedRecordsSdf.setToken(this.token);
        getCompressedRecordsSdf.setEComp(ECompression.eGzip);
        GetCompressedRecordsSdfResponse getCompressedRecordsSdfResponse = null;
        javax.activation.DataHandler dh = null;
        try {
            getCompressedRecordsSdfResponse = stub.getCompressedRecordsSdf(getCompressedRecordsSdf);
            dh = getCompressedRecordsSdfResponse.getGetCompressedRecordsSdfResult();

        } catch (RemoteException e) {
            throw new ChemIDStructureException("Problem retrieving ChemSpider webservices: ", e);
        }
        String savedFile = null;

        if (dh != null) {
            savedFile = saveFile(dh, location);

        }

        return savedFile;
    }

    /**
     * Read the sdf string and get the iAtomContainer object.
     *
     * @param sdfString : The String containing chemical structures in sdf format.
     * @return : The list of structures as iAtomContainer objects.
     * @throws ChemIDStructureException
     */
    protected List<IAtomContainer> getAtomContainerFromString(String sdfString) throws ChemIDStructureException {
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(sdfString));
        List<IAtomContainer> containersList;
        List<IAtomContainer> ret = new ArrayList<>();
        try {
            ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
            containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
            for (IAtomContainer container : containersList) {
                ret.add(container);
            }

            reader.close();
        } catch (CDKException | IOException e) {
            throw new ChemIDStructureException("Problem getting atom container : ", e);

        }
        return ret;
    }
}