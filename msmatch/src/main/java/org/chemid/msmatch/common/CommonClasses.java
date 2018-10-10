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

package org.chemid.msmatch.common;

import java.io.File;
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

    /** Genarate MsMatch output SD File name
     *
     * @param ouputSDFpath
     * @return String of output SD File
     */
   public String createMsMatchOuputSDF(String ouputSDFpath){
       int index = ouputSDFpath.lastIndexOf('/');
       Timestamp timestamp = new Timestamp(System.currentTimeMillis());
       String fileName ="ms_"+dateFormat.format(timestamp);
       String newsdf = ouputSDFpath.substring(0, index);
       String newFilePath = newsdf+"/"+fileName;
       return newFilePath;


   }



}

