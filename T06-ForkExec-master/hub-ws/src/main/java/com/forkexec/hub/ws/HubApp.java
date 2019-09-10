package com.forkexec.hub.ws;


/**
 * The application is where the service starts running. The program arguments
 * are processed here. Other configurations can also be done here.
 */
public class HubApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + HubApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}

		String uddiURL = null;
		String wsName = null;
		String wsURL = null;

		// Create server implementation object, according to options
		HubEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new HubEndpointManager(wsURL);

		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			endpoint = new HubEndpointManager(uddiURL, wsName, wsURL);

		}
		
		try {
			endpoint.start();
			
			
			/*for (String r: endpoint.getRestaurants()) {
				System.out.print(r);
				if (wsURL != null) {
					System.out.printf("Creating client for server at %s%n", wsURL);
					rest = new RestaurantClient(wsURL);
				} else if (uddiURL != null) {
					System.out.printf("Creating client using UDDI at %s for server with name %s%n", uddiURL, wsName);
					rest = new RestaurantClient(uddiURL, wsName);
				}
				System.out.println("Invoke ping()...");
				String result = rest.ctrlPing("client");
				System.out.print("Result: ");
				System.out.println(result);
			}*/

		//System.out.printf(sb.toString());
			endpoint.awaitConnections();
			
		} finally {
			endpoint.stop();
		}

	}


}