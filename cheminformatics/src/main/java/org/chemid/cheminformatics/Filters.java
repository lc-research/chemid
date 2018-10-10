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

package org.chemid.cheminformatics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Filter Class for preFilter
 */
public class Filters {
    public String inputFilePath, keepCompounds, mustContain;
    public boolean removeDisconnected, removeHeavyIsotopes, removeStereoisomers, eliminateCharges, keepPositiveCharges;
    public List<String> keepCompoundsWith, compoundsMustContain;
    public ConcurrentMap<String, String> map;

    /**
     *
     * @param inputFilePath
     * @param removeDisconnected
     * @param removeHeavyIsotopes
     * @param removeStereoisomers
     * @param keepCompounds
     * @param mustContain
     * @param eliminateCharges
     * @param keepPositiveCharges
     */
   public Filters(String inputFilePath, boolean removeDisconnected, boolean removeHeavyIsotopes, boolean removeStereoisomers, String keepCompounds, String mustContain, boolean eliminateCharges, boolean keepPositiveCharges){

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


}

