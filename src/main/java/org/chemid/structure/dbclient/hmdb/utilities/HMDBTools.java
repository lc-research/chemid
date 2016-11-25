package org.chemid.structure.dbclient.hmdb.utilities;

/**
 * Created by Kaushi on 11/22/2016.
 */
public class HMDBTools {
    public static double getLowerMassValue(double mass, double error) {

        double lowerValue = Double.parseDouble(String.format("%.5f", (mass - error)));
        return lowerValue;
    }
    public static double getUpperMassValue(double mass, double error) {

        double upperValue = Double.parseDouble(String.format("%.5f", (mass + error)));
        return upperValue;
    }
}
