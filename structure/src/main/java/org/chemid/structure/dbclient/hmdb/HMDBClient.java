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

package org.chemid.structure.dbclient.hmdb;

import org.chemid.structure.common.Constants;
import org.chemid.structure.exception.ChemIDStructureException;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.chemid.cheminformatics.FileIO.filterBundledSDFByMass;

public class HMDBClient {


    /**
     * @param lowerMassValue
     * @param upperMassValue
     * @param location
     * @return sdf file saved location
     * @throws ChemIDStructureException
     */
    public String searchHMDB(double lowerMassValue, double upperMassValue, String location) throws ChemIDStructureException, IOException, CDKException {
        String sdfFileName = new SimpleDateFormat(Constants.SDF_FILE_NAME).format(new Date());
        String outputSDFPath = location+sdfFileName;
        filterBundledSDFByMass(Constants.HMDBConstants.HMDB_FILE_PATH,outputSDFPath,Constants.HMDBConstants.HMDB_MOLECULAR_WEIGHT,lowerMassValue,upperMassValue);
        return outputSDFPath;
    }







}
