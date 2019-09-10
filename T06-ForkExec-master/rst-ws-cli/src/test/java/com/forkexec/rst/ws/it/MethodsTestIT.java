package com.forkexec.rst.ws.it;

import static org.junit.Assert.assertNotNull;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;
import com.forkexec.rst.ws.*;

import org.junit.Test;

public class MethodsTestIT extends BaseIT {
 	private final static List<MenuInit> initialMenus = new ArrayList<MenuInit>();
	private final static MenuInit menuInit1 = new MenuInit();
	private final static Menu menu1 = new Menu();
	private final static MenuId menuId1 = new MenuId();
	private final static Menu menu2 = new Menu();
	private final static MenuId menuId2 = new MenuId();
	private final static MenuInit menuInit2 = new MenuInit();

	@Before
	public void setUp() throws BadInitFault_Exception{
		initialMenus.clear();
		menuInit1.setQuantity(10);
		menuId1.setId("menuId1");
		menuInit1.setMenu(menu1);
	   	menu1.setId(menuId1);
    	menu1.setEntree("Entree1");
    	menu1.setPlate("Plate1");
    	menu1.setDessert("Dessert1");
    	menu1.setPrice(50);
    	menu1.setPreparationTime(10);

    	
		menuInit2.setQuantity(10);
		menuInit2.setMenu(menu2);
		menuId2.setId("menuId2");

	   	menu2.setId(menuId2);
    	menu2.setEntree("Entree2");
    	menu2.setPlate("Plate2");
    	menu2.setDessert("Dessert2");
    	menu2.setPrice(25);
    	menu2.setPreparationTime(5);

    	initialMenus.add(menuInit1);
    	initialMenus.add(menuInit2);


		client.ctrlInit(initialMenus);
	}

	@After
	public void tearDown() {
		client.ctrlClear();
	}


	// SearchMenus Methods
	@Test
	public void SearchMenuSuccess() throws BadTextFault_Exception{
		List<Menu> test = new ArrayList<Menu>();
		test.add(menu1);
		test.add(menu2);
		List<Menu> resp = client.searchMenus("Plate");

		for(int i = 0 ; i<resp.size();i++) {
			assertTrue(menuEquals(resp.get(i),test.get(i)));

		}
    }

    @Test(expected = BadTextFault_Exception.class) 
    public void EmptyText() throws BadTextFault_Exception{
    	client.searchMenus("");
    }  

    @Test(expected = BadTextFault_Exception.class)
    public void TextWithSpaces() throws BadTextFault_Exception{
    	client.searchMenus(" Plate ");
    } 
    ////////////////

  
    //GetMenu Methods
    @Test
    public void GetMenuSuccess() throws BadMenuIdFault_Exception {
    	assertTrue(menuEquals(menu1,client.getMenu(menu1.getId())));
    }

    @Test(expected = BadMenuIdFault_Exception.class) 
    public void GetMenuInvalid() throws BadMenuIdFault_Exception{
    	MenuId p = new MenuId();
    	p.setId("invalidId");
	    client.getMenu(p);
    } 

    ////////////////

    // OrderMenu Methods
	@Test(expected = BadMenuIdFault_Exception.class) 
    public void OrderMenuInvalidMenuId() throws BadMenuIdFault_Exception,BadQuantityFault_Exception,InsufficientQuantityFault_Exception{
    	MenuId p = new MenuId();
    	p.setId("invalidId");
	    client.orderMenu(p,10);
    }

    @Test(expected = BadQuantityFault_Exception.class) 
    public void OrderMenuBadQuantity() throws BadMenuIdFault_Exception,BadQuantityFault_Exception,InsufficientQuantityFault_Exception{
	    client.orderMenu(menuId1,0);
    } 

    @Test(expected = InsufficientQuantityFault_Exception.class) 
    public void OrderMenuInsufficientQuantity() throws BadMenuIdFault_Exception,BadQuantityFault_Exception,InsufficientQuantityFault_Exception{
	    client.orderMenu(menuId1,menuInit1.getQuantity()+1);
    }

    @Test(expected = InsufficientQuantityFault_Exception.class) 
    public void OrderMenuInsufficientQuantity2() throws BadMenuIdFault_Exception,BadQuantityFault_Exception,InsufficientQuantityFault_Exception{
	    client.orderMenu(menuId1,menuInit1.getQuantity()-1);
	    client.orderMenu(menuId1,menuInit1.getQuantity()-1);
    }

    ////////////////////////

     

   
    //Aux Function
    private boolean menuEquals(Menu n1, Menu n2) {
    	return (n1.getId().getId().equals(n2.getId().getId()))
    	&& (n1.getEntree().equals(n2.getEntree())) &&
			(n1.getPlate().equals(n2.getPlate())) &&
			(n1.getDessert().equals(n2.getDessert())) &&
			(n1.getPrice()==n2.getPrice()) &&
			(n1.getPreparationTime()==n2.getPreparationTime());
    }
}
