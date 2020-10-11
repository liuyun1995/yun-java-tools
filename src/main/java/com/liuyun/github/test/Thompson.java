package com.liuyun.github.test;

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
    public static boolean alphabet(char c){ return alpha(c) || c == Constants.EPSILON;}

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
        result.transitions.add(new Transition(0, 1, Constants.EPSILON));

        //复制n的转移函数到新NFA中
        for (Transition t : n.transitions){
            result.transitions.add(new Transition(t.from + 1, t.to + 1, t.symbol));
        }

        //添加一条从n的最终状态到新NFA最终状态的空转换
        result.transitions.add(new Transition(n.states.size(), n.states.size() + m.states.size() + 1, Constants.EPSILON));

        //添加一条从0到m的初始状态的空转换
        result.transitions.add(new Transition(0, n.states.size() + 1, Constants.EPSILON));

        //复制m的转移函数到新NFA中
        for (Transition t : m.transitions){
            result.transitions.add(new Transition(t.from + n.states.size() + 1, t.to + n.states.size() + 1, t.symbol));
        }

        //添加一条从m的最终状态到新NFA最终状态的空转换
        result.transitions.add(new Transition(m.states.size() + n.states.size(), n.states.size() + m.states.size() + 1, Constants.EPSILON));

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
        int nsize = n.states.size();
        //删除m的初始状态
        m.states.remove(0);

        //复制m的转移函数到n
        for (Transition t : m.transitions){
            n.transitions.add(new Transition(t.from + nsize - 1, t.to + nsize - 1, t.symbol));
        }

        //添加m的状态到n中
        for (Integer s : m.states){
            n.states.add(s + nsize - 1);
        }

        //设置n的最终状态
        n.finalState = nsize + m.states.size() - 1;
        return n;
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
        result.transitions.add(new Transition(0, 1, Constants.EPSILON));

        //复制原NFA的转移函数到新NFA中
        for (Transition t : n.transitions){
            result.transitions.add(new Transition(t.from + 1, t.to + 1, t.symbol));
        }

        //添加一条从原NFA最终状态到新NFA最终状态的空转换
        result.transitions.add(new Transition(n.states.size(), n.states.size() + 1, Constants.EPSILON));

        //添加一条从原NFA的最终状态到初始状态的空转换
        result.transitions.add(new Transition(n.states.size(), 1, Constants.EPSILON));

        //添加一条从新NFA的初始状态到最终状态的空转换
        result.transitions.add(new Transition(0, n.states.size() + 1, Constants.EPSILON));

        //设置新NFA的最终状态
        result.finalState = n.states.size() + 1;
        return result;
    }


    public static void main(String[] args) {
        NFA nfa = Thompson.compile("a(b|c)*");
        nfa.display();
    }

}
