package com.teamll.expectlauncher.model;

public class Rectangle {
    public int Left;
    public int Top;
    public int Width;
    public int Height;

    public Rectangle(int left, int top, int width, int height) {
        Left = left;
        Top = top;
        Width = width;
        Height = height;
    }

    public Rectangle(int width, int height) {
        Width = width;
        Height = height;
    }
    public Rectangle() {

    }

    public int getLeft() {
        return Left;
    }

    public int getTop() {
        return Top;
    }

    public int getWidth() {
        return Width;
    }

    public int getHeight() {
        return Height;
    }

    public void setLeft(int left) {
        Left = left;
    }

    public void setTop(int top) {
        Top = top;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public void setHeight(int height) {
        Height = height;
    }
    public void setSize(int width, int height) {
        Width = width;
        Height = height;
    }
    public void setSize(int[] wh){
        Width = wh[0];
        Height = wh[1];
    }
    public void setPosition(int left, int top) {
        Left = left;
        Top = top;
    }
}
