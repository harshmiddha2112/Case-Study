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
    private Scanner sc;
    private static int n, m;
    private StringBuilder sb;

    public static void main(String[] args) throws IOException {

        Spreadsheet ss = new Spreadsheet();

        ss.sc = new Scanner(System.in);
        m = ss.sc.nextInt();
        n = ss.sc.nextInt();

        ss.sc.nextLine();

        String input[][] = ss.takeInput(n, m);
        String output[][] = new String[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                boolean[][] visited = new boolean[n][m];
                output[i][j] = ss.evalSentence(input[i][j].split(" "), visited, i, j, input);
            }
        }

        ss.printOutput(output);
    }

    private String[][] takeInput(int n, int m) {
        String[][] input = new String[n][m];

        for (int i = 0, size = n * m; i < size; i++) {
            String str = sc.nextLine();
            input[i / (m)][i - (i / m) * m] = str;
        }

        return input;
    }
    
    private void printOutput(String[][] output) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.println(String.format("%.5f",Float.valueOf(output[i][j])));
            }
        }
    }

    private String evalSentence(String[] words, boolean[][] visited, int i, int j, String[][] input) {

        Stack < Float > stack = new Stack < > ();
        boolean[][] visitedCopy = new boolean[n][m];
        visitedCopy = visited;
        for (String word: words) {
            char firstCharacter = word.charAt(0);
            String firstTwoChar = word.substring(0, Math.min(word.length(), 2));

            if (firstTwoChar.equals("++") ||
                firstTwoChar.equals("--") ||
                (firstCharacter >= 'A' && firstCharacter <= 'Z')) {

                stack.push(evalWord(word, visited, input, i, j));
                evalWordWithPostOperators(word, input, stack.peek());

            } else if ((firstCharacter >= '0' && firstCharacter <= '9')
                     || (firstCharacter == '-' && word.length() > 1)) {
                 //push to stack if it is a number +ve or -ve
                stack.push(evalWord(word, visited, input, i, j));

            } else {
                //pop numbers from stack if it is an operator
                Float a = stack.pop();
                Float b = stack.pop();

                switch (word) {
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
        Float sentenceValue = stack.pop();
        input[i][j] = String.format("%.5f", sentenceValue);

        return String.format("%.5f", sentenceValue);
    }

    private Float evalWord(String word, boolean[][] visited, String input[][], int i, int j) throws NumberFormatException {
        String firstTwoChar = word.substring(0, Math.min(word.length(), 2));
        int preIncrementValue = 0;

        String wordWithoutOperators = getWordWithoutOperators(word);

        char rowName = wordWithoutOperators.charAt(0);
        sb = new StringBuilder(wordWithoutOperators);
        sb.deleteCharAt(0);
        int column = Integer.parseInt(sb.toString());

        if (isNumeric(word)) {
            return Float.valueOf(word);
        }

        if (visited[i][j]) {
            System.out.println("Loop detected");
            System.exit(1);
        }

        visited[i][j] = true;

        sb = new StringBuilder(word);

        if (firstTwoChar.equals("++")) {
            sb.deleteCharAt(0);
            sb.deleteCharAt(0);

            word = sb.toString();
            preIncrementValue = 1;
        } else if (firstTwoChar.equals("--")) {
            sb.deleteCharAt(0);
            sb.deleteCharAt(0);

            word = sb.toString();
            preIncrementValue = -1;
        } else {
            if ((word.charAt(0) >= 'A' || word.charAt(0) <= 'Z') && isNumeric(input[rowName - 'A'][column - 1])) {

                return Float.parseFloat(input[rowName - 'A'][column - 1]);
            } else if (word.charAt(0) == '-' && word.charAt(1) == '-') {
                sb.deleteCharAt(0);
                sb.deleteCharAt(0);

                word = sb.toString();
                input[rowName - 'A'][column - 1] = Float.toString(evalWord(word, visited, input, i, j) - 1);
            } else if (word.charAt(0) == '+' && word.charAt(1) == '+') {
                sb.deleteCharAt(0);
                sb.deleteCharAt(0);

                word = sb.toString();
                input[rowName - 'A'][column - 1] = Float.toString(evalWord(word, visited, input, i, j) + 1);
            } else if (word.charAt(0) == '-') {
                sb.deleteCharAt(0);

                word = sb.toString();
                input[rowName - 'A'][column - 1] = Float.toString( -1 * evalWord(word, visited, input, i, j) + 1);
            } else {
                input[i][j] = Float.toString(Float.valueOf(evalSentence(input[rowName - 'A'][column - 1].split(" "), visited, rowName - 'A', column - 1, input)) + preIncrementValue);
            }

            return Float.valueOf(input[i][j]);
        }


        return evalWord(word, visited, input, i, j) + preIncrementValue;
    }

    private void evalWordWithPostOperators(String word, String[][] input, Float wordValue) {

        int count = postUnaryOperatorsValue(word);

        String wordWithoutOperators = getWordWithoutOperators(word);

        char rowName = wordWithoutOperators.charAt(0);
        sb = new StringBuilder(wordWithoutOperators);
        sb.deleteCharAt(0);
        int column = Integer.parseInt(sb.toString());

        input[rowName - 'A'][column - 1] =
            String.valueOf(
                count +
                wordValue
            );
    }

    private int postUnaryOperatorsValue(String word) {
        int count = 0;
        int i = word.length() - 1;
        OUTER:
            while (i >= 0) {
                switch (word.charAt(i)) {
                    case '+':
                        count++;
                        break;
                    case '-':
                        count--;
                        break;
                    default:
                        break OUTER;
                }
                i = i - 2;
            }

        return count;
    }

    private boolean isNumeric(String str) {

        try
        {
          double d = Float.parseFloat(str);
        }
        catch(NumberFormatException nfe)
        {
          return false;
        }
        return true;
    }

    private String getWordWithoutOperators(String word) {

        String wordWithoutOperators = word.replace("++", "");
        wordWithoutOperators = wordWithoutOperators.replace("--", "");
        wordWithoutOperators = wordWithoutOperators.replace("-", "");

        return wordWithoutOperators;
    }
}