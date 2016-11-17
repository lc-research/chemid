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
import org.chemid.structure.dbclient.pubchem.PubChemClient;
import org.chemid.structure.dbclient.pubchem.utilities.PubchemTools;
import org.chemid.structure.dbclient.utilities.Tools;
import org.chemid.structure.exception.CatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.FileHandler;

/**
 * This class includes RESTful API methods for chemical structure service.
 */
@Path("/rest/structure")
public class ChemicalStructureServiceRESTAPI {
    private static final Logger logger = LoggerFactory.getLogger(ChemicalStructureServiceRESTAPI.class);
    FileHandler fh;

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


    @GET
    @Path("{database}/{mass}/{adduct}/{error}/{errorUnit}/{fileFormat}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getChemicalStructures(@PathParam("database") String database,
                                        @PathParam("mass") Double mass,
                                        @PathParam("adduct") String adduct,
                                        @PathParam("error") Double error,
                                        @PathParam("errorUnit") String errorUnit,
                                        @PathParam("fileFormat") String fileFormat,
                                        @QueryParam("location") String location) throws CatchException {
        String sdfPath = null;
        String loc = location;
        try {
            double searchMass = Tools.getSearchMass(mass, adduct);
            error = Tools.getMassError(mass, error, errorUnit);
            if (loc.trim().equals("null") || loc.trim().isEmpty() || loc.trim() == null) {
                loc = "D://";
            } else {
                loc = location;
            }


            switch (database.toLowerCase().trim()) {
                case "pubchem":
                    String massRange = PubchemTools.getMassRange(searchMass, error);
                    PubChemClient pubChemClient = new PubChemClient();
                    String Url = pubChemClient.getDownloadURL(massRange);
                    sdfPath = pubChemClient.saveFile(Url, loc.trim());

                case "chemspider":
                    ChemSpiderClient client = ChemSpiderClient.getInstance(Constants.ChemSpiderConstants.TOKEN, true);
                    sdfPath = client.getChemicalStructuresByMass(searchMass, error, loc.trim());

                case "hmdb":
                    HMDBClient hmdbClient = new HMDBClient();
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve data from databases : " + e);
        }
        if(sdfPath == null){
            sdfPath = database.toLowerCase()+" not returning compounds for given mass range";
        }
        return sdfPath;
    }
}
