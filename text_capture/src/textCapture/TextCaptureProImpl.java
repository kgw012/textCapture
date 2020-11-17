package textCapture;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

class KeyHooker implements NativeKeyListener {
	
	private boolean isPressedAlt = false;
	private boolean isPressedShift = false;

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if(e.getKeyCode() == NativeKeyEvent.VC_ALT) {
			isPressedAlt = true;
		}
		if(e.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
			isPressedShift = true;
		}
		if(isPressedAlt && isPressedShift && e.getKeyCode() == NativeKeyEvent.VC_C) {
			isPressedAlt = false;
			isPressedShift = false;
			new CaptureFrame();
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		if(e.getKeyCode() == NativeKeyEvent.VC_ALT) {
			isPressedAlt = false;
		}
		if(e.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
			isPressedShift = false;
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {}

}

class TextCaptureProImpl implements TextCapturePro {
	
	KeyHooker keyHooker;
	static BufferedImage capturedImage = null;
	
	
	TextCaptureProImpl(){
		keyHooker = null;
		capturedImage = null;
	}

	@Override
	public void captureModeStart() {
		keyHooker = new KeyHooker();
		LogManager.getLogManager().reset();
		Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
		try {
			GlobalScreen.registerNativeHook();
		}catch (NativeHookException e) {
			System.out.println("There was a problem registering the native hook.");
			System.out.println(e.getMessage());
		}
		GlobalScreen.addNativeKeyListener(keyHooker);
	}

	@Override
	public void captureModeStop() {
		GlobalScreen.removeNativeKeyListener(keyHooker);
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			System.out.println("There was a problem registering the native hook.");
			System.out.println(e.getMessage());
		}
	}

	@Override
	public boolean save(String fileName) {
		return true;
	}

	@Override
	public void saveCapturedImage(Image capturedImage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getText(Image img) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String translateText(String text) {
		// TODO Auto-generated method stub
		return null;
	}

}
