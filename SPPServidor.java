import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.bluetooth.*;
import java.io.*;
public class SPPServidor extends List implements Runnable{

    //Objetos Bluetooth necesarios
    public LocalDevice dispositivoLocal;
    public DiscoveryAgent da;
    public boolean fin = false;
    public StreamConnectionNotifier servidor;

    //Constructor
    public SPPServidor(){
        super("Servidor SPP",List.EXCLUSIVE);
        addCommand(new Command("Salir",Command.EXIT,1));
        setCommandListener(SPPServidorMIDlet.SPPs);
    }

    //Este metodo se encarga de dar un aviso de alarma cuando se produce una excepcion
    public void mostrarAlarma(Exception e, Screen s, int tipo){
        Alert alerta;
        if(tipo == 0){
            alerta = new Alert("Excepcion:","Se ha producido la excepcion "+e.getClass().getName(), null,
            AlertType.ERROR);
        }else{
            alerta = new Alert("Error:","No ha seleccionado un dispositivo ", null, AlertType.ERROR);
        }
        alerta.setTimeout(Alert.FOREVER);
        SPPServidorMIDlet.display.setCurrent(alerta,s);
    }

    //Este metodo se va a encargar de inicializar el servidor
    public void inicializar(){
        try{
            dispositivoLocal = LocalDevice.getLocalDevice();
            dispositivoLocal.setDiscoverable(DiscoveryAgent.GIAC);
            //Lanzamos un hilo servidor (solo aceptara un cliente)
            Thread hilo = new Thread(this);
            hilo.start();
        }
        catch(BluetoothStateException be){
            System.out.println("Se ha producido un error al inicializar el hilo servidor");
        }
    }
    public void run(){
        //Le damos un nombre a nuestra aplicacion
        String nombre = "Ejemplo SPP";
        //Definimos un UUID unico para este servicio. Elegimos uno a nuestro gusto.
        UUID uuid = new UUID(0xABCD);
        servidor = null;//Similar a un socket servidor
        StreamConnection sc = null;//Similar a un socket cliente
        //Intentamos crear una conexion, usando SPP
        try{
            servidor = (StreamConnectionNotifier)Connector.open("btspp://localhost:"+uuid.toString()+";name="+nombre);
            //Obtenemos el service record
            ServiceRecord rec = dispositivoLocal.getRecord(servidor);
            //Rellenamos el BluetoothProfileDescriptionList usando el SerialPort version 1
            DataElement e1 = new DataElement(DataElement.DATSEQ);
            DataElement e2 = new DataElement(DataElement.DATSEQ);
            e2.addElement(new DataElement(DataElement.UUID,new UUID(0x1101)));
            //agregamos el puerto serie
            e2.addElement(new DataElement(DataElement.INT_8,1));//version 1
            e1.addElement(e2);
            //agregamos al service record el BluetoothProfileDescriptionList
            rec.setAttributeValue(0x0009,e1);
        }
        catch(Exception e){
            System.out.println("Se ha producido un error al lanzar el hilo servidor");
            mostrarAlarma(e, this,0);
            return;
        }
        while(!fin){
            try{
                append("Esperando mensaje:",null);
                //Aceptamos conexiones del cliente y obtenemos el objeto remoto
                sc = servidor.acceptAndOpen();
                RemoteDevice rd = RemoteDevice.getRemoteDevice(sc);
                //Obtenemos el input stream del objeto remoto
                DataInputStream in = sc.openDataInputStream();
                //Leemos el mensaje y lo mostramos por pantalla
                append(in.readUTF(),null);
                //Cerramos la conexion
                sc.close();
            }
            catch(Exception e){
                mostrarAlarma(e,this, 0);
                return;
            }
        }
    }
}
