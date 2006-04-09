package util;

import java.io.Serializable;

/**
Represente un vecteur 2D.
*/
public class Vecteur implements Serializable{

    public int x;
    public int y;

    public Vecteur() {
    }


    public Vecteur( Vecteur v ) {
        x = v.x; y = v.y;
    }


    public Vecteur( int px, int py ) {
        x = px; y = py;
    }

    public void add( Vecteur v ) {
        x += v.x; y += v.y;
    }

    public void addColinear( int k, Vecteur v ) {
        x += k * v.x;
        y += k * v.y;
    }

    public void multiply( int  k ) {
        x *= k; y *= k;
    }

    public int dotProduct( Vecteur v ) {
        return x * v.x + y * v.y;
    }


    public double length() {
        return Math.sqrt(x * x + y * y);
    }


    public int min() {
        return Math.min(x,y);
    }


    public int max() {
        return Math.max(x,y);
    }


    public void negate() {
        x = -x; y = -y;
    }


    public void normalize() {
        double l = length();
        x /= l;
        y /= l;
    }


    public void square() {
        x *= x; y *= y;
    }


    public void substract( Vecteur right ) {
        x -= right.x;
        y -= right.y;
    }


    public void setAddition( Vecteur left, Vecteur right ) {
        x = left.x + right.x;
        y = left.y + right.y;
    }


    public void setSubstraction( Vecteur left, Vecteur right ) {
        x = left.x - right.x;
        y = left.y - right.y;
    }

    public void setColinear( int k, Vecteur v ) {
        x = k * v.x;
        y = k * v.y;
    }

    public void setPerpendicular( Vecteur v ) {
        x = - v.y;
        y = v.x;
    }


    public void setValue( Vecteur v ) {
        x = v.x;
        y = v.y;
    }

    public void setValue( int cx, int cy ) {
        x = cx;
        y = cy;
    }


    public String toString() {
        return "Vecteur(" + x + "," + y + ", length = " + length() + ")";
    }
};


