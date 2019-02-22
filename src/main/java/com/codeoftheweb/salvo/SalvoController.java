package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import  java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository game;

    @Autowired
    private GameRepository gameRepository;

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

    @RequestMapping("/leaderBoard") public List<Map<String, Object>> makeLeaderBoard() {
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

    //Para crear usuarios
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestParam String userName, @RequestParam String password) {
        if (userName.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>("Name already used", HttpStatus.CONFLICT);
        }

        playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>("Player "+userName+" added", HttpStatus.CREATED);
    }

    //restringir las vistas
    @RequestMapping("/game_view/{gpid}")
    public ResponseEntity<Object> cheat(@PathVariable long gpid, Authentication authentication) {
        Player player = playerAuthentication (authentication);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gpid).orElse(null);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "Forbidden"), HttpStatus.FORBIDDEN);
        }

        if (player.getId() != gamePlayer.getPlayer().getId()) {
            return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }


        //return gameViewDTO(gamePlayerRepository.findById(gpid).get());

        //private Map<String, Object> gameViewDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("creationDate", gamePlayer.getGame().getCreationDate().getTime());
        dto.put("gamePlayers", getGamePlayersList(gamePlayer.getGame().getGamePlayers()));
        dto.put("ships", getShipList(gamePlayer.getShips()));
        dto.put("salvoes", getSalvoList(gamePlayer.getGame()));
        //return dto;
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private Map<String, Object> makeMap(String key, String value){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }
    public Player playerAuthentication(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return playerRepository.findByUserName(authentication.getName());
        }
    }
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> newGame(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        Player player = playerAuthentication(authentication);
        if (player == null){
            return new ResponseEntity<>(makeMap("error", "Forbidden"), HttpStatus.FORBIDDEN); }
        else {
        Game game=gameRepository.save(new Game(new Date()));
        GamePlayer gamePlayer=gamePlayerRepository.save(new GamePlayer());
        response.put("gpid",gamePlayer.getId());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    }


    @RequestMapping(path = "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@PathVariable Long nn,Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        Player player = playerAuthentication(authentication);
        if (player == null){
            response.put("error","no autenticado");
            return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
        }else{
            Game game=gameRepository.getOne(nn);
            if(game!=null){
                if(game.getGamePlayers().size()<2){
                    GamePlayer gamePlayer=gamePlayerRepository.save(new GamePlayer());
                    response.put("gpid",gamePlayer.getId());
                    return new ResponseEntity<>(response,HttpStatus.CREATED);
                }else{
                    response.put("error","game is full");
                    return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
                }
            }else{
                response.put("error","no such game");
                return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
            }
        }

    }
}