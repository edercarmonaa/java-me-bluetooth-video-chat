import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class CameraMIDlet extends MIDlet{
	
	private CameraCanvas cameraCanvas=null;
	private DisplayCanvas displayCanvas=null;
	
	public CameraMIDlet(){
		}
		
	public void startApp(){
		Displayable current=Display.getDisplay(this).getCurrent();
		if(current==null){
			//firstcall
			cameraCanvas = new CameraCanvas(this);
			displayCanvas = new DisplayCanvas(this);
			Display.getDisplay(this).setCurrent(cameraCanvas);
			cameraCanvas.start();
		}
		else{
			//returning from pauseApp
			if(current==cameraCanvas){
				cameraCanvas.start();
			}
			Display.getDisplay(this).setCurrent(current);
		}
	}
	
	
	public void pauseApp(){
		if(Display.getDisplay(this).getCurrent()==cameraCanvas){
			cameraCanvas.stop();
		}
	}
	
	public void destroyApp(boolean unconditional){
		if(Display.getDisplay(this).getCurrent()==cameraCanvas){
			cameraCanvas.stop();
		}
	}
	
	private void exitRequested(){
		destroyApp(false);
		notifyDestroyed();
	}
	
	void cameraCanvasExit(){
		exitRequested();
	}
	
	void cameraCanvasCaptured(byte[]pngData){
		cameraCanvas.stop();
		displayCanvas.setImage(pngData);
		Display.getDisplay(this).setCurrent(displayCanvas);
	}
	
	void displayCanvasBack(){
		Display.getDisplay(this).setCurrent(cameraCanvas);
		cameraCanvas.start();
	}
}

