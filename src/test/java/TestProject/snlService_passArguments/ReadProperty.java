package TestProject.snlService_passArguments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperty {
	
	public String knowAuthenticationType() throws FileNotFoundException, IOException
	{
		Properties p=new Properties();
		p.load(new FileReader("resources/data.properties"));
		return p.getProperty("authenticationtype");
	}

}
