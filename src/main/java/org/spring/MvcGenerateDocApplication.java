package org.spring;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.spring.config.CoreConfig;
import org.spring.entity.ApiDoc;
import org.spring.util.JavaDocReader;
import org.spring.util.ParseDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class MvcGenerateDocApplication implements CommandLineRunner {

    private CoreConfig coreConfig;
    private ParseDoc parseDoc;
    private Configuration configuration;

    private static List<RootDoc> docList = new ArrayList<>();

    @Override
    public void run(String... strings) throws Exception {
        File file = new File(coreConfig.getCodePath());
        readFile(file);
        Map<ClassDoc, List<ApiDoc>> map = new HashMap<>();
        for (RootDoc rootDoc : docList) {
            for (ClassDoc classDoc : rootDoc.classes()) {
                List<ApiDoc> apiDocs = parseDoc.parseJavaDocToApiDoc(classDoc);
                map.put(classDoc, apiDocs);
            }
        }


        Template template = configuration.getTemplate("doc.ftl");
        for (Map.Entry<ClassDoc, List<ApiDoc>> apiDocEntry : map.entrySet()) {
            List<ApiDoc> apiDocs = apiDocEntry.getValue();
            Map<String, Object> data = new HashMap<>();
            data.put("docs", apiDocs);
            String str = FreeMarkerTemplateUtils
                    .processTemplateIntoString(template, data);
            String rawCommentText = apiDocEntry.getKey().getRawCommentText();
            String[] split = rawCommentText.split("\n");
            String fileName = split[0].trim() + ".md";
            File apiDocFile = new File(coreConfig.getSaveDocPath(), fileName);
            if(!apiDocFile.getParentFile().exists()) {
                apiDocFile.getParentFile().mkdirs();
            }
            try(FileWriter fw = new FileWriter(apiDocFile)) {
                fw.write(str);
            }
        }

    }

    private void readFile(File file) {
        if(file.isFile()) {
            docList.add(JavaDocReader.getDocClass(coreConfig.getClassPath(), file.getAbsolutePath()));
            return;
        }
        if(file.isDirectory()) {
            File[] fileList = file.listFiles();
            if(fileList == null) {
                return;
            }
            for (File childrenFile : fileList) {
                readFile(childrenFile);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MvcGenerateDocApplication.class, args);
    }

    @Autowired
    public void setCoreConfig(CoreConfig coreConfig,
            ParseDoc parseDoc,
            Configuration configuration) {
        this.coreConfig = coreConfig;
        this.parseDoc = parseDoc;
        this.configuration = configuration;
    }
}
