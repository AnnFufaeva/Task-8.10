package com.company.graphs;

public class TraceData implements TableData {
    private int resistance;

    public TraceData(int resistance) {
        this.resistance = resistance;
    }

    public TraceData(){
        this(0);
    }

    @Override
    public Object[] getFieldData() {
        return new Object[]{resistance};
    }

    @Override
    public void setData(Object data, int indexField) {
        int val = Integer.parseInt(data.toString());
        switch (indexField) {
            case 0:
                resistance = val;
                break;
        }
    }

    @Override
    public void setData(Object[] data) {
        for (int i = 0; i < data.length; i++) {
            setData(data[i],i);
        }
    }

    public void setData(String data) {
        resistance = Integer.parseInt(data.toString());
    }
    @Override
    public String toString() {
        return String.format("%d", resistance);
    }

    public int getResistance() {
        return resistance;
    }

}
