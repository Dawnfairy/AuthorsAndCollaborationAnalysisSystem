package com.fnk.parsers;

import com.fnk.model.Author;
import com.fnk.model.GraphModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

import java.util.*;

public class ExcelParser {
    public static void parseExcel(String filePath, GraphModel graphModel) {
        try (FileInputStream excelFile = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(excelFile)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            if (iterator.hasNext()) iterator.next(); // Başlık satırını atla
            List<Author> mainAuthorList = new ArrayList<>();
            Map<Author, Map<String, List<String>>> coauthorsMap = new HashMap<>();

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Cell orcidCell = currentRow.getCell(0);
                Cell authorNameCell = currentRow.getCell(3);
                Cell coauthorsCell = currentRow.getCell(4);
                Cell paperTitleCell = currentRow.getCell(5);

                if (orcidCell == null || authorNameCell == null || coauthorsCell == null || paperTitleCell == null)
                    continue;

                String orcid = orcidCell.getStringCellValue();
                String authorName = authorNameCell.getStringCellValue();
                String coauthorsStr = coauthorsCell.getStringCellValue();
                String paperTitle = paperTitleCell.getStringCellValue();

                Author mainAuthor = graphModel.getOrCreateAuthor(orcid, authorName);
                List<String> coauthors = parseCoAuthors(coauthorsStr);
                System.out.println(mainAuthor.toString());
                mainAuthor.addPaper(paperTitle);
                mainAuthorList.add(mainAuthor);


                Map<String, List<String>> cellMap = coauthorsMap.computeIfAbsent(mainAuthor, k -> new HashMap<>());
                cellMap.computeIfAbsent(paperTitle, k -> new ArrayList<>()).addAll(coauthors);

            }
            graphModel.synchronizeAuthors(coauthorsMap, mainAuthorList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Toplam Yazar Sayısı: " + graphModel.getAuthorCount());
        System.out.println("Toplam Kenar Sayısı: " + graphModel.getEdgeCount());


    }
    private static List<String> parseCoAuthors(String coAuthorsStr) {
        List<String> coAuthors = new ArrayList<>();

        coAuthorsStr = coAuthorsStr.trim();
        if (coAuthorsStr.startsWith("[") && coAuthorsStr.endsWith("]")) {
            coAuthorsStr = coAuthorsStr.substring(1, coAuthorsStr.length() - 1);
        }

        String[] names = coAuthorsStr.replace("'", "").split(",");

        for (String name : names) {
            String trimmedName = name.trim();
            if (!trimmedName.isEmpty()) {
                coAuthors.add(trimmedName);
            }
        }

        return coAuthors;
    }
}
