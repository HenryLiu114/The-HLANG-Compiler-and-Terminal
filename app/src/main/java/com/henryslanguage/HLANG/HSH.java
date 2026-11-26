package com.henryslanguage.HLANG;

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
            if (!cur.isEmpty() && cur.charAt(0) != '#') {
                res.append(cur);
            }
        }

        fc.close();
        return res.toString();
    }

    public static void main(String[] args) {
        if (args.length != 1)
        {
            System.err.println("Usage: hsh /path/to/hlang-source-file.hlang");
            System.exit(1);
        }

        try {
            String stuff = readFromFile(args[0]);
            HLANG.Compiler(stuff, new HashMap<>(), new Stack<>(), new HashMap<>(), new Stack<>());
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Cannot find file " + args[0]);
            System.exit(1);
        } catch (Exception e)
        {
            System.err.println("Error running program: " + e.getMessage());
            System.exit(1);
        }
    }
    //public static void main(String[] args) throws FileNotFoundException, Exception {
        //String stuff = readFromFile("./getindex.hlang");
        //HLANG.Compiler(stuff, new HashMap<>(), new Stack<>(), new HashMap<>(), new Stack<>());
    //}
}
