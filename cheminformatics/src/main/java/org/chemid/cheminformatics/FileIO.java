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
     * @param candidateFilePath
     * @param pubchemCID
     * @param chemspiderCSID
     * @param hmdbID
     */
        public static void getmolProperty(String candidateFilePath,String pubchemCID,String chemspiderCSID,String hmdbID) {
            IteratingSDFReader reader = null;
            ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

            File sdfFile = new File(candidateFilePath);


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

    //***********************************************CleanUpStructures***************************************************************************

    public static String inputFilePath, savedPath;;
    public static SDFWriter writer;
    public static boolean flag = true;
    public static int k = 0;

    public static boolean removeDisconnected, removeHeavyIsotopes, removeStereoisomers, eliminateCharges, keepPositiveCharges;
    public static List<String> keepCompoundsWith, compoundsMustContain;
    public static int molCharge;
    public static ConcurrentMap<String, String> map;

    /**
     *
     * @param locationSeparatorLeft
     * @param locationSeparator
     * @param filenotFound
     * @param emptymsg
     * @param sdfFilename
     * @param pubchemISOTOPICatim
     * @param generatedSmile
     * @param pubChemCID
     * @param chemSpiderCSID
     * @param hmdbID
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static String Structures(String locationSeparatorLeft,String locationSeparator,String filenotFound,String emptymsg,String sdfFilename,String pubchemISOTOPICatim,String generatedSmile,String pubChemCID,String chemSpiderCSID,String hmdbID) throws Exception, IOException {
        int index =0;
        index = inputFilePath.lastIndexOf(locationSeparatorLeft);
        if (index  <= 0) {
            index = inputFilePath.lastIndexOf(locationSeparator);
        }

        createFile(inputFilePath.substring(0, index),sdfFilename,locationSeparator);
        File sdfFile = new File(inputFilePath);
        if (!sdfFile.exists()) {
            savedPath = filenotFound;
        } else {
            Iterable<IAtomContainer> iterable = getIterables(sdfFile);
            Stream<IAtomContainer> stream = StreamSupport.stream(iterable.spliterator(), true).filter(mol -> {
                if (!removeDisconnectedStructures(removeDisconnected, mol)) {
                    return false;
                } else {
                    flag = true;
                }
                if (!removeHeavyIsotopes(mol, removeHeavyIsotopes,pubchemISOTOPICatim)) {
                    return false;
                } else {
                    flag = true;
                }
                if (keepCompoundsWith.size() > 0) {
                    if (!keepThisCompounds(keepCompoundsWith, mol)) {
                        return false;
                    } else {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (compoundsMustContain.size() > 0) {
                    if (!compoundsMustContain(compoundsMustContain, mol)) {
                        return false;
                    } else {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (keepPositiveCharges || eliminateCharges) {
                    molCharge = AtomContainerManipulator.getTotalFormalCharge(mol);
                    if (keepPositiveCharges) {
                        if (molCharge > 0) {
                            flag = true;
                        } else {
                            return false;
                        }

                    } else if (eliminateCharges) {
                        if (molCharge == 0) {
                            flag = true;
                        } else {
                            return false;
                        }
                    }
                }
                try {
                    if (!removeStereoisomers(mol, removeStereoisomers,generatedSmile,pubChemCID,chemSpiderCSID,hmdbID)) {
                        return false;
                    } else {
                        flag = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return flag;
            });
            writeToFile(stream);

            if (k == 0) {
                savedPath = emptymsg;
            }
        }
        return savedPath;
    }

    /**
     *
     * @param location2
     * @param sdfFilename
     * @param locationSeparator
     * @throws IOException
     */
    private static void createFile(String location2,String sdfFilename,String locationSeparator) throws IOException {
        String location = Paths.get(location2).toString();
        System.out.println(location+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        String outputName = new SimpleDateFormat(sdfFilename).format(new Date());
        if (location.endsWith(locationSeparator)) {
            savedPath = location + outputName;
        } else {
            savedPath = location + locationSeparator + outputName;
        }
        File output = new File(savedPath);
        FileWriter fileWriter = new FileWriter(output);
        writer = new SDFWriter(fileWriter);

    }

    /**
     *
     * @param sdfFile
     * @return
     */
    private static Iterable<IAtomContainer> getIterables(File sdfFile) {
        Iterable<IAtomContainer> iterables = null;
        IteratingSDFReader reader;

        try {
            reader = new IteratingSDFReader(
                    new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());
            final IteratingSDFReader finalReader = reader;
            iterables = () -> finalReader;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return iterables;
    }

    /**
     *
     * @param removeDisconnected
     * @param mol
     * @return
     */
    private static boolean removeDisconnectedStructures(boolean removeDisconnected, IAtomContainer mol) {
        return !removeDisconnected || ConnectivityChecker.isConnected(mol);
    }

    /**
     *
     * @param mol
     * @param removeHeavyIsotopes
     * @param pubchemISOTOPICatim
     * @return
     */
    private static boolean removeHeavyIsotopes(IAtomContainer mol, boolean removeHeavyIsotopes,String pubchemISOTOPICatim) {
        if (removeHeavyIsotopes) {
            if (mol.getProperty(pubchemISOTOPICatim) != null) {
                if (Integer.parseInt(mol.getProperty(pubchemISOTOPICatim).toString()) > 0) {
                    return false;
                }
            } else {
                return true;
            }

        }
        return true;
    }

    /**
     *
     * @param keepCompoundsWith
     * @param mol
     * @return
     */
    private static boolean keepThisCompounds(List<String> keepCompoundsWith, IAtomContainer mol) {
        if (keepCompoundsWith.size() == 0) {
            return true;
        }
        boolean hasElem = false, hasFlag = false;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            String atom = mol.getAtom(i).getSymbol().toUpperCase();
            for (String aKeepCompoundsWith : keepCompoundsWith) {
                String returnAtom = aKeepCompoundsWith.toUpperCase();
                if (atom.equals(returnAtom)) {
                    hasElem = true;
                    break;
                } else {
                    hasElem = false;
                }
            }
            if (hasElem) {
                hasFlag = true;
            } else {
                hasFlag = false;
                break;
            }
        }
        return hasFlag;
    }

    /**
     *
     * @param compoundsMustContain
     * @param mol
     * @return
     */
    private static boolean compoundsMustContain(List<String> compoundsMustContain, IAtomContainer mol) {
        if (compoundsMustContain.size() == 0) {
            return true;
        }
        List<String> atomList1 = new ArrayList<>();
        for (int y = 0; y < mol.getAtomCount(); y++) {
            String molecule = mol.getAtom(y).getSymbol().toUpperCase();
            if (!atomList1.contains(molecule)) {
                atomList1.add(molecule);
            }
        }
        if (atomList1.size() > 0) {
            return getEqualLists(compoundsMustContain, atomList1);
        } else {
            return false;
        }
    }

    /**
     *
     * @param list1
     * @param list2
     * @return
     */
    private static boolean getEqualLists(List<String> list1, List<String> list2) {
        if (list1 == null && list2 == null)
            return false;
        if (list2 == null)
            return false;

        for (String itemList1 : list1) {
            if (!list2.contains(itemList1))
                return false;
        }
        return true;
    }

    /**
     *
     * @param mol
     * @param removeStereoisomers
     * @param generatedSmile
     * @param pubChemCID
     * @param chemSpiderCSID
     * @param hmdbID
     * @return
     * @throws Exception
     */
    private static boolean removeStereoisomers(IAtomContainer mol, boolean removeStereoisomers,String generatedSmile,String pubChemCID,String chemSpiderCSID,String hmdbID) throws Exception {
        String smile = createSmile(mol);

        mol.setProperty(generatedSmile, smile);
        if (removeStereoisomers) if (map != null) {
            if (map.containsKey(smile)) {
                return false;
            } else {
                if (mol.getProperty(pubChemCID) != null) {
                    map.put(smile, mol.getProperty(pubChemCID));
                } else if (mol.getProperty(chemSpiderCSID) != null) {
                    map.put(smile, mol.getProperty(chemSpiderCSID));
                } else if (mol.getProperty(hmdbID) != null) {
                    map.put(smile, mol.getProperty(hmdbID));
                }
                return true;
            }

        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @param mol
     * @return
     * @throws Exception
     */
    private static String createSmile(IAtomContainer mol) throws Exception {
        SmilesGenerator smileGen = SmilesGenerator.unique();
        String smile;
        try {
            smile = smileGen.create(mol);
        } catch (CDKException e) {
            throw new Exception(e.getMessage(), e);
        }
        return smile;
    }

    /**
     *
     * @param stream
     */
    private static void writeToFile(Stream<IAtomContainer> stream) {
        stream.forEach(item -> {
            try {
                k++;
                writer.write(item);
            } catch (CDKException | RuntimeException e) {
                throw new RuntimeException(e);
            }
        });
    }





}