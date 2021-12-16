import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;




public class LabTest {
    public static void main(String[] args) {
        try {
            MyFunc myFunc = new MyFunc();

            String input = myFunc.readFromFile(args[0]);
//            String input = myFunc.readFromFile("D:\\2021compile\\compilation-principle\\input.txt");
            System.out.println(input);
            CharStream charStream = CharStreams.fromString(input);
            MiniSysLexer miniSysLexer = new MiniSysLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(miniSysLexer);
            MiniSysParser miniSysParser = new MiniSysParser(tokens);
            AntlrError antlrError = new AntlrError();
            miniSysParser.addErrorListener(antlrError);
            ParseTree tree = miniSysParser.compUnit();
            AntlrVisitor antlrVisitor = new AntlrVisitor();
            antlrVisitor.visit(tree);

            System.out.println("-------------------------");
            String output = antlrVisitor.getStringBuilder().toString();
            System.out.println(output);
            System.out.println("-------------------------");

            myFunc.writeToFile(args[1],output);
//            myFunc.writeToFile("D:\\2021compile\\compilation-principle\\output.txt",output);

            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }


}
