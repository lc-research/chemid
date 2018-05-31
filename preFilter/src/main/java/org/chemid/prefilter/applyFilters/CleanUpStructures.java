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
package org.chemid.prefilter.applyFilters;

import org.chemid.prefilter.common.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static org.chemid.cheminformatics.FileIO.Structures;


public class CleanUpStructures {
    private String inputFilePath, keepCompounds, mustContain;
    private boolean removeDisconnected, removeHeavyIsotopes, removeStereoisomers, eliminateCharges, keepPositiveCharges;
    private List<String> keepCompoundsWith, compoundsMustContain;
    private ConcurrentMap<String, String> map;


    public CleanUpStructures(String inputFilePath, boolean removeDisconnected, boolean removeHeavyIsotopes, boolean removeStereoisomers, String keepCompounds, String mustContain, boolean eliminateCharges, boolean keepPositiveCharges) {
        this.inputFilePath = inputFilePath;
        this.removeDisconnected = removeDisconnected;
        this.removeHeavyIsotopes = removeHeavyIsotopes;
        this.removeStereoisomers = removeStereoisomers;
        this.eliminateCharges = eliminateCharges;
        this.keepCompounds = keepCompounds;

        this.mustContain = mustContain;
        this.keepPositiveCharges = keepPositiveCharges;
        map = new ConcurrentHashMap<>();
        if (keepCompounds.length() > 0) {
            keepCompoundsWith = new ArrayList<>(Arrays.asList(keepCompounds.split(",")));
        } else {
            keepCompoundsWith = new ArrayList<>();
        }
        if (mustContain.length() > 0) {
            compoundsMustContain = new ArrayList<>(Arrays.asList(mustContain.split(",")));
            compoundsMustContain.replaceAll(String::toUpperCase);
        } else {
            compoundsMustContain = new ArrayList<>();
        }
    }

    public String FilterStructures() throws Exception {
        String file_struct = Structures(Constants.LOCATION_SEPARATOR_LEFT, Constants.LOCATION_SEPARATOR, Constants.FILE_NOT_FOUND, Constants.EMPTY_MSG, Constants.SDF_FILE_NAME, Constants.PUBCHEM_ISOTOPIC_ATOM_COUNT, Constants.GENERATED_SMILE, Constants.PUBCHEM_COMPOUND_CID, Constants.CHEMSPIDER_CSID, Constants.HMDB_ID);
        return file_struct;
    }
}