import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class MyFunc {
    public String readFromFile(String filename){
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String readLine = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((readLine = bufferedReader.readLine())!= null){
                stringBuilder.append(System.lineSeparator() + readLine);
            }
            bufferedReader.close();
            return stringBuilder.toString();
        }catch (Exception e){
            System.out.println("read error");
            e.printStackTrace();
            System.exit(-2);
            return null;
        }
    }
    public void writeToFile(String filename,String content){
        try {
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.flush();
        }catch (Exception e){
            System.out.println("write error");
            e.printStackTrace();
            System.exit(-3);
        }
    }
}
