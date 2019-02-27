package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;


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
        dto.put("shipLocations", ship.getShipLocations());
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
        Player player = playerAuthentication(authentication);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gpid).orElse(null);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "Forbidden"), HttpStatus.FORBIDDEN);
        }

        if (player.getId() != gamePlayer.getPlayer().getId()) {
            return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        return gameViewDTO(gamePlayerRepository.findById(gpid).get());
    }

    private ResponseEntity<Object> gameViewDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        GamePlayer self = gamePlayer;
        GamePlayer opponent = gamePlayer.getGame().getGamePlayers().stream().filter(gp -> gp.getId() != gamePlayer.getId()).findFirst().orElse(new GamePlayer());
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreationDate().getTime());
        dto.put("gamePlayers", getGamePlayersList(gamePlayer.getGame().getGamePlayers()));
        dto.put("ships", getShipList(gamePlayer.getShips()));
        dto.put("salvoes", getSalvoList(gamePlayer.getGame()));
        dto.put("hits", makeHitsDTO(self, opponent));
        dto.put("gameState", getGameState(self, opponent));

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private  Map<String, Object> makeHitsDTO(GamePlayer self,GamePlayer opponent){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("self", getHits(self, opponent));
        dto.put("opponent", getHits(opponent, self));

        return dto;
    }
    private List<Map> getHits(GamePlayer gamePlayer, GamePlayer opponentGamePlayer) { //le paso dos gameplayers
        List<Map> hits = new ArrayList<>();
        Integer carrierDamage = 0;
        Integer battleshipDamage = 0;
        Integer submarineDamage = 0;
        Integer destroyerDamage = 0;
        Integer patrolboatDamage = 0; //declaro variables
        List <String> carrierLocation = new ArrayList<>();
        List <String> battleshipLocation = new ArrayList<>();
        List <String> submarineLocation = new ArrayList<>();
        List <String> destroyerLocation = new ArrayList<>();
        List <String> patrolboatLocation = new ArrayList<>(); //creo una nueva lista con las ubicaciones de cada barco
        gamePlayer.getShips().forEach(ship -> {
            switch (ship.getShipType()) {
                case "Carrier":
                    carrierLocation.addAll(ship.getShipLocations());
                    break;
                case "Battleship":
                    battleshipLocation.addAll(ship.getShipLocations());
                    break;
                case "Submarine":
                    submarineLocation.addAll(ship.getShipLocations());
                    break;
                case "Destroyer":
                    destroyerLocation.addAll(ship.getShipLocations());
                    break;
                case "Patrol Boat":
                    patrolboatLocation.addAll(ship.getShipLocations());    //paso las ubicaciones de gameplayer a las nuevas listas
                    break;
            }
        });
        for (Salvo salvo : opponentGamePlayer.getSalvoes()) {  //obtener los salvos del oponente
            Integer carrierHitsInTurn = 0;
            Integer battleshipHitsInTurn = 0;
            Integer submarineHitsInTurn = 0;
            Integer destroyerHitsInTurn = 0;
            Integer patrolboatHitsInTurn = 0; //declaro variables de los hits acertados
            Integer missedShots = salvo.getSalvoLocations().size(); //para ir descontando los tiros errados
            Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();  //mapa de los hits
            Map<String, Object> damagesPerTurn = new LinkedHashMap<>();  //mapa del daño que le hace al gp?
            List<String> salvoLocationsList = new ArrayList<>(); //
            List<String> hitCellsList = new ArrayList<>();  //acumula las celdas pegadas
            salvoLocationsList.addAll(salvo.getSalvoLocations());
            for (String salvoShot : salvoLocationsList) {
                if (carrierLocation.contains(salvoShot)) {
                    carrierDamage++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (battleshipLocation.contains(salvoShot)) {
                    battleshipDamage++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (submarineLocation.contains(salvoShot)) {
                    submarineDamage++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (destroyerLocation.contains(salvoShot)) {
                    destroyerDamage++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (patrolboatLocation.contains(salvoShot)) {
                    patrolboatDamage++;
                    patrolboatHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
            }
            damagesPerTurn.put("carrierHits", carrierHitsInTurn);
            damagesPerTurn.put("battleshipHits", battleshipHitsInTurn);
            damagesPerTurn.put("submarineHits", submarineHitsInTurn);
            damagesPerTurn.put("destroyerHits", destroyerHitsInTurn);
            damagesPerTurn.put("patrolboatHits", patrolboatHitsInTurn);
            damagesPerTurn.put("Carrier", carrierDamage);
            damagesPerTurn.put("Battleship", battleshipDamage);
            damagesPerTurn.put("Submarine", submarineDamage);
            damagesPerTurn.put("Destroyer", destroyerDamage);
            damagesPerTurn.put("Patrol Boat", patrolboatDamage);
            hitsMapPerTurn.put("turn", salvo.getTurn());
            hitsMapPerTurn.put("hitLocations", hitCellsList);
            hitsMapPerTurn.put("damages", damagesPerTurn);
            hitsMapPerTurn.put("missed", missedShots);
            hits.add(hitsMapPerTurn);
        }
        return hits;
    }

    private Map<String, Object> makeMap(String key, String value){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }
    public Player playerAuthentication(Authentication authentication) {
        //En Java || es un O (OR)
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
        GamePlayer gamePlayer=gamePlayerRepository.save(new GamePlayer(game,player));
        response.put("gpid",gamePlayer.getId());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    }

 // para entrar en el juego
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
                    GamePlayer gamePlayer=gamePlayerRepository.save(new GamePlayer(game, player));
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

    @RequestMapping(path =  "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication) {

        Player player = playerAuthentication(authentication);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if (gamePlayer == null) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}

        if (player.getId() != gamePlayer.getPlayer().getId()) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (gamePlayer.getShips().size()>=5) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        for (Ship ship : ships){
            ship.setGamePlayer(gamePlayer);
            shipRepository.save(ship);
        }

        return new ResponseEntity<>(makeMap("OK", "Ships placed! :D "), HttpStatus.OK);
    }

    @RequestMapping(path =  "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvoes(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {

        Player player = playerAuthentication(authentication);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if (gamePlayer == null) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}

        if (player == null) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}

        if (player.getId() != gamePlayer.getPlayer().getId()) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}

        if (gamePlayer.isTurnLoaded(salvo.getTurn())) {return new ResponseEntity<>(HttpStatus.FORBIDDEN);}

        salvoRepository.save(salvo);

        return new ResponseEntity<>(makeMap("OK", "Salvo placed! :D "), HttpStatus.CREATED);
    }
    private String getGameState(GamePlayer selfGP, GamePlayer opponentGP) {
        List<Ship> selfShips = selfGP.getShips();
        List<Salvo> selfSalvoes = selfGP.getSalvoes();
        if (selfShips.size() == 0){
            return "PLACESHIPS";
        }
        if (opponentGP.getShips() == null){
            return "WAITINGFOROPP";
        }
        int turn = getCurrentTurn(selfGP, opponentGP);
        List<Ship> opponentShips = opponentGP.getShips();
        List<Salvo> opponentSalvoes = opponentGP.getSalvoes();
        if (opponentShips.size() == 0){
            return "WAIT";
        }
        if(selfSalvoes.size() == opponentSalvoes.size()){
            Player self = selfGP.getPlayer();
            Game game = selfGP.getGame();
            if (allPlayerShipsSunk(selfShips, opponentSalvoes) && allPlayerShipsSunk(opponentShips, selfSalvoes)){
                Score score = new Score(new Date(),self, game, 0.5f);
                if(!existScore(score, game)) {
                    scoreRepository.save(score);
                }
                return "TIE";
            }
            if (allPlayerShipsSunk(selfShips, opponentSalvoes)){
                Score score = new Score(new Date(), self, game, 0 );
                if(!existScore(score, game)) {
                    scoreRepository.save(score);
                }
                return "LOST";
            }
            if(allPlayerShipsSunk(opponentShips, selfSalvoes)){
                Score score = new Score(new Date(), self, game, 1);
                if(!existScore(score, game)) {
                    scoreRepository.save(score);
                }
                return "WON";
            }
        }
        if (selfSalvoes.size() != turn){
            return "PLAY";
        }
        return "WAIT";
    }
    private int getCurrentTurn(GamePlayer selfGP, GamePlayer opponentGP){
        int selfGPSalvoes = selfGP.getSalvoes().size();
        int opponentGPSalvoes = opponentGP.getSalvoes().size();

        int totalSalvoes = selfGPSalvoes + opponentGPSalvoes;

        if(totalSalvoes % 2 == 0)
            return totalSalvoes / 2 + 1;

        return (int) (totalSalvoes / 2.0 + 0.5);
    }
    private boolean allPlayerShipsSunk(List<Ship> selfShips, List<Salvo> opponentSalvoes){
        List<String> carrierLocations = new ArrayList<>();
        List<String> battleshipLocations = new ArrayList<>();
        List<String> patrolboatLocations = new ArrayList<>();
        List<String> submarineLocations = new ArrayList<>();
        List<String> destroyerLocations = new ArrayList<>();
        int carrierDamage = 0;
        int battleshipDamage = 0;
        int patrolboatDamage = 0;
        int submarineDamage = 0;
        int destroyerDamage = 0;
        for(Ship ship : selfShips){
            if(ship.getShipType().equals("carrier")){
                carrierLocations.addAll(ship.getShipLocations());
            }else if(ship.getShipType().equals("battleship")){
                battleshipLocations.addAll(ship.getShipLocations());
            }else if(ship.getShipType().equals("patrolboat")){
                patrolboatLocations.addAll(ship.getShipLocations());
            }else if(ship.getShipType().equals("submarine")){
                submarineLocations.addAll(ship.getShipLocations());
            }else if(ship.getShipType().equals("destroyer")){
                destroyerLocations.addAll(ship.getShipLocations());
            }
        }
        List<String> salvoesLocations = new ArrayList<>();
        for(Salvo salvo : opponentSalvoes){
            salvoesLocations.addAll(salvo.getSalvoLocations());
        }
        for(String salvoLocation : salvoesLocations){
            if(existLocation(salvoLocation, carrierLocations)){
                carrierDamage++;
            }else if(existLocation(salvoLocation, battleshipLocations)) {
                battleshipDamage++;
            }else if(existLocation(salvoLocation, patrolboatLocations)){
                patrolboatDamage++;
            }else if(existLocation(salvoLocation, submarineLocations)){
                submarineDamage++;
            }else if(existLocation(salvoLocation, destroyerLocations)){
                destroyerDamage++;
            }
        }
        if(carrierDamage == 5 && battleshipDamage == 4 && destroyerDamage == 3 && submarineDamage == 3 && patrolboatDamage == 2){
            return true;
        }
        return false;
    }
    private boolean existLocation(String location, List<String> locations){
        for(String _location : locations){
            if(location.equals(_location)){
                return true;
            }
        }
        return false;
    }
    private boolean existSalvo(Salvo salvo, Set<Salvo> salvoes){
        for(Salvo s : salvoes){
            if(salvo.getTurn() == s.getTurn()){
                return true;
            }
        }
        return false;
    }
    private boolean existScore(Score score, Game game){
        List<Score> scores = game.getScores();
        for(Score s : scores){
            if(score.getPlayer().getUserName().equals(s.getPlayer().getUserName())){
                return true;
            }
        }
        return false;
    }
}
