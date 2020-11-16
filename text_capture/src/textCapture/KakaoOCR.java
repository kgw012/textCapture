package textCapture;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class KakaoOCR {
	
	public static String kakaoOCR(File[] files) {

		if (files.length == 0)
            return null;

		String ACCESS_TOKEN = "a659281409fe828af0540f54b5dcd3f0";
        String CRLF = "\r\n";
        String TWO_HYPHENS = "--";
        String BOUNDARY = "---------------------------012345678901234567890123456";
        HttpsURLConnection conn = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int MAX_BUFFER_SIZE = 1 * 1024 * 1024;

        // Request
        try {
            URL url = new URL("https://dapi.kakao.com/v2/vision/text/ocr");
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" +
                                      BOUNDARY);
            conn.setRequestProperty("Authorization", "KakaoAK " + ACCESS_TOKEN);
            conn.setRequestProperty("Cache-Control", "no-cache");

            dos = new DataOutputStream(conn.getOutputStream());

            for (File f : files) {
                dos.writeBytes(TWO_HYPHENS + BOUNDARY + CRLF);
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";" +
                                " filename=\"" + f.getName() + "\"" + CRLF);
                dos.writeBytes(CRLF);
                fis = new FileInputStream(f);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
                buffer = new byte[bufferSize];
                bytesRead = fis.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
                    bytesRead = fis.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(CRLF);
            }

            // finish delimiter
            dos.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + CRLF);

            fis.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (fis != null) try { fis.close(); } catch (IOException ignore) { }
            if (dos != null) try { dos.close(); } catch (IOException ignore) { }
        }

        // Response
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
            
            StringBuilder builder2 = new StringBuilder();
            JSONParser parser = new JSONParser();
            try {
				Object obj = parser.parse(builder.toString());
				JSONObject resultObj = (JSONObject)obj;
				
				JSONArray jsonArray = (JSONArray)parser.parse(resultObj.get("result").toString());
				
				class Tmp{
					int x;
					int y;
					String msg;
					
					Tmp(int x, int y, String msg){
						this.x = x;
						this.y = y;
						this.msg = msg;
					}
				}
				
				Tmp[] tmpArray = new Tmp[jsonArray.size()];
				
				for(int i = 0; i < jsonArray.size(); i++) {
					JSONObject obj2 = (JSONObject)jsonArray.get(i);
					JSONArray array2 = (JSONArray)parser.parse(obj2.get("boxes").toString());
					
					StringTokenizer st = new StringTokenizer(array2.get(0).toString(), "[,]");
					
					int x = Integer.parseInt(st.nextToken());
					int y = Integer.parseInt(st.nextToken());
					
					String s = obj2.get("recognition_words").toString();
					String s2 = s.substring(2, s.length() - 2);
					tmpArray[i] = new Tmp(x, y, s2);
				}
				
				Arrays.sort(tmpArray, new Comparator<Tmp>() {

					@Override
					public int compare(Tmp o1, Tmp o2) {
						if(Math.abs(o1.y - o2.y) < 10)  {
							if(o1.x < o2.x) {
								return -1;
							}else {
								return 1;
							}
						}else if(o1.y < o2.y) {
							return -1;
						}else if(o1.y > o2.y) {
							return 1;
						}
						return 0;
					}
					
				});
				
				int y = 0;
				for(int i = 0; i < tmpArray.length; i++) {
					Tmp tmp = tmpArray[i];
					if(Math.abs(y - tmp.y) < 10) {
						builder2.append(" " + tmp.msg);
					}else {
						y = tmp.y;
						builder2.append("\n" + tmp.msg);
					}
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            return builder2.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignore) {}
            }
            if (inputStream != null) {
                try { inputStream.close(); } catch (IOException ignore) {}
            }
            conn.disconnect();
        }

        return null;
    }
	
}
