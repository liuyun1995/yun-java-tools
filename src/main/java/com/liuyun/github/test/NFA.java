package com.liuyun.github.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class NFA {

    /** 初始状态 */
    public int startState;
    /** 状态集合 */
    public List<Integer> states;
    /** 状态转移函数集合 */
    public List<Transition> transitions;
    /** 最终状态 */
    public int finalState;

    public NFA(){
        this.startState = 0;
        this.states = new ArrayList();
        this.transitions = new ArrayList();
        this.finalState = 0;
    }

    public NFA(int size){
        this.startState = 0;
        this.states = new ArrayList();
        this.transitions = new ArrayList();
        this.finalState = 0;
        this.setStateSize(size);
    }

    public NFA(char c){
        this.startState = 0;
        this.states = new ArrayList();
        this.transitions = new ArrayList();
        this.setStateSize(2);
        this.finalState = 1;
        this.transitions.add(new Transition(0, 1, c));
    }

    public void setStateSize(int size){
        for (int i = 0; i < size; i++) {
            this.states.add(i);
        }
    }

    public Set<Integer> getStartStates() {
        HashSet hashSet = new HashSet();
        hashSet.add(this.states.get(0));
        return hashSet;
    }

    public Set<Character> getSymbolSet() {
        Set<Character> charSet = new HashSet();
        for (Transition transition : transitions) {
            if(Constants.EPSILON != transition.getSymbol()) {
                charSet.add(transition.getSymbol());
            }
        }
        return charSet;
    }

    public Set<Integer> delta(Set<Integer> stateSet, char symbol) {
        Set<Integer> resultSet = new HashSet();
        for (Transition transition : transitions) {
            if(stateSet.contains(transition.getFrom()) && transition.getSymbol() == symbol) {
                resultSet.add(transition.getTo());
            }
        }
        return resultSet;
    }

    public Set<Integer> epsilonClosures(Set<Integer> stateSet) {
        Set<Integer> resultSet = new HashSet();
        Queue<Integer> queue = new LinkedList();
        queue.addAll(stateSet);

        while (!queue.isEmpty()) {
            Integer state = queue.poll();
            resultSet.add(state);
            for (Transition transition : transitions) {
                if(transition.getFrom() == state && transition.getSymbol() == Constants.EPSILON
                        && !resultSet.contains(transition.getTo())) {
                    queue.add(transition.getTo());
                }
            }
        }

        return resultSet;
    }

    public void display(){
        for (Transition t: transitions){
            System.out.println("("+ t.from +", "+ t.symbol + ", "+ t.to +")");
        }
    }

}
