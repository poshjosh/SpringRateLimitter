package com.looseboxes.spring.ratelimiter.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RateRepository<ID, E> {

    Optional<E> findById(ID id);

    Page<E> findAll(Pageable pageable);

    Page<E> findAll(Example<E> example, Pageable pageable);
}
