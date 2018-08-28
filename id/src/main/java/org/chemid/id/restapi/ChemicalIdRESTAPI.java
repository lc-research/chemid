package org.chemid.id.restapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import org.chemid.id.common.Constants;

import static org.chemid.cheminformatics.FileIO.calWeightProperty;


@Path("rest/id")
public class ChemicalIdRESTAPI {

    private static final Logger LOGGER=LoggerFactory.getLogger(ChemicalIdRESTAPI.class);

  @GET()
  @Path("version")
  @Produces(MediaType.TEXT_HTML)
  public String version(){
      return "Chemical ID Service V 1.0";
    }


    @POST
    @Path("/property")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addProperty(
                @FormParam("input_sdf_path") String inputSDFpath,
                @FormParam("experimental_ri") double valueRI,
                @FormParam("experimental_ecom50") double valueECOM50,
                @FormParam("experimental_ccs") double valueCCS,
                @FormParam("exprimnetal_cfmid")double valueCFMID,
                @FormParam("ri_weight")double weightRI,
                @FormParam("ecom50_weight")double weightECOM50,
                @FormParam("ccs_weight")double weightCCS,
                @FormParam("cfmid_weight")double weightCFMID,
                @FormParam("keep_ri_weight")boolean keepweightRI,
                @FormParam("keep_ecom50_weight")boolean keepweightECOM50,
                @FormParam("keep_ccs_weight")boolean keepweightCCS,
                @FormParam("keep_cfmid_weight")boolean keepweightCFMID
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
