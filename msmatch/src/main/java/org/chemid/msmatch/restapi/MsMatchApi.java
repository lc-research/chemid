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

package org.chemid.msmatch.restApi;

import org.chemid.msmatch.algorithm.CFMIDAlgorithm;
import org.chemid.msmatch.common.CommonClasses;
import org.chemid.msmatch.common.Constants;
import org.chemid.msmatch.exception.ChemIDMsMatchException;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.chemid.msmatch.common.Constants.DATA_SEPARATOR;

@Path("/rest/msmatch")
public class MsMatchApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsMatchApi.class);

    @GET
    @Path("/version")
    @Produces(MediaType.TEXT_PLAIN)
    public String version() {
        return "ms-match Service V 1.0";
    }

    @POST
    @Path("/rank")
    @Produces(MediaType.TEXT_HTML)
    public String rankCandidate(
            @FormParam("candidate_file_path") String candidateFilePath,
            @FormParam("spectrum_file_path") String spectrumFilePath,
            @FormParam("ppm_mass_tol") double ppmMassTollerence,
            @FormParam("abs_mass_tol") double absMassTollerence,
            @FormParam("prob_thresh") double problemThreshold,
            @FormParam("score_type") String scoreType,
            @FormParam("output_file_path") String outputFilePath,
            @FormParam("algorithm") String algorithm) {
        String outPutPath = null;
        File sdfFile = new File(candidateFilePath);
        IteratingSDFReader reader = null;
        if(!sdfFile.exists()){
            return "Candidate File not Exit or invalid file";
        }

        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();


        try {
            reader = new IteratingSDFReader(
                    new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());

            while (reader.hasNext()) {
                IAtomContainer molecule = (IAtomContainer) reader.next();
                String inChI = generateInChI(molecule);
                if (molecule.getProperty(Constants.PUBCHEM_COMPOUND_CID) != null) {
                    if (map.size()<150 &&!map.containsKey(molecule.getProperty(Constants.PUBCHEM_COMPOUND_CID))) {

                        map.put(molecule.getProperty(Constants.PUBCHEM_COMPOUND_CID), inChI);
                    }
                } else if (molecule.getProperty(Constants.CHEMSPIDER_CSID) != null) {
                    if (map.size()<150 &&!map.containsKey(molecule.getProperty(Constants.CHEMSPIDER_CSID))) {

                        map.put(molecule.getProperty(Constants.CHEMSPIDER_CSID), inChI);
                    }
                } else if (molecule.getProperty(Constants.HMDB_ID) != null) {
                    if (map.size()<150 &&!map.containsKey(molecule.getProperty(Constants.HMDB_ID))) {

                        map.put(molecule.getProperty(Constants.HMDB_ID), inChI);
                    }
                }
            }


        } catch (FileNotFoundException e1) {
            LOGGER.error("Something wrong with file paths", e1);
        }

        String newCandidateFilePAth = null;
        newCandidateFilePAth=saveNewCandidateFile(candidateFilePath, map);
        if (algorithm.equals(Constants.CFMID)) {
            CFMIDAlgorithm cfm = new CFMIDAlgorithm();
            try {
                outPutPath = cfm.rankstructures(newCandidateFilePAth, spectrumFilePath, ppmMassTollerence, absMassTollerence, problemThreshold, scoreType, outputFilePath);
            } catch (ChemIDMsMatchException e) {
                LOGGER.error(Constants.OUTPUT_WRITE_ERROR,e);
            }
        }else {
            outputFilePath="Invalid algorithm selection";
        }
        if (outputFilePath==null){
             outputFilePath = "Sorry!Something going wrong.";
        }
        return outPutPath;
    }

    private String saveNewCandidateFile(String candidateFilePath, final ConcurrentMap<String, String> map)  {
        CommonClasses getpath = new CommonClasses();
        String newFilePath = null;

        try {

            newFilePath = getpath.createOutputFileTimeStamp(candidateFilePath);
            java.nio.file.Path newFile = Paths.get(newFilePath);

            try(Writer writer = Files.newBufferedWriter(newFile)) {
                map.forEach((key, value) -> {
                    try { writer.write(key + DATA_SEPARATOR + value + System.lineSeparator()); }
                    catch (IOException ex) {
                        LOGGER.error("Something wrong with candidate file.",ex); }
                });
            } catch(UncheckedIOException ex) {
                LOGGER.error("Error while writing writing candidate file",ex); }
        } catch (IOException e) {
            LOGGER.error("Candidate file not found",e);
        }

        return newFilePath;
    }

    private String generateInChI(IAtomContainer mol)  {
        String inchi = null;
        try {
            InChIGeneratorFactory generator = InChIGeneratorFactory.getInstance();
            inchi = generator.getInChIGenerator(mol).getInchi();
        } catch (CDKException e) {
            LOGGER.error("Something wrong with generating Inchi",e);
        }

        return inchi;
    }

}
