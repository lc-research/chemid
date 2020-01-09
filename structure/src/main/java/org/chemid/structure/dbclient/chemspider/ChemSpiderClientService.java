package org.chemid.structure.dbclient.chemspider;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import org.chemid.structure.common.Constants;

public class ChemSpiderClientService {

    static SimpleDateFormat dateFormat = new SimpleDateFormat(org.chemid.common.Constants.TIME_STAMP_FORMAT);
    /**
     * Get the Query ID for corresponding mass & range
     * @param mass
     * @param range
     * @return
     * @throws IOException
     */

    public static String getQueryID(String mass,String range) throws IOException {

        final String POST_PARAMS = Constants.ChemSpiderConstants.POST_MASS+ mass+Constants.ChemSpiderConstants.POST_RANGE+range+Constants.ChemSpiderConstants.POST_DATA_SOURCES;
        String queryID=null;
        URL obj = new URL(Constants.ChemSpiderConstants.QUERY_ID_URL);
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod(Constants.ChemSpiderConstants.POST_TYPE);
        postConnection.setRequestProperty(Constants.ChemSpiderConstants.API_VALUE, Constants.ChemSpiderConstants.APIKEY);
        postConnection.setRequestProperty(Constants.ChemSpiderConstants.CONTENT_TYPE, Constants.ChemSpiderConstants.APPLICATION_TYPE);
        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = postConnection.getResponseCode();
        if (responseCode == Constants.ChemSpiderConstants.HTTP_SUCCES) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            queryID=response.toString();
            queryID=queryID.substring(12,48);
        } else {
            System.out.println(Constants.ChemSpiderConstants.POST_MESSAGE);
        }
        return queryID;
    }

    /**
     *
     * @param queryID
     * @return
     * @throws IOException
     */
    public static String getResults(String queryID) throws IOException {
        String results = null;
        URL obj = new URL(Constants.ChemSpiderConstants.POST_QUERY_ID+queryID+Constants.ChemSpiderConstants.POST_SDF);
        HttpURLConnection getcon = (HttpURLConnection) obj.openConnection();
        getcon.setRequestMethod(Constants.ChemSpiderConstants.GET_TYPE);
        getcon.setRequestProperty(Constants.ChemSpiderConstants.GET_AGENT, Constants.ChemSpiderConstants.AGENT_TYPE);
        getcon.setRequestProperty(Constants.ChemSpiderConstants.API_VALUE, Constants.ChemSpiderConstants.APIKEY);
        getcon.setRequestProperty(Constants.ChemSpiderConstants.CONTENT_TYPE, Constants.ChemSpiderConstants.APPLICATION_TYPE);

        int responseCode = getcon.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(getcon.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {

                response.append(inputLine);
            }
            in.close();

            String data[]=(response.toString()).split(":");
            String data2[]=(data[1]).split("\"");
            results=data2[1];
        } else {
            System.out.println(Constants.ChemSpiderConstants.GET_MESSAGE);
        }
        return results;
    }

    /**
     *
     * @param encoded
     * @param location
     * @throws Base64DecodingException
     * @throws IOException
     */
    public static void decompressSDF(String encoded,String location) throws Base64DecodingException, IOException {
        byte[] compressed = Base64.decode(encoded);
        ArrayList<String> ar = new ArrayList<String>();

        if ((compressed == null) || (compressed.length == 0)) {
            throw new IllegalArgumentException(Constants.ChemSpiderConstants.DECODE_MESSAGE);
        }
        if (!isZipped(compressed)) {
            System.out.println(compressed);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder output = new StringBuilder();
                        String line;
                        while((line = bufferedReader.readLine()) != null){
                            output.append(line);
                            ar.add(line);
                        }
                    }
                }
            }
        } catch(IOException e) {
            throw new RuntimeException(Constants.ChemSpiderConstants.DECODE_MESSAGE, e);
        }
        FileWriter writer = new FileWriter(location+generateOutputFileName());
        for(String str: ar) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
    }



    public static String generateOutputFileName() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileName = Constants.ChemSpiderConstants.CHEMSPIDER_DB_NAME + dateFormat.format(timestamp)+Constants.ChemSpiderConstants.OUTPUT_TYPE;

        String newFilePath = fileName;
        return newFilePath;
    }

    public static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    /**
     *
     * @param mass
     * @param range
     * @param location
     * @return
     * @throws IOException
     * @throws Base64DecodingException
     */
    public String getChemicalStructuresByMass(double mass,double range,String location) throws IOException, Base64DecodingException {

        decompressSDF(getResults(getQueryID(Double.toString(mass),Double.toString(range))),location);

        return location+generateOutputFileName();
    }
}
