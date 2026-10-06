import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.io.IOException;

class CameraCanvas extends Canvas implements CommandListener {
	
	private final CameraMIDlet midlet;
	private final Command salirCommand;
	private Command iniciarCommand=null;
	private Player player=null;
	private VideoControl videoControl=null;
	private boolean active=false;
	
	
	private String message1=null;
	private String message2=null;
	
	CameraCanvas(CameraMIDlet midlet){
		this.midlet=midlet;
		salirCommand= new Command("Salir", Command.EXIT, 1);
		addCommand(salirCommand);
		setCommandListener(this);
		
		try{
			player=Manager.createPlayer("capture://video");
			player.realize();
			videoControl=(VideoControl)(player.getControl("VideoControl"));
			if(videoControl==null){
				discardPlayer();
				message1="Unsupported:";
				message2="Can'tgetvideocontrol";
			}
			else{
				videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO,this);
				int canvasWidth=getWidth();
				int canvasHeight=getHeight();
				int displayWidth=videoControl.getDisplayWidth();
				int displayHeight=videoControl.getDisplayHeight();
				int x=(canvasWidth-displayWidth)/2;
				int y=(canvasHeight-displayHeight)/2;
				videoControl.setDisplayLocation(x,y);
				iniciarCommand= new Command ("Iniciar", Command.               SCREEN, 1);
				addCommand(iniciarCommand);
			}
		}
		
		catch(IOException ioe){
			discardPlayer();
			message1="IOException:";
			message2=ioe.getMessage();
		}
		
		catch(MediaException me){
			discardPlayer();
			message1="MediaException:";
			message2=me.getMessage();
		}
		catch(SecurityException se){
			discardPlayer();
			message1="SecurityException";
			message2=se.getMessage();
		}
	}
	
	
	private void discardPlayer(){
		if(player!=null){
			player.close();
			player=null;
		}
		videoControl=null;
	}
	
	public void paint(Graphics g){
		g.setColor(0xFFFFFFFF);
		g.fillRect(0,0,getWidth(),getHeight());
		if(message1!=null){
			g.setColor(0x00000000);
			g.drawString(message1,1,1,Graphics.TOP|Graphics.LEFT);
			g.drawString(message2,1,1+g.getFont().getHeight(),Graphics.TOP|Graphics.LEFT);
		}
	}
	
	synchronized void start(){
		if((player!=null)&&!active){
			try{
				player.start();
				videoControl.setVisible(true);
			}
			catch(MediaException me){
				message1="Mediaexception:";
				message2=me.getMessage();
			}
			catch(SecurityException se){
				message1="SecurityException";
				message2=se.getMessage();
			}
			active=true;
		}
	}
	
	synchronized void stop(){
		if((player!=null)&&active){
			try{
				videoControl.setVisible(false);
				player.stop();
			}
			catch(MediaException me){
				message1="MediaException:";
				message2=me.getMessage();
			}
			active=false;
		}
	}
	
	public void commandAction(Command c,Displayable d){
		if(c==salirCommand){
			midlet.cameraCanvasExit();
		}else if(c==iniciarCommand){
			takeSnapshot();
		}
	}
	
	public void keyPressed(int keyCode){
		if(getGameAction(keyCode)==FIRE){
			takeSnapshot();
		}
	}
	
	private void takeSnapshot(){
		if(player!=null){
			try{
				byte[]pngImage=videoControl.getSnapshot(null);
				midlet.cameraCanvasCaptured(pngImage);
			}
			catch(MediaException me){
				message1="MediaException:";
				message2=me.getMessage();
			}
		}
	}
}
