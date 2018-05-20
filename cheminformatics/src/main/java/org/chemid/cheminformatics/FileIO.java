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

package org.chemid.cheminformatics;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.*;
import java.util.stream.StreamSupport;

public class FileIO {

    /**
     *
     * @param FilePathToSDF File Path to SD File
     * @return Method Returns an Iterator of IAtomContainers
     * @throws FileNotFoundException
     */
    public static IteratingSDFReader readSDF(String FilePathToSDF) throws FileNotFoundException {
        File sdf = new File(FilePathToSDF);
        IteratingSDFReader sdfReader = new IteratingSDFReader(new FileInputStream(sdf), DefaultChemObjectBuilder.getInstance());
        return (sdfReader);
    }


    /**
     *
     * @param pathToInputSDF File Path to Input SD File
     * @param pathToOutputSDF File Path to Output SD File
     * @param MWTag Input SD File Tag/Property that Identifies Molecular Weight
     * @param lowerMassValue Lower Cutoff of the Mass Range
     * @param upperMassValue Upper Cutoff of the Mass Range
     */
    public static void filterBundledSDFByMass(String pathToInputSDF, String pathToOutputSDF, String MWTag, double lowerMassValue, double upperMassValue) {
        try {
            SDFWriter sdfWriter = new SDFWriter(new FileWriter(new File(pathToOutputSDF)));
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(pathToInputSDF);
            IteratingSDFReader sdfReader = new IteratingSDFReader(
                    inputStream, DefaultChemObjectBuilder.getInstance());
            Iterable<IAtomContainer> iterables = () -> sdfReader;
            StreamSupport.stream(iterables.spliterator(), true).
                    filter(iAtomContainer ->
                            {
                                double mimw = Double.parseDouble(iAtomContainer.getProperty(MWTag));
                                if (mimw > lowerMassValue && mimw < upperMassValue) {
                                    return true;
                                }
                                else{
                                    return false;
                                }
                            }
                    ).forEach(mol -> {
                try {
                    //System.out.println(mol.getAtomCount());
                    sdfWriter.write(mol);
                }
                catch(CDKException e){
                    throw new RuntimeException(e);
                }
            });
            sdfWriter.close();
            sdfReader.close();
            inputStream.close();
        }
        catch(IOException | RuntimeException e)
        {
            throw new RuntimeException(e);
        }

    }

}
