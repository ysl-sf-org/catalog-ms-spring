package application.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import application.catalog.models.Item;
import application.catalog.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(value="Catalog API")
@RequestMapping(value= "/")
public class ItemController {
	
	@Autowired
	ItemService iserv;	

	/**
	 * Method to save the items in the database.
	 * @param items
	 * @return
	 */
	@ApiOperation(value = "Save items by passing them as list")
	@PostMapping(value= "/saveitems")
	public String saveItem(@RequestBody List<Item> items) {
		iserv.saveItem(items);
		return "Records saved in the db.";
	}

	/**
	 * Method to fetch all items from the database.
	 * @return
	 */
	@ApiOperation(value = "View a list of available items")
	@GetMapping(value= "/items")
	@ResponseBody
	public List<Item> getAllItems() {
		Iterable<Item> items = iserv.findAllItems();
		List<Item> items_list = new ArrayList<>();
		for(Item t: items) {
			items_list.add(t);
		}
		return items_list;	
	}
	
	/**
	 * Method to fetch the item details on the basis of id.
	 * @param name
	 * @return
	 */
	@ApiOperation(value = "View an item based on its id")
	@GetMapping(value= "/items/{item-id}")
	public Optional<Item> getById(@PathVariable(name= "item-id") long item_id) {
		return iserv.findById(item_id);
	}

}
