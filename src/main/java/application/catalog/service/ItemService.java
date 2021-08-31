package application.catalog.service;

import java.util.List;
import java.util.Optional;

import application.catalog.models.Item;


public interface ItemService {
	
	/**
	 * Method to save the collection of items in the database.
	 * @param employees
	 */
	public void saveItem(List<Item> items);

	/**
	 * Method to fetch all items from the database.
	 * @return
	 */
	public Iterable<Item> findAllItems();
	
	/**
	 * Method to fetch the item details on the basis of id.
	 * @param id
	 * @return
	 */
	public Optional<Item> findById(long id);

}
