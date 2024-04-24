package com.cowrycode.cassandradataloader.author;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorResository extends CassandraRepository<Author, String> {
}
