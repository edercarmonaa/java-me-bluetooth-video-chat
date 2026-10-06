import javax.microedition.lcdui.*;
public class Mensaje extends TextBox{
    public Mensaje(){
        super("Introducir mensaje","Hola Mundo",50,TextField.ANY);
        addCommand(new Command("OK",Command.SCREEN, 1));
        this.setCommandListener(SPPClienteMIDlet.SPPc);
    }
}

