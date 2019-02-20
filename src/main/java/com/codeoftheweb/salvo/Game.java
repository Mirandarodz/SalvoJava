package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private List<GamePlayer> gamePlayers;


    public Game() {
        setCreationDate(new Date());
    }

    public long getId () {
        return id;
    }

    public Date getCreationDate () {
        return creationDate;
    }

    public void setCreationDate (Date creationDate){
        this.creationDate = creationDate;
    }
    public String toString () {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(getCreationDate());
    }


    public List<GamePlayer> getGamePlayers () {
        return gamePlayers;
    }

    public void addGamePlayers (GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        this.getGamePlayers().add(gamePlayer);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGamePlayers(List<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
}