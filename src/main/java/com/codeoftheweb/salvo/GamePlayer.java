package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity

public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private List<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer")
    //van en plural salvoes y ships, por los datos que se sacan de la lista...luego se definen sus respectivos get y set
    private List<Salvo> salvoes;

    public GamePlayer() {
        this.ships = new ArrayList<>();
        this.salvoes = new ArrayList<>();
    }

    public long getId() { return id; }

    public GamePlayer(Game game, Player player) {
        this.setGame(game);
        this.setPlayer(player);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public void setSalvoes(List<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public List<Salvo> getSalvoes() {
        return salvoes;
    }

    public boolean isTurnLoaded (int turn) {
        Salvo salvo = this.getSalvoes().stream().filter(s -> s.getTurn()==turn).findFirst().orElse(null);
        return salvo != null;
    }

    public int getLastTurn(){
        int lastTurn = 0;
        for(Salvo salvo : getSalvoes()) {
            if (salvo.getTurn()>lastTurn){
                lastTurn = salvo.getTurn();
            }
        }
        return lastTurn;
    }
}