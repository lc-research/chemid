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

package org.chemid.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Genarate Time Stamp for output SD Files
 */

public class CommonClass {

   static SimpleDateFormat  dateFormat = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);

    /** Genarate Name for output SD File (ID Module)
     *
     * @param ouputSDFpath : for id module
     * @return : String of outputFile
     */
   public static String createOuputSDF(String ouputSDFpath){
        int index = ouputSDFpath.lastIndexOf('/');
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileName ="id_"+dateFormat.format(timestamp)+".sdf";
        String newsdf = ouputSDFpath.substring(0, index);
        String newFilePath = newsdf+"/"+fileName;
        return newFilePath;

    }

    /** Genarate Name for output SD File (preFilter Module)
     *
     * @param ouputSDFpath : for preFilter module
     * @return : String of outputFile
     */
    public static String generateOutputFileName(String ouputSDFpath){
        int index = ouputSDFpath.lastIndexOf('/');
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileName ="preFilter_"+dateFormat.format(timestamp)+".sdf";
        String newsdf = ouputSDFpath.substring(0, index);
        String newFilePath = newsdf+"/"+fileName;
        return newFilePath;

    }

}
