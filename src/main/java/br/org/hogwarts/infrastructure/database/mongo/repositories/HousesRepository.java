package br.org.hogwarts.infrastructure.database.mongo.repositories;

import br.org.hogwarts.domain.entities.Hogwarts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HousesRepository extends MongoRepository<Hogwarts, String> {
    Boolean existsByName(String name);

    Boolean existsByIdNotAndName(String id, String name);
}
