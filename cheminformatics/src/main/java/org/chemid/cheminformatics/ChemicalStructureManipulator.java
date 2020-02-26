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
import org.chemid.exception.CheminformaticsException;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import static org.chemid.common.CommonClass.generateOutputFileName;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

/**
 *  This Class includes filtering methods for molecules
 */

public class ChemicalStructureManipulator {

    public static ConcurrentMap<String, String> map;
    public static int molCharge;

    private ChemicalStructureManipulator(){

    }


    /**preFilter Module
     *
     * @param filters import from Filter Class
     * @return String of sdf file updated or not
     * @throws CheminformaticsException
     * @throws IOException
     */

    public static String Structures(Filters filters) throws CheminformaticsException, IOException {

        System.out.println(filters.inputFilePath);
        if(filters.inputFilePath==null){

            return Constants.FILE_NOT_FOUND;

        }

        else {

            try {
                String outputpath = (generateOutputFileName(filters.inputFilePath));
                System.out.println(outputpath);
                SDFWriter sdfWriter = new SDFWriter(new FileWriter(new File(outputpath)));
                IteratingSDFReader sdfReader = new IteratingSDFReader(new FileInputStream(filters.inputFilePath), DefaultChemObjectBuilder.getInstance());
                Iterable<IAtomContainer> iterables = () -> sdfReader;
                StreamSupport.stream(iterables.spliterator(), true).filter(iAtomContainer ->
                        {
                            if (filters.removeDisconnected || ConnectivityChecker.isConnected(iAtomContainer)) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                ).filter(iAtomContainer -> {

                    if (filters.removeHeavyIsotopes) {
                        if (iAtomContainer.getProperty(Constants.PUBCHEM_ISOTOPIC_ATOM_COUNT) != null) {
                            if (Integer.parseInt(iAtomContainer.getProperty(Constants.PUBCHEM_ISOTOPIC_ATOM_COUNT).toString()) > 0) {
                                return true;
                            }
                        }
                        return true;

                    } else {

                        return false;
                    }


                }).filter(iAtomContainer -> {
                    if (filters.keepCompoundsWith.size() > 0) {
                        if (keepCompounds(filters.keepCompoundsWith, iAtomContainer)) {
                            return true;
                        }

                        return true;
                    } else {
                        return false;
                    }

                }).filter((iAtomContainer -> {
                    if (filters.compoundsMustContain.size() > 0) {
                        if (compoundsMustContain(filters.compoundsMustContain, iAtomContainer)) {
                            return true;
                        }
                        return true;
                    } else {
                        return false;
                    }


                }))
                        .filter((iAtomContainer -> {

                            if (filters.keepPositiveCharges || filters.eliminateCharges) {
                                molCharge = AtomContainerManipulator.getTotalFormalCharge(iAtomContainer);

                                if (filters.keepPositiveCharges) {
                                    if (molCharge > 0) {
                                        return true;
                                    } else {
                                        return false;
                                    }

                                } else if (filters.eliminateCharges) {
                                    if (molCharge == 0) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                                return true;
                            } else {
                                return false;
                            }
                        })).forEach(filteredIAtomContainer -> {
                    try {
                        sdfWriter.write(filteredIAtomContainer);
                    } catch (CDKException e) {
                        throw new RuntimeException(e);
                    }
                });
                sdfWriter.close();
                sdfReader.close();

                if (filters.removeStereoisomers) {

                    updateSterioIsomers(outputpath);
                }
            } catch (IOException | RuntimeException e) {
                throw new RuntimeException(e);
            }


            return Constants.PROPERTY_UPDATED;
        }
    }


    /**Update Sterio Isomers
     *
     * @param inputSDF File Path to Input SD File
     */
    private static void updateSterioIsomers(String inputSDF){
        File sdfFile = new File(inputSDF);
        IteratingSDFReader sdfReader =null;
        HashMap<String, String> hashMap = new HashMap<>();


            try {

                sdfReader = new IteratingSDFReader(new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());
                while (sdfReader.hasNext()) {
                    IAtomContainer molecule = (IAtomContainer) sdfReader.next();

                    String hmdb_ID = (String) (molecule.getProperty(Constants.HMDB_ID));
                    String cs_ID = (String) (molecule.getProperty(Constants.CHEMSPIDER_CSID));
                    String pubchem_ID = (String) (molecule.getProperty(Constants.PUBCHEM_COMPOUND_CID));
                    String uniquesmile = createSmile(molecule);
                    String id = null;

                    if (cs_ID == null && pubchem_ID == null) {
                        id = hmdb_ID;
                    } else if (cs_ID == null && hmdb_ID == null) {
                        id = pubchem_ID;
                    } else if (pubchem_ID == null && hmdb_ID == null) {
                        id = cs_ID;
                    }

                    if (hashMap.containsKey(uniquesmile)) {
                        hashMap.put(uniquesmile, hashMap.get(uniquesmile) + "," + id);
                    } else {
                        hashMap.put(uniquesmile, id);
                    }


                }

                sdfReader.close();

                sdfReader = new IteratingSDFReader(new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());
                SDFWriter sdfWriter = new SDFWriter(new FileWriter(generateOutputFileName(inputSDF)));
                while (sdfReader.hasNext()) {

                    IAtomContainer molecule = (IAtomContainer) sdfReader.next();
                    String uniquesmile = createSmile(molecule);

                    if (hashMap.containsKey(uniquesmile)) {
                        molecule.setProperty("SterioIsomers", hashMap.get(uniquesmile));
                        sdfWriter.write(molecule);
                        hashMap.remove(uniquesmile);
                    }


                }


                sdfWriter.close();
                sdfReader.close();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    /**Get the atoms of IAtomContainer and keep the compound by matching with array
     *
     * @param keepCompoundsWith : Compunds Array List
     * @param mol : Molecule from SD File
     * @return : boolean of KeepCompounds
     */
    private static boolean keepCompounds(List<String> keepCompoundsWith, IAtomContainer mol) {
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
     * @param compoundsMustContain Compunds Array List
     * @param mol : Molecule from sdf File
     * @return : boolean of KeepCompounds
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

    /** Get Equals from Compounds Array and Molecule Array
     *
     * @param list1 : Compunds Array List
     * @param list2 : Molecule Array List
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

    /**Create Smiles for molecules
     *
     * @param mol : Molecule from SD File
     * @return : String Smile
     * @throws CheminformaticsException
     */

    private static String createSmile(IAtomContainer mol) throws CheminformaticsException {
        SmilesGenerator smileGen = SmilesGenerator.unique();
        String smile;
        try {
            smile = smileGen.create(mol);
        } catch (CDKException e) {
            throw new CheminformaticsException(e.getMessage(), e);
        }

        return smile;
    }


}