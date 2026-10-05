package pdv.demo4.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class VendaTest {

    @Test
    void copiarComoNovaCriaVendaIndependenteComItensEsemPagamento() {
        Venda original = new Venda(1);
        original.adicionarItem(new ItemVenda("Produto", 12.50));
        original.finalizarVenda(20.00);

        Venda copia = original.copiarComoNova(2);

        assertEquals(2, copia.getId());
        assertEquals(12.50, copia.getTotalVenda());
        assertEquals(0.0, copia.getValorPago());
        assertEquals(0.0, copia.getTroco());
        assertNotSame(original.getItens(), copia.getItens());
        assertNotSame(original.getItens().get(0), copia.getItens().get(0));

        copia.getItens().get(0).setValor(15.00);
        assertEquals(12.50, original.getItens().get(0).getValor());
        assertEquals(15.00, copia.getItens().get(0).getValor());
    }
}
