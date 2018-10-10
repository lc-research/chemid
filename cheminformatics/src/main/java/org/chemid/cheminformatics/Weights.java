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
import java.util.List;

/**
 * Class for Store ID,RI,ECOM50,CCS & CFMID values from a SD File
 */
public class Weights {

    private ArrayList<String> id =new ArrayList<>();
    private ArrayList<Double> ri=new ArrayList<>();
    private ArrayList<Double> ecom=new ArrayList<>();
    private ArrayList<Double> ccs=new ArrayList<>();
    private ArrayList<Double> cfmid=new ArrayList<>();
    private double ecommax,ecommin,ccsmax,ccsmin,cfmidmax,cfmidmin;
    private int rimax,rimin;

    public List<String> getId() {
        return id;
    }

    public void setId(ArrayList<String> id) {
        this.id = id;
    }

    public ArrayList<Double> getRi() {
        return ri;
    }

    public void setRi(ArrayList<Double> ri) {
        this.ri = ri;
    }

    public ArrayList<Double> getEcom() {
        return ecom;
    }

    public void setEcom(ArrayList<Double> ecom) {

        this.ecom = ecom;
    }

    public ArrayList<Double> getCcs() {
        return ccs;
    }

    public void setCcs(ArrayList<Double> ccs) {
        this.ccs = ccs;
    }

    public ArrayList<Double> getCfmid() {
        return cfmid;
    }

    public void setCfmid(ArrayList<Double> cfmid) {
        this.cfmid = cfmid;
    }

}
