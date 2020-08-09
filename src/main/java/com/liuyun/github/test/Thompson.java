package com.liuyun.github.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Thompson {

    /**
     * 编译正则表达式
     * @param regex
     * @return
     */
    public static NFA compile(String regex){
        //验证输入字符串是否是正则表达式
        if (!validRegEx(regex)){
            System.out.println("Invalid Regular Expression Input.");
            return new NFA();
        }
        //操作符栈
        Stack<Character> operators = new Stack();
        //操作数栈
        Stack <NFA> operands = new Stack();
        //连接数栈
        Stack <NFA> concatStack = new Stack();
        //连接标识
        boolean ccflag = true;
        //当前操作符和字符
        char op, c;
        int paraCount = 0;
        NFA nfa1, nfa2;

        for (int i = 0; i < regex.length(); i++){
            c = regex.charAt(i);
            if (alphabet(c)){
                operands.push(new NFA(c));
                if (ccflag){
                    //用'.'替换连接符
                    operators.push('.');
                } else {
                    ccflag = true;
                }
            } else{
                if (c == ')'){
                    ccflag = true;
                    if (paraCount == 0){
                        System.out.println("Error: More end paranthesis than beginning paranthesis");
                        System.exit(1);
                    } else{
                        paraCount--;
                    }
                    //处理操作符栈直到遇到左括号
                    while (!operators.empty() && operators.peek() != '('){
                        op = operators.pop();
                        if (op == '.'){
                            nfa2 = operands.pop();
                            nfa1 = operands.pop();
                            operands.push(concat(nfa1, nfa2));
                        } else if (op == '|'){
                            nfa2 = operands.pop();
                            if(!operators.empty() && operators.peek() == '.'){
                                concatStack.push(operands.pop());
                                while (!operators.empty() && operators.peek() == '.'){
                                    concatStack.push(operands.pop());
                                    operators.pop();
                                }
                                nfa1 = concat(concatStack.pop(), concatStack.pop());
                                while (concatStack.size() > 0){
                                    nfa1 = concat(nfa1, concatStack.pop());
                                }
                            } else{
                                nfa1 = operands.pop();
                            }
                            operands.push(union(nfa1, nfa2));
                        }
                    }
                } else if (c == '*'){
                    operands.push(kleene(operands.pop()));
                    ccflag = true;
                } else if (c == '('){
                    operators.push(c);
                    paraCount++;
                    ccflag = false;
                } else if (c == '|'){
                    operators.push(c);
                    ccflag = false;
                }
            }
        }
        while (operators.size() > 0){
            if (operands.empty()){
                System.out.println("Error: imbalanace in operands and operators");
                System.exit(1);
            }
            op = operators.pop();
            if (op == '.'){
                nfa2 = operands.pop();
                nfa1 = operands.pop();
                operands.push(concat(nfa1, nfa2));
            } else if (op == '|'){
                nfa2 = operands.pop();
                if( !operators.empty() && operators.peek() == '.'){
                    concatStack.push(operands.pop());
                    while (!operators.empty() && operators.peek() == '.'){
                        concatStack.push(operands.pop());
                        operators.pop();
                    }
                    nfa1 = concat(concatStack.pop(), concatStack.pop());
                    while (concatStack.size() > 0){
                        nfa1 = concat(nfa1, concatStack.pop());
                    }
                } else{
                    nfa1 = operands.pop();
                }
                operands.push(union(nfa1, nfa2));
            }
        }
        return operands.pop();
    }

    /**
     * 判断输入字符是否是字母
     * @param c
     * @return
     */
    public static boolean alpha(char c){ return c >= 'a' && c <= 'z';}

    /**
     * 判断输入字符是否是字母或空串
     * @param c
     * @return
     */
    public static boolean alphabet(char c){ return alpha(c) || c == 'E';}

    /**
     * 判断是否是正则运算符
     * @param c
     * @return
     */
    public static boolean regexOperator(char c){
        return c == '(' || c == ')' || c == '*' || c == '|';
    }

    /**
     * 校验输入字符是否合法
     * @param c
     * @return
     */
    public static boolean validRegExChar(char c){
        return alphabet(c) || regexOperator(c);
    }

    /**
     * 验证是否是正则表达式
     * @param regex
     * @return
     */
    public static boolean validRegEx(String regex){
        if (regex.isEmpty()) {
            return false;
        }
        for (char c: regex.toCharArray()) {
            if (!validRegExChar(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 选择运算
     * @param n
     * @param m
     * @return
     */
    public static NFA union(NFA n, NFA m){
        //新NFA的状态数是原NFA状态数加2
        NFA result = new NFA(n.states.size() + m.states.size() + 2);

        //添加一条从0到1的空转换
        result.transitions.add(new Transition(0, 1, 'E'));

        //复制n的转移函数到新NFA中
        for (Transition t : n.transitions){
            result.transitions.add(new Transition(t.from + 1, t.to + 1, t.symbol));
        }

        //添加一条从n的最终状态到新NFA最终状态的空转换
        result.transitions.add(new Transition(n.states.size(), n.states.size() + m.states.size() + 1, 'E'));

        //添加一条从0到m的初始状态的空转换
        result.transitions.add(new Transition(0, n.states.size() + 1, 'E'));

        //复制m的转移函数到新NFA中
        for (Transition t : m.transitions){
            result.transitions.add(new Transition(t.from + n.states.size() + 1, t.to + n.states.size() + 1, t.symbol));
        }

        //添加一条从m的最终状态到新NFA最终状态的空转换
        result.transitions.add(new Transition(m.states.size() + n.states.size(), n.states.size() + m.states.size() + 1, 'E'));

        //设置新NFA的最终状态
        result.finalState = n.states.size() + m.states.size() + 1;
        return result;
    }


    /**
     * 连接运算
     * @param n
     * @param m
     * @return
     */
    public static NFA concat(NFA n, NFA m){

        m.states.remove(0); // delete m's initial state

        // copy NFA m's transitions to n, and handles connecting n & m
        for (Transition t: m.transitions){
            n.transitions.add(new Transition(t.from + n.states.size()-1, t.to + n.states.size() - 1, t.symbol));
        }

        // take m and combine to n after erasing inital m state
        for (Integer s: m.states){
            n.states.add(s + n.states.size() + 1);
        }

        n.finalState = n.states.size() + m.states.size() - 2;
        return n;

//        //新NFA的状态数是n和m的状态数之和
//        NFA result = new NFA(n.states.size() + m.states.size());
//
//        //复制n的转移函数到新NFA中
//        for (Transition t : n.transitions){
//            result.transitions.add(new Transition(t.from, t.to, t.symbol));
//        }
//
//        //添加一条从n的最终状态到m的初始状态的空转换
//        result.transitions.add(new Transition(n.states.size() - 1, n.states.size(), 'E'));
//
//        //复制m的转移函数到新NFA中
//        for (Transition t : m.transitions){
//            result.transitions.add(new Transition(n.states.size() + t.from, n.states.size() + t.to, t.symbol));
//        }
//
//        //设置新NFA的最终状态
//        result.finalState = n.states.size() + m.states.size() - 1;
//        return result;
    }

    /**
     * 柯林闭包
     * @param n
     * @return
     */
    public static NFA kleene(NFA n) {
        //新NFA的状态数是原NFA状态数加2
        NFA result = new NFA(n.states.size() + 2);

        //添加一条从0到1的空转换
        result.transitions.add(new Transition(0, 1, 'E'));

        //复制原NFA的转移函数到新NFA中
        for (Transition t : n.transitions){
            result.transitions.add(new Transition(t.from + 1, t.to + 1, t.symbol));
        }

        //添加一条从原NFA最终状态到新NFA最终状态的空转换
        result.transitions.add(new Transition(n.states.size(), n.states.size() + 1, 'E'));

        //添加一条从原NFA的最终状态到初始状态的空转换
        result.transitions.add(new Transition(n.states.size(), 1, 'E'));

        //添加一条从新NFA的初始状态到最终状态的空转换
        result.transitions.add(new Transition(0, n.states.size() + 1, 'E'));

        //设置新NFA的最终状态
        result.finalState = n.states.size() + 1;
        return result;
    }

    public static class NFA {
        /** 状态集合 */
        public List<Integer> states;
        /** 状态转移函数集合 */
        public List <Transition> transitions;
        /** 最终状态 */
        public int finalState;

        public NFA(){
            this.states = new ArrayList();
            this.transitions = new ArrayList();
            this.finalState = 0;
        }

        public NFA(int size){
            this.states = new ArrayList();
            this.transitions = new ArrayList();
            this.finalState = 0;
            this.setStateSize(size);
        }

        public NFA(char c){
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

        public void display(){
            for (Transition t: transitions){
                System.out.println("("+ t.from +", "+ t.symbol + ", "+ t.to +")");
            }
        }
    }

    private static class Transition {
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
    }

    public static void main(String[] args) {
        NFA nfa = compile("a(b|c)*");
        nfa.display();
    }

}
