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
package org.chemid.structure.dbclient.utilities;


public class Tools {


    public static double getSearchMass(double mass, String adduct) {
        double searchMass;

        switch (adduct.trim()) {
            case "M+3H":
                searchMass = (mass - 1.007276) * 3;
                break;
            case "M+2H+Na":
                searchMass = (mass - 8.334590) * 3;
                break;
            case "M+H+2Na":
                searchMass = (mass - 15.7661904) * 3;
                break;
            case "M+3Na":
                searchMass = (mass - 22.989218) * 3;
                break;
            case "M+2H":
                searchMass = (mass - 1.007276) * 2;
                break;
            case "M+H+NH4":
                searchMass = (mass - 9.520550) * 2;
                break;
            case "M+H+Na":
                searchMass = (mass - 11.998247) * 2;
                break;
            case "M+H+K":
                searchMass = (mass - 19.985217) * 2;
                break;
            case "M+ACN+2H":
                searchMass = (mass - 21.520550) * 2;
                break;
            case "M+2Na":
                searchMass = (mass - 22.989218) * 2;
                break;
            case "M+2ACN+2H":
                searchMass = (mass - 42.033823) * 2;
                break;
            case "M+3ACN+2H":
                searchMass = (mass - 62.547097) * 2;
                break;
            case "M+H":
                searchMass = mass - 1.007276;
                break;
            case "M+NH4":
                searchMass = mass - 18.033823;
                break;
            case "M+Na":
                searchMass = mass - 22.989218;
                break;
            case "M+CH3OH+H":
                searchMass = mass - 33.033489;
                break;
            case "M+K":
                searchMass = mass - 38.963158;
                break;
            case "M+ACN+H":
                searchMass = mass - 42.033823;
                break;
            case "M+2Na-H":
                searchMass = mass - 44.971160;
                break;
            case "M+IsoProp+H":
                searchMass = mass - 61.06534;
                break;
            case "M+ACN+Na":
                searchMass = mass - 64.015765;
                break;
            case "M+2K-H":
                searchMass = mass - 76.919040;
                break;
            case "M+DMSO+H":
                searchMass = mass - 79.02122;
                break;
            case "M+2ACN+H":
                searchMass = mass - 83.060370;
                break;
            case "M+IsoProp+Na+H":
                searchMass = mass - 84.05511;
                break;
            case "2M+H":
                searchMass = (mass - 1.007276) / 2;
                break;
            case "2M+NH4":
                searchMass = (mass - 18.033823) / 2;
                break;
            case "2M+Na":
                searchMass = (mass - 22.989218) / 2;
                break;
            case "2M+K":
                searchMass = (mass - 38.963158) / 2;
                break;
            case "2M+ACN+H":
                searchMass = (mass - 42.033823) / 2;
                break;
            case "2M+ACN+Na":
                searchMass = (mass - 64.015765) / 2;
                break;
            case "M-3H":
                searchMass = (mass + 1.007276) * 3;
                break;
            case "M-2H":
                searchMass = (mass + 1.007276) * 2;
                break;
            case "M-H2O-H":
                searchMass = mass + 19.01839;
                break;
            case "M-H":
                searchMass = mass + 1.007276;
                break;
            case "M+Na-2H":
                searchMass = mass - 20.974666;
                break;
            case "M+Cl":
                searchMass = mass - 34.969402;
                break;
            case "M+K-2H":
                searchMass = mass - 36.948606;
                break;
            case "M+FA-H":
                searchMass = mass - 44.998201;
                break;
            case "M+Hac-H":
                searchMass = mass - 59.013851;
                break;
            case "M+Br":
                searchMass = mass - 78.918885;
                break;
            case "M+TFA-H":
                searchMass = mass - 112.985586;
                break;
            case "2M-H":
                searchMass = (mass + 1.007276) / 2;
                break;
            case "2M+FA-H":
                searchMass = (mass - 44.998201) / 2;
                break;
            case "2M+Hac-H":
                searchMass = (mass - 59.013851) / 2;
                break;
            case "3M-H":
                searchMass = (mass + 1.007276) / 3;
                break;
            case "M":
                searchMass = mass;
                break;
            default:
                searchMass = mass;
                break;
        }
        return searchMass;
    }


    public static Double getMassError(Double mass, Double error, String errorUnit) {
        double searchError = 0;
        int ppm = 1000000;
        int ppb = 1000000000;
        switch (errorUnit.toLowerCase().trim()) {
            case "ppm":
                searchError = (error * mass) / ppm;
                break;
            case "ppb":
                searchError = (error * mass) / ppb;
                break;
            case "da":
                searchError = error;
                break;
        }
        return searchError;
    }
}
