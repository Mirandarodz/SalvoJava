package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            GamePlayerRepository gamePlayerRepository,
            ShipRepository shipRepository,
            SalvoRepository salvoRepository,
            ScoreRepository scoreRepository
) {
        return (args) -> {

            // save a couple of players
            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
            Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
            Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

            Game game1 = new Game(new Date());
            Game game2 = new Game();
            game2.setCreationDate(Date.from(game1.getCreationDate().toInstant().plusSeconds(3600)));
            Game game3 = new Game();
            game3.setCreationDate(Date.from(game2.getCreationDate().toInstant().plusSeconds(3600)));
            Game game4 = new Game();
            game4.setCreationDate(Date.from(game3.getCreationDate().toInstant().plusSeconds(3600)));
            Game game5 = new Game();
            game5.setCreationDate(Date.from(game4.getCreationDate().toInstant().plusSeconds(3600)));
            Game game6 = new Game();
            game6.setCreationDate(Date.from(game5.getCreationDate().toInstant().plusSeconds(3600)));
            Game game7 = new Game();
            game7.setCreationDate(Date.from(game6.getCreationDate().toInstant().plusSeconds(3600)));
            Game game8 = new Game();
            game8.setCreationDate(Date.from(game7.getCreationDate().toInstant().plusSeconds(3600)));
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);
            gameRepository.save(game6);


            GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
            GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
            GamePlayer gamePlayer3 = new GamePlayer(game2, player1);
            GamePlayer gamePlayer4 = new GamePlayer(game2, player2);
            GamePlayer gamePlayer5 = new GamePlayer(game3, player2);
            GamePlayer gamePlayer6 = new GamePlayer(game3, player4);
            GamePlayer gamePlayer7 = new GamePlayer(game4, player2);
            GamePlayer gamePlayer8 = new GamePlayer(game4, player1);
            GamePlayer gamePlayer9 = new GamePlayer(game5, player4);
            GamePlayer gamePlayer10 = new GamePlayer(game5, player1);
            GamePlayer gamePlayer11 = new GamePlayer(game6, player3);

            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);
            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);
            gamePlayerRepository.save(gamePlayer6);
            gamePlayerRepository.save(gamePlayer7);
            gamePlayerRepository.save(gamePlayer8);
            gamePlayerRepository.save(gamePlayer9);
            gamePlayerRepository.save(gamePlayer10);
            gamePlayerRepository.save(gamePlayer11);

           List<String> ship1Location = new ArrayList<String>();
            ship1Location.add("A1");
            ship1Location.add("A2");
            ship1Location.add("A3");
            ship1Location.add("A4");
            ship1Location.add("A5");

            List<String> ship2Location = new ArrayList<String>();
            ship2Location.add("C1");
            ship2Location.add("D1");
            ship2Location.add("E1");
            ship2Location.add("F1");

            List<String> Ship3Location = new ArrayList<String>();
            Ship3Location.add("H1");
            Ship3Location.add("H2");
            Ship3Location.add("H3");

            List<String> Ship4Location = new ArrayList<String>();
            Ship4Location.add("C3");
            Ship4Location.add("C4");
            Ship4Location.add("C5");

            List<String> Ship5Location = new ArrayList<String>();
            Ship4Location.add("F3");
            Ship4Location.add("F4");

            String shipType1 = "carrier";//length = 5
            String shipType2 = "battleship"; //length = 4
            String shipType3 = "submarine"; //length = 3
            String shipType4 = "destroyer"; //length = 3
            String shipType5 = "patrol boat"; //length = 2

            Ship ship2 = new Ship(shipType3, ship2Location, gamePlayer1);
            Ship ship1 = new Ship(shipType2, ship1Location, gamePlayer1);
            Ship ship3 = new Ship(shipType4, ship1Location, gamePlayer2);
            shipRepository.save(ship1);
            shipRepository.save(ship2);
            shipRepository.save(ship3);

            List<String> salvoLocations1 = new ArrayList<>();
            salvoLocations1.add("B4");
            salvoLocations1.add("B5");
            salvoLocations1.add("F1");

            List<String> salvoLocations2 = new ArrayList<>();
            salvoLocations2.add("C4");
            salvoLocations2.add("C5");
            salvoLocations2.add("B6");

            List<String> salvoLocations3 = new ArrayList<>();
            salvoLocations3.add("F2");
            salvoLocations3.add("D5");

            List<String> salvoLocations4 = new ArrayList<>();
            salvoLocations4.add("E1");
            salvoLocations4.add("H3");
            salvoLocations4.add("A2");

            List<String> salvoLocations5 = new ArrayList<>();
            salvoLocations5.add("A3");
            salvoLocations5.add("A4");
            salvoLocations5.add("G6");

            List<String> salvoLocations6 = new ArrayList<>();
            salvoLocations6.add("B5");
            salvoLocations6.add("D5");
            salvoLocations6.add("C7");

            List<String> salvoLocations7 = new ArrayList<>();
            salvoLocations7.add("A3");
            salvoLocations7.add("H6");

            List<String> salvoLocations8 = new ArrayList<>();
            salvoLocations8.add("C5");
            salvoLocations8.add("C6");

            Salvo salvo1 = new Salvo(gamePlayer1, 1, salvoLocations1);
            Salvo salvo2 = new Salvo(gamePlayer1, 2, salvoLocations2);
            Salvo salvo3 = new Salvo(gamePlayer2, 1, salvoLocations4);

            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            Date creationDate = new Date();

            Date finishDate = Date.from(creationDate.toInstant().plusSeconds(1800));
            Date finishDate2 = Date.from(creationDate.toInstant().plusSeconds(5400));
            Score score1 = new Score(finishDate, player1, game1, 1);
            Score score2 = new Score(finishDate, player2, game1, 0);
            Score score3 = new Score(finishDate2, player1, game2, (float) 0.5);
            Score score4 = new Score(finishDate2, player2, game2, (float) 0.5);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
        };

    }
}

            @EnableWebSecurity


            @Configuration
            class WebSecurityConfig extends WebSecurityConfigurerAdapter {

                /*@Bean
                @Override
                public UserDetailsService userDetailsService(){
                   UserDetails user =
                         User.withDefaultPasswordEncoder()
                               .username("user")
                               .password("password")
                               .build();
                   return  new InMemoryUserDetailsManager();
                }*/
                @Override
                protected void configure(HttpSecurity http) throws Exception {
                    //who can see what
                    http.authorizeRequests()
                            .antMatchers("/web/games_3.html").permitAll()
                            .antMatchers("/web/**").permitAll()
                            .antMatchers("/api/games").permitAll()
                            .antMatchers("/api/players").permitAll()
                            .antMatchers("/api/game_view").hasAuthority("user")
                            .antMatchers("/rest/*").denyAll()
                            .anyRequest().permitAll()

                            .and().formLogin()
                            .usernameParameter("name")
                            .passwordParameter("pwd")
                            .loginPage("/api/login");

                    http.logout().logoutUrl("/api/logout");

                    // turn off checking for CSRF tokens
                    http.csrf().disable();

                    // if user is not authenticated, just send an authentication failure response
                    http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

                    // if login is successful, just clear the flags asking for authentication
                    http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

                    // if login fails, just send an authentication failure response
                    http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

                    // if logout is successful, just send a success response
                    http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
                }

                private void clearAuthenticationAttributes(HttpServletRequest request) {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                    }
                }

                @Configuration
                class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

                    @Autowired
                    PasswordEncoder passwordEncoder;
                    @Autowired
                    PlayerRepository playerRepository;

                    @Override
                    public void init(AuthenticationManagerBuilder auth) throws Exception {
                        auth.userDetailsService(userName -> {
                            Player player = playerRepository.findByUserName(userName);
                            if (player != null) {
                                return new User(player.getUserName(), player.getPassword(),
                                        AuthorityUtils.createAuthorityList("USER"));
                            } else {
                                throw new UsernameNotFoundException("Unknown user: " + userName);
                            }
                        });
                    }

                }
            }