package org.chemid.cheminformatics;

import java.util.ArrayList;
import java.util.List;

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
