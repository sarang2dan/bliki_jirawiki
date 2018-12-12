package info.bliki.html;

import info.bliki.html.wikipedia.ToWikipedia;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


public class HTML2WikipediaTest {

	public static void main(String[] args) {

		FileReader fr = null;
		FileWriter fw = null;

		BufferedReader br = null;
		BufferedWriter bw = null;
		String lineStr = null;

		try {

			// "ReadFile.txt" 파일을 읽는 FileReader 객체 생성
			// BufferedReader 객체 생성
			fr = new FileReader("/Users/sonumb/jira_temp_jiraissue_org_2017-07-07.sql");
			br = new BufferedReader(fr);

			// FileWriter로 파일 "CopyFile.txt"에 출력한다. 기존 파일에 덮어쓴다.
			// BufferedWriter 객체 생성
			fw = new FileWriter("/Users/sonumb/jira_temp_jiraissue_wiki_2017-07-07.sql", false);
			bw = new BufferedWriter(fw);

			while ((lineStr = br.readLine()) != null) {
				HTML2WikiConverter conv = new HTML2WikiConverter();
				//lineStr = lineStr.replaceAll("\r","");
				conv.setInputHTML(lineStr);
				// conv.fInputHTML = conv.fInputHTML.replaceAll("&nbsp;", " ");

				String resultContents = conv.toWiki(new ToWikipedia());
				// resultContents = resultContents.replaceAll("-&gt;", "/");
				resultContents = resultContents.replaceAll("&nbsp;", " ");
				resultContents = resultContents.replaceAll("&amp;", "&");
				resultContents = resultContents.replaceAll("&gt;", ">");
				resultContents = resultContents.replaceAll("&lt;", "<");
				resultContents = resultContents.replaceAll("&#39;", "\\\\\'");
				resultContents = resultContents.replaceAll("&quot;", "\\\\\"");
				resultContents = resultContents.replaceAll("&#61548;", "");
				resultContents = resultContents.replaceAll("&#61672;", " ");
				resultContents = resultContents.replaceAll("&#61550;", " ");
				resultContents = resultContents.replaceAll("&#8211;", "-");
				//System.out.println(resultContents);
				bw.write(resultContents);
				bw.newLine();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// BufferedReader FileReader를 닫아준다.
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
				}
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
				}

			// BufferedWriter FileWriter를 닫아준다.
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
				}
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
				}
		}
	}
}
