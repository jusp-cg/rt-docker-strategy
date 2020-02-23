package com.capgroup.dcip.app.data;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.domain.data.Series;
import com.capgroup.dcip.domain.data.SeriesFilter;
import com.capgroup.dcip.infrastructure.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * implementation that provides functionality for the domain object 'Series'
 */
@Service
public class SeriesServiceImpl implements SeriesService {
    SeriesMapper seriesMapper;
    SeriesRepository seriesRepository;

    @Autowired
    public SeriesServiceImpl(SeriesMapper seriesMapper, SeriesRepository repository) {
        this.seriesMapper = seriesMapper;
        this.seriesRepository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeriesModel> findById(long seriesId) {
        return seriesRepository.findById(seriesId).map(seriesMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public SeriesModel findByIdOrElseThrow(long seriesId) {
        return seriesRepository.findById(seriesId).map(seriesMapper::map).orElseThrow(() -> new ResourceNotFoundException("Series", Long.toString(seriesId)));
    }

    @Transactional(readOnly = true)
    public Iterable<SeriesModel> findAll(EnumSet<SeriesFilter> filters) {
        Iterable<Series> series = seriesRepository.findAll();
        return seriesMapper.mapAllSeries(StreamSupport.stream(series.spliterator(), false)
                .filter(s -> s.isValidForFilters(filters)).collect(Collectors.toList()));
    }
}
