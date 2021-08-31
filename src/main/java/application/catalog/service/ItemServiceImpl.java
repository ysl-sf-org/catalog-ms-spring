package application.catalog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.catalog.models.Item;
import application.catalog.models.ItemRepo;

@Service
public class ItemServiceImpl implements ItemService {
	
	// The dao repository will use the Elastic-Search-Repository to perform the database operations.
    @Autowired
	private ItemRepo idao;

	@Override
	public void saveItem(List<Item> items) {
		idao.saveAll(items);
		
	}

	@Override
	public Iterable<Item> findAllItems() {
		return idao.findAll();
	}

	@Override
	public Optional<Item> findById(long id) {
		return idao.findById(id);
	}

}
