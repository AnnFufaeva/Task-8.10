package com.company.utils;

import com.company.gui.Point;

public class Geometry {
    private final static double EXP = 1e-6;

    /*public static boolean inRectangle(Point point, Point rect, double width, double height) {
        return point.x >= rect.x && point.x <= rect.x + width &&
                point.y >= rect.y && point.y <= rect.y + height;
    }*/

    public static boolean inOval (Point point, Point ov, double radius) {
        return Math.sqrt((ov.x - point.x)*(ov.x - point.x) + (ov.y - point.y)*(ov.y - point.y)) <= radius;
    }
    public static boolean isLayToLine(Point check, Point line1, Point line2) {
        double exp1 = (check.y - line1.y) / (line2.y - line1.y);
        double exp2 = (check.x - line1.x) / (line2.x - line1.x);
        return compareDouble(exp1, exp2) == 0;
    }

    public static boolean isLayToStrongCut(Point check, Point line1, Point line2, double stroke) {
        Double tgalfa = ((double) (line2.y - line1.y)) / ((double) (line2.x - line1.x));
        Double alfa = Math.atan(tgalfa);
        Double x1 = line1.x * Math.cos(alfa) + line1.y * Math.sin(alfa);
        Double x2 = line2.x * Math.cos(alfa) + line2.y * Math.sin(alfa);
        Double y1 = line1.y * Math.cos(alfa) - line1.x * Math.sin(alfa);
        Double y2 = line2.y * Math.cos(alfa) - line2.x * Math.sin(alfa);
        Double px = check.x * Math.cos(alfa) + check.y * Math.sin(alfa);
        Double py = check.y * Math.cos(alfa) - check.x * Math.sin(alfa);
        if ((Math.max(x1, x2) + stroke/2) >= px && (Math.min(x1, x2) - stroke/2) <= px){
            if ((y1 + stroke/2) >=  py && (y1 - stroke/2) <=  py)
                return true;
        }

        /*if (Math.max(line1.x, line2.x) < check.x || Math.min(line1.x, line2.x) > check.x ||
                Math.max(line1.y, line2.y) < check.y || Math.min(line1.y, line2.y) > check.y) return false;
        double a = line2.y - line1.y;
        double b = line2.x - line1.x;
        //double r = Math.abs((b*(check.y - line1.y) - a*(check.x - line1.x))/(Math.sqrt(Math.pow(a,2)+ Math.pow(b,2))));
        return Math.abs((b * (check.y - line1.y) - a * (check.x - line1.x)) / (Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)))) < stroke / 2;*/
        return false;
    }


    public static int compareDouble(double value1, double value2) {
        if (Math.abs(value1 - value2) < EXP) return 0;
        if (value1 > value2) return 1;
        else return -1;
    }
}

