package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    //propiedad username de tipo string privado
    private String userName;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    public Player() {
    }

    public Player(String user) {
        userName = user;
    }
    public String getUserName() {
        return  userName;
    }

    public long getId() {
        return id;
    }

    //setter asigna el argumento (userName) que le llega a la propiedad privada(this.userName)
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String toString() {
        return userName;
    }


    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void addGamePlayers(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        this.gamePlayers.add(gamePlayer);
    }
}
