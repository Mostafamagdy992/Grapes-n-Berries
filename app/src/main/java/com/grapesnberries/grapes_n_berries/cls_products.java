package com.grapesnberries.grapes_n_berries;


public class cls_products{
    private String Id;
    private String ImageWidth;
    private String ImageHeight;
    private String ImageUrl;

    private String Price;
    private String Description;

    public cls_products(String Id, String ImageWidth, String ImageHeight, String ImageUrl, String Price, String Description) {
        this.Id = Id;
        this.ImageWidth = ImageWidth;
        this.ImageHeight = ImageHeight;
        this.ImageUrl = ImageUrl;
        this.Price = Price;
        this.Description = Description;
    }

    public String getProductID() { return Id; }
    public String getImageWidth() { return ImageWidth; }
    public String getImageHeight() { return ImageHeight; }
    public String getImageUrl() { return ImageUrl; }
    public String getPrice() { return Price; }
    public String getDescription() { return Description; }
}