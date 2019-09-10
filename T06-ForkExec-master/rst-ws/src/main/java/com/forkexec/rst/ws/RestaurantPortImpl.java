package com.forkexec.rst.ws;

import java.util.*;

import javax.jws.WebService;

import com.forkexec.rst.domain.Restaurant;


/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.rst.ws.RestaurantPortType",
            wsdlLocation = "RestaurantService.wsdl",
            name ="RestaurantWebService",
            portName = "RestaurantPort",
            targetNamespace="http://ws.rst.forkexec.com/",
            serviceName = "RestaurantService"
)
public class RestaurantPortImpl implements RestaurantPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private RestaurantEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public RestaurantPortImpl(RestaurantEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	// Main operations -------------------------------------------------------
	
	@Override
	public Menu getMenu(MenuId menuId) throws BadMenuIdFault_Exception {
		Restaurant res = Restaurant.getInstance();
		com.forkexec.rst.domain.Menu menu = res.getMenu(menuId.getId());
		if (menu==null)
			throwBadMenuId("Bad Menu Id");

		Menu m = newMenuView(menu);
		return m;
		
	}
	
	@Override
	public List<Menu> searchMenus(String descriptionText) throws BadTextFault_Exception {
		if (descriptionText.indexOf(" ")!=-1) 
			throwBadText("Description contains spaces");
		else if  (descriptionText.length() ==0)
			throwBadText("Empty description");

		Restaurant res = Restaurant.getInstance();
		List<Menu> menus = new ArrayList<Menu>();

		for(com.forkexec.rst.domain.Menu m : res.searchMenus(descriptionText)) {
			menus.add(newMenuView(m));
		}
		return menus;
	}
	

	@Override
	public MenuOrder orderMenu(MenuId arg0, int arg1)
			throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
		// TODO Auto-generated method stub
	    Restaurant res = Restaurant.getInstance();

	    com.forkexec.rst.domain.Menu menu = res.getMenu(arg0.getId());
		if (menu==null)
			throwBadMenuId("Bad Menu Id");
		else if(arg1<=0)
			throwBadQuantity("Quantity must be positive");
		else if (menu.getQuantity()<arg1)
			throwInsufficientQuantity("Insufficient Quantity");

	    return newMenuOrderView(res.orderMenu(arg0.getId(), arg1));

	}

	

	// Control operations ----------------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the park does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Restaurant";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		Restaurant res = Restaurant.getInstance();
		res.clearAll();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInit(List<MenuInit> initialMenus) throws BadInitFault_Exception {
		Restaurant res = Restaurant.getInstance();
		for(MenuInit menuInit:initialMenus) {
			
			Menu menu = menuInit.getMenu();
			com.forkexec.rst.domain.Menu menuAdd = new com.forkexec.rst.domain.Menu();

			menuAdd.setId(menu.getId().getId());
	    	menuAdd.setEntree(menu.getEntree());
	    	menuAdd.setPlate(menu.getPlate());
	    	menuAdd.setDessert(menu.getDessert());
	    	menuAdd.setPrice(menu.getPrice());
	    	menuAdd.setPreparationTime(menu.getPreparationTime());
	    	menuAdd.setQuantity(menuInit.getQuantity());
	    	res.addMenu(menuAdd);
		}
	}

	// View helpers ----------------------------------------------------------

	private Menu newMenuView(com.forkexec.rst.domain.Menu menu) {
		Menu view = new Menu();
		MenuId id = new MenuId();
		id.setId(menu.getId());

		view.setId(id);
    	view.setEntree(menu.getEntree());
    	view.setPlate(menu.getPlate());
    	view.setDessert(menu.getDessert());
    	view.setPrice(menu.getPrice());
    	view.setPreparationTime(menu.getPreparationTime());

		return view;
	}

	private MenuOrder newMenuOrderView(com.forkexec.rst.domain.MenuOrder menu) {
		MenuOrder view = new MenuOrder();
		MenuOrderId id = new MenuOrderId();
		MenuId menuId = new MenuId();
		id.setId(menu.getId());
		menuId.setId(menu.getMenuId());

		view.setId(id);
    	view.setMenuId(menuId);
    	view.setMenuQuantity(menu.getMenuQuantity());

		return view;
	}

	
	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInitFault_Exception {
		BadInitFault faultInfo = new BadInitFault();
		faultInfo.message = message;
		throw new BadInitFault_Exception(message, faultInfo);
	}

	private void throwBadText(final String message) throws BadTextFault_Exception {
		BadTextFault faultInfo = new BadTextFault();
		faultInfo.message = message;
		throw new BadTextFault_Exception(message, faultInfo);
	}

	private void throwBadMenuId(final String message) throws BadMenuIdFault_Exception {
		BadMenuIdFault faultInfo = new BadMenuIdFault();
		faultInfo.message = message;
		throw new BadMenuIdFault_Exception(message, faultInfo);
	}

	private void throwBadQuantity(final String message) throws BadQuantityFault_Exception {
		BadQuantityFault faultInfo = new BadQuantityFault();
		faultInfo.message = message;
		throw new BadQuantityFault_Exception(message, faultInfo);
	}

	private void throwInsufficientQuantity(final String message) throws InsufficientQuantityFault_Exception {
		InsufficientQuantityFault faultInfo = new InsufficientQuantityFault();
		faultInfo.message = message;
		throw new InsufficientQuantityFault_Exception(message, faultInfo);
	}
}
