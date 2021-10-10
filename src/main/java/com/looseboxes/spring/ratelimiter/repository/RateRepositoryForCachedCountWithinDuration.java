package com.looseboxes.spring.ratelimiter.repository;

import com.looseboxes.spring.ratelimiter.cache.RateCache;
import com.looseboxes.spring.ratelimiter.rates.CountWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.CountWithinDurationDTO;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.function.Predicate;

public class RateRepositoryForCachedCountWithinDuration<ID> implements RateRepository<ID, CountWithinDurationDTO> {

    private final Logger log = LoggerFactory.getLogger(RateRepositoryForCachedCountWithinDuration.class);

    private final RateCache<ID> rateCache;

    public RateRepositoryForCachedCountWithinDuration(RateCache<ID> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public Optional<CountWithinDurationDTO> findById(ID id) {
        Rate rate = this.rateCache.get(id);
        return rate == null ? Optional.empty() : Optional.of(toDto(id, rate));
    }

    @Override
    public Page<CountWithinDurationDTO> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<CountWithinDurationDTO> findAll(Example<CountWithinDurationDTO> example, Pageable pageable) {
        log.debug("Request to get rate-limit data: {}", pageable);

        final Page<CountWithinDurationDTO> result;

        final long offset = pageable.getOffset();
        final long pageSize = pageable.getPageSize();

        if(pageSize < 1 || offset < 0) {
            result = Page.empty(pageable);
        }else{
            final List<CountWithinDurationDTO> rateList = example == null ? findAll() : findAll(example);

            log.debug("Found {} rates for {}", rateList.size(), example);

            final int total = rateList.size();
            if(offset >= total) {
                result = Page.empty(pageable);
            }else{

                final Sort sort = pageable.getSort();
                if(sort.isSorted() && !sort.isEmpty()) {
                    rateList.sort(new ComparatorFromSort<>(sort));
                }

                long end = offset + pageSize;
                if(end > total) {
                    end = total;
                }

                result = new PageImpl<>(rateList.subList((int)offset, (int)end), pageable, total);
            }
        }

        return result;
    }

    private List<CountWithinDurationDTO> findAll(Example<CountWithinDurationDTO> example) {
        return findAll(new FilterFromExample<>(example));
    }

    private List<CountWithinDurationDTO> findAll() {
        return findAll(countWithinDurationDTO -> true);
    }

    private List<CountWithinDurationDTO> findAll(Predicate<CountWithinDurationDTO> filter) {
        final List<CountWithinDurationDTO> rateList;
        if(rateCache == null) {
            rateList = Collections.emptyList();
        }else {
            rateList = new ArrayList<>();
            rateCache.forEach((id, rate) -> {
                CountWithinDurationDTO dto = toDto(id, rate);
                if (filter.test(dto)) {
                    rateList.add(dto);
                }
            });
        }
        return rateList;
    }

    private CountWithinDurationDTO toDto(ID id, Rate rate) {
        return new CountWithinDurationDTO(id, (CountWithinDuration) rate);
    }
}
