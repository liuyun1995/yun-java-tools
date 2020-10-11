package com.liuyun.github.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SubsetConstruction {


    public static DFA compile(NFA nfa) {
        List<Set<Integer>> stateList = new ArrayList();
        List<Map<String, Object>> tranList = new ArrayList();
        List<Set<Integer>> finalStateList = new ArrayList();

        Queue<Set<Integer>> workQueue = new LinkedList();
        Set<Character> symbolSet = nfa.getSymbolSet();

        //获得NFA初始状态的epsilon闭包
        Set<Integer> firstStates = nfa.epsilonClosures(nfa.getStartStates());
        stateList.add(firstStates);
        workQueue.add(firstStates);

        while (!workQueue.isEmpty()) {
            Set<Integer> sourceStates = workQueue.poll();
            //若集合包含NFA的接受状态，则将此集合作为DFA的接受状态
            if(sourceStates.contains(nfa.finalState)) {
                finalStateList.add(sourceStates);
            }
            //遍历NFA上的所有标号字符
            for (char symbol : symbolSet) {
                Set<Integer> targetStates = nfa.epsilonClosures(nfa.delta(sourceStates, symbol));
                if(targetStates == null || targetStates.size() == 0) {
                    continue;
                }
                Map<String, Object> map = new HashMap();
                map.put(Constants.FROM, sourceStates);
                map.put(Constants.TO, targetStates);
                map.put(Constants.SYMBOL, symbol);
                tranList.add(map);
                //若当前状态集合未包含在之前的集合里面
                if(!isContain(stateList, targetStates)) {
                    stateList.add(targetStates);
                    workQueue.add(targetStates);
                }
            }
        }

        return generate(stateList, tranList, finalStateList);
    }


    private static DFA generate(List<Set<Integer>> stateList, List<Map<String, Object>> tranList,
                                List<Set<Integer>> finalStateList) {
        List<Integer> states = new ArrayList();
        List<Transition> transitions = new ArrayList();
        Set<Integer> finalStates = new HashSet();

        Map<Set<Integer>, Integer> stateMap = new HashMap();
        //设置所有状态集合
        for (int i = 0; i < stateList.size(); i++) {
            states.add(i);
            stateMap.put(stateList.get(i), i);
        }
        //设置转换函数
        for (Map<String, Object> map : tranList) {
            Set<Integer> from = (HashSet) map.get(Constants.FROM);
            Set<Integer> to = (HashSet) map.get(Constants.TO);
            char symbol = (char) map.get(Constants.SYMBOL);
            Transition transition = new Transition(stateMap.get(from), stateMap.get(to), symbol);
            transitions.add(transition);
        }
        //设置接受状态集合
        for (Set<Integer> stateSet : finalStateList) {
            finalStates.add(stateMap.get(stateSet));
        }
        return new DFA(states, transitions, finalStates);
    }


    private static boolean isContain(List<Set<Integer>> totalList, Set<Integer> set) {
        for (Set<Integer> item : totalList) {
            if(item.containsAll(set)) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        NFA nfa = Thompson.compile("a(b|c)*");
        DFA dfa = SubsetConstruction.compile(nfa);
        dfa.display();

        System.out.println();

        System.out.printf("%-10s%-10s\n", "字符", "匹配结果");
        System.out.printf("%-10s%-10s\n", "abc", dfa.recognize("abc"));
        System.out.printf("%-10s%-10s\n", "abccc", dfa.recognize("abccc"));
        System.out.printf("%-10s%-10s\n", "abcbc", dfa.recognize("abcbc"));
        System.out.printf("%-10s%-10s\n", "mbc", dfa.recognize("mbc"));
        System.out.printf("%-10s%-10s\n", "bcc", dfa.recognize("bcc"));
    }

}
