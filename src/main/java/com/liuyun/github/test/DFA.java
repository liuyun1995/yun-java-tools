package com.liuyun.github.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DFA {

    /** 初始状态 */
    public int startState;
    /** 状态集合 */
    public List<Integer> states;
    /** 状态转移函数集合 */
    public List<Transition> transitions;
    /** 接受状态集合 */
    public Set<Integer> finalStates;
    /** 状态转移函数映射 */
    public Map<Integer, List<Transition>> transMap;


    public DFA(List<Integer> states, List <Transition> transitions, Set<Integer> finalStates) {
        this.startState = states.get(0);
        this.states = states;
        this.transitions = transitions;
        this.finalStates = finalStates;
    }


    public boolean recognize(String word) {
        if(transMap == null) {
            transMap = new HashMap();
            for (Transition transition : transitions) {
                transMap.computeIfAbsent(transition.getFrom(), e->new ArrayList()).add(transition);
            }
        }
        //当前状态设置为初始状态
        int currentState = startState;
        //遍历输入的字符
        for (char itemChar : word.toCharArray()) {
            List<Transition> transitions = transMap.get(currentState);
            if(transitions == null || transitions.size() == 0) {
                continue;
            }
            for (Transition transition : transitions) {
                if(transition.getSymbol() == itemChar) {
                    currentState = transition.getTo();
                }
            }
        }
        return finalStates.contains(currentState);
    }


    public void display(){
        for (Transition t: transitions){
            System.out.println("("+ t.from +", "+ t.symbol + ", "+ t.to +")");
        }
    }


}
