package com.codeoftheweb.salvo;

import javax.persistence.*;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;


@Entity
public class Ship{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String shipType;

    @ElementCollection
    @Column(name = "shipLocation")
    private List<String> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {
    }

    public Ship(String shipType, List<String> shipLocation, GamePlayer gamePlayer) {
        this.setShipType(shipType);
        this.setLocations(shipLocation);
        this.setGamePlayer(gamePlayer);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }
}