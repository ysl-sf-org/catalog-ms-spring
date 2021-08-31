package application.catalog.models;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepo extends ElasticsearchRepository<Item, Long> {
    
    /**
     * Method to fetch the item details on the basis of name by using Elastic-Search-Repository.
     * @param designation
     * @return
     */
    public Optional<Item> findById(long id);

}
