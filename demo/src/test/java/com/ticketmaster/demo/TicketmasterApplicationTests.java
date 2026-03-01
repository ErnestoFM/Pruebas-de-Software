package com.ticketmaster.demo;

import com.ticketmaster.demo.security.SecurityFilter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Disabled
class TicketmasterApplicationTests {
	@MockitoBean
	private SecurityFilter securityFilter;
	@Test
	void contextLoads(){
	}

}
