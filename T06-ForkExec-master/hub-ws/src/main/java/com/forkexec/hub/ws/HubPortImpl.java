package com.forkexec.hub.ws;

import java.util.List;

import javax.jws.WebService;

import com.forkexec.hub.domain.*;

import com.forkexec.rst.ws.cli.*;
import com.forkexec.rst.ws.*;
import com.forkexec.pts.ws.cli.*;
import com.forkexec.cc.ws.cli.*;
import java.util.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

import java.util.Collections.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.hub.ws.HubPortType",
            wsdlLocation = "HubService.wsdl",
            name ="HubWebService",
            portName = "HubPort",
            targetNamespace="http://ws.hub.forkexec.com/",
            serviceName = "HubService"
)
public class HubPortImpl implements HubPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private HubEndpointManager endpointManager;

	private PointsManager pointsManager;

	/** Constructor receives a reference to the endpoint manager. */
	public HubPortImpl(HubEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		pointsManager = new PointsManager(endpointManager);
	}
	
	// Main operations -------------------------------------------------------
	
	@Override
	public void activateAccount(String userId) throws InvalidUserIdFault_Exception {
		// TODO Auto-generated method stub
        if(userId == null || userId.trim().length() == 0 || !userId.matches("\\w+(\\.?\\w)*@\\w+(\\.?\\w)*")) {
            throwInvalidUserId("Invalid email.");
        }
		//VERFICAR EMAIL
		Hub hub = Hub.getInstance();

		Collection<UDDIRecord> pointsURLs = getPoints();
			

		for(UDDIRecord ptsURL : pointsURLs) {
			try {
				PointsClient client = new PointsClient(ptsURL.getUrl());
				client.activateUser(userId);
			} catch(Exception e) {
				throwInvalidUserId("Email already exists");
			}
			
		}

		hub.addCart(userId);


		
	}

	@Override
	public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
			throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
		// TODO Auto-generated method stub
        if(userId == null || userId.trim().length() == 0 || !userId.matches("\\w+(\\.?\\w)*@\\w+(\\.?\\w)*")) {
            throwInvalidUserId("Invalid email.");
        }


        if(moneyToAdd<=0){
        	throwInvalidMoney("Money should be positive.");
        }
        boolean valid = false;
        try {
        		for(UDDIRecord ccURL : getCC()) {
        			CreditCardClient client = new CreditCardClient(ccURL.getUrl());
					valid = client.validateNumber(creditCardNumber);
        		}
			
		} catch(Exception e) {
			throwInvalidCreditCard("Email does not exist");
		}

        if(creditCardNumber == null || valid == true || creditCardNumber.matches("[0-9]+")== false || creditCardNumber.length()!= 16){
        	throwInvalidCreditCard("Invalid credit card number.");
        }



		pointsManager.addPoints(userId, moneyToAdd);
		
	}
	
	
	@Override
	public List<Food> searchDeal(String description) throws InvalidTextFault_Exception {
		// TODO return lowest price menus first
		if(description == null || description.length()==0 || description.matches("[ ]+")==true){
			throwInvalidText("Invalid description.");
		}

		try{
		List<Food> foodMenus = searchMenu(description);
		foodMenus.sort(Comparator.comparingInt(f -> f.getPrice()));
		Collections.reverse(foodMenus);
		return foodMenus;

		}catch(Exception e){
			throwInvalidText("Description does not exist");
		}
		return new ArrayList<Food>();
	}
	
	@Override
	public List<Food> searchHungry(String description) throws InvalidTextFault_Exception {
		// TODO return lowest preparation time first
		if(description == null || description.length()==0 || description.matches("[ ]+")==true){
			throwInvalidText("Invalid description.");
		}
		try{
		List<Food> foodMenus = searchMenu(description);
		foodMenus.sort(Comparator.comparingInt(f -> f.getPreparationTime()));
		Collections.reverse(foodMenus);
		return foodMenus;
		}catch(Exception e){
			throwInvalidText("Description does not exist");
		}

		return new ArrayList<Food>();
	}

	
	@Override
	public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
			throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		// TODO 
        if(userId == null || userId.trim().length() == 0 || !userId.matches("\\w+(\\.?\\w)*@\\w+(\\.?\\w)*")) {
            throwInvalidUserId("Invalid email.");
        }

		Hub hub = Hub.getInstance();

		if(hub.getCart(userId)==null){
			throwInvalidUserId("User does not exists");
		}

		try{
			getFood(foodId);
		}catch(Exception e){
			throwInvalidFoodId("Food does not exist in any restaurant");
		}

		Collection<UDDIRecord> restaurantsURLs = getRestaurants();

		for(UDDIRecord res : restaurantsURLs) {
			try{
				if(foodId.getRestaurantId().equals(res.getOrgName())){
					RestaurantClient client = new RestaurantClient(res.getUrl());
					MenuId menuId = new MenuId();
					menuId.setId(foodId.getMenuId());
					client.orderMenu(menuId, foodQuantity);
				}
			}catch(Exception e){
					throwInvalidFoodQuantity("Not enough food in restaurant");
			}

			
		}



		com.forkexec.hub.domain.FoodId f =new com.forkexec.hub.domain.FoodId();
		f.setRestaurantId(foodId.getRestaurantId());
		f.setMenuId(foodId.getMenuId());

		hub.addFoodToCart(userId, f, foodQuantity);


	}

	@Override
	public void clearCart(String userId) throws InvalidUserIdFault_Exception {
        if(userId == null || userId.trim().length() == 0 || !userId.matches("\\w+(\\.?\\w)*@\\w+(\\.?\\w)*")) {
            throwInvalidUserId("Invalid email.");
        }

		Hub hub = Hub.getInstance();

		if(hub.getCart(userId)==null){
			throwInvalidUserId("User does not exists");
		}

		hub.clearCart(userId);
		// TODO 
		
	}

	@Override
	public FoodOrder orderCart(String userId)
			throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
		// TODO                																					gnfgfkgfgf
        if(userId == null || userId.trim().length() == 0 || !userId.matches("\\w+(\\.?\\w)*@\\w+(\\.?\\w)*")) {
            throwInvalidUserId("Invalid email.");
        }
/*
		Hub hub = Hub.getInstance();

		if(hub.getCart(userId)==null){
			throwInvalidUserId("User does not exists");
		}

		if(hub.getCart(userId).getFoods().size()<=0){
			throwEmptyCart("Cart is empty");
		}
		List<FoodOrderItem> carCont = new ArrayList<FoodOrderItem>(cartContents(userId));

		FoodOrder foodOrder = new FoodOrder();

		int totalPrice = 0;
		try{
			for (FoodOrderItem i: carCont ) {
				foodOrder.getItems().add(i);
				totalPrice+=getFood(i.getFoodId()).getPrice();
			}

			if(totalPrice > accountBalance(userId)){
				throwNotEnoughPoints("Not enough points");
			}

			pointsManager.spendPoints(userId, totalPrice);

			clearCart(userId);


			
			return foodOrder;
		}catch(Exception e){
			throwNotEnoughPoints("Not enough points");
		}*/

		int totalPrice = 50;
			if(totalPrice > accountBalance(userId)){
				throwNotEnoughPoints("Not enough points");
			}

		pointsManager.spendPoints(userId, totalPrice);


		return null;
	}

	@Override
	public int accountBalance(String userId) throws InvalidUserIdFault_Exception {
			
		int result=0;

		result = pointsManager.getBalance(userId);

		return result;
	}

	@Override
	public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception {
		Hub hub = Hub.getInstance();
		Collection<UDDIRecord> restaurantsURLs = getRestaurants();
		Food food = null;

		for(UDDIRecord res : restaurantsURLs) {
			try{
				if(foodId.getRestaurantId().equals(res.getOrgName())){
					RestaurantClient client = new RestaurantClient(res.getUrl());
					MenuId menuId = new MenuId();
					menuId.setId(foodId.getMenuId());
					food = newFoodView(client.getMenu(menuId), res.getOrgName());

				}
			}catch(Exception e){
				throwInvalidFoodId("Food does not exist in any restaurant");
			}
		}
			
		

		return food;

	}

	@Override
	public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception {
		// TODO
        if(userId == null || userId.trim().length() == 0 || !userId.matches("\\w+(\\.?\\w)*@\\w+(\\.?\\w)*")) {
            throwInvalidUserId("Invalid email.");
        }

		Hub hub = Hub.getInstance();

		if(hub.getCart(userId)==null){
			throwInvalidUserId("User does not exists");
		}

		List<FoodOrderItem> carCont = new ArrayList<FoodOrderItem>();
		List<FoodCart> foodCarts = new ArrayList<FoodCart>(hub.getFoodCarts(userId));

		for (FoodCart ft: foodCarts) {
			carCont.add(newFoodOrderItemView(ft));
		}

		return carCont;
	}

	// Control operations ----------------------------------------------------

	/** Diagnostic operation to check if service is running. */

	public Collection<UDDIRecord> getCC() {

		Collection<UDDIRecord> records = null;

		try {
			// verificacao
			records = endpointManager.getUddiNaming().listRecords("CC");

			
		} catch(UDDINamingException e) {
			System.out.println("Failed to contact the UDDI server:"+e.getMessage()+" ("+e.getClass().getName()+")\n");
		}
				
		return records;
	}

	public Collection<UDDIRecord> getRestaurants() {

		Collection<UDDIRecord> records = null;

		try {
			// verificacao
			records = endpointManager.getUddiNaming().listRecords("T06_Restaurant"+"%");

			
		} catch(UDDINamingException e) {
			System.out.println("Failed to contact the UDDI server:"+e.getMessage()+" ("+e.getClass().getName()+")\n");
		}
				
		return records;
	}

	public Collection<UDDIRecord> getPoints() {
		Collection<UDDIRecord> pointsURLs = null;
		try {
			// verificacao
			pointsURLs = endpointManager.getUddiNaming().listRecords("T06_Points" + "%");

			
		} catch(UDDINamingException e) {
			System.out.println("Failed to contact the UDDI server:"+e.getMessage()+" ("+e.getClass().getName()+")\n");
		}

		return pointsURLs;
	}


	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the service does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Hub";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		
		Collection<UDDIRecord> restaurantsURLs = getRestaurants();
		builder.append("\nFound "+restaurantsURLs.size()+" restaurants on UDDI.\n");
		
		for(UDDIRecord resURL : restaurantsURLs) {
			try {
				RestaurantClient client = new RestaurantClient(resURL.getUrl());

				String supplierPingResult = client.ctrlPing(endpointManager.getWsName());
				builder.append(supplierPingResult+"\n");
			} catch(Exception e) {
				builder.append(e.getMessage()+" ("+e.getClass().getName()+")\n");
			}
			
		}
		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		Hub hub = Hub.getInstance();
		hub.clearAllCarts();
		Collection<UDDIRecord> restaurantsURLs = getRestaurants();

		for(UDDIRecord res : restaurantsURLs) {
			try {
				RestaurantClient client = new RestaurantClient(res.getUrl());
				client.ctrlClear();
			} catch(Exception e) {
			}

		}

		Collection<UDDIRecord> pointsURLs = getPoints();
			

		for(UDDIRecord ptsURL : pointsURLs) {
			try {
				PointsClient client = new PointsClient(ptsURL.getUrl());
				client.ctrlClear();
			} catch(Exception e) {
			}
			
		}

	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {
		Collection<UDDIRecord> restaurantsURLs = getRestaurants();
		List<MenuInit> initialMenus = new ArrayList<MenuInit>();

		for(UDDIRecord res : restaurantsURLs) {
			try {
				initialMenus = new ArrayList<MenuInit>();
				RestaurantClient client = new RestaurantClient(res.getUrl());

				for(FoodInit f: initialFoods) {
					if (f.getFood().getId().getRestaurantId().equals(res.getOrgName())) {
						initialMenus.add(newMenuInitView(f));
					}
				}
				client.ctrlInit(initialMenus);
				
			} catch(Exception e) {
				
			}
			
		}
		// TODO Auto-generated method stub
	}

	private List<Food> searchMenu(String description) throws InvalidTextFault_Exception {
		// TODO return lowest preparation time first
		Collection<UDDIRecord> restaurantsURLs = getRestaurants();
		List<Food> foodMenus = new ArrayList<Food>();

		for(UDDIRecord res : restaurantsURLs) {
			try {
				RestaurantClient client = new RestaurantClient(res.getUrl());


				List<Menu> restaurantMenus = client.searchMenus(description);

				for(Menu m: restaurantMenus) {
					foodMenus.add(newFoodView(m,res.getOrgName()));
				}
				

			} catch(Exception e) {
				
			}
			
		}

		return foodMenus;
	}

	private MenuInit newMenuInitView(FoodInit food) {
		MenuInit p = new MenuInit();
		Menu men = new Menu();
		MenuId menId = new MenuId();

		Food f = food.getFood();
		p.setQuantity(food.getQuantity());

		menId.setId(f.getId().getMenuId());

		men.setId(menId);
    	men.setEntree(f.getEntree());
    	men.setPlate(f.getPlate());
    	men.setDessert(f.getDessert());
    	men.setPrice(f.getPrice());
    	men.setPreparationTime(f.getPreparationTime());
    	p.setMenu(men);
    	return p;
	}

	private Food newFoodView(Menu menu, String restaurantId) {
		Food food = new Food();
		FoodId foodId = new FoodId();

		foodId.setMenuId(menu.getId().getId());
		foodId.setRestaurantId(restaurantId);
		food.setId(foodId);

    	food.setEntree(menu.getEntree());
    	food.setPlate(menu.getPlate());
    	food.setDessert(menu.getDessert());
    	food.setPrice(menu.getPrice());
    	food.setPreparationTime(menu.getPreparationTime());
    	
    	return food;
	}

	private FoodOrderItem newFoodOrderItemView(FoodCart foodCart) {
		FoodOrderItem foodOI = new FoodOrderItem();

		FoodId f =new FoodId();
		f.setRestaurantId(foodCart.getFoodId().getRestaurantId());
		f.setMenuId(foodCart.getFoodId().getMenuId());

		foodOI.setFoodQuantity(foodCart.getQuantity());
		foodOI.setFoodId(f);

		return foodOI;
	}
	
	@Override
	public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
		// TODO Auto-generated method stub
		Collection<UDDIRecord> pointsURLs = getPoints();
			

		for(UDDIRecord ptsURL : pointsURLs) {
			try {
				PointsClient client = new PointsClient(ptsURL.getUrl());
				client.ctrlInit(startPoints);
			} catch(Exception e) {
			}
			
		}
		
	}

	
	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new BadInit exception. */
	private void throwInvalidInit(final String message) throws InvalidInitFault_Exception {
		InvalidInitFault faultInfo = new InvalidInitFault();
		faultInfo.message = message;
		throw new InvalidInitFault_Exception(message, faultInfo);
	}

	private void throwInvalidCreditCard(final String message) throws InvalidCreditCardFault_Exception {
		InvalidCreditCardFault faultInfo = new InvalidCreditCardFault();
		faultInfo.message = message;
		throw new InvalidCreditCardFault_Exception(message, faultInfo);
	}
	private void throwInvalidFoodId(final String message) throws InvalidFoodIdFault_Exception {
		InvalidFoodIdFault faultInfo = new InvalidFoodIdFault();
		faultInfo.message = message;
		throw new InvalidFoodIdFault_Exception(message, faultInfo);
	}
	private void throwInvalidFoodQuantity(final String message) throws InvalidFoodQuantityFault_Exception {
		InvalidFoodQuantityFault faultInfo = new InvalidFoodQuantityFault();
		faultInfo.message = message;
		throw new InvalidFoodQuantityFault_Exception(message, faultInfo);
	}
	private void throwInvalidMoney(final String message) throws InvalidMoneyFault_Exception {
		InvalidMoneyFault faultInfo = new InvalidMoneyFault();
		faultInfo.message = message;
		throw new InvalidMoneyFault_Exception(message, faultInfo);
	}
	private void throwInvalidText(final String message) throws InvalidTextFault_Exception {
		InvalidTextFault faultInfo = new InvalidTextFault();
		faultInfo.message = message;
		throw new InvalidTextFault_Exception(message, faultInfo);
	}
	private void throwInvalidUserId(final String message) throws InvalidUserIdFault_Exception {
		InvalidUserIdFault faultInfo = new InvalidUserIdFault();
		faultInfo.message = message;
		throw new InvalidUserIdFault_Exception(message, faultInfo);
	}
	private void throwNotEnoughPoints(final String message) throws NotEnoughPointsFault_Exception {
		NotEnoughPointsFault faultInfo = new NotEnoughPointsFault();
		faultInfo.message = message;
		throw new NotEnoughPointsFault_Exception(message, faultInfo);
	}
	private void throwEmptyCart(final String message) throws EmptyCartFault_Exception {
		EmptyCartFault faultInfo = new EmptyCartFault();
		faultInfo.message = message;
		throw new EmptyCartFault_Exception(message, faultInfo);
	}

}
