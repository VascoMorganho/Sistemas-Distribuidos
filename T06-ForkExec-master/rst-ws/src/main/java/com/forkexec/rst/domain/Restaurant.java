package com.forkexec.rst.domain;
import java.util.*;

/**
 * Restaurant
 *
 * A restaurant server.
 *
 */
public class Restaurant {
	private List<Menu> _menus = new ArrayList<Menu>();
	private List<MenuOrder> _orders = new ArrayList<MenuOrder>();

	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Restaurant() {
		// Initialization of default values
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Restaurant INSTANCE = new Restaurant();
	}

	public static synchronized Restaurant getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public Menu getMenu(String menuId) {
		for (Menu men : _menus) {
			if (men.getId().equals(menuId)) return men;
		}
		
		return null;
		//throw new BadMenuIdFault_Exception();
	}

	public List<Menu> searchMenus(String desc) {
		List<Menu> temp = new ArrayList<Menu>();
		for (Menu men: _menus) {
			if (men.getEntree().indexOf(desc)!=-1 || men.getPlate().indexOf(desc)!=-1 || men.getDessert().indexOf(desc)!=-1) temp.add(men);
		}
		return temp;
	}

	public MenuOrder orderMenu(String orderId, int quantity) {
		//Verificacoes
		MenuOrder or = new MenuOrder();

		for(Menu men: _menus) {
			if (men.getId().equals(orderId)) {
				or.setMenuId(orderId);
				or.setMenuQuantity(quantity);
				men.setQuantity(men.getQuantity()-quantity);
				_orders.add(or);
				return or;
			}
		}

		return null;
	}

	public void addMenu(Menu m) {
		_menus.add(m);
	}

	public void clearAll() {
		_menus.clear();
		_menus = new ArrayList<Menu>();
		_orders.clear();
		_orders = new ArrayList<MenuOrder>();
	}
	
}
