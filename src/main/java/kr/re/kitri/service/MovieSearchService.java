package kr.re.kitri.service;

import kr.re.kitri.model.Item;
import kr.re.kitri.util.MovieConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static kr.re.kitri.util.MovieConstants.*;

public class MovieSearchService {

    public MovieSearchService() {
        try {
            Class.forName(MovieConstants.DRIVER_POSTGRES);
            System.out.println("dirver ok...");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String InputKeyword() {
        String keyword = "";
        Scanner scanner = new Scanner(System.in);
        System.out.println("검색어를 입력하세요: ");

        keyword = scanner.nextLine();
        return keyword;
    }


    public static String searchAndReturnJson(String keyword) {
        try {
            String text = URLEncoder.encode(keyword, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text + "&display=10"; // json 결과

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);


            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            //System.out.println(response.toString());
            br.close();


            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    public List<Item> InsertMoiveList(JSONArray list) {

        String searchSql = "INSERT INTO public.search(\n" +
                "\tsearch_id, last_build_date, total)\n" +
                "\tVALUES (?, ?, ?);";

        String itemsql = "INSERT INTO public.items(\n" +
                "\titem_id, title, link, image, subtitle, pub_date, director, actor, user_rating, search_id)\n" +
                "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";


        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("connetion ok...");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement prpsnt1 = conn.prepareStatement(searchSql);
            PreparedStatement prpsnt2 = conn.prepareStatement(itemsql);
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());


            int i = 1;
            //while(list!=null){
            for (Object item : list) {
                prpsnt1.setInt(1, i);//search_id
                prpsnt1.setDate(2, sqlDate);
                prpsnt1.setInt(4, i);//total
                prpsnt1.setString(3,list.getString(1));//keword

                prpsnt2.setInt(1, i);//item_id
                prpsnt2.setString(2, list.getString(1));//title
                prpsnt2.setString(3, list.getString(2));//link
                prpsnt2.setString(4, list.getString(3));//image
                prpsnt2.setString(5, list.getString(4));//subtitle
                prpsnt2.setDate(6, LocalDate.of(list.getInt(5)));//pub_date
                prpsnt2.setString(7, list.getString(6));//director
                prpsnt2.setString(8, list.getString(7));//actor
                prpsnt2.setFloat(9, (float) list.getDouble(8));//user_rating
                prpsnt2.setInt(10, i);//search_id
                i++;

                //}
            }

            prpsnt1.executeUpdate();
            prpsnt2.executeUpdate();
            System.out.println("insert ok...");
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("insert failed...");
        }

        return null;
    }

    public void insertDataToPostgres(String lastBuildDate, int total, String keyword, List<Item> list) {
        String insertSearch = "";
        String insertItem = "";



    }


}
