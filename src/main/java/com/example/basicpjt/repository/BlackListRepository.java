package com.example.basicpjt.repository;

import com.example.basicpjt.domain.Token.BlackList;
import org.springframework.data.repository.CrudRepository;

public interface BlackListRepository extends CrudRepository<BlackList, String> {
}
