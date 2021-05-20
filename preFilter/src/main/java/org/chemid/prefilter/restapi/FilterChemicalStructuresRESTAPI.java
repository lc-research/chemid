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

package org.chemid.prefilter.restapi;


import org.chemid.prefilter.common.Constants;
import org.chemid.cheminformatics.Filters;
import static org.chemid.cheminformatics.ChemicalStructureManipulator.Structures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

/**
 * This class includes RESTful API methods for chemical structure pre filter.
 */

@Path("/rest/prefilter")
public class FilterChemicalStructuresRESTAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterChemicalStructuresRESTAPI.class);
    /**
     *  This method returns the version number of the preFilter service.
     * @return API Version
     */
    @GET
    @Path("/version")
    @Produces(MediaType.TEXT_PLAIN)
    public String version() {
        return "preFilter Service V 1.0";
    }

    /**
     *
     * @param inputFilePath : File Path to Input SD File
     * @param removeDisconnected : Whether to remove disconnected structures or not
     * @param removeHeavyIsotopes : Whether to remove heavy isotopes or not
     * @param removeStereoisomers : Whether to remove stereoisomers or not
     * @param keepCompounds : User specified set of elements
     * @param mustContain : User specified set of elements
     * @param eliminateCharges : Whether to remove charged structures
     * @param keepPositiveCharges : Whether to remove neutral or negatively charged structures
     * @return String of saved path
     */
    @POST
    @Path("applyPreFilters")
    @Produces(MediaType.TEXT_HTML)
    public String removeIrrelevantStructures(
            @FormParam("inputFilePath") String inputFilePath,
            @FormParam("removeDisconnectedStructures") boolean removeDisconnected,
            @FormParam("removeHeavyIsotopes") boolean removeHeavyIsotopes,
            @FormParam("removeStereoisomers") boolean removeStereoisomers,
            @FormParam("keepCompounds") String keepCompounds,
            @FormParam("compoundMustContain") String mustContain,
            @FormParam("eliminateOverallCharges") boolean eliminateCharges,
            @FormParam("keepPositiveCharges") boolean keepPositiveCharges) {
        String savedPath = null;

        try {
            if (Objects.equals(inputFilePath, "") || inputFilePath == null) {
                savedPath = Constants.FILE_PATH_EMPTY;
            } else {

                Filters filters=new Filters(inputFilePath, removeDisconnected, removeHeavyIsotopes, removeStereoisomers, keepCompounds, mustContain, eliminateCharges, keepPositiveCharges);
                savedPath =Structures(filters);

            }
        } catch (Exception e) {
            LOGGER.error(Constants.ZERO_COMPOUNDS_ERROR_LOG_PREFILTER, e);

        }

        return savedPath;


    }
}