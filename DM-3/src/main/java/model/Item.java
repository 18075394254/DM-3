package model;

/**
 * Created by Administrator on 16-10-31.
 */
public class Item {
    private int imageId;
    private String name;


    public Item(int imageId, String name){
        this.imageId = imageId;
        this.name = name;

    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

}
