package com.liuyun.github.test;

public class Transition {

    /** 当前状态 */
    public int from;
    /** 目标状态 */
    public int to;
    /** 标号字符 */
    public char symbol;

    public Transition(int from, int to, char symbol){
        this.from = from;
        this.to = to;
        this.symbol = symbol;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public char getSymbol() {
        return symbol;
    }

}
