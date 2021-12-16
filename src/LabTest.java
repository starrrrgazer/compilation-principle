import java.util.Scanner;


public class LabTest {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            String[] inputArr = input.trim().split("\\s+");
            for (String str : inputArr) {
                lexer.lexAnalyze(str);
            }
        }
        System.exit(0);
    }
}
