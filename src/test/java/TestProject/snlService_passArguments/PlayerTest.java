package TestProject.snlService_passArguments;
import static org.assertj.core.api.Assertions.assertThat;


import java.io.FileReader;
import java.io.IOException;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import org.json.simple.parser.JSONParser;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class PlayerTest {
	requests request ;
	Integer board_id;
	static Integer player_id;
	JSONParser parser;
	String version;
	String auth;
	
	@BeforeTest
	public void createboard() throws Exception
	{
		
		auth = new ReadProperty().knowAuthenticationType();
		System.out.println(auth);
		
		parser = new JSONParser();

		Object obj = parser.parse(new FileReader("resources/data.json"));

		JSONObject jsonObject = (JSONObject) obj;
		JSONObject authobj = (JSONObject) jsonObject.get(auth);
		version = (String) authobj.get("version");
		System.out.println("hello " + version);
		
		String auth = new ReadProperty().knowAuthenticationType();
		System.out.println(auth);
	
		parser = new JSONParser();
		parser=new JSONParser();
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/board/new.json");
		String response = request.sendGet();
		
		JSONObject obj2=(JSONObject) parser.parse(response);
		JSONObject responseObj=(JSONObject) obj2.get("response");
		JSONObject board=(JSONObject) responseObj.get("board");
		board_id=Integer.valueOf(board.get("id").toString());
		
		
	}

	@Test(priority=1)
	public void registerplayer() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/player.json");
		String message = "{\"board\":" + "\"" + board_id + "\"" + ",\"player\":{\"name\": \"shadab\"}}";
		String response = request.sendPost("http://10.0.1.86/snl/rest/"+version+"/player.json", message);
		JSONObject obj=(JSONObject) parser.parse(response);
		org.json.simple.JSONObject responseObj = (org.json.simple.JSONObject) obj.get("response");
		JSONObject playerObj = (JSONObject) responseObj.get("player");
		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
		assertThat(Integer.valueOf(playerObj.get("board_id").toString())).isEqualTo(board_id);
		assertThat(playerObj.get("name")).isEqualTo("shadab");
		player_id = Integer.valueOf( playerObj.get("id").toString());

	}

	@Test(priority=2)
	public void getplayerdetails() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		String response = request.sendGet();
		JSONObject obj=(JSONObject) parser.parse(response);
		System.out.println(response);
		org.json.simple.JSONObject responseObj = (org.json.simple.JSONObject) obj.get("response");
		JSONObject playerObj = (JSONObject) responseObj.get("player");
		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
		assertThat(Integer.valueOf(playerObj.get("id").toString())).isEqualTo(player_id);
	}

	@Test(priority=3)
	public void updateplayerdetailtest() throws IOException, ParseException, OAuthSystemException, OAuthProblemException{
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		String data = "{\"player\":{\"name\":\"shad\"}}";

		String response = request.putrequest("http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json", data);
		JSONObject obj=(JSONObject) parser.parse(response);
		JSONObject responseObj = (JSONObject) obj.get("response");
		JSONObject playerObj = (JSONObject) responseObj.get("player");
		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
		assertThat(playerObj.get("name")).isEqualTo("shad");
	}

	

	@Test(priority=4)
	public void moveplayertest() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		String response = request.sendGet();
		JSONObject obj=(JSONObject) parser.parse(response);
		System.out.println(response);
		JSONObject responseObj = (JSONObject) obj.get("response");
		JSONObject playerObj = (JSONObject) responseObj.get("player");
		Integer startingposition = Integer.valueOf(playerObj.get("position").toString());
		System.out.println("get details");
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/move/" + board_id + ".json?player_id=" + player_id);
		String response2 = request.sendGet();
		System.out.println("move");

		JSONObject obj2=(JSONObject) parser.parse(response2);
		JSONObject responseObj2 = (JSONObject) obj2.get("response");
		JSONObject playerObj2 = (JSONObject) responseObj2.get("player");
		Integer finalposition = Integer.valueOf(playerObj2.get("position").toString());
		assertThat(Integer.valueOf(responseObj2.get("status").toString())).isEqualTo(1);
		assertThat(startingposition).isNotEqualTo(finalposition);
	}
	
	@Test(priority=5)
	public void deleteplayertest() throws Exception {
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		String response = request.deleteRequest("http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		
		JSONObject obj=(JSONObject) parser.parse(response);
		JSONObject responseObj = (JSONObject) obj.get("response");
		assertThat(Integer.valueOf(responseObj.get("status").toString())).isEqualTo(1);
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		String response2 = request.sendGet();
		assertThat(response2).isEqualTo("not found");
		
	}
	
	@Test(priority=6)
	public void alreadydeletedplayer_deletetest() throws IOException, OAuthSystemException, OAuthProblemException, ParseException
	{
		request = new requests(auth,"http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		String response = request.deleteRequest("http://10.0.1.86/snl/rest/"+version+"/player/" + player_id + ".json");
		assertThat(response).isEqualTo("not exist");
	}

	/*public static void main(String args[]) throws Exception {
		playertest test = new playertest();
		test.deleteplayertest();
		//System.out.println("player moved");
	}*/

}
