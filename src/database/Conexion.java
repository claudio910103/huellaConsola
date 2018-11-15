package database;
/**
*
* @author claudio
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
	public String puerto="3306";
    public String nomServidor="104.248.237.128";
    public String db="easylab";
    public String user="itswa";
    public String pass="123456";
    Connection conn=null;
    
    public Connection conectar(){
        try {
            String ruta="jdbc:mysql://";
            String servidor=nomServidor+":"+puerto+"/"+db+"?useSSL=false&serverTimezone=UTC&";
            Class.forName("com.mysql.jdbc.Driver");
            String conexion = ruta+servidor+db+user+pass;
            System.out.println(conexion);
            conn = DriverManager.getConnection(ruta+servidor,user,pass);
            if(conn!=null){
                System.out.println("Conexión a "+ruta+servidor+" listo!!!");
            }else if(conn==null){
                throw new SQLException();
            }
        } catch (SQLException e) {            
            System.out.println(e.getMessage());
        }catch(ClassNotFoundException e){            
            System.out.println("Se produjo el sgte. error: "+e.getMessage());
        }catch(NullPointerException e){            
            System.out.println("Se produjo el sgte. error: "+e.getMessage());
        }finally{
            return conn;
        }
    }
    
    public void desconectar(){
        conn = null;
        System.out.println("Desconexion... listo!!!");
    }

}
