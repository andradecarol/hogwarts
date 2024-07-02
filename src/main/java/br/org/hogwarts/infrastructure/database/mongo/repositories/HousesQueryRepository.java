package br.org.hogwarts.infrastructure.database.mongo.repositories;

import br.org.hogwarts.domain.entities.Hogwarts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class HousesQueryRepository {

    private final MongoTemplate mongoTemplate;

    public Page<Hogwarts> findWithFilter(String name, String color, String leaderName, Pageable pageable) {

        var query = new Query().with(pageable);

        if (nonNull(name)) {
            query.addCriteria(Criteria.where("name")
                    .regex(Pattern.compile(name, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
        }

        if (nonNull(color)) {
            query.addCriteria(Criteria.where("color")
                    .regex(Pattern.compile(color, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
        }

        if (nonNull(leaderName)) {
            query.addCriteria(Criteria.where("leaderName")
                    .regex(Pattern.compile(leaderName, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
        }


        final var housesList = mongoTemplate.find(query, Hogwarts.class);

        return PageableExecutionUtils.getPage(housesList, pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Hogwarts.class));
    }
}
