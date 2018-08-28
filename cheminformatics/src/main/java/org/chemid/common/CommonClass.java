package org.chemid.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class CommonClass {

   static SimpleDateFormat  dateFormat = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);

    public static String createOuputSDF(String ouputSDFpath){
        int index = ouputSDFpath.lastIndexOf('/');
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileName ="id_"+dateFormat.format(timestamp);
        String newsdf = ouputSDFpath.substring(0, index);
        String newFilePath = newsdf+"/"+fileName;
        return newFilePath;


    }
}
