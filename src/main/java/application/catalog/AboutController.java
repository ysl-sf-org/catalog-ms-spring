package application.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import application.catalog.models.About;
import application.catalog.service.AboutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * Class is responsible for handling rest end points
 */
@RestController
@RequestMapping("/")
@Api(value = "About Catalog service")
public class AboutController {
	
	@Autowired
    AboutService aboutService;
	
	/**
	 * @return about customer
	 */
	@ApiOperation(value = "About Catalog")
	@GetMapping(path = "/about", produces = "application/json")
	@ResponseBody 
	public About aboutCatalog() {
		return aboutService.getInfo();
	}

}
