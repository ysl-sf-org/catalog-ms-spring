package application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import application.catalog.InventoryRefreshTask;

@SpringBootApplication
public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	@Autowired
	private InventoryRefreshTask refreshTask;

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
    	return new SimpleAsyncTaskExecutor();
	}
    
    @Bean
    public CommandLineRunner schedulingRunner(final TaskExecutor executor) {
    	
    	return new CommandLineRunner() {
			
			@Override
			public void run(String... args) throws Exception {
				logger.info("Starting Inventory Refresh background task ...");
				executor.execute(refreshTask);
				
			}
		};
    	
    }

}
