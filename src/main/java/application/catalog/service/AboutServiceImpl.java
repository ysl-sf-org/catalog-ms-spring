package application.catalog.service;

import org.springframework.stereotype.Service;

import application.catalog.models.About;

@Service
public class AboutServiceImpl implements AboutService {

	@Override
	public About getInfo() {
		// TODO Auto-generated method stub
		return new About("Catalog Service", "Storefront", "Caches all the inventory data");
	}

}
