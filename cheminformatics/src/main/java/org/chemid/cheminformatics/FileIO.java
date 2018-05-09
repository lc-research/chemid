package org.chemid.cheminformatics;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileIO {
    public static IteratingSDFReader readSDF(String FilePathToSDF) throws FileNotFoundException {
        File sdf = new File(FilePathToSDF);
        IteratingSDFReader sdfReader = new IteratingSDFReader(new FileInputStream(sdf), DefaultChemObjectBuilder.getInstance());
        return (sdfReader);
    }

}
