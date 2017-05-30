/*
 * Copyright (c) 2015, ChemID. (http://www.chemid.org)
 *
 * ChemID licenses this file to you under the Apache License V 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.chemid.structure.restapi;

import org.chemid.structure.common.Constants;
import org.chemid.structure.dbclient.chemspider.ChemSpiderClient;
import org.chemid.structure.dbclient.hmdb.HMDBClient;
import org.chemid.structure.dbclient.hmdb.utilities.HMDBTools;
import org.chemid.structure.dbclient.pubchem.PubChemClient;
import org.chemid.structure.dbclient.pubchem.beans.PubChemESearch;
import org.chemid.structure.dbclient.pubchem.utilities.PubchemTools;
import org.chemid.structure.dbclient.utilities.Tools;
import org.chemid.structure.exception.ChemIDStructureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Locale;

/**
 * This class includes RESTful API methods for chemical structure service.
 */
@Path("/rest/structure")
public class ChemicalStructureServiceRESTAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChemicalStructureServiceRESTAPI.class);

    /**
     * This method returns the version number of the chemical structure service.
     *
     * @return API version
     */
    @GET
    @Path("version")
    @Produces(MediaType.TEXT_PLAIN)
    public String version() {
        return "Chemical Structure Service V 1.0";
    }

    @POST
    @Path("/searchdb")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createTrackInJSON(@FormParam("database") String database,
                                    @FormParam("mass") String massString,
                                    @FormParam("adduct") String adduct,
                                    @FormParam("error") String errorString,
                                    @FormParam("errorUnit") String errorUnit,
                                    @FormParam("fileFormat") String fileFormat,
                                    @FormParam("location") String location) {

        double mass = Double.parseDouble(massString);
        double error = Double.parseDouble(errorString);
        String sdfPath = null;
        String loc = location;
        String checkNull = "null";
        try {
            double searchMass = Tools.getSearchMass(mass, adduct);
            Double massError = Tools.getMassError(mass, error, errorUnit);
            if (loc.trim().equals(checkNull) || loc.trim().isEmpty() || loc.trim() == null) {
                loc = Constants.DEFAULT_LOCATION;
            } else {
                loc = location;
            }


            switch (database.toLowerCase(Locale.ENGLISH).trim()) {
                case Constants.PubChemClient.PUBCHEM_DB_NAME:
                    PubChemESearch pubChemESearch = new PubChemESearch();
                    PubChemClient pubChemClient = new PubChemClient(pubChemESearch);
                    String url = pubChemClient.getDownloadURL(PubchemTools.getMassRange(searchMass, massError));
                    sdfPath = pubChemClient.saveFile(url, loc.trim());
                    break;
                case Constants.ChemSpiderConstants.CHEMSPIDER_DB_NAME:
                    String token = Constants.ChemSpiderConstants.CHEM_SPIDER_TOKEN;
                    ChemSpiderClient client = ChemSpiderClient.getInstance(token, true);
                    sdfPath = client.getChemicalStructuresByMass(searchMass, massError, loc.trim());
                    break;
                case Constants.HMDBConstants.HMDB_DB_NAME:

                    HMDBClient hmdbClient = new HMDBClient();
                    double lowerVal = HMDBTools.getLowerMassValue(searchMass, massError);
                    double upperVal = HMDBTools.getUpperMassValue(searchMass, error);
                    sdfPath = hmdbClient.searchHMDB(lowerVal, upperVal, loc.trim());
                    break;
                default:
                    sdfPath = null;
                    break;
            }
        } catch (ChemIDStructureException | RuntimeException | IOException e) {
            LOGGER.error(Constants.ZERO_COMPOUNDS_ERROR_LOG, e);

        }
        if (sdfPath == null) {
            sdfPath = Constants.NO_COMPOUNDS;
        }
        return sdfPath;

    }




}