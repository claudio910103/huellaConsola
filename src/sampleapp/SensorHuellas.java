package sampleapp;

/**
*
* @author claudio
*/
import database.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.verification.DPFPVerification;

public class SensorHuellas {

	private DPFPCapture lector = DPFPGlobal.getCaptureFactory().createCapture();
	private DPFPVerification verificador = DPFPGlobal.getVerificationFactory().createVerification();
	private DPFPEnrollment reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
	private DPFPTemplate template;
	public static String TEMPLATE_PROPERTY = "template";
	
	Conexion con=new Conexion();
	
	protected void iniciar() {
		lector.addDataListener(new DPFPDataAdapter() {
			public void dataAcquired(final DPFPDataEvent e) {
				EnviarTexto("Huella Capturada");
			}
		});
		
		lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
			public void readerConnected(DPFPReaderStatusEvent e) {
				EnviarTexto("Sensor activo");
			}
			public void readerDisconnected(DPFPReaderStatusEvent e) {
				EnviarTexto("Sensor desconectado o inactivo");
			}
		});
		
		lector.addSensorListener(new DPFPSensorAdapter() {
			public void fingerTouched(final DPFPSensorEvent e){
				EnviarTexto("Dedo Colocado sobre el sensor");
			}
			public void fingerGone(final DPFPSensorEvent e){
				EnviarTexto("Dedo quitado del sensor");
			}
		});
		
		lector.addErrorListener(new DPFPErrorAdapter() {
			@SuppressWarnings("unused")
			public void errorReader(final DPFPErrorAdapter e){
				EnviarTexto("Error: " + e.toString());
			}
		});
	}
	
	public void EnviarTexto(String string){        
       System.out.println(string);
    }
	
	public void estadoHuellas(){
        EnviarTexto("Muestra de Huellas necesarias para guardar el template" +
        reclutador.getFeaturesNeeded());
    }
	
	public void start(){
        lector.startCapture();
        EnviarTexto("Utilizando lector de huella");
    }
	
    public void stop(){
        lector.stopCapture();
        EnviarTexto("No se esta utilizando el lector");
    }
    /*
    public void setTemplate(DPFPTemplate template){
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }*/
    
    public void identificarHuella() {
    	try {
    		Connection c = con.conectar();
    		PreparedStatement consultaDB = c.prepareStatement("select Huella, UsuarioID  from HUELLAS  order by FechaAlta desc ");
    		ResultSet rs = consultaDB.executeQuery();
    		boolean isNoCapturada = true;
    		while (rs.next()) {
    			byte templateBuffer[]=rs.getBytes("Huella");
                String usuarioID = rs.getString("UsuarioID");
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //setTemplate(referenceTemplate);
    		}
    		
    		if(isNoCapturada){
                System.out.println("Huella no reconocida");
            }
    		
    	} catch (SQLException e) {
            System.err.println("Error al identificar huella dactilar. "+e.getMessage());            
        }finally{
            con.desconectar();
        }
    }
    
    
    public void iniciarAplicacion() {
    	EnviarTexto("Aplicacion iniciada...");
    	iniciar();
    	start();
    	estadoHuellas();
    }
    
    public static void main (String[] args) {
    	SensorHuellas se = new SensorHuellas(); 
    	se.iniciarAplicacion();
    }
}
