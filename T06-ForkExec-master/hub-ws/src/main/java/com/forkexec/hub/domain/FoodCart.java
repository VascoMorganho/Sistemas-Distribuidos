package com.forkexec.hub.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for foodId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="foodId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="restaurantId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="menuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "foodId", propOrder = {
    "restaurantId",
    "menuId"
})
public class FoodCart{

    protected int quantity;
    protected FoodId foodId;

    public FoodCart(int quant){
        this.quantity = quant;
    }

    public void setQuantity(int quant) {
        this.quantity = quant;
    }

    public int getQuantity() {
        return quantity;
    }

    public FoodId getFoodId(){
        return foodId;
    }

    public void setFoodId(FoodId fID){
        this.foodId=fID;
    }

}
