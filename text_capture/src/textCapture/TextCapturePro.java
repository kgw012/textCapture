package textCapture;

import java.awt.Image;

interface TextCapturePro {
	
	void captureModeStart();
	void captureModeStop();
	boolean save(String fileName);
	void saveCapturedImage(Image capturedImage);
	String getText(Image img);
	String translateText(String text);
}
