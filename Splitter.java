import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class Splitter {
    public static void main(String[] args) throws Exception {
        splitFile("agent-api/src/main/java/io/novel2video/agent/dto/Dto.java", "agent-core/src/main/java/io/novel2video/agent/dto");
        splitFile("agent-api/src/main/java/io/novel2video/agent/dto/AssetDto.java", "agent-core/src/main/java/io/novel2video/agent/dto");
    }

    static void splitFile(String inFile, String outDir) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(inFile)));
        Pattern p = Pattern.compile("(@Data[\\s\\S]*?class\\s+(\\w+)[\\s\\S]*?(?=\\n@|\\z))");
        Matcher m = p.matcher(content);
        
        String imports = "package io.novel2video.agent.dto;\n\nimport lombok.*;\nimport java.time.LocalDateTime;\nimport java.util.List;\nimport java.util.Map;\n\n";
        
        while (m.find()) {
            String block = m.group(1);
            String className = m.group(2);
            if (block.contains("public class")) {
                continue; // Skip ApiResponse
            }
            // Add @NoArgsConstructor if missing (helps with some serialization/instantiation)
            if (!block.contains("@NoArgsConstructor")) {
                block = block.replaceFirst("@Data", "@Data\n@NoArgsConstructor");
            }
            block = block.replaceFirst("class " + className, "public class " + className);
            
            Path outFile = Paths.get(outDir, className + ".java");
            Files.write(outFile, (imports + block).getBytes());
            System.out.println("Overwritten: " + outFile);
        }
    }
}
