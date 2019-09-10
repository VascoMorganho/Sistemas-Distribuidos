package com.forkexec.hub.domain;



import java.util.*;
import java.lang.*;

public class Cart {

    protected List<FoodCart> foods;
    protected String userId;


    public Cart(String userI){
        this.userId = userI;
        this.foods = new ArrayList<FoodCart>();
    }

    public void addToCart(FoodId food, int quant){
            FoodCart foodC = new FoodCart(quant);
    		this.foods.add(foodC);
    	
    }

    public List<FoodCart> getFoods(){
    	return this.foods;
    }

    public void clear(){
    	foods.clear();
    }

    public String getUserId(){
        return userId;
    }
 }
