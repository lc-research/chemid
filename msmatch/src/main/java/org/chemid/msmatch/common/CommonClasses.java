package org.chemid.msmatch.common;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class CommonClasses {
    SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);

    public String createOutputFileTimeStamp(String OutputFilePath){
        int index = OutputFilePath.lastIndexOf('/');
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileName =dateFormat.format(timestamp);
        String newCandidateFile = OutputFilePath.substring(0, index);
        String newFilePath = newCandidateFile+"/"+fileName;
        File candidateFile = new File(newFilePath);

        return newFilePath;
    }


}

