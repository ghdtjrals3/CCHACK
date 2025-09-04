package kopo.poly.dto;

import java.util.List;

public record WasteGuide(
    String updatedAt,
    String hours,
    String locationsGeneral,
    String locationsFood,
    String locationsBulky,
    String bagColor,
    String bulkyUrl,
    Section recyclable,
    Section food,
    Section classification,
    List<Company> companies
) {
    public record Section(
        String sourceUrl,
        String title,
        List<String> paragraphs,   // 본문 문단
        List<String> bullets,      // 글머리표
        List<Table> tables         // 표(머리글+행들)
    ) {}
    public record Table(
        List<String> headers,
        List<List<String>> rows
    ) {}
    public record Company(
        String name,
        String phone,
        String areaOrAddr
    ) {}
}
