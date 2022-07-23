package com.secure.dglocker;

public class Model {
    private String imageUrl;
    private String person,card;

    public Model(){

    }
    public Model(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public Model(String person, String card) {
        this.person = person;
        this.card = card;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPerson(){
        return person;
    }

    public void setPerson(String person){
        this.person = person;
    }

    public String getCard(){
        return person;
    }

    public void setCard(String person){
        this.person = person;
    }
}