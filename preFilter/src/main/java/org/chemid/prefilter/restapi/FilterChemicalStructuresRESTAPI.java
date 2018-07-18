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



import org.chemid.cheminformatics.ChemicalStructureManipulator;
import org.chemid.prefilter.common.Constants;
import org.chemid.cheminformatics.Filters;
//import static org.chemid.cheminformatics.ChemicalStructureManipulator.filterChemStructures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

/**
 * This class includes RESTful API methods for chemical structure pre filter.
 */

@Path("/rest/filter")
public class FilterChemicalStructuresRESTAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterChemicalStructuresRESTAPI.class);

    @POST
    @Path("doFilters")
    @Produces(MediaType.TEXT_HTML)
    public String removeIrrelevantStructures(
            @FormParam("input_file_path") String inputFilePath,
            @FormParam("remove_disconnected_structures") boolean removeDisconnected,
            @FormParam("remove_heavy_isotopes") boolean removeHeavyIsotopes,
            @FormParam("remove_stereoisomers") boolean removeStereoisomers,
            @FormParam("keep_compounds") String keepCompounds,
            @FormParam("compound_must_contain") String mustContain,
            @FormParam("eliminate_overall_charges") boolean eliminateCharges,
            @FormParam("keep_positive_charges") boolean keepPositiveCharges) {
        String savedPath = null;
        try {
            if (Objects.equals(inputFilePath, "") || inputFilePath == null) {
                savedPath = Constants.FILE_PATH_EMPTY;
            } else {
                ChemicalStructureManipulator filter = new ChemicalStructureManipulator(inputFilePath, removeDisconnected, removeHeavyIsotopes, removeStereoisomers, keepCompounds, mustContain, eliminateCharges, keepPositiveCharges);
                savedPath = filter.FilterStructures();
            }
        } catch (Exception e) {
            LOGGER.error(Constants.ZERO_COMPOUNDS_ERROR_LOG_PREFILTER, e);

        }

        return savedPath;


    }
}
