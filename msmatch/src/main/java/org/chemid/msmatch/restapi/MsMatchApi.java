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

package org.chemid.msmatch.restapi;


import org.chemid.msmatch.algorithm.CFMIDAlgorithm;
import org.chemid.msmatch.common.CommonClasses;
import org.chemid.msmatch.common.Constants;
import org.chemid.msmatch.exception.ChemIDMsMatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.chemid.cheminformatics.FileIO.addPropertySDF;
import static org.chemid.cheminformatics.FileIO.getmolProperty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.chemid.msmatch.common.Constants.DATA_SEPARATOR;

/**
 *  This class includes RESTful API methods for MsMatch service
 */
@Path("/rest/msmatch")
public class MsMatchApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsMatchApi.class);

    /**
     *  This method returns the version number of the MsMatch service.
     * @return API Version
     */
    @GET
    @Path("/version")
    @Produces(MediaType.TEXT_PLAIN)
    public String version() {
        return "ms-match Service V 1.0";
    }

    /**
     *
     * @param candidateFilePath : File Path to Input SD File
     * @param spectrumFilePath : File Path to Spectrum File
     * @param ppmMassTollerence : Mass tolerance in ppm
     * @param absMassTollerence : Absolute mass tolerance in Daltons
     * @param problemThreshold : Probability below unlikely fragmentations are pruned
     * @param scoreType : Scoring function for comparing spectra. Options: Jaccard,DotProduct.
     * @param outputFilePath : File Path to Output SD File
     * @param algorithm mass spectra simulator – CFM-ID
     * @return String of output path
     */
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
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        //CDK dependecies method import from Cheminfomatics
        getmolProperty(sdfFile,Constants.PUBCHEM_COMPOUND_CID,Constants.CHEMSPIDER_CSID,Constants.HMDB_ID,map);
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
            outputFilePath="Sorry!Something going wrong.";
        }

        CommonClasses getpath = new CommonClasses();

        try {
            File createdFile=getLatestFilefromDir(outputFilePath);
            HashMap<String,String>val=getScores(createdFile);
            addPropertySDF(candidateFilePath,val,getpath.createMsMatchOuputSDF(outputFilePath),"CFMIDScore");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outPutPath;
    }

    /**
     *
     * @param candidateFilePath
     * @param map
     * @return
     */
    private String saveNewCandidateFile(String candidateFilePath, final ConcurrentMap<String, String> map)  {

        String newFilePath = null;
        CommonClasses getpath = new CommonClasses();
        try {
            newFilePath = getpath.createOutputFileTimeStamp(candidateFilePath);
            java.nio.file.Path newFile = Paths.get(newFilePath);
            try(Writer writer = Files.newBufferedWriter(newFile)) {
                map.forEach((key, value) -> {
                    try { writer.write(key + DATA_SEPARATOR + value + System.lineSeparator());
                    }
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



    /** Get Scores from Text file and store in Hashmap
     *
     * @param inputfile : File path to input text file
     * @return String Hashmap with ID key and CFMID score value
     * @throws IOException
     */


    public HashMap<String,String> getScores(File inputfile) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(inputfile));
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            String parts[] = sCurrentLine.split(" ");
            map.put(parts[2], parts[1]);
        }
        return map;

    }

    /** Get the Last Created SD File from Time Stamps
     * @param dirPath
     * @return
     */
    public File getLatestFilefromDir(String dirPath){
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

}