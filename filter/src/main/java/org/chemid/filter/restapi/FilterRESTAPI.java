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

package org.chemid.filter.restapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;

import org.chemid.filter.common.Constants;
import org.chemid.filter.common.CommonClass;
import static org.chemid.cheminformatics.FileIO.getFilterValues;
import static org.chemid.cheminformatics.FileIO.addPropertySDF;

/**
 * This class includes RESTful API methods for filter service
 */

@Path("rest/filter")
public class FilterRESTAPI {

    private static final Logger LOGGER=LoggerFactory.getLogger(FilterRESTAPI.class);

  @GET()
  @Path("version")
  @Produces(MediaType.TEXT_HTML)
  public String version(){
      return "Chemical filter Service V 1.0";
    }


    @POST
    @Path("/applyFilter")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addProperty(
                @FormParam("inputFilePath") String inputFilePath,
                @FormParam("propertyFilePath") String propertyFilePath,
                @FormParam("experinmentalValue") double experinmentalValue,
                @FormParam("error") double error,
                @FormParam("propertyName")String propertyName

    ) throws IOException {
        CommonClass getpath = new CommonClass();
        String outputSDFpath=getpath.generateOutputFileName(inputFilePath);
        HashMap<String,String> hashMapValues=getFilterValues(propertyFilePath,experinmentalValue,error);
        String outPutPath = null;
        if(inputFilePath==null){

            outPutPath=Constants.FILE_PATH_EMPTY;
        }
        else {
           addPropertySDF(inputFilePath,hashMapValues,outputSDFpath,propertyName);

        }
        return outPutPath;
    }


}
