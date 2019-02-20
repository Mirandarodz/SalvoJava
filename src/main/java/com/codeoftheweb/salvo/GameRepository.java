package com.codeoftheweb.salvo;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Player> findByCreationDate(Date creationDate);
}
