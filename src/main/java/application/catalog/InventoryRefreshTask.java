package application.catalog;

import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import application.catalog.models.Item;
import application.catalog.service.ItemService;

@Component
public class InventoryRefreshTask implements Runnable {
	
    private static final Logger logger = LoggerFactory.getLogger(InventoryRefreshTask.class);
	
	private static final int INVENTORY_REFRESH_SLEEP_TIME_MS = 120000;
	
	@Autowired
	ItemService iserv;
	
	@Value( "${inventoryService.url}" )
	private String invResourceUrl;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				logger.debug("Querying Inventory Service for all items ...");
				
				RestTemplate restTemplate = new RestTemplate();
				
				ResponseEntity<Item[]> responseEntity = restTemplate.getForEntity(invResourceUrl, Item[].class);
				Item[] items_array = responseEntity.getBody();
                
                ArrayList<Item> items= new ArrayList<Item>();
                Collections.addAll(items, items_array);
				
			    iserv.saveItem(items);
			    
			    logger.info("Loaded in to the cache");
			    
				
			} catch (Exception e) {
				logger.warn("Caught exception, ignoring", e);
			}
			try {
				Thread.sleep(INVENTORY_REFRESH_SLEEP_TIME_MS);
			} catch (InterruptedException e) {
				logger.warn("Caught InterruptedException, quitting");
				break;
			}
		}
		
	}

}
