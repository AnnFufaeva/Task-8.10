package com.company.graphs;

import com.company.utils.Geometry;

public class Way implements Comparable<Way> {
    private double time;
    private int maxWaySpeed;
    private int priceSum;

    public Way(double time, int maxWaySpeed, int priceSum) {
        this.time = time;
        this.maxWaySpeed = maxWaySpeed;
        this.priceSum = priceSum;
    }

    public double getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void addWay(int speed, int distance) {
        time += (double) distance / (double) speed;
        if (speed > maxWaySpeed)
            maxWaySpeed = speed;
    }

    public void addPrice(int priceSum){
        this.priceSum+=priceSum;
    }

    public int getMaxWaySpeed() {
        return maxWaySpeed;
    }

    public void setMaxWaySpeed(int maxWaySpeed) {
        this.maxWaySpeed = maxWaySpeed;
    }

    public int getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(int priceSum) {
        this.priceSum = priceSum;
    }

    @Override
    public int compareTo(Way o) {
        if (priceSum != o.priceSum) return Integer.compare(priceSum, o.priceSum);
        else {
            int c1 = Geometry.compareDouble(time, o.time);
            if (c1 != 0) return c1;
            else {
                if(maxWaySpeed != o.maxWaySpeed)
                    return Integer.compare(maxWaySpeed, o.maxWaySpeed);
                else return 0;
            }
        }
    }

    public Way copy(){
        return new Way(time,maxWaySpeed,priceSum);
    }
}
