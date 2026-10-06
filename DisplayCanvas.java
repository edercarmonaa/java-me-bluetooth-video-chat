import javax.microedition.lcdui.*;

class DisplayCanvas extends Canvas implements CommandListener{
	
	private final CameraMIDlet midlet;
	private Image image=null;
	
	DisplayCanvas(CameraMIDlet midlet){
		this.midlet=midlet;
		addCommand(new Command("Atras",Command.BACK,1));
		setCommandListener(this);
	}
	
	public void paint(Graphics g){
		g.setColor(0x0000FFFF);//cyan
		g.fillRect(0,0,getWidth(),getHeight());
		if(image!=null){
			g.drawImage(image,getWidth()/2,getHeight()/2,Graphics.VCENTER|Graphics.HCENTER);
		}
	}
	
	void setImage(byte[] pngImage){
		image=Image.createImage(pngImage,0,pngImage.length);
	}
	
	public void commandAction(Command c,Displayable d){
		midlet.displayCanvasBack();
	}
}

