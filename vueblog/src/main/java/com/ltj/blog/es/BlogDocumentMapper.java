package com.ltj.blog.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogDocumentMapper extends ElasticsearchRepository<BlogDocument, Long> {
}
