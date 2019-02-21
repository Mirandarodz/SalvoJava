package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import  java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository game;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<Map<String, Object>> getAllGames() {
        return game
                .findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(Collectors.toList());}

    @RequestMapping("/games")
    public Map<String,Object> makeLoggedPlayer(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            dto.put("player", "Guest");
        else
            dto.put("player", loggedPayerDTO(playerRepository.findByUserName((authentication.getName()))));
        dto.put("games", getAllGames());

        return dto;
    }

    public Map<String, Object> loggedPayerDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("name", player.getUserName());

        return dto;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("creationDate", game.getCreationDate().getTime());
        dto.put("gamePlayers", getGamePlayersList(game.getGamePlayers()));
        dto.put("scores", getScores(game.getScores()));

        return dto;
    }

    private List<Map<String, Object>> getGamePlayersList(List<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(Collectors.toList());
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        dto.put("creationDate", gamePlayer.getGame().getCreationDate());

        return dto;
    }

    private List<Map<String, Object>> getScores(List<Score> scores) {
        return scores
                .stream()
                .map(score -> makeScoreDTO(score))
                .collect(Collectors.toList());
    }

    private Map<String, Object> makeScoreDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", score.getId());
        dto.put("playerID", score.getPlayer().getId());
        dto.put("score", score.getScore());


        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("userName", player.getUserName());

        return dto;
    }

    private Map<String, Object> shipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("shipType", ship.getShipType());
        dto.put("shipLocation", ship.getShipLocation());
        dto.put("player", ship.getGamePlayer().getPlayer().getId());
        return dto;
    }

    private List<Map<String, Object>> makeShipList(List<Ship> ships) {
        return ships
                .stream()
                .map(ship -> shipDTO(ship))
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getShipList(List<Ship> ships) {
        {
            return ships
                    .stream()
                    .map(ship -> shipDTO(ship))
                    .collect(Collectors.toList());
        }
    }

    private Map<String, Object> salvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getSalvoLocations());
        return dto;
    }

    private List<Map<String, Object>> makeSalvoList(List<Salvo> salvoes) {
        return salvoes
                .stream()
                .map(salvo -> salvoDTO(salvo))
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getSalvoList(Game game) {
        List<Map<String, Object>> myList = new ArrayList<>();
        game.getGamePlayers().forEach(gamePlayer -> myList.addAll(makeSalvoList(gamePlayer.getSalvoes())));
        return myList;
    }

    @RequestMapping("/game_view/{id}")
    public Map<String, Object> getGameView(@PathVariable long id) {
        return gameViewDTO(gamePlayerRepository.findById(id).get());
    }

    private Map<String, Object> gameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("creationDate", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers", getGamePlayersList(gamePlayer.getGame().getGamePlayers()));
        dto.put("ships", getShipList(gamePlayer.getShips()));
        dto.put("salvoes", getSalvoList(gamePlayer.getGame()));
        return dto;
    }

    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> makeLeaderBoard() {
        return playerRepository
                .findAll()
                .stream()
                .map(player -> playerLeaderDTO(player))
                .collect(Collectors.toList());
    }

    public Map<String, Object> playerLeaderDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("userName", player.getUserName());
        dto.put("score", makeScoreList(player));
        return dto;
    }

    public Map<String, Object> makeScoreList(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("total", player.getScore(player));
        dto.put("won", player.getWins(player.getScores()));
        dto.put("lost", player.getLoses(player.getScores()));
        dto.put("tied", player.getDraws(player.getScores()));

        return dto;
    }
}