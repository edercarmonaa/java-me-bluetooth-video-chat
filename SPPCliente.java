import javax.microedition.lcdui.*;
public class SPPCliente extends List{
    public SPPCliente(){
        super("Cliente SPP",List.EXCLUSIVE);
        addCommand(new Command("Busqueda",Command.SCREEN, 1));
        addCommand(new Command("Enviar",Command.SCREEN, 1));
        addCommand(new Command("Salir",Command.EXIT, 1));
        this.setCommandListener(SPPClienteMIDlet.SPPc);
    }

    //Este metodo se encarga de limpiar la pantalla y de mostrar un mensaje

    public void escribirMensaje(String str){
        while(this.size() > 0) {
            delete(0);
        }
        append(str,null);
    }

    //Este metodo muestra los "friendly names" de los dispositivos remotos

    public void mostrarDispositivos(){
        while(this.size() > 0) {
            delete(0);
        }
        if(SPPClienteMIDlet.dispositivos_encontrados.size()>0){
            for(int i=0;i<SPPClienteMIDlet.dispositivos_encontrados.size();i++){
                try{
                    RemoteDevice dispositivoRemoto = (RemoteDevice)
                    SPPClienteMIDlet.dispositivos_encontrados.elementAt(i);
                    append(dispositivoRemoto.getFriendlyName(false),null);
                }catch(Exception e){
                    System.out.println("Se ha producido una excepcion");
                }
            }
            }
        else append("Pulse Busqueda",null);
    }
}

