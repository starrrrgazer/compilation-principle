import java.util.Scanner;

public class Lexer {
    String[] tokenString = {
            "=", ";", "(", ")", "{", "}", "+", "*", "/", "<", ">","==",
            "if", "else", "while", "break", "continue", "return"
            };
    char[] tokenChar = {'=', ';', '(', ')', '{' , '}', '+', '*', '/', '<', '>'};
    String[] output = {
            "Assign", "Semicolon", "LPar", "RPar", "LBrace", "RBrace", "Plus", "Mult", "Div", "Lt", "Gt", "Eq",
            "If", "Else", "While", "Break", "Continue", "Return",
            "Err"};
    Boolean isDigit(String str) {
        return str.matches("^[0-9]+$");
    }
    Boolean isIdentifier(String str) {
        return str.matches("^[a-zA-Z0-9_]+$");
    }
    void printIdent(String str){
        System.out.println("Ident(" + str + ")");
    }
    void printNumber(String str){
        System.out.println("Number(" + str + ")");
    }
    void printToken(int index){
        System.out.println(output[index]);
    };
    int getIndexOfStringArr(String str, String[] strings){
        int len = strings.length;
        for(int i = 0; i < len; i++){
            if(strings[i].equals(str)){
                return i;
            }
        }
        return -1;
    }
    int getIndexOfCharArr(char c, char[] chars){
        int len = chars.length;
        for(int i = 0; i < len; i++){
            if(c == chars[i]){
                return i;
            }
        }
        return -1;
    }
    void lexAnalyze(String str){
        //这个字符串就是token
        int index = getIndexOfStringArr(str, tokenString);
        if(index != -1){
            printToken(index);
            return;
        }
        //这个字符串不是token
        char[] chars = str.toCharArray();
        int len = str.length();
        for(int i = 0;i < len; i++){
            int index1 = getIndexOfCharArr(chars[i],tokenChar);
//            System.out.println(str + " index:" + index + " index1:" + index1);
            //非 = 的特殊符号
            if(index1 > 0){
                printToken(index1);
            }
            //是 =
            else if(index1 == 0){
                //是 ==
                if(i+1<len && chars[i+1] == '='){
                    System.out.println("Eq");
                    i++;
                }
                //只是 =
                else{
                    printToken(index1);
                }
            }
            //是数字
            else if(Character.isDigit(chars[i])){
                int j=i+1;
                while(j<len && Character.isDigit(chars[j])){
                    j++;
                }
                printNumber(str.substring(i,j));
                i=j-1;
            }
            //是标识符
            else if(Character.isLetter(chars[i]) ||  chars[i] == '_'){
                int j=i+1;
                while(j<len && (Character.isLetter(chars[j]) ||  chars[j] == '_' || Character.isDigit(chars[j]))){
                    j++;
                }
                int index2 = getIndexOfStringArr(str.substring(i,j),tokenString);
                //就是标识符
                if(index2 == -1){
                    printIdent(str.substring(i,j));
                }
                //这个子字符串是其他token
                else{
                    printToken(index2);
                }
                i=j-1;
            }
            else{
                System.out.println("Err");
                System.exit(0);
            }
        }
    }
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while( !input.isEmpty()){
            String[] inputArr = input.trim().split("\\s+");
            input = scanner.nextLine();
            for(String str : inputArr){
                lexer.lexAnalyze(str);
            }
        }
        System.exit(0);
    }
}
