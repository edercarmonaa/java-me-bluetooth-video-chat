import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.bluetooth.*;
import java.io.*;
import java.util.*;

public class SPPClienteMIDlet extends MIDlet implements CommandListener {

    //Creamos las variables necesarias
    public static SPPClienteMIDlet SPPc= null;
    public static Display display;
    private SPPCliente c =null;
    private Mensaje msg = null;

    //Objetos Bluetooth necesarios
    public LocalDevice dispositivoLocal;
    public DiscoveryAgent da;

    //Lista de dispositivos y servicios encontrados
    public static Vector dispositivos_encontrados = new Vector();
    public static Vector servicios_encontrados = new Vector();
    public static int dispositivo_seleccionado = -1;

    //Constructor
    public SPPClienteMIDlet(){
        SPPc = this;
    }

    //Implementamos el ciclo de vida del MIDlet
    public void startApp() {
        display = Display.getDisplay(this);
        c = new SPPCliente();
        msg = new Mensaje();
        //Mostramos la lista de dispositivos(vacia al principio)

        c.mostrarDispositivos();
        display.setCurrent(c);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    //Este metodo se encarga de las tareas necesarias para salir del MIDlet
    public static void salir(){
        SPPc.destroyApp(true);
        SPPc.notifyDestroyed();
        SPPc = null;
    }

    //Este metodo se encarga de dar un aviso de alarma cuando se produce una excepcion

    public void mostrarAlarma(Exception e, Screen s, int tipo){
        Alert alerta=null;
        if(tipo == 0){
            alerta = new Alert("Excepcion","Se ha producido la excepcion "+e.getClass().getName(), null,
            AlertType.ERROR);
        }
        else if(tipo==1){
            alerta = new Alert("Error","No ha seleccionado un dispositivo ", null, AlertType.ERROR);
        }
        else if(tipo==2){
            alerta = new Alert("Informacion","El mensaje ha sido enviado ", null, AlertType.INFO);
        }
        alerta.setTimeout(Alert.FOREVER);
        display.setCurrent(alerta,s);
    }

    //Manejamos la accion del usuario

    public void commandAction(Command co, Displayable d){
        if(d==c && co.getLabel().equals("Busqueda")){
            //Limpiamos la lista
            dispositivos_encontrados.removeAllElements();
            servicios_encontrados.removeAllElements();
            try{
                dispositivoLocal = LocalDevice.getLocalDevice();
                dispositivoLocal.setDiscoverable(DiscoveryAgent.GIAC);
                da = dispositivoLocal.getDiscoveryAgent();
                da.startInquiry(DiscoveryAgent.GIAC,new Listener());
                c.escribirMensaje("Por favor espere...");
            }
            catch(BluetoothStateException be){
                mostrarAlarma(be,c, 0);
            }
        }
        else if(d==c && co.getLabel().equals("Enviar")){
            dispositivo_seleccionado = c.getSelectedIndex();
            //Nos aseguramos de que el usuario selecciono un dispositivo
            if(dispositivo_seleccionado == -1 || dispositivo_seleccionado >= dispositivos_encontrados.size()){
                mostrarAlarma(null, c,1);
                return;
            }
            display.setCurrent(msg);
        }
        else if(d==c && co.getLabel().equals("Salir")){
            salir();
        }
        else if(d==msg && co.getLabel().equals("OK")){
            servicios_encontrados.removeAllElements();
            //Buscamos el servicio de puerto serie en el dispositivo seleccionado
            RemoteDevice dispositivo_remoto =(RemoteDevice)dispositivos_encontrados.elementAt
            (dispositivo_seleccionado);
            try{
                //Buscamos en el puerto serie 0x1101
                da.searchServices(null,new UUID[]{new UUID(0x1101)},dispositivo_remoto,new Listener());
            }
            catch(BluetoothStateException be){
                mostrarAlarma(be, c, 0);
            }
        }
    }

    //Este metodo se va a encargar de enviar un mensaje al primer ServiceRecord usando el Serial Port Profile
    public void enviarMensaje(String msg){
        ServiceRecord sr = (ServiceRecord)servicios_encontrados.elementAt(0);
        //Obtenemos la URL asociada a este servicio en el dispositivo remoto
        String URL = sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
        try{
            //Obtenemos la conexio y el stream de este servicio
            StreamConnection con = (StreamConnection)Connector.open(URL);
            DataOutputStream out = con.openDataOutputStream();
            //Escribimos datos en el stream
            out.writeUTF(msg);
            out.flush();
            //Cerramos la conexion
            out.close();
            con.close();
            mostrarAlarma(null, c, 2);
        }
        catch(Exception e){
            mostrarAlarma(e, c, 0);
        }
    }
    public class Listener implements DiscoveryListener{

    //Implementamos los metodos del interfaz DiscoveryListener
    public void deviceDiscovered(RemoteDevice dispositivoRemoto, DeviceClass clase){
        System.out.println("Se ha encontrado un dspositivo Bluetooth");
        dispositivos_encontrados.addElement(dispositivoRemoto);
    }

    public void inquiryCompleted(int completado){
        System.out.println("Se ha completado la busqueda de dispositivos");
        if(dispositivos_encontrados.size()==0){
            Alert alerta = new Alert("Problema","No se ha encontrado dispositivos",null, AlertType.INFO);
            alerta.setTimeout(3000);
            c.escribirMensaje("Presione descubrir dispositivos");
            display.setCurrent(alerta,c);
        }
        else{
            c.mostrarDispositivos();
            display.setCurrent(c);
        }
    }

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord){
        System.out.println("Se ha encontrado un servicio remoto");
        for(int i=0;i<servRecord.length;i++){
            ServiceRecord record = servRecord[i];
            servicios_encontrados.addElement(record);
        }
    }

    public void serviceSearchCompleted(int transID, int respCode){
        System.out.println("Terminada la busqueda de servicios");
        //Si encontramos un servicio, lo usamos para mandar el mensaje(todos los servicios que hemos buscado son de puerto serie)
        if(servicios_encontrados.size()>0){
            enviarMensaje(msg.getString());
        }
        else{
            //Si no encontramos ningun servicio de puerto serie
            c.mostrarDispositivos();
            display.setCurrent(c);
        }
    }
}
}
