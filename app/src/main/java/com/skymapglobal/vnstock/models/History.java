package com.skymapglobal.vnstock.models;

import java.util.List;

public class History {
    private List<Long> t;
    private List<Float> c;
    private List<Float> o;
    private List<Float> h;
    private List<Float> l;
    private List<Long> v;

    public List<Long> getT() {
        return t;
    }

    public List<Float> getC() {
        return c;
    }

    public List<Float> getO() {
        return o;
    }

    public List<Float> getH() {
        return h;
    }

    public List<Float> getL() {
        return l;
    }

    public List<Long> getV() {
        return v;
    }
}
