package com.liuyun.github.regular;

import java.util.*;

/**
 * @author kissx on 2017/7/25.
 *         实现 Thompson ( RE --> NFA )
 *         正则表达式到有限非确定自动机的转换
 *         <p>
 *         要点：栈保存“表达式中间结果或局部结果”，是通过记录 始末节点 在List的位置完成的
 */
public class Thompson {

    private List<NfaNode> nfaList;
    private int num = 0;

    //NFA 节点的开始于 List 的position
    private int start;
    //NFA 节点的结束于 List 的position
    private int end;
    //代表空字符
    private final char EPSILON = '空';

    /**
     * NFA节点的数据结构
     */
    class NfaNode {
        //该节点的标记
        int id;
        //指向的下一个节点（其中 key 指的是下一节点的标记，而 value 指的是驱动的字符）
        Map<Integer, Character> nextId;

        NfaNode(int id, Map<Integer, Character> nextId) {
            this.id = id;
            this.nextId = nextId;
        }

        @Override
        public String toString() {
            if (nextId != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Integer, Character> entry : nextId.entrySet()) {
                    sb.append(entry.getKey()).append("-").append(entry.getValue()).append("&");
                }
                sb.delete(sb.length() - 1, sb.length());
                return "(nid: " + id + " nextId: " + sb.toString() + ")";
            } else {
                return "(nid: " + id + ")";
            }
        }
    }


    /**
     * 根据输入参数 re 生成等价的 NFA
     *
     * @param re 正则表达式（注意：字符串中需用 . 代表“连接”运算 | 代表“选择”运算 * 代表闭包运算。建议使用 "()" 来区分优先级）
     * @return NFA节点的 List 形式
     */
    public List<NfaNode> thompson(String re) {
        nfaList = new ArrayList<>();
        num = 0;
        //存放待处理的栈
        LinkedList<String> stack = new LinkedList<>();
        //用于存放运算符的栈
        LinkedList<String> labelStack = new LinkedList<>();
        char c = re.charAt(0);
        if (!isOperator(c)) {
            createState(stack, c);
        } else {
            stack.addFirst(String.valueOf(c));
            labelStack.addFirst(String.valueOf(c));
        }
        int i = 1;
        while (stack.size() > 1 || i == 1) {
            for (; i < re.length(); i++) {
                //读取正则表达式的一个字符
                char c1 = re.charAt(i);
                //如果不是操作符
                if (!isOperator(c1)) {
                    createState(stack, c1);
                } else if (c1 == '(') {
                    stack.addFirst("(");
                    labelStack.addFirst(String.valueOf(c1));
                } else if (c1 == ')') {
                    while (!labelStack.getFirst().equals("(")) {
                        count(stack, labelStack);
                    }
                    String element = stack.pop();
                    stack.pop();
                    stack.addFirst(element);
                } else if (c1 == '|') {
                    //并运算
                    if (labelStack.size() > 0 && !labelStack.get(0).equals("(")) {
                        String element1 = stack.pop();
                        String label = stack.pop();
                        labelStack.pop();
                        String element2 = stack.pop();
                        if (label.equals("|")) {
                            or(stack, element1, element2);
                        } else if (label.equals(".")) {
                            and(stack, element1, element2);
                        }
                    }
                    stack.addFirst(String.valueOf(c1));
                    labelStack.addFirst(String.valueOf(c1));
                } else if (c1 == '.') {
                    if (labelStack.size() > 0 && !labelStack.get(0).equals("|") && !labelStack.get(0).equals("(")) {
                        String element1 = stack.pop();
                        stack.pop();
                        labelStack.pop();
                        String element2 = stack.pop();
                        and(stack, element1, element2);
                    }
                    stack.addFirst(String.valueOf(c1));
                    labelStack.addFirst(String.valueOf(c1));
                } else if (c1 == '*') {
                    closure(stack);
                } else {
                    throw new RuntimeException("第 " + i + " 个位置出现错误！");
                }
            }
            //读完 正则表达式 后，对栈中余下部分的计算[或称为转换]处理
            count(stack, labelStack);
        }
        //此时栈中最后一个元素也就是栈顶元素保存了“结果”
        String element = stack.get(0);
        start = Integer.parseInt(element.split(":")[0]);
        end = Integer.parseInt(element.split(":")[1]);
        return nfaList;
    }


    /**
     * 判断字符c是否为正则表达式中的基本运算符
     */
    private boolean isOperator(char c) {
        return c == '|' || c == '*' || c == '.' || c == '(' || c == ')';
    }


    /**
     * 针对一个 c 字符驱动的转换创建两个 Node 节点
     */
    private void createState(LinkedList<String> stack, char c) {
        HashMap<Integer, Character> map = new HashMap<>();
        map.put(num + 1, c);
        nfaList.add(new NfaNode(num++, map));
        nfaList.add(new NfaNode(num++, null));
        stack.addFirst((num - 2) + ":" + (num - 1));
    }


    private void count(LinkedList<String> stack, LinkedList<String> labelStack) {
        String element1 = stack.pop();
        String label = stack.pop();
        labelStack.pop();
        String element2 = stack.pop();
        if (label.equals("|")) {
            or(stack, element1, element2);
        } else {
            and(stack, element1, element2);
        }
    }


    /**
     * 选择运算处理
     */
    private void or(LinkedList<String> stack, String element1, String element2) {
        int start1 = Integer.parseInt(element1.split(":")[0]);
        int end1 = Integer.parseInt(element1.split(":")[1]);
        int start2 = Integer.parseInt(element2.split(":")[0]);
        int end2 = Integer.parseInt(element2.split(":")[1]);
        NfaNode sNode = new NfaNode(num++, null);
        NfaNode eNode = new NfaNode(num++, null);
        Map<Integer, Character> map = new HashMap();
        map.put(start1, EPSILON);
        map.put(start2, EPSILON);
        sNode.nextId = map;
        if (nfaList.get(end1).nextId == null) {
            nfaList.get(end1).nextId = new HashMap<>();
        }
        nfaList.get(end1).nextId.put(num - 1, EPSILON);
        if (nfaList.get(end2).nextId == null) {
            nfaList.get(end2).nextId = new HashMap();
        }
        nfaList.get(end2).nextId.put(num - 1, EPSILON);
        nfaList.add(sNode);
        nfaList.add(eNode);
        stack.addFirst((num - 2) + ":" + (num - 1));
    }

    /**
     * 连接运算处理
     */
    private void and(LinkedList<String> stack, String element1, String element2) {
        int start1 = Integer.parseInt(element1.split(":")[0]);
        int end1 = Integer.parseInt(element1.split(":")[1]);
        int start2 = Integer.parseInt(element2.split(":")[0]);
        int end2 = Integer.parseInt(element2.split(":")[1]);
        if (nfaList.get(end2).nextId == null) {
            nfaList.get(end2).nextId = new HashMap();
        }
        nfaList.get(end2).nextId.put(start1, EPSILON);
        stack.addFirst((start2) + ":" + (end1));
    }

    /**
     * 闭包运算处理
     */
    private void closure(LinkedList<String> stack) {
        String element = stack.pop();
        int start = Integer.parseInt(element.split(":")[0]);
        int end = Integer.parseInt(element.split(":")[1]);
        NfaNode sNode = new NfaNode(num++, null);
        NfaNode eNode = new NfaNode(num++, null);
        Map<Integer, Character> map = new HashMap();
        map.put(start, EPSILON);
        map.put(num - 1, EPSILON);
        sNode.nextId = map;
        if (nfaList.get(end).nextId == null) {
            nfaList.get(end).nextId = new HashMap();
        }
        nfaList.get(end).nextId.put(num - 1, EPSILON);
        nfaList.get(end).nextId.put(start, EPSILON);
        nfaList.add(sNode);
        nfaList.add(eNode);
        stack.addFirst((num - 2) + ":" + (num - 1));
    }

    /**
     * 将 List<NfaNode> 结果，转换为 char[][].
     * 即使用 二维表 来描述所生成的 nfa
     * 其中 二维表的标号 即 nfa 的状态（NfaNode 中的 id）
     */
    public char[][] toArray() {
        char[][] arrays = new char[nfaList.size()][nfaList.size()];
        for (char[] array : arrays) {
            Arrays.fill(array, '无');
        }
        for (int i = 0; i < nfaList.size(); i++) {
            NfaNode node = nfaList.get(i);
            if (node.nextId != null) {
                for (Map.Entry<Integer, Character> entry : node.nextId.entrySet()) {
                    int end = entry.getKey();
                    arrays[i][end] = entry.getValue();
                }
            }
        }
        return arrays;
    }

    /**
     * 获取nfa开始节点在List中的位置
     * @return
     */
    public int getStart() {
        return start;
    }

    /**
     * 获取nfa结束节点在List中的位置
     * @return
     */
    public int getEnd() {
        return end;
    }

    /**
     * 获取nfa的List数据结构描述
     * @return
     */
    public List<NfaNode> getNfaList() {
        return nfaList;
    }

    public static void main(String[] args) {
        //测试 (a|b).c 这个正则表达式
        Thompson thompson = new Thompson();
        List<NfaNode> list = thompson.thompson("(a|b).c*");
        System.out.println("nfa List结构的大小: " + list.size());
        System.out.println("nfa 内容: ");
        System.out.println(list);
        System.out.println("nfa 二维表描述: ");
        char[][] chars = thompson.toArray();
        for (char[] aChar : chars) {
            for (int j = 0; j < aChar.length; j++) {
                if (j != aChar.length - 1)
                    System.out.printf(aChar[j] + " , ");
                else
                    System.out.println(aChar[j]);
            }
        }
        System.out.println("start: " + thompson.getStart());
        System.out.println("end: " + thompson.getEnd());
    }

}
