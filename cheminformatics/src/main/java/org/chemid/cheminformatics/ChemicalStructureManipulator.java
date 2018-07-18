package org.chemid.cheminformatics;
import org.chemid.common.Constants;
import org.chemid.exception.CheminformaticsException;
import org.chemid.cheminformatics.Filters;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class ChemicalStructureManipulator {

    private String inputFilePath, keepCompounds, mustContain, savedPath;
    private SDFWriter writer;
    private boolean flag = true;
    private int k = 0;

    private boolean removeDisconnected, removeHeavyIsotopes, removeStereoisomers, eliminateCharges, keepPositiveCharges;
    private List<String> keepCompoundsWith, compoundsMustContain;
    private int molCharge;
    private ConcurrentMap<String, String> map;

    public ChemicalStructureManipulator(String inputFilePath, boolean removeDisconnected, boolean removeHeavyIsotopes, boolean removeStereoisomers, String keepCompounds, String mustContain, boolean eliminateCharges, boolean keepPositiveCharges) {
        this.inputFilePath = inputFilePath;
        this.removeDisconnected = removeDisconnected;
        this.removeHeavyIsotopes = removeHeavyIsotopes;
        this.removeStereoisomers = removeStereoisomers;
        this.eliminateCharges = eliminateCharges;
        this.keepCompounds = keepCompounds;
        this.mustContain = mustContain;
        this.keepPositiveCharges = keepPositiveCharges;
        map = new ConcurrentHashMap<>();
        if (keepCompounds.length() > 0) {
            keepCompoundsWith = new ArrayList<>(Arrays.asList(keepCompounds.split(",")));
        } else {
            keepCompoundsWith = new ArrayList<>();
        }
        if (mustContain.length() > 0) {
            compoundsMustContain = new ArrayList<>(Arrays.asList(mustContain.split(",")));
            compoundsMustContain.replaceAll(String::toUpperCase);
        } else {
            compoundsMustContain = new ArrayList<>();
        }
    }

    public String FilterStructures() throws CheminformaticsException, IOException {
        int index =0;
        index = inputFilePath.lastIndexOf(Constants.LOCATION_SEPARATOR_LEFT);
        if (index  <= 0) {
            index = inputFilePath.lastIndexOf(Constants.LOCATION_SEPARATOR);
        }

        createFile(inputFilePath.substring(0, index));
        File sdfFile = new File(inputFilePath);
        if (!sdfFile.exists()) {
            savedPath = Constants.FILE_NOT_FOUND;
        } else {
            Iterable<IAtomContainer> iterable = getIterables(sdfFile);
            Stream<IAtomContainer> stream = StreamSupport.stream(iterable.spliterator(), true).filter(mol -> {
                if (!removeDisconnectedStructures(removeDisconnected, mol)) {
                    return false;
                } else {
                    flag = true;
                }
                if (!removeHeavyIsotopes(mol, removeHeavyIsotopes)) {
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
                    if (!removeStereoisomers(mol, removeStereoisomers)) {
                        return false;
                    } else {
                        flag = true;
                    }
                } catch (CheminformaticsException e) {
                    e.printStackTrace();
                }
                return flag;
            });
            writeToFile(stream);

            if (k == 0) {
                savedPath = Constants.EMPTY_MSG;
            }
        }

        return savedPath;
    }

    private void createFile(String location2) throws IOException {
        String location = Paths.get(location2).toString();
        String outputName = new SimpleDateFormat(Constants.SDF_FILE_NAME).format(new Date());
        if (location.endsWith(Constants.LOCATION_SEPARATOR)) {
            savedPath = location + outputName;
        } else {
            savedPath = location + Constants.LOCATION_SEPARATOR_LEFT + outputName;
        }
        File output = new File(savedPath);
        FileWriter fileWriter = new FileWriter(output);
        writer = new SDFWriter(fileWriter);

    }

    private Iterable<IAtomContainer> getIterables(File sdfFile) {
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

    private boolean removeDisconnectedStructures(boolean removeDisconnected, IAtomContainer mol) {
        return !removeDisconnected || ConnectivityChecker.isConnected(mol);
    }

    private boolean removeHeavyIsotopes(IAtomContainer mol, boolean removeHeavyIsotopes) {
        if (removeHeavyIsotopes) {
            if (mol.getProperty(Constants.PUBCHEM_ISOTOPIC_ATOM_COUNT) != null) {
                if (Integer.parseInt(mol.getProperty(Constants.PUBCHEM_ISOTOPIC_ATOM_COUNT).toString()) > 0) {
                    return false;
                }
            } else {
                return true;
            }

        }
        return true;
    }

    private boolean keepThisCompounds(List<String> keepCompoundsWith, IAtomContainer mol) {
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

    private boolean compoundsMustContain(List<String> compoundsMustContain, IAtomContainer mol) {
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

    private boolean getEqualLists(List<String> list1, List<String> list2) {
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

    private boolean removeStereoisomers(IAtomContainer mol, boolean removeStereoisomers) throws CheminformaticsException {
        String smile = createSmile(mol);

        mol.setProperty(Constants.GENERATED_SMILE, smile);
        if (removeStereoisomers) if (map != null) {
            if (map.containsKey(smile)) {
                return false;
            } else {
                if (mol.getProperty(Constants.PUBCHEM_COMPOUND_CID) != null) {
                    map.put(smile, mol.getProperty(Constants.PUBCHEM_COMPOUND_CID));
                } else if (mol.getProperty(Constants.CHEMSPIDER_CSID) != null) {
                    map.put(smile, mol.getProperty(Constants.CHEMSPIDER_CSID));
                } else if (mol.getProperty(Constants.HMDB_ID) != null) {
                    map.put(smile, mol.getProperty(Constants.HMDB_ID));
                }
                return true;
            }

        } else {
            return false;
        }
        return true;
    }

    private String createSmile(IAtomContainer mol) throws CheminformaticsException {
        SmilesGenerator smileGen = SmilesGenerator.unique();
        String smile;
        try {
            smile = smileGen.create(mol);
        } catch (CDKException e) {
            throw new CheminformaticsException(e.getMessage(), e);
        }
        return smile;
    }

    private void writeToFile(Stream<IAtomContainer> stream) {
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
