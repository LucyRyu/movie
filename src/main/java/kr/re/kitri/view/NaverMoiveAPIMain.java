package kr.re.kitri.view;

import kr.re.kitri.service.MovieSearchService;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by danawacomputer on 2017-05-12.
 */
public class NaverMoiveAPIMain {
    public static void main(String[] args) {

        //서비스객체 생성 및 초기화 드라이버 연결
        MovieSearchService service = new MovieSearchService();

        //keyword값 입력
        String keyword = service.InputKeyword();

        //keyword값으로 네이버api 호출하여 json string 리턴
        String json = MovieSearchService.searchAndReturnJson(keyword);
        JSONObject obj = new JSONObject(json);
        JSONArray list = obj.getJSONArray("items");
        System.out.printf("%d개의 결과가 정상적으로 조회되었습니다.\n",list.length());

/*
        for(int i=0; i<list.length(); i++){
            System.out.println(list.getJSONObject(i).getString("director"));
        }
*/

        //리턴 받은 값으로 db테이블에 값 insert
        service.InsertMoiveList(list);
    }
}
