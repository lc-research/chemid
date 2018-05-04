package org.chemid.msmatch.algorithm;

import org.chemid.msmatch.common.CommonClasses;
import org.chemid.msmatch.common.Constants;
import org.chemid.msmatch.exception.ChemIDMsMatchException;

import java.io.*;
import java.text.SimpleDateFormat;

import static org.chemid.msmatch.common.Constants.OUTPUT_WRITE_ERROR;

public class CFMIDAlgorithm {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);

    public String rankstructures(String candidateFilePath, String spectrumFilePath, double ppmMassTollerence, double absMassTollerence, double problemThreshold, String scoreType, String outputFilePath) throws ChemIDMsMatchException {
        File candidateFile = new File(candidateFilePath);
        File spectrumFile = new File(spectrumFilePath);
        CommonClasses createFile = new CommonClasses();
        String createdOutPutFile = null;
        if (!candidateFile.exists()) {
            return Constants.CANDIDATE_NOT_FOUND;
        }
        if (!spectrumFile.exists()) {
            return Constants.SPECTRUM_NOT_FOUND;
        }
        createdOutPutFile = createFile.createOutputFileTimeStamp(outputFilePath);


        try {

            ClassLoader classLoader = getClass().getClassLoader();

            File config_file = new File(classLoader.getResource("param_config.txt").getFile());
            File output_file = new File(classLoader.getResource("param_output0.log").getFile());

            System.out.println(output_file.getAbsolutePath());
            String command = Constants.CFM_FILE_PATH + Constants.CALL_CFM + " " + spectrumFilePath + " " + Constants.CFMID_ID + " " + candidateFilePath + " " + Constants.NUM_HIGHEST + " " + ppmMassTollerence + " " + absMassTollerence + " " + problemThreshold + " " + output_file.getAbsolutePath() + " " + config_file.getAbsolutePath() + " " + scoreType;
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            if (error.readLine() != null) {
            }
            String sCurrentLine;

            FileWriter writer = new FileWriter(createdOutPutFile, true);
            while ((sCurrentLine = reader.readLine()) != null) {
                writer.write(sCurrentLine);
                writer.write("\r\n");
            }

            writer.close();
            reader.close();
            error.close();
            return outputFilePath;
        } catch (IOException e) {
            throw new ChemIDMsMatchException(OUTPUT_WRITE_ERROR, e);
        }
    }
}
