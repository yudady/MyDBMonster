package com.foya;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

public class RunMainApp {

	public static void main(String[] args) {
		try {
			String userDir = System.getProperty("user.dir");
			InputStream inStream = new FileInputStream(new File(userDir + "/src/main/resources/dbmonster.properties"));

			Properties prop = new Properties();
			prop.load(inStream);
			Iterator<Entry<Object, Object>> it = prop.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Object, Object> pair = it.next();
				System.setProperty("" + pair.getKey(), "" + pair.getValue());
			}
			args = new String[] { "-s", userDir + "/src/main/resources/test-schema.xml" };

			MyLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
