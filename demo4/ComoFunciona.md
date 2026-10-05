# PDV Elmix - Como está o sistema

## O que foi feito

O sistema PDV da Elmix foi construído do zero em **JavaFX puro** (sem FXML), seguindo a arquitetura **MVC** e persistindo dados em arquivos `.ser` (serialização Java).

---

## Estrutura dos Arquivos

```
src/main/java/
├── module-info.java              → Configuração de módulos Java
├── pdv/demo4/
│   ├── ElmixApp.java             → Classe principal (conecta tudo)
│   ├── Launcher.java             → Lançador do sistema
│   │
│   ├── model/                    → MODELOS (dados)
│   │   ├── ItemVenda.java        → Um item de uma venda (produto + valor)
│   │   ├── Venda.java            → Uma venda completa (itens, total, troco)
│   │   └── Configuracao.java     → Configurações do sistema (cores, nome)
│   │
│   ├── view/                     → TELAS (interface)
│   │   ├── TelaInicialView.java  → Tela principal com passagem bíblica
│   │   ├── TelaPDVView.java      → Tela de vendas (calculadora)
│   │   ├── TelaContaView.java    → Informações da loja
│   │   ├── TelaConfigView.java   → Tela de configurações
│   │   ├── TelaHistoricoView.java→ Histórico de vendas
│   │   └── TelaBackupView.java   → Backup por período
│   │
│   ├── controller/               → CONTROLADORES (lógica)
│   │   ├── VendaController.java  → Lógica de vendas e relatórios
│   │   └── ConfiguracaoController.java → Lógica de configurações
│   │
│   └── util/                     → UTILITÁRIOS
│       └── PersistenciaUtil.java → Salvar/carregar dados em .ser
```

---

## Telas do Sistema

### 1. Tela Inicial
- **Header**: Nome "ELMIX" em vermelho suave
- **Centro**: Passagem bíblica (Jeremias 29:11)
- **Esquerda**: Botões - Conta, Salvar Backup, Carregar Backup, Configurações
- **Direita**: Botões - Iniciar Venda, Histórico, Fechar Caixa
- **Footer**: Créditos + link GitHub

### 2. Tela PDV (Ponto de Venda)
- Campo para nome do produto (se vazio, vira "Diversos")
- Campo para valor
- Lista de itens da venda
- Campo de valor pago + cálculo automático de troco
- **Atalhos**: F1 = nova venda, F3 = campo pagamento, F4 = finalizar

### 3. Tela Conta
- Mostra nome da loja, endereço da Elmix e link do Google Maps

### 4. Tela Configurações
- Mudar nome da loja, cores do sistema, bordas, animações, link do site
- Botão de restaurar padrão

### 5. Tela Histórico
- Lista todas as vendas realizadas
- Clica numa venda para ver os detalhes

### 6. Tela Backup
- Escolhe data inicial e final
- Gera relatório TXT com as vendas do período

---

## Como os Dados são Salvos

Os dados ficam na pasta `dados_elmix/` no diretório onde o programa roda:
- `vendas.ser` → Todas as vendas
- `configuracao.ser` → Configurações do sistema

São arquivos serializados do Java (`.ser`), que funcionam como um banco de dados simples.

---

## Como Rodar

```bash
./mvnw clean javafx:run
```

---

## O que falta (futuro)
- Animação de folhas caindo na tela inicial (descrita no documento mas não implementada ainda - MVP primeiro)
- Passagem bíblica por API (atualmente é fixa)
- Link para futuro site da loja
