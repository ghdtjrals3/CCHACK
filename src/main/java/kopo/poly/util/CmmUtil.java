package kopo.poly.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CmmUtil {

    public static String nvl(String str, String chg_str) {
        return (str == null || str.isEmpty()) ? chg_str : str;
    }

    public static String nvl(String str) {
        return nvl(str, "");
    }

    public static String checked(String str, String com_str) {
        return Objects.equals(str, com_str) ? " checked" : "";
    }

    public static String checked(String[] str, String com_str) {
        if (str == null) return ""; // null 방어

        for (String s : str) {
            if (Objects.equals(s, com_str)) {
                return " checked";
            }
        }
        return "";
    }

    public static String select(String str, String com_str) {
        return Objects.equals(str, com_str) ? " selected" : "";
    }

    public static String saveFile(MultipartFile file, String baseDir, String subfolder) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String original = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
        if (original.contains("..")) throw new IllegalArgumentException("잘못된 파일명");

        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot != -1) ext = original.substring(dot);

        String filename = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                .withZone(java.time.ZoneId.of("UTC"))
                .format(java.time.Instant.now())
                + "_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0,8)
                + ext;

        java.nio.file.Path root = java.nio.file.Paths.get(baseDir).toAbsolutePath().normalize();
        java.nio.file.Path dir  = (subfolder == null || subfolder.isBlank()) ? root : root.resolve(subfolder).normalize();
        java.nio.file.Files.createDirectories(dir);

        java.nio.file.Path target = dir.resolve(filename);
        try (java.io.InputStream in = file.getInputStream()) {
            java.nio.file.Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        // 브라우저에서 접근할 URL 반환
        return "/uploads/" + (subfolder == null || subfolder.isBlank() ? "" : subfolder + "/") + filename;
    }


    public static List<Map<String, String>> readCsvAsMaps(String location) throws Exception {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("CSV 경로가 null/blank 입니다.");
        }

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        if (location.startsWith("classpath:")) {
            String cp = location.substring("classpath:".length());
            try (InputStream in = CmmUtil.class.getResourceAsStream(cp.startsWith("/") ? cp : "/" + cp)) {
                if (in == null) throw new IllegalArgumentException("Classpath 리소스를 찾을 수 없습니다: " + location);
                try (var br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class).with(schema).readValues(br);
                    return it.readAll();
                }
            }
        } else {
            Path p = Path.of(location);
            if (!Files.exists(p)) {
                throw new IllegalArgumentException("CSV not found: " + p.toAbsolutePath());
            }
            try (var in = Files.newInputStream(p);
                 var br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class).with(schema).readValues(br);
                return it.readAll();
            }
        }
    }

}