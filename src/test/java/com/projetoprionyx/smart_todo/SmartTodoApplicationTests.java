package com.projetoprionyx.smart_todo; // O pacote pode ser diferente no seu caso

import com.projetoprionyx.smart_todo.api.SmartTodoApplication; // Importe sua classe principal
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// CORRIJA ESTA LINHA
@SpringBootTest(classes = SmartTodoApplication.class)
class SmartTodoApplicationTests {

	@Test
	void contextLoads() {
		// Este teste simplesmente verifica se o contexto da aplicação
		// Spring consegue subir sem erros.
	}

}