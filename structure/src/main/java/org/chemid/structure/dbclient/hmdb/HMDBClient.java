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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HMDBClient {


    private boolean isInitial;
    private String savedPath;
    private SDFWriter writer;
    private IAtomContainer container;
    private FileInputStream inputStream;

    /**
     * @param lowerMassValue
     * @param upperMassValue
     * @param location
     * @return sdf file saved location
     * @throws ChemIDStructureException
     */
    public String searchHMDB(double lowerMassValue, double upperMassValue, String location) throws ChemIDStructureException, IOException {

        createFile(location);

        String molWeight = Constants.HMDBConstants.HMDB_MOLECULAR_WEIGHT;

        Iterable<IAtomContainer> iterables;

        iterables = getIterables();


        Stream<IAtomContainer> stream = StreamSupport.stream(iterables.spliterator(), true)
                .filter(mol -> {
                    double mimw = Double.parseDouble(mol.getProperty(molWeight));
                    return mimw > lowerMassValue && mimw < upperMassValue;
                });
        writeOutputSDFFile(stream, location);


        return savedPath;
    }

    /**
     * @param stream
     * @param location
     */
    private void writeOutputSDFFile(Stream<IAtomContainer> stream, String location) {

        stream.forEach(m -> {
            try {
                if (isInitial) {
                    createFile(location);
                }
                writer.write(m);
            } catch (CDKException | RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    /**
     * @param location
     * @throws IOException
     */
    private void createFile(String location) throws IOException {
        isInitial = false;
        String outputName = new SimpleDateFormat(Constants.SDF_FILE_NAME).format(new Date());
        if (location.endsWith(Constants.LOCATION_SEPARATOR)) {
            savedPath = location + outputName;
        } else {
            savedPath = location + Constants.LOCATION_SEPARATOR + outputName;
        }
        File output = new File(savedPath);
        FileWriter fileWriter = new FileWriter(output);
        writer = new SDFWriter(fileWriter);

    }

    /**
     * @return
     * @throws ChemIDStructureException
     */
    private Iterable<IAtomContainer> getIterables() throws ChemIDStructureException {
        String doc = getResourceDocument();
        File sdfFile = new File(doc);
        Iterable<IAtomContainer> iterables;

        try {
            inputStream = new FileInputStream(sdfFile);

            IteratingSDFReader finalReader = new IteratingSDFReader(
                    inputStream, DefaultChemObjectBuilder.getInstance());
            iterables = () -> finalReader;
        } catch (IOException e) {
            throw new ChemIDStructureException("Error occurred while reading file: ", e);
        }

        return iterables;
    }

    /**
     * @return
     */
    private String getResourceDocument() {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String hmdbRes = Constants.HMDBConstants.HMDB_RESOURCES;
        String outputFile = Constants.HMDBConstants.HMDB_OUTPUT_FILE;
        URL resource = classLoader.getResource(hmdbRes + outputFile);

        if (resource != null) {
            return resource.getPath();
        }
        return Constants.HMDBConstants.FILE_NOT_FOUND;
    }


}
