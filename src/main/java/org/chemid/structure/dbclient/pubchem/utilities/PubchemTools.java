/*
 * Copyright (c) 2016, ChemID. (http://www.chemid.org)
 *
 * ChemID licenses this file to you under the Apache License V 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.chemid.structure.dbclient.pubchem.utilities;


public class PubchemTools {
    public static String getMassRange(double mass, double error) {

        double UpperValue = Double.parseDouble(String.format("%.5f", (mass + error)));
        double lowerValue = Double.parseDouble(String.format("%.5f", (mass - error)));
        return lowerValue + ":" + UpperValue + "[exactmass]";
    }
}
