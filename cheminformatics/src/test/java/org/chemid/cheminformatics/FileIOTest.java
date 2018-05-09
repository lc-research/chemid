package org.chemid.cheminformatics;

import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.testng.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.testng.Assert.*;

public class FileIOTest {

    @org.testng.annotations.Test
    public void testReadSDF() throws IOException {
        String FilePathToSDF = "src/test/resources/test.sdf";
        IteratingSDFReader sdfReader = FileIO.readSDF(FilePathToSDF);
        int moleculeCount = 0;
        while (sdfReader.hasNext()){
            moleculeCount++;
            sdfReader.next();
        }
        sdfReader.close();
        Assert.assertEquals(moleculeCount,2);
    }
}