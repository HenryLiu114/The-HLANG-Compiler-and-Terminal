package com.henryslanguage.HLANG;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.io.*;

@CrossOrigin(origins = "https://henryslanguage.com")
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
