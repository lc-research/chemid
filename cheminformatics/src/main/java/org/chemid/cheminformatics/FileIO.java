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

import org.chemid.common.Constants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileIO.class);
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
                    ).forEach(filteredIAtomContainer -> {
                try {
                    sdfWriter.write(filteredIAtomContainer);
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


    //**********************************************MsMatch***************************************************************************

    /**
     *
     * @param mol
     * @return
     */
    public static String generateInChI(IAtomContainer mol)  {
        String inchi = null;
        try {
            InChIGeneratorFactory generator = InChIGeneratorFactory.getInstance();
            inchi = generator.getInChIGenerator(mol).getInchi();
        } catch (CDKException e) {
            LOGGER.error("Something wrong with generating Inchi",e);
        }

        return inchi;
    }

    /**
     *
     * @param sdfFile
     * @param pubchemCID
     * @param chemspiderCSID
     * @param hmdbID
     * @param map
     */
        public static void getmolProperty(File sdfFile,String pubchemCID,String chemspiderCSID,String hmdbID, ConcurrentMap<String, String> map) {
            IteratingSDFReader reader = null;

            if(!sdfFile.exists()){
                System.out.print("Candidate File not Exit or invalid file");
            }

            try {
                reader = new IteratingSDFReader(
                        new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());

                while (reader.hasNext()) {
                    IAtomContainer molecule = (IAtomContainer) reader.next();
                    String inChI = generateInChI(molecule);
                    if (molecule.getProperty(pubchemCID) != null) {
                        if (map.size() < 150 && !map.containsKey(molecule.getProperty(pubchemCID))) {

                            map.put(molecule.getProperty(pubchemCID), inChI);
                        }
                    } else if (molecule.getProperty(chemspiderCSID) != null) {
                        if (map.size() < 150 && !map.containsKey(molecule.getProperty(chemspiderCSID))) {

                            map.put(molecule.getProperty(chemspiderCSID), inChI);
                        }
                    } else if (molecule.getProperty(hmdbID) != null) {
                        if (map.size() < 150 && !map.containsKey(molecule.getProperty(hmdbID))) {

                            map.put(molecule.getProperty(hmdbID), inChI);
                        }
                    }
                }


            } catch (FileNotFoundException e1) {
               LOGGER.error("Something wrong with file paths",e1);
            }
        }

    /**
     *
      * @param inputpathsdf
     * @param scorevalues
     * @param outputpathsdf
     * @throws IOException
     */
    public static void addPropertySDF(String inputpathsdf, HashMap<String,String> scorevalues, String outputpathsdf) throws IOException {

        File sdfFile = new File(inputpathsdf);
        IteratingSDFReader sdfReader =null;
        SDFWriter sdfWriter = new SDFWriter(new FileWriter(outputpathsdf));
        try {
            sdfReader = new IteratingSDFReader(new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());
            while (sdfReader.hasNext()) {
                IAtomContainer molecule = (IAtomContainer) sdfReader.next();
                String hmdb_ID=(String)(molecule.getProperty(Constants.HMDB_ID));
                String cs_ID=(String)(molecule.getProperty(Constants.CHEMSPIDER_CSID));
                String pubchem_ID=(String)(molecule.getProperty(Constants.PUBCHEM_COMPOUND_CID));
                scorevalues.forEach((key, v) -> {
                    if ((hmdb_ID!=null && (hmdb_ID.equals(key))) || (cs_ID!=null && (cs_ID.equals(key))) || (pubchem_ID!=null && (pubchem_ID.equals(key)))) {
                        molecule.setProperty("CFMIDScore", v);
                        try {
                            sdfWriter.write(molecule);
                        } catch (CDKException e) {
                            e.printStackTrace();
                        }
                    }

                });

            }

        }catch (Exception e){
            throw new RuntimeException(e);
        }
        sdfWriter.close();
        sdfReader.close();


    }


    //***********************************************CleanUpStructures***************************************************************************






}