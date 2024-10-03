package com.example.amity_1;

public class Staff {
    private String name;
    private String position;
    private int imageResource; // Assuming you're using drawable resources for images

    public Staff(String name, String position, int imageResource) {
        this.name = name;
        this.position = position;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getImageResource() {
        return imageResource;
    }
}
