package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany (mappedBy = "player", fetch = FetchType.EAGER)
    private List<Score> scores;

    public Player() {
    }

    public Player(String userName, String password) {
        this.userName   =   userName;
        this.password   =   password;
    }

    public float getScore (Player player){
        return getWins(player.getScores())*1    +   getDraws(player.getScores())*((float) 0.5)  +   getLoses(player.getScores())*0;
    }

    public float getWins (List<Score> scores){
        return  scores
                .stream()
                .filter(score -> score.getScore()==1)
                .count();
    }
    public float getDraws (List<Score> scores) {
        return scores
                .stream()
                .filter(score -> score.getScore() == 0.5)
                .count();
    }
    public float getLoses (List<Score> scores) {
        return scores
                .stream()
                .filter(score -> score.getScore() == 0)
                .count();
    }

    public String toString() {
        return getUserName();
    }

    //setter asigna el argumento (userName) que le llega a la propiedad privada(this.userName)
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return  userName;
    }

    public long getId() {
        return id;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}