package TestProject.snlService_passArguments;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BoardTest {

	requests request;
	static String version;
	JSONObject jsonObject;
	static int board_id;
	JSONParser parser;
	String auth ;

	@BeforeTest
	public void findVersion()
			throws FileNotFoundException, IOException, OAuthSystemException, OAuthProblemException, ParseException {

		auth = new ReadProperty().knowAuthenticationType();
		System.out.println(auth);
		
		parser = new JSONParser();

		Object obj = parser.parse(new FileReader("resources/data.json"));

		JSONObject jsonObject = (JSONObject) obj;
		JSONObject authobj = (JSONObject) jsonObject.get(auth);
		version = (String) authobj.get("version");
		System.out.println("hello " + version);

	}

	@Test(priority = 1)
	public void createboardtest() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/" + version + "/board/new.json");
		String response = request.sendGet();
		System.out.println(response);
		JSONObject obj = (JSONObject) parser.parse(response);

		JSONObject responseObj = (JSONObject) obj.get("response");
		JSONObject board = (JSONObject) responseObj.get("board");
		System.out.println(responseObj.get("status"));

		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
		board_id = Integer.valueOf(board.get("id").toString());
	}

	@Test(priority = 2)
	public void getlistofboardtest() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/" + version + "/board.json");
		String response = request.sendGet();
		JSONObject obj = (JSONObject) parser.parse(response);
		JSONObject responseObj = (JSONObject) obj.get("response");
		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
	}

	@Test(priority = 3)
	public void getddetailofboard() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json");
		String response = request.sendGet();
		JSONObject obj = (JSONObject) parser.parse(response);
		JSONObject responseObj = (JSONObject) obj.get("response");
		JSONObject board = (JSONObject) responseObj.get("board");
		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
		assertThat(Integer.valueOf(board.get("id").toString())).isEqualTo(board_id);
	}

	@Test(priority = 4)
	public void resetboardtest() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json");
		String response = request.putrequest("http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json",
				null);
		JSONObject obj = (JSONObject) parser.parse(response);
		JSONObject responseObj = (JSONObject) obj.get("response");
		JSONObject board = (JSONObject) responseObj.get("board");
		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);

	}

	@Test(priority = 5)
	public void deleteboardtest() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json");
		String response = request
				.deleteRequest("http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json");
		System.out.println("response is :" + response);
		JSONObject obj = (JSONObject) parser.parse(response);
		System.out.println("object is :" + obj.toJSONString());
		JSONObject responseObj = (JSONObject) obj.get("response");

		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
		request = new requests(auth,"http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json");
		String response2 = request.sendGet();
		JSONObject obj2 = (JSONObject) parser.parse(response2);
		JSONObject responseObj2 = (JSONObject) obj2.get("response");
		System.out.println(responseObj2.get("status").toString());
		assertThat(Integer.valueOf(responseObj2.get("status").toString())).isEqualTo(-1);

	}

	@Test(priority = 6)
	public void alreadydeletedboard_deletetest() throws IOException, OAuthSystemException, OAuthProblemException, ParseException {
		request = new requests(auth,"http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json");
		String response = request
				.deleteRequest("http://10.0.1.86/snl/rest/" + version + "/board/" + board_id + ".json");
		assertThat(response).isEqualTo("not exist");
	}

}
