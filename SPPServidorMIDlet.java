import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
public class SPPServidorMIDlet extends MIDlet implements CommandListener {

    //Se crean las variables necesarias
    public static SPPServidorMIDlet SPPs= null;
    public static Display display;
    private SPPServidor s =null;

    //Constructor
    public SPPServidorMIDlet() {
    SPPs = this;
    }

    //Implementación  del ciclo de vida del MIDlet

    public void startApp() {
        display = Display.getDisplay(this);
        s = new SPPServidor();
        s.inicializar();
        display.setCurrent(s);
    }
    public void pauseApp() {

    }
    public void destroyApp(boolean unconditional) {

    }

    //Este metodo se encarga de las tareas necesarias para salir del MIDlet

    public void salir(){
        SPPs.destroyApp(true);
        SPPs.notifyDestroyed();
        SPPs = null;
    }

    //Se  maneja la accion del usuario

    public void commandAction(Command c, Displayable d) {
        if (d == s && c.getLabel().equals("Salir")) {
            //Salimos de la aplicacion
            try{
                s.fin = true;
                s.servidor.close();
            }
            catch(Exception e){}
            salir();
        }
    }
}
