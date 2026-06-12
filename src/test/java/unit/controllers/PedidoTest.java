package unit.controllers;

import Model.Cliente;
import Model.Pedido;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    @Test
    public void deveArmazenarEAtribuirValorCorretamente() {
        Pedido pedido = new Pedido();
        Double valorEsperado = 150.50;
        
        pedido.setValor_total(valorEsperado);
        
        assertEquals(valorEsperado, pedido.getValor_total(), "O valor total recuperado deve ser igual ao atribuÃ­do.");
    }

    @Test
    public void deveVincularUmClienteAoPedido() {
        Pedido pedido = new Pedido();
        Cliente cliente = new Cliente();
        cliente.setNome("JoÃ£o Silva");
        
        pedido.setCliente(cliente);
        
        assertNotNull(pedido.getCliente(), "O objeto Cliente nÃ£o deve ser nulo.");
        assertEquals("JoÃ£o Silva", pedido.getCliente().getNome(), "O nome do cliente no pedido deve ser JoÃ£o Silva.");
    }

    @Test
    public void deveValidarADataDoPedido() {
        Pedido pedido = new Pedido();
        String dataEsperada = "27/04/2026";
        
        pedido.setData_pedido(dataEsperada);
        
        assertEquals(dataEsperada, pedido.getData_pedido(), "A data do pedido deve ser a mesma que foi definida.");
    }
}
