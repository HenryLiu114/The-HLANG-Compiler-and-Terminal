package com.henryslanguage.HLANG;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"https://henryslanguage.com",
                        "https://www.henryslanguage.com",
                        "https://celebrated-torrone-5dcaf2.netlify.app"})
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class HLANGController {

    @PostMapping("/run")
    public Map<String, String> run(@RequestBody Map<String, String> request) throws Exception {

        String code = readFromInput(request.get("code"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(outputStream));

            HLANG.Compiler(
                    code,
                    new HashMap<>(),
                    new Stack<>(),
                    new HashMap<>(),
                    new Stack<>());

        } finally {
            System.setOut(originalOut);
        }

        return Map.of("output", outputStream.toString());
    }

    private static String readFromInput(String code) {
        StringBuilder res = new StringBuilder();
        Scanner fc = new Scanner(code);

        while (fc.hasNextLine()) {
            String cur = fc.nextLine().trim();

            if (!cur.isEmpty() && cur.charAt(0) != '#') {
                res.append(cur);
            }
        }

        fc.close();
        return res.toString();
    }
}
