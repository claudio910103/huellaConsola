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
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorListener;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusListener;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorListener;
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
		lector.addDataListener(new DPFPDataListener() {
			
			@Override
			public void dataAcquired(DPFPDataEvent arg0) {
				EnviarTexto("Huella Capturada");
				
			}
		});
		
		lector.addReaderStatusListener(new DPFPReaderStatusListener() {
			
			@Override
			public void readerDisconnected(DPFPReaderStatusEvent arg0) {
				EnviarTexto("Sensor desconectado o inactivo");
				
			}
			
			@Override
			public void readerConnected(DPFPReaderStatusEvent arg0) {
				EnviarTexto("Sensor activo");
				
			}
		});
		
		lector.addSensorListener(new DPFPSensorListener() {
			
			@Override
			public void imageAcquired(DPFPSensorEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void fingerTouched(DPFPSensorEvent e) {
				EnviarTexto("Dedo Colocado sobre el sensor");
				
			}
			
			@Override
			public void fingerGone(DPFPSensorEvent e) {
				EnviarTexto("Dedo quitado sobre el sensor");
				
			}
		});
		
		lector.addErrorListener(new DPFPErrorListener() {
			
			@Override
			public void exceptionCaught(DPFPErrorEvent e) {
				EnviarTexto("Error: " + e.toString());
				
			}
			
			@Override
			public void errorOccured(DPFPErrorEvent e) {
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
