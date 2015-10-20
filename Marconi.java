import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Query;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.pi4j.gpio.extension.piface.PiFaceGpioProvider;
import com.pi4j.gpio.extension.piface.PiFacePin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.spi.SpiChannel;


public class Marconi implements Container {


	static PiFaceGpioProvider gpioProvider;
	static GpioController gpio;
	static GpioPinDigitalInput myInputs[];
	static GpioPinDigitalOutput myOutputs[];
	static ArrayList<GpioPinDigitalOutput> outputs = new ArrayList<GpioPinDigitalOutput>();
	static ArrayList<GpioPinDigitalInput> inputs = new ArrayList<GpioPinDigitalInput>();


	public static void main(String[] list) throws Exception {

		gpioProvider = new PiFaceGpioProvider(PiFaceGpioProvider.DEFAULT_ADDRESS,SpiChannel.CS0);
		gpio = GpioFactory.getInstance();

		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_00));
		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_01));
		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_02));
		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_03));
		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_04));
		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_05));
		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_06));
		inputs.add(gpio.provisionDigitalInputPin(gpioProvider, PiFacePin.INPUT_07));

		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_00));
		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_01));
		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_02));
		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_03));
		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_04));
		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_05));
		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_06));
		outputs.add(gpio.provisionDigitalOutputPin(gpioProvider, PiFacePin.OUTPUT_07));

		Container container = new Marconi();
		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(80);

		connection.connect(address);

		for (int count = 0; count < 8; count++) {
			setOutputPin(count, true);
		    Thread.sleep(1000);
            setOutputPin(count, false);
       	     Thread.sleep(1000);
        }
	}


	public void handle(Request request, Response response) {
		try {
			Query query = request.getQuery();
			String setOutput = query.get("setoutput"); 
			String getInput = query.get("getinput");
			String webinterface = query.get("web");
			
			PrintStream body = response.getPrintStream();
			long time = System.currentTimeMillis();
			response.setValue("Content-Type", "text/html");
			
			response.setValue("Server", "Marconi/1.0");
			response.setDate("Date", time);
			response.setDate("Last-Modified", time);
			
			if (webinterface != null) {
				body.println("<html><head><title>Marconi</title>");
				body.println("<h2>Marconi Webinterface</h2>");
				body.println("<table border=0><tr><th>Input</th>");
				//
				body.println("<td><form action = \"?getinput=0&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 0\"></form></td>");
				body.println("<td><form action = \"?getinput=1&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 1\"></form></td>");
				body.println("<td><form action = \"?getinput=2&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 2\"></form></td>");
				body.println("<td><form action = \"?getinput=3&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 3\"></form></td>");
				body.println("<td><form action = \"?getinput=4&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 4\"></form></td>");
				body.println("<td><form action = \"?getinput=5&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 5\"></form></td>");
				body.println("<td><form action = \"?getinput=6&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 6\"></form></td>");
				body.println("<td><form action = \"?getinput=7&web=\" method = \"post\"><input type = \"submit\" value=\"Get input 7\"></form></td>");
				body.println("</tr><tr><th>Output</th>");
				
				
				body.println("<td><form action = \"?setoutput=0on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 0 on\"></form>");
				body.println("<form action = \"?setoutput=0off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 0 off\"></form></td>");
				body.println("<td><form action = \"?setoutput=1on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 1 on\"></form>");
				body.println("<form action = \"?setoutput=1off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 1 off\"></form></td>");				
				body.println("<td><form action = \"?setoutput=2on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 2 on\"></form>");
				body.println("<form action = \"?setoutput=2off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 2 off\"></form></td>");				
				body.println("<td><form action = \"?setoutput=3on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 3 on\"></form>");
				body.println("<form action = \"?setoutput=3off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 3 off\"></form></td>");				
				body.println("<td><form action = \"?setoutput=4on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 4 on\"></form>");
				body.println("<form action = \"?setoutput=4off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 4 off\"></form></td>");
				body.println("<td><form action = \"?setoutput=5on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 5 on\"></form>");
				body.println("<form action = \"?setoutput=5off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 5 off\"></form></td>");
				body.println("<td><form action = \"?setoutput=6on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 6 on\"></form>");
				body.println("<form action = \"?setoutput=6off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 6 off\"></form></td>");
				body.println("<td><form action = \"?setoutput=7on&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 7 on\"></form>");
				body.println("<form action = \"?setoutput=7off&web=\" method = \"post\"><input type = \"submit\" value=\"Set output 7 off\"></form></td>");
				//
				body.println("</tr></table></body></html>");
				if (getInput != null) body.print("Selected input is: ");
			}

			
			if (getInput != null) {
				switch(getInput) {
				case "0": body.println(inputs.get(0).getState()); break;
				case "1": body.println(inputs.get(1).getState()); break;
				case "2": body.println(inputs.get(2).getState()); break;
				case "3": body.println(inputs.get(3).getState()); break;
				case "4": body.println(inputs.get(4).getState()); break;
				case "5": body.println(inputs.get(5).getState()); break;
				case "6": body.println(inputs.get(6).getState()); break;
				case "7": body.println(inputs.get(7).getState()); break;
				default: body.println("error"); break;
				}
					
			}
			if (setOutput != null) {
				switch(setOutput) {
				case "0on": setOutputPin(0,true); break;
				case "0off": setOutputPin(0,false); break;
				case "1on": setOutputPin(1,true); break;
				case "1off": setOutputPin(1,false); break;
				case "2on": setOutputPin(2,true); break;
				case "2off": setOutputPin(2,false); break;
				case "3on": setOutputPin(3,true); break;
				case "3off": setOutputPin(3,false); break;
				case "4on": setOutputPin(4,true); break;
				case "4off": setOutputPin(4,false); break;
				case "5on": setOutputPin(5,true); break;
				case "5off": setOutputPin(5,false); break;
				case "6on": setOutputPin(6,true); break;
				case "6off": setOutputPin(6,false); break;
				case "7on": setOutputPin(7,true); break;
				case "7off": setOutputPin(7,false); break;
				default: body.println("error"); break;
				}
			}
			
			if(setOutput==null && getInput==null && webinterface == null) {
				body.println("Are you a human? If so, click <a href=\"?web=\">here</a>");
			}
			
			body.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	private static void setOutputPin(int port, boolean state) throws InterruptedException {
		if(state) outputs.get(port).high(); else outputs.get(port).low();	
	} 
}