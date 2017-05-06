/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.*;
/**
 *
 * @author harshmiddha
 */
public class Spreadsheet {
    
    /**
     * @throws java.io.IOException
     */
    private String input[][];
    public static Scanner sc =new Scanner(System.in);
            
    public static void main(String[] args) throws IOException {
                
                
                int m = sc.nextInt();
                int n = sc.nextInt();
                sc.nextLine();

                Spreadsheet ss = new Spreadsheet();
                ss.input = ss.takeInput(n, m);
                
                for (int i = 0; i<n; i++) {
                    for (int j = 0; j<m; j++) {
                        boolean[][] visited = new boolean[n][m];
                        System.out.println(ss.evalRPN(ss.input[i][j].split(" "), n ,m, visited, i, j));
                    }
                }
	}
 
    private String evalRPN(String[] tokens, int n , int m, boolean[][] visited, int i, int j) {

            String operators = "+-*/";

            Stack<Float> stack = new Stack<>();
            boolean[][] visitedCopy = new boolean[n][m];
            visitedCopy = visited;
            for (String t : tokens) {
                    char firstCharacter = t.charAt(0);

                    if ((firstCharacter >= '0' && firstCharacter <= '9') || firstCharacter == '-') {
                             //push to stack if it is a number
                            stack.push(Float.valueOf(t));
                    } else if(firstCharacter >= 'A' && firstCharacter <= 'Z') {
                            StringBuilder sb = new StringBuilder(t);
                            sb.deleteCharAt(0);
                            int column = Integer.parseInt(sb.toString());

                            try {   
                                    Integer.parseInt(this.input[firstCharacter - 'A'][column - 1]);
                            } catch(NumberFormatException ex) {
                                    if (visited[i][j]) {
                                        System.out.println("Loop detected");
                                        System.exit(1);
                                    }
                                    visited[i][j] = true;
                                    input[firstCharacter - 'A'][column - 1] = String.valueOf(evalRPN((input[firstCharacter - 'A'][column - 1]).split(" "), n ,m, visited, firstCharacter - 'A' ,column - 1));

                            }

                            stack.push(Float.valueOf(input[firstCharacter - 'A'][column - 1]));
                    } else {
                            //pop numbers from stack if it is an operator
                            Float a = stack.pop();
                            Float b = stack.pop();

                            switch (t) {
                            case "+":
                                    stack.push(a + b);
                                    break;
                            case "-":
                                    stack.push(b - a);
                                    break;
                            case "*":
                                    stack.push(a * b);
                                    break;
                            case "/":
                                    stack.push(b / a);
                                    break;
                            }
                    }
                    visited = visitedCopy;
            }

            return String.format("%.5f",stack.pop());
    }

    private String[][] takeInput(int n, int m) {
        
            String input[][] = new String[n][m];
            
            for (int i =0, size = n*m; i<size; i++) {
                String str = sc.nextLine();
                input[i/(m)][i - (i/m)*m] =str;
            }
        
        return input;
    }
}
