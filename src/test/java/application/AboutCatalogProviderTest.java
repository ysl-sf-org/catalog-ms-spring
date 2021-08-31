package application;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import application.catalog.InventoryRefreshTask;
import application.catalog.models.About;
import application.catalog.service.AboutService;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;

@RunWith(SpringRestPactRunner.class)
@SpringBootTest(classes=Main.class,properties={"spring.profiles.active=test","spring.cloud.config.enabled=false"},webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@PactBroker
@Provider("catalog_provider")
public class AboutCatalogProviderTest {
	
	@MockBean
	private InventoryRefreshTask invRefreshTask;
	
	@MockBean
	private AboutService aboutService;
	
	@TestTarget
	public final Target target = new HttpTarget(8080);
	
	@State("test GET")
	public void toGetState() throws Exception{ 
		About abt = new About("Catalog Service", "Storefront", "Caches all the inventory data");
		Mockito.when(aboutService.getInfo()).thenReturn(abt);
	}

}
