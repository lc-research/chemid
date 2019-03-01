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

package org.chemid.id.restapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import org.chemid.id.common.Constants;
import static org.chemid.cheminformatics.FileIO.calWeightProperty;

/**
 * This class includes RESTful API methods for ID service
 */

@Path("rest/id")
public class ChemicalIdRESTAPI {

    private static final Logger LOGGER=LoggerFactory.getLogger(ChemicalIdRESTAPI.class);

    /**
     * This method returns the version number of the ID service.
     * @return API version
     */
  @GET()
  @Path("version")
  @Produces(MediaType.TEXT_HTML)
  public String version(){
      return "Chemical ID Service V 1.0";
    }

    /**
     *
     * @param inputSDFpath : File Path to Input SD File
     * @param valueRI : Experimental RI Value
     * @param valueECOM50 : Experimental ECOM50 Value
     * @param valueCCS : Experimental CCS Value
     * @param valueCFMID : Experimental CFMID Value
     * @param weightRI : RI weight
     * @param weightECOM50 : ECOM50 weight
     * @param weightCCS : CCS weight
     * @param weightCFMID : CFMID weight
     * @param keepweightRI : Boolean of RI
     * @param keepweightECOM50 : Boolean of ECMO50
     * @param keepweightCCS : Boolean of CCS
     * @param keepweightCFMID : Boolean of CFMID
     * @return String of output path
     * @throws IOException
     */
    @POST
    @Path("/property")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addProperty(
                @FormParam("inputSDFpath") String inputSDFpath,
                @FormParam("experimentalRI") double valueRI,
                @FormParam("experimentalECOM50") double valueECOM50,
                @FormParam("experimentalCCS") double valueCCS,
                @FormParam("exprimnetalCFMID")double valueCFMID,
                @FormParam("weightRI")double weightRI,
                @FormParam("weightECOM50")double weightECOM50,
                @FormParam("weightCCS")double weightCCS,
                @FormParam("weightCFMID")double weightCFMID,
                @FormParam("keepRIWeight")boolean keepweightRI,
                @FormParam("keepECOM50Weight")boolean keepweightECOM50,
                @FormParam("keepCCSweight")boolean keepweightCCS,
                @FormParam("keepCFMIDweight")boolean keepweightCFMID
    ) throws IOException {

        String outPutPath = null;
        if(inputSDFpath==null){

            outPutPath=Constants.FILE_PATH_EMPTY;
        }
        else {
            outPutPath = calWeightProperty(inputSDFpath, valueRI, valueECOM50, valueCCS, valueCFMID,
                    weightRI, weightECOM50, weightCCS, weightCFMID, keepweightRI, keepweightECOM50, keepweightCCS, keepweightCFMID);
        }
        return outPutPath;
    }


}
