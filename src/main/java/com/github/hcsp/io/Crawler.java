package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);

        HttpEntity entity = response.getEntity();
        // 获取流
        InputStream content = entity.getContent();
        // IOUtils的toString方法将InputStream流转换为字符串
        String html = IOUtils.toString(content);
        Document document = Jsoup.parse(html);
        Elements issues = document.select(".js-issue-row");
        List<String> lines = new ArrayList<>();
        lines.add("number,author,title");
        for (int i = 0; i < n; i++) {
            int num = Integer.parseInt(issues.get(i).id().split("_")[1]);
            String title = issues.get(i).select("[data-hovercard-type=pull_request]").text();
            String author = issues.get(i).select("[data-hovercard-type=user]").text();
            lines.add(num + "," + author + "," + title);
        }
        FileUtils.writeLines(csvFile, lines);
        response.close();
    }
}
