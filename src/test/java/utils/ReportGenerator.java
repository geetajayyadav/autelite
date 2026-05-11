package utils;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {

    public static void main(String[] args) {

        File reportOutputDirectory = new File("target/cucumber-html-reports");

        List<String> jsonFiles = new ArrayList<>();

        // 🔥 ABSOLUTE PATH FIX (IMPORTANT)
        jsonFiles.add(new File("target/cucumber.json").getAbsolutePath());

        Configuration configuration = new Configuration(
                reportOutputDirectory,
                "Autelite Automation"
        );

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();

        System.out.println("✅ Advanced Cucumber Report Generated Successfully");
    }
}