package com.forkexec.rst.domain;

public class MenuOrder {

    protected String id;
    protected String menuId;
    protected int menuQuantity;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link MenuOrderId }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link MenuOrderId }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the menuId property.
     * 
     * @return
     *     possible object is
     *     {@link MenuId }
     *     
     */
    public String getMenuId() {
        return menuId;
    }

    /**
     * Sets the value of the menuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link MenuId }
     *     
     */
    public void setMenuId(String value) {
        this.menuId = value;
    }

    /**
     * Gets the value of the menuQuantity property.
     * 
     */
    public int getMenuQuantity() {
        return menuQuantity;
    }

    /**
     * Sets the value of the menuQuantity property.
     * 
     */
    public void setMenuQuantity(int value) {
        this.menuQuantity = value;
    }

}
