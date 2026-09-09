import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class HSH {
    private static String readFromFile(String URL) throws FileNotFoundException {
        StringBuilder res = new StringBuilder();
        Scanner fc = new Scanner(new File(URL).getAbsoluteFile());

        while (fc.hasNextLine()) {
            String cur = fc.nextLine().trim(); // removes tabs/spaces at start and end

            if (!cur.isEmpty()) {
                res.append(cur);
            }
        }

        fc.close();
        return res.toString();
    }

    public static void main(String[] args) throws FileNotFoundException, Exception {
       String stuff = readFromFile(args[0]);
       HLANG.Compiler(stuff, new HashMap<>(), new Stack<>(), new HashMap<>(), new Stack<>());
    }
    //public static void main(String[] args) throws FileNotFoundException, Exception {
      //  String stuff = readFromFile("getindex.hlang");
      //  HLANG.Compiler(stuff, new HashMap<>(), new Stack<>(), new HashMap<>(), new Stack<>());
    //}
}
